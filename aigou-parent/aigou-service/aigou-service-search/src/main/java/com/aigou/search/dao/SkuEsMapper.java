package com.aigou.search.dao;

import com.aigou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author anchao
 * @date 2020/2/23 15:11
 */
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
