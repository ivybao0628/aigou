package com.aigou.search.service;

import java.util.Map;

/**
 * @author anchao
 * @date 2020/2/23 15:07
 */
public interface SkuService {



    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String, String> searchMap);




    /**
     * 导入商品信息到索引库
     */
    void importData();
}
