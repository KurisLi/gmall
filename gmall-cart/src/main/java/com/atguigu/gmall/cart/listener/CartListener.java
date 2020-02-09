package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.PmsFeignClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-02-07 20:26
 */
@Component
public class CartListener {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PRICE_PREFIX = "cart:price:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart-price-queue",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"cart.update"}
    ))
    public void updatePrice(Long spuId){
        Resp<List<SkuInfoEntity>> skuInfoResp = pmsFeignClient.querySkuInfoBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();
        if (!CollectionUtils.isEmpty(skuInfoEntities)){
            skuInfoEntities.forEach(skuInfoEntity -> {
                redisTemplate.opsForValue().set(PRICE_PREFIX+skuInfoEntity.getSkuId().toString(),skuInfoEntity.getPrice().toString());
            });
        }
    }
}
