package com.atguigu.gmall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.dao.OrderDao;
import com.atguigu.gmall.oms.dao.OrderItemDao;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.feign.PmsFeignClient;
import com.atguigu.gmall.oms.service.OrderService;
import com.atguigu.gmall.oms.vo.OrderItemVo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }

    @Transactional
    @Override
    public OrderEntity saveOrder(OrderSubmitVo orderSubmitVo, Long userId) {
        // 新增主表（订单表oms_order）
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken()); // 订单编号
        orderEntity.setTotalAmount(orderSubmitVo.getTotalPrice());
        orderEntity.setPayType(orderSubmitVo.getPayType());
        orderEntity.setSourceType(0);
        orderEntity.setDeliveryCompany(orderSubmitVo.getDeliveryCompany());
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(orderEntity.getCreateTime());
        orderEntity.setStatus(0);
        orderEntity.setDeleteStatus(0);
        //        orderEntity.setGrowth(); // 通过购买商品的积分优惠信息设置
        orderEntity.setMemberId(userId);
        // 收货地址
        MemberReceiveAddressEntity address = orderSubmitVo.getAddress();
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverRegion(address.getRegion());
//        orderEntity.setMemberUsername(); // 根据查询用户名
        boolean flag = this.save(orderEntity);
        if (flag) {
            List<OrderItemVo> items = orderSubmitVo.getItems();
            if (!CollectionUtils.isEmpty(items)) {
                items.forEach(orderItemVO -> {
                    OrderItemEntity itemEntity = new OrderItemEntity();
                    itemEntity.setOrderSn(orderSubmitVo.getOrderToken());
                    itemEntity.setOrderId(orderEntity.getId());
                    // 先查询sku信息
                    Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsFeignClient.querySkuInfoBySkuId(orderItemVO.getSkuId());
                    SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                    if (skuInfoEntity != null) {
                        itemEntity.setSkuId(orderItemVO.getSkuId());
                        itemEntity.setSkuQuantity(orderItemVO.getCount());
                        itemEntity.setSkuPic(orderItemVO.getImage());
                        itemEntity.setSkuName(orderItemVO.getSkuTitle());
                        itemEntity.setSkuAttrsVals(JSON.toJSONString(orderItemVO.getSaleAttrs()));
                        itemEntity.setSkuPrice(skuInfoEntity.getPrice());

                        // 根据spuId查询 spu设置spu信息
                        Long spuId = skuInfoEntity.getSpuId();
                        Resp<SpuInfoEntity> spuInfoEntityResp = pmsFeignClient.querySpuInfoBySpuId(spuId);
                        SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
                        if (spuInfoEntity != null) {
                            itemEntity.setSpuId(spuId);
                            itemEntity.setSpuName(spuInfoEntity.getSpuName());
                            itemEntity.setSpuBrand(spuInfoEntity.getBrandId().toString());
                            itemEntity.setCategoryId(spuInfoEntity.getCatalogId());
                        }
                    }
                    orderItemDao.insert(itemEntity);
                });
            }
        }
        return orderEntity;
    }
}