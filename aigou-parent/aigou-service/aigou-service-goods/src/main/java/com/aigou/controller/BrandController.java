package com.aigou.controller;

import com.aigou.goods.pojo.Brand;
import com.aigou.service.BrandService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author anchao
 * @date 2020/2/18 16:31
 */
@CrossOrigin
@RestController
@RequestMapping(value = "brand")
public class BrandController {

    @Resource
    private BrandService brandService;


    /**
     * 分页条件搜索
     */
    @PostMapping(value = "search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@PathVariable("page")Integer page,@PathVariable("size")Integer size,Brand brand){
        PageInfo<Brand> pages = brandService.findPage(page, size, brand);
        return new Result<>(true, StatusCode.OK, "条件搜索品牌成功！", pages);

    }

    /**
     * 分页
     */
    @GetMapping(value = "search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@PathVariable("page")Integer page,@PathVariable("size")Integer size){
        return new Result<>(true, StatusCode.OK, "条件搜索品牌成功！", brandService.findPage(page,size));

    }

    /**
     * 多条件查询商品品牌
     */
    @PostMapping(value = "search")
    public Result findList(@RequestBody Brand brand) {
        return new Result<List<Brand>>(true, StatusCode.OK, "条件搜索品牌成功！", brandService.findList(brand));

    }


    /**
     * 根据Id删除品牌
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "{id}")
    public Result delete(@PathVariable(value = "id") Integer id) {
        brandService.delete(id);
        return new Result(true, StatusCode.OK, "删除品牌成功！");
    }

    /**
     * 根据Id更新品牌
     *
     * @param id
     * @param brand
     * @return
     */
    @PutMapping(value = "{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Brand brand) {
        brand.setId(id);
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改品牌成功！");
    }

    /**
     * 添加商品品牌
     *
     * @param brand
     * @return
     */
    @PostMapping(value = "add")
    public Result add(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "增加品牌成功！");
    }


    /**
     * 查询所有品牌
     */
    @GetMapping
    public Result findAll() {
        List<Brand> brands = brandService.findAll();
        return new Result(true, StatusCode.OK, "查询所有品牌成功！", brands);
    }

    /**
     * 根据ID查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping(value = "{id}")
    public Result findById(@PathVariable(value = "id") Integer id) {
        Brand brand = brandService.findById(id);
        return new Result(true, StatusCode.OK, "根据ID查询品牌成功！", brand);
    }


}
