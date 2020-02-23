package com.aigou.goods.feign;

import com.aigou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author anchao
 * @date 2020/2/23 15:01
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * 查询所有商品信息
     */
    @GetMapping
    Result<List<Sku>> findAll();


    /***
     * 根据审核状态查询Sku
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable String status);
}
