package com.atguigu.gmall.order.pojo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;
import org.hibernate.validator.constraints.EAN;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-02-07 21:32
 */
@Data
public class OrderConfirmVo {
    private List<MemberReceiveAddressEntity> addresses;

    private List<OrderItemVo> orderItemVos;

    private Integer bounds;

    private String orderToken;
}
