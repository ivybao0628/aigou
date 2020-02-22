package com.aigou.goods.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品信息组合对象
 * @author anchao
 * @date 2020/2/21 11:11
 */
@Data
public class Goods implements Serializable {

    private Spu spu;

    private List<Sku> skuList;
}
