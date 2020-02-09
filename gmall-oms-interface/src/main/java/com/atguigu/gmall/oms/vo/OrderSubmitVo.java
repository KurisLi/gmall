package com.atguigu.gmall.oms.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-02-09 17:21
 */
@Data
public class OrderSubmitVo {
    //收货地址信息
    private MemberReceiveAddressEntity address;

    //防重
    private String orderToken;

    //支付方式
    private Integer payType;

    //物流公司
    private String deliveryCompany;

    //购物信息
    private List<OrderItemVo> items;

    //消费积分
    private Integer bounds;

    //总价
    private BigDecimal totalPrice;
}
