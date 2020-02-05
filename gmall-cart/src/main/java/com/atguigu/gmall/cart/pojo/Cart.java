package com.atguigu.gmall.cart.pojo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-30 19:53
 */
@Data
public class Cart {
    private Long skuId;
    private String skuTitle;
    private String image;
    private List<SkuSaleAttrValueEntity> saleAttrs;//销售属性
    private BigDecimal price;
    private Integer count;
    private Boolean store = false;
    private Boolean check;
    private List<ItemSaleVo> sales;
}
