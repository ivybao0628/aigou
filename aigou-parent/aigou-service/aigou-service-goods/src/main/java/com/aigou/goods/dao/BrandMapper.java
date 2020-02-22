package com.aigou.goods.dao;
import com.aigou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:anchao
 * @Description:Brand的Dao
 * @Date 2020
 *****/
public interface BrandMapper extends Mapper<Brand> {

    /**
     *根据分类Id查询品牌集合
     * @param categoryId
     * @return
     */
    @Select("SELECT tb.* FROM tb_brand tb,tb_category_brand tcb WHERE tb.id=tcb.brand_id AND tcb.category_id=#{categoryId}")
    List<Brand> findByCategory(Integer categoryId);
}
