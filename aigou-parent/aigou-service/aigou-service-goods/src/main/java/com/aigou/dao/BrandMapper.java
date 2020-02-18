package com.aigou.dao;

import com.aigou.goods.pojo.Brand;
import tk.mybatis.mapper.common.Mapper;

/**
 * 继承tk.mybatis.mapper
 * 继承方便进行单表的CRUD
 * @author anchao
 * @date 2020/2/18 16:38
 */
public interface BrandMapper extends Mapper<Brand> {
}
