package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.PmsFeignClient;
import com.atguigu.gmall.cart.feign.SmsFeignClient;
import com.atguigu.gmall.cart.feign.WmsFeignClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareOrderTaskDetailEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lzzzzz
 * @create 2020-02-01 18:35
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private WmsFeignClient wmsFeignClient;

    @Autowired
    private SmsFeignClient smsFeignClient;

    private static final String KEY_PREFIX = "cart:item:";

    public void addCart(Cart cart) {
        String key = KEY_PREFIX;
        //判断登录状态
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        if (userInfo.getUserId() != null) {
            key += userInfo.getUserId();
        }else {
            key += userInfo.getUserKey();
        }
        //判断购物车中是否有该商品
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        String skuId = cart.getSkuId().toString();
        Integer count = cart.getCount();
        if (hashOps.hasKey(skuId)){
            //有，增加数量
            String cartJson = (String) hashOps.get(skuId);
            cart = JSON.parseObject(cartJson,Cart.class);
            cart.setCount(cart.getCount()+count);
        }else {
            //没有，新增购物车
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsFeignClient.querySkuInfoBySkuId(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            cart.setImage(skuInfoEntity.getSkuDefaultImg());
            cart.setPrice(skuInfoEntity.getPrice());
            cart.setSkuTitle(skuInfoEntity.getSkuTitle());
            Resp<List<SkuSaleAttrValueEntity>> skuAttrListResp = pmsFeignClient.querySkuSaleAttrValuesBySkuId(cart.getSkuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuAttrListResp.getData();
            cart.setSaleAttrs(skuSaleAttrValueEntities);

            Resp<List<ItemSaleVo>> itemsaleVoResp = smsFeignClient.queryItemSaleVoBySkuId(cart.getSkuId());
            List<ItemSaleVo> itemSaleVos = itemsaleVoResp.getData();
            cart.setSales(itemSaleVos);

            Resp<List<WareSkuEntity>> listResp = wmsFeignClient.queryWareBySkuId(cart.getSkuId());
            List<WareSkuEntity> wareSkuEntities = listResp.getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)){
                cart.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
            }
            cart.setCheck(true);
        }
        hashOps.put(skuId,JSON.toJSONString(cart));
    }

    public List<Cart> queryCarts() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = KEY_PREFIX + userInfo.getUserKey();
        Long userId = userInfo.getUserId();
        //查询未登录状态下的购物车
        BoundHashOperations<String, Object, Object> userKeyHashOps = redisTemplate.boundHashOps(userKey);
        List<Object> values = userKeyHashOps.values();
        List<Cart> userKeyCarts = null;
        if (!CollectionUtils.isEmpty(values)){
            userKeyCarts = values.stream().map(cartJson -> JSON.parseObject(cartJson.toString(),Cart.class)).collect(Collectors.toList());
        }
        //未登录，直接返回未登录的购物车
        if (userId == null) {
            return userKeyCarts;
        }
        //已登录，合并，合并后删除未登录的购物车
            //循环遍历未登录状态下的购物车，若已登录状态下有该项购物车，则增加数量，若没有，则增加该项购物车
        BoundHashOperations<String, Object, Object> userIdHashOps = redisTemplate.boundHashOps(KEY_PREFIX + userId);
        userKeyCarts.forEach(cart -> {
            if (userIdHashOps.hasKey(cart.getSkuId().toString())){
                //合并数量
                Integer count = cart.getCount();
                String cartJson = (String) userIdHashOps.get(cart.getSkuId().toString());
                cart = JSON.parseObject(cartJson, Cart.class);
                cart.setCount(cart.getCount() + count);
            }//增加该项
            userIdHashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
        });
        //合并后删除未登录状态的购物车
        redisTemplate.delete(userKey);
        //返回登录状态下的购物车
        List<Object> userIdCartJsons = userIdHashOps.values();
        if (!CollectionUtils.isEmpty(userIdCartJsons)){
            return userIdCartJsons.stream().map(userIdCartJson -> JSON.parseObject(userIdCartJson.toString(),Cart.class)).collect(Collectors.toList());
        }
        return null;
    }
}
