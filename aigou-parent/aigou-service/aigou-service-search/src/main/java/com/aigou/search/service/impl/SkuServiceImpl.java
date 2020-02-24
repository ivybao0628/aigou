package com.aigou.search.service.impl;

import com.aigou.goods.feign.SkuFeign;
import com.aigou.goods.pojo.Sku;
import com.aigou.search.dao.SkuEsMapper;
import com.aigou.search.pojo.SkuInfo;
import com.aigou.search.service.SkuService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author anchao
 * @date 2020/2/23 15:07
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Resource
    private SkuEsMapper skuEsMapper;

    @Resource
    private SkuFeign skuFeign;

    @Resource
    private ElasticsearchTemplate es;


    /**
     * 多条件搜索
     */
    @Override
    public LinkedHashMap search(Map<String,String> searchMap) {
        //返回对象构造
        LinkedHashMap<String, Object> hashMap = Maps.newLinkedHashMap();
        //基本搜索条件构造
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //搜索条件封装
        buildBaseQuery(searchMap, builder);
        //组装分页
        initPage(searchMap, builder);
        //开始条件搜索
        searchList(hashMap,searchMap,builder);
        return hashMap;

    }

    /**
     * 分页条件组装
     */
    private void initPage(Map<String, String> searchMap, NativeSearchQueryBuilder builder) {

        if (!ObjectUtils.isEmpty(searchMap)
                && !StringUtils.isEmpty(searchMap.get("page"))
                && !StringUtils.isEmpty(searchMap.get("size"))) {
            PageRequest page = PageRequest.of(Integer.parseInt(searchMap.get("page"))-1, Integer.valueOf(searchMap.get("size")));
            builder.withPageable(page);
        } else {
            //默认分页
            PageRequest page = PageRequest.of(0, 10);
            builder.withPageable(page);
        }

    }

    /**
     * 条件搜索
     */
    private void searchList(LinkedHashMap<String, Object> hashMap, Map<String, String> searchMap, NativeSearchQueryBuilder builder) {
        //构造分组条件
        buildGroupList(builder, searchMap);
        //设置高亮 搜索返回结果集
        AggregatedPage<SkuInfo> page = initHighlight(builder);
        List<SkuInfo> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();
        hashMap.put("rows", content);
        hashMap.put("total", totalElements);
        hashMap.put("totalPages", totalPages);
        buildResultAggregations(hashMap, searchMap, page);
    }


    /**
     * 组装 分组 搜索返回数据
     */
    private void buildResultAggregations(LinkedHashMap<String, Object> hashMap, Map<String, String> searchMap, AggregatedPage<SkuInfo> page) {
        //获取分组数据
        if (StringUtils.isEmpty(searchMap.get("category"))) {
            StringTerms skuCategory = page.getAggregations().get("skuCategory");
            //获取分类分组集合数据
            LinkedList skuCategoryList  = getGroupList(skuCategory);
            hashMap.put("skuCategoryList",skuCategoryList);
        }
        if (StringUtils.isEmpty(searchMap.get("brand"))) {
            StringTerms skuBrand = page.getAggregations().get("skuBrand");
            //获取品牌分组数据
            LinkedList skuBrandList  = getGroupList(skuBrand);
            hashMap.put("skuBrandList",skuBrandList);
        }
        StringTerms skuSpec = page.getAggregations().get("skuSpec");
        //获取规格分组数据
        HashMap<String, Set<String>> stringSetHashMap = searchSpecList(skuSpec);
        hashMap.put("specList",stringSetHashMap);
    }

    /**
     * 高亮设置
     */
    private AggregatedPage<SkuInfo> initHighlight(NativeSearchQueryBuilder builder) {
        //指定高亮域
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        //前缀 <em style="color:red">
        field.preTags("<em style=\"color:red\">");
        ///后缀 </em>
        field.postTags("</em>");
        //关键词碎片长度 高亮区域
        field.fragmentSize(100);
        //添加高亮显示
        builder.withHighlightFields(field);
        //搜索后 结果集 高亮数据 与 非高亮数据
        return es.queryForPage(builder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                LinkedList<T> linkedList = Lists.newLinkedList();
                //搜索后的结果集
                SearchHits hits = response.getHits();
                for (SearchHit hit : hits) {
                    //获取所有数据
                    String sourceAsString = hit.getSourceAsString();
                    SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
                    //获取高亮域的数据
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    if (highlightField != null && highlightField.getFragments() != null) {
                        //读取高亮数据
                        Text[] fragments = highlightField.getFragments();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Text fragment : fragments) {
                            stringBuilder.append(fragment.toString());
                        }
                        //非高亮数据指定的域 替换成高亮数据
                        skuInfo.setName(stringBuilder.toString());
                    }
                    //高亮数据 添加进集合
                    linkedList.add((T) skuInfo);
                }
                return new AggregatedPageImpl(linkedList, pageable, response.getHits().getTotalHits(),response.getAggregations());
            }
        });
    }

    /**
     * 搜索条件封装
     */
    private void buildBaseQuery(Map<String, String> searchMap, NativeSearchQueryBuilder builder) {

        //BoolQuery must must_not should 多条件组装对象
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (!Objects.isNull(searchMap) && searchMap.size() > 0) {
            //输入了关键词
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                //商品名称 域(字段)
                QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(keywords).field("name");
                boolQueryBuilder.must(queryBuilder);
            }

            //输入了分类
            String category = searchMap.get("category");
            if (!StringUtils.isEmpty(category)) {
                //商品名称 域(字段)
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
                boolQueryBuilder.must(termQueryBuilder);
            }
            //输入了品牌
            String brand = searchMap.get("brand");
            if (!StringUtils.isEmpty(brand)) {
                //商品名称 域(字段)
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
                boolQueryBuilder.must(termQueryBuilder);
            }

            //选择了规格 进行规格过滤 (spec_网络 spec_颜色)
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                if (key.startsWith("spec_")) {
                    String value = entry.getValue();
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value);
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
            //价格区间 0-500 500-1000 ...
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                String replace = price.replace("元", "").replace("以上", "");
                String[] priceArr = replace.split("-");
                if (!StringUtils.isEmpty(priceArr)) {
                    //  价格>
                    if (priceArr.length >= 1) {
                        RangeQueryBuilder price1 = QueryBuilders.rangeQuery("price").gt(Integer.valueOf(priceArr[0]));
                        boolQueryBuilder.must(price1);
                    }
                    // 价格<=
                    if (priceArr.length > 1) {
                        RangeQueryBuilder price2 = QueryBuilders.rangeQuery("price").lte(Integer.valueOf(priceArr[1]));
                        boolQueryBuilder.must(price2);
                    }
                }
            }
            //排序 域 排序规则
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
                FieldSortBuilder fieldSortBuilder = new FieldSortBuilder(sortField);
                FieldSortBuilder order = fieldSortBuilder.order(SortOrder.valueOf(sortRule));
                builder.withSort(order);
            }
        }
        builder.withQuery(boolQueryBuilder);
    }


    /**
     * 2 规格分组查询
     */
    private HashMap<String,Set<String>> searchSpecList(StringTerms skuSpec) {
        HashMap<String, Set<String>> specMaps = Maps.newHashMap();
        skuSpec.getBuckets().forEach(s ->{
            //其中一个规格名称
            String spec = s.getKeyAsString();
            //json 转 map 将map 合成 Map<String,Set<String>>
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                String key = entry.getKey();//规格名称
                String value = entry.getValue();//规格值
                Set<String> hashSet = specMaps.get(key)==null?Sets.newHashSet():specMaps.get(key);
                hashSet.add(value);
                specMaps.put(key,hashSet);
            }
        });
        return specMaps;
    }



    /**
     * 构造条件
     * 分组查询  分类 品牌 规格
     */
    private void buildGroupList(NativeSearchQueryBuilder builder, Map<String, String> searchMap) {

        if (StringUtils.isEmpty(searchMap.get("category"))) {
            builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }

        if (StringUtils.isEmpty(searchMap.get("brand"))) {
            builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }

        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
    }

    /**
     * 获取分组集合数据
     */
    private LinkedList<String> getGroupList(StringTerms stringTerms) {
        LinkedList<String> linkedList = Lists.newLinkedList();
        stringTerms.getBuckets().forEach(s ->{
            String categoryName = s.getKeyAsString();
            linkedList.add(categoryName);
        });
        return linkedList;
    }


    @Override
    public void importData() {
        //feign 调用
        Result<List<Sku>> skuList = skuFeign.findAll();
        //json数组 转化 集合
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuList.getData()), SkuInfo.class);

        //遍历skuInfos 获取spec 规格
        skuInfos.forEach(s ->{

            String spec = s.getSpec();

            Map<String,Object> map = JSON.parseObject(spec, Map.class);

            //需要生成动态的域(字段) 可以将数据放入Map中 该map的key为域的名称
            //当前Map的值 为 sku对象该域对应的值
            s.setSpecMap(map);
        });


        //调用dao 数据导入到ES
        skuEsMapper.saveAll(skuInfos);



    }
}
