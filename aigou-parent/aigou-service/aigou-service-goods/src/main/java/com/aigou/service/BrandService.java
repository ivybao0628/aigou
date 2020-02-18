package com.aigou.service;

import com.aigou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author anchao
 * @date 2020/2/18 16:39
 */
public interface BrandService {

    /**
     * 分页多条件搜索
     * @param page 当前几页
     * @param size 页中条数
     * @param brand
     * @return
     */
    PageInfo<Brand> findPage(Integer page,Integer size,Brand brand);

    /**
     * 分页
     * @param page 当前几页
     * @param size 页中条数
     * @return
     */
    PageInfo<Brand> findPage(Integer page,Integer size);

    /**
     * 根据品牌信息多条件搜索
     */
    List<Brand> findList(Brand brand);

    /**
     * 根据id删除品牌
     * @param id
     */
    void delete(Integer id);

    /**
     * 修改品牌
     * @param brand
     */
    void update(Brand brand);

    /**
     * 增加品牌
     * @param brand
     */
    void add(Brand brand);

    /**
     * 查询所有品牌
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据Id查询品牌
     * @param id
     * @return
     */
    Brand findById(Integer id);
}
