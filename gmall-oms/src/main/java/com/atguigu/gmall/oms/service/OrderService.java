package com.atguigu.gmall.oms.service;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 订单
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-05 21:41:19
 */
public interface OrderService extends IService<OrderEntity> {

    PageVo queryPage(QueryCondition params);

    OrderEntity saveOrder(OrderSubmitVo orderSubmitVo, Long userId);
}

