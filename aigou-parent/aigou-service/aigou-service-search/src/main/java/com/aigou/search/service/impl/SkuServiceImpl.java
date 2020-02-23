package com.aigou.search.service.impl;

import com.aigou.goods.feign.SkuFeign;
import com.aigou.goods.pojo.Sku;
import com.aigou.search.dao.SkuEsMapper;
import com.aigou.search.pojo.SkuInfo;
import com.aigou.search.service.SkuService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import entity.Result;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
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
        //搜索条件构造
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        initPage(searchMap, builder);

        keyWords(searchMap, builder);

        searchList(hashMap, builder);

        LinkedList<String> linkedList = searchCategoryList(builder);

        hashMap.put("linkedList", linkedList);

        return hashMap;

    }

    /**
     * 分页条件组装
     */
    private void initPage(Map<String, String> searchMap, NativeSearchQueryBuilder builder) {
        PageRequest page = PageRequest.of(Integer.valueOf(searchMap.get("page")), Integer.valueOf(searchMap.get("size")));
        builder.withPageable(page);
    }

    /**
     * 条件搜索
     */
    private void searchList(LinkedHashMap<String, Object> hashMap, NativeSearchQueryBuilder builder) {
        //搜索后 结果集
        AggregatedPage<SkuInfo> page = es.queryForPage(builder.build(), SkuInfo.class);
        List<SkuInfo> content = page.getContent();
        //总记录数
        long totalElements = page.getTotalElements();
        //总页数
        int totalPages = page.getTotalPages();
        hashMap.put("rows", content);
        hashMap.put("total", totalElements);
        hashMap.put("totalPages", totalPages);
    }

    /**
     * 1----->根据关键词搜索
     */
    private void keyWords(Map<String, String> searchMap, NativeSearchQueryBuilder builder) {
        if (!Objects.isNull(searchMap) && searchMap.size() > 0) {
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                //商品名称 域(字段)
                QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(keywords).field("name");
                builder.withQuery(queryBuilder);
            }
        }
    }


    /**
     * 2.----->设置分组条件  取别名 商品分类域(字段)
     */
    private LinkedList<String> searchCategoryList(NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        //搜索返回结果集
        AggregatedPage<SkuInfo> aggrePage = es.queryForPage(builder.build(), SkuInfo.class);
        //获取分组数据
        StringTerms skuCategory = aggrePage.getAggregations().get("skuCategory");
        LinkedList<String> linkedList = Lists.newLinkedList();
        skuCategory.getBuckets().forEach(s ->{
            String categoryName = s.getKeyAsString();//其中一个分类名称
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
