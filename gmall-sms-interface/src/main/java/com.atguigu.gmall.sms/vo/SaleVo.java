package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-05 16:40
 */
@Data
public class SaleVo {
    private Long skuId;
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
}
