package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-05 13:10
 */
@Data
public class SkuInfoVo extends SkuInfoEntity {
    private List<String> images;
    /**
     * 满减
     */
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
    /**
     * 打折
     */
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
    /**
     * 优惠信息
     */
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    /**
     * 销售属性
     */
    private List<SkuSaleAttrValueEntity> saleAttrs;
}
