package com.aigou.service.impl;

import com.aigou.dao.BrandMapper;
import com.aigou.goods.pojo.Brand;
import com.aigou.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author anchao
 * @date 2020/2/18 16:39
 */
@Slf4j
@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandMapper brandMapper;


    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size, Brand brand) {
        if (Objects.isNull(brand)) {
            return new PageInfo<>();
        }
        Example example = createExample(brand);
        PageHelper.startPage(page,size);
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(brands);
    }

    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {
        PageHelper.startPage(page,size);
        List<Brand> brands = brandMapper.selectAll();
        if(CollectionUtils.isEmpty(brands)){
            return new PageInfo<>();
        }
        return new PageInfo<>(brands);
    }

    @Override
    public List<Brand> findList(Brand brand) {

        if (Objects.isNull(brand)) {
            return new ArrayList<>();
        }

        Example example = createExample(brand);

        List<Brand> brands = brandMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(brands)) {
            return new ArrayList<>();
        }

        return brands;
    }

    /**
     * 组装条件构造器
     * @param brand
     * @return
     */
    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(brand.getName())) {
            criteria.andLike("name", "%" + brand.getName() + "%");
        }

        if (StringUtils.isNotBlank(brand.getLetter())) {
            criteria.andEqualTo("letter", brand.getLetter());
        }
        return example;
    }


    @Override
    public void delete(Integer id) {
        int delete = brandMapper.deleteByPrimaryKey(id);
        log.info("商品品牌删除={},数量={}",delete>0,delete);
    }

    @Override
    public void update(Brand brand) {
        //忽略null值Selective
        int update = brandMapper.updateByPrimaryKeySelective(brand);
        log.info("商品品牌修改={},数量={}",update>0,update);
    }

    @Override
    public void add(Brand brand) {
        //忽略null Selective
        int insert = brandMapper.insertSelective(brand);
        log.info("商品品牌添加={},数量={}",insert>0,insert);
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }


    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
