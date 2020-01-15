package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-14 16:33
 */
@Data
public class ItemVo {
    //sku相关
    private Long skuId;
    private String skuTitle;
    private String skuSubTitle;
    private BigDecimal price;
    private BigDecimal weight;

    private Boolean store;
    private List<SkuImagesEntity> images;
    //品牌相关
    private Long brandId;
    private String brandName;

    //分类相关
    private Long categoryId;
    private String categoryName;

    //spu相关
    private Long spuId;
    private String spuName;

    //折扣相关
    private List<ItemSaleVo> sales;

    //销售属性相关
    private List<SkuSaleAttrValueEntity> saleAttrs;

    //属性组及组下的属性
    private List<ItemGroupVo> groupVos;

    //spu描述信息
    private List<String> desc;

}
