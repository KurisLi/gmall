package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @author lzzzzz
 * @create 2020-02-09 18:54
 */
@Data
public class SkuLockVo {
    private Long skuId;
    private Integer count;
    private Boolean lock = false; //锁定状态，true 验库并锁库成功
    private Long wareId;
    private String orderToken;
}
