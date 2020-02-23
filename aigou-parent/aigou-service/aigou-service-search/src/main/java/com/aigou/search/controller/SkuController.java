package com.aigou.search.controller;

import com.aigou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author anchao
 * @date 2020/2/23 15:17
 */
@RestController
@RequestMapping(value = "/search")
@CrossOrigin
public class SkuController{

    @Resource
    private SkuService skuService;


    /**
     * 搜索
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String,String> searchMap){
        return  skuService.search(searchMap);
    }




    /**
     * 导入商品数据到索引库
     */
    @GetMapping("/import")
    public Result importData(){
        skuService.importData();
        return new Result(true, StatusCode.OK,"导入商品数据到索引库中成功！");
    }




}
