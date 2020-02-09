package com.atguigu.gmall.order.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.exception.OrderException;
import com.atguigu.gmall.oms.vo.OrderItemVo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.pojo.OrderConfirmVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author lzzzzz
 * @create 2020-02-07 21:46
 */
@Service
public class OrderService {

    @Autowired
    private UmsFeignClient umsFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private WmsFeignClient wmsFeignClient;

    @Autowired
    private SmsFeignClient smsFeignClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OmsFeignClient omsFeignClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    private static final String TOKEN_PREFIX = "order:token:";

    public OrderConfirmVo confirm() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();
        //远程调用ums获取地址信息
        CompletableFuture<Void> addressesFuture = CompletableFuture.runAsync(() -> {
            if (userId != null) {
                Resp<List<MemberReceiveAddressEntity>> addresses = umsFeignClient.queryAddressesByUserId(userId);
                orderConfirmVo.setAddresses(addresses.getData());
            }
        }, threadPoolExecutor);


        //获取订单详情列表
        CompletableFuture<Void> checkedFuture = CompletableFuture.supplyAsync(() -> {
            return cartFeignClient.getCheckedCarts(userId);
        }, threadPoolExecutor).thenAcceptAsync(checkedCarts -> {
            if (!CollectionUtils.isEmpty(checkedCarts)) {
                List<OrderItemVo> orderItemVos = checkedCarts.stream().map(cart -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    //此处不能使用beanutils进行拷贝，因为是两个微服务，不能将数据的准确性交给别的服务，需要自己查询
                    Long skuId = cart.getSkuId();
                    Integer count = cart.getCount();
                    orderItemVo.setCount(count);
                    orderItemVo.setSkuId(skuId);
                    CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                        Resp<SkuInfoEntity> skuInfoEntityResp = pmsFeignClient.querySkuInfoBySkuId(cart.getSkuId());
                        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                        if (skuInfoEntity != null) {
                            orderItemVo.setImage(skuInfoEntity.getSkuDefaultImg());
                            orderItemVo.setPrice(skuInfoEntity.getPrice());
                            orderItemVo.setSkuTitle(skuInfoEntity.getSkuTitle());
                            orderItemVo.setWeight(skuInfoEntity.getWeight());
                        }
                    }, threadPoolExecutor);
                    CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
                        Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = pmsFeignClient.querySkuSaleAttrValuesBySkuId(skuId);
                        List<SkuSaleAttrValueEntity> saleAttrValueEntities = saleAttrValueResp.getData();
                        orderItemVo.setSaleAttrs(saleAttrValueEntities);
                    }, threadPoolExecutor);
                    CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {
                        Resp<List<WareSkuEntity>> wareResp = wmsFeignClient.queryWareBySkuId(skuId);
                        List<WareSkuEntity> wareSkuEntities = wareResp.getData();
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            boolean store = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                            orderItemVo.setStore(store);
                        }
                    }, threadPoolExecutor);
                    CompletableFuture<Void> saleFuture = CompletableFuture.runAsync(() -> {
                        Resp<List<ItemSaleVo>> itemsaleVoResp = smsFeignClient.queryItemSaleVoBySkuId(skuId);
                        List<ItemSaleVo> itemSaleVos = itemsaleVoResp.getData();
                        orderItemVo.setSales(itemSaleVos);
                    }, threadPoolExecutor);
                    CompletableFuture.allOf(skuInfoFuture, saleAttrFuture, storeFuture, saleFuture).join();
                    return orderItemVo;
                }).collect(Collectors.toList());
                orderConfirmVo.setOrderItemVos(orderItemVos);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            //远程调用ums
            Resp<MemberEntity> memberEntityResp = umsFeignClient.info(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            if (memberEntity != null) {
                orderConfirmVo.setBounds(memberEntity.getIntegration());
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> tokenFuture = CompletableFuture.runAsync(() -> {
            //防止重复提交的唯一标志
            //实现方式1.uuid 2.redis incr  3.分布式id生成器  idworker
            String orderToken = IdWorker.getTimeId();
            orderConfirmVo.setOrderToken(orderToken);
            //要把orderToken放到redis中，用于防重复提交，第一次提交时删除redis中的orderToken
            redisTemplate.opsForValue().set(TOKEN_PREFIX + orderToken, orderToken);
        }, threadPoolExecutor);
        CompletableFuture.allOf(addressesFuture, checkedFuture, memberFuture, tokenFuture).join();
        return orderConfirmVo;
    }

    public void submit(OrderSubmitVo orderSubmitVo) {
        // 1.防重(判断redis中是否有orderToken，有则放行并删除)
       /* 不能保证原子性
       String orderToken = orderSubmitVo.getOrderToken();
        String flag = redisTemplate.opsForValue().get(TOKEN_PREFIX + orderToken);
        if (StringUtils.isEmpty(flag)){
            throw new OrderException("不能重复提交");
        }
        redisTemplate.delete(TOKEN_PREFIX + orderToken);*/

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();
        String orderToken = orderSubmitVo.getOrderToken();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long flag = (Long) redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(TOKEN_PREFIX + orderToken), orderToken);
        if (flag == 0) {
            throw new OrderException("请不要重复提交");
        }
        // 2.验价
        BigDecimal totalPrice = orderSubmitVo.getTotalPrice();
        List<OrderItemVo> items = orderSubmitVo.getItems();
        if (CollectionUtils.isEmpty(items)) {
            throw new OrderException("请勾选要购买的商品");
        }
        Optional<BigDecimal> totalPriceOptional = items.stream().map(item -> {
            Integer count = item.getCount();
            Long skuId = item.getSkuId();
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsFeignClient.querySkuInfoBySkuId(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity != null) {
                BigDecimal price = skuInfoEntity.getPrice();
                return price.multiply(new BigDecimal(count));
            }
            return new BigDecimal(0);
        }).reduce((a, b) -> a.add(b));
        BigDecimal totalPriceFromDB = totalPriceOptional.get();

        if (totalPrice.compareTo(totalPriceFromDB) != 0) {
            throw new OrderException("页面已过期，请刷新后再试");
        }
        // 3.验库存并锁定库存
        List<SkuLockVo> skuLockVos = items.stream().map(orderItemVo -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(orderItemVo.getSkuId());
            skuLockVo.setCount(orderItemVo.getCount());
            skuLockVo.setOrderToken(orderSubmitVo.getOrderToken());
            return skuLockVo;
        }).collect(Collectors.toList());
        Resp<List<SkuLockVo>> skuLockVoResp = wmsFeignClient.checkAndLock(skuLockVos);
        List<SkuLockVo> skuLockVoList = skuLockVoResp.getData();
        if (!CollectionUtils.isEmpty(skuLockVoList)){
            throw new OrderException(JSON.toJSONString(skuLockVoList));
        }
        // 4.提交订单，生成订单
        try {
            omsFeignClient.saveOrder(orderSubmitVo,userId);
        } catch (Exception e) {
            e.printStackTrace();
            //订单创建异常，立马释放库存
            amqpTemplate.convertAndSend("ORDER-CART-EXCHANGE","stock.unlock",orderSubmitVo.getOrderToken());
            throw new OrderException("订单保存失败，服务器错误");
        }
        // 5.删除购物车中的相关记录
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId);
            List<Long> skuIds = items.stream().map(orderItemVo -> orderItemVo.getSkuId()).collect(Collectors.toList());
            map.put("skuIds", JSON.toJSONString(skuIds));
            amqpTemplate.convertAndSend("ORDER-CART-EXCHANGE","cart.delete",map);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}
