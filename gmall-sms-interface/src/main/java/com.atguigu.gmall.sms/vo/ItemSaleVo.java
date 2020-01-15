package com.atguigu.gmall.sms.vo;

import lombok.Data;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:06
 */
@Data
public class ItemSaleVo {
    //销售信息的类型：打折，积分，满减
    private String type;

    private String saleInfo;

}
