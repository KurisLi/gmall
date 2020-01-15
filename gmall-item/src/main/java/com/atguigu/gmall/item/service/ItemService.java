package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.config.ThreadPoolConfig;
import com.atguigu.gmall.item.feign.PmsFeignClient;
import com.atguigu.gmall.item.feign.SmsFeignClient;
import com.atguigu.gmall.item.feign.WmsFeignClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lzzzzz
 * @create 2020-01-14 18:29
 */
@Service
public class ItemService {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private SmsFeignClient smsFeignClient;

    @Autowired
    private WmsFeignClient wmsFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public ItemVo queryItemVo(Long skuId) {
        ItemVo itemVo = new ItemVo();
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsFeignClient.querySkuInfoBySkuId(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity == null) {
                return null;
            }
            itemVo.setSkuId(skuId);
            itemVo.setSkuTitle(skuInfoEntity.getSkuTitle());
            itemVo.setSkuSubTitle(skuInfoEntity.getSkuSubtitle());
            itemVo.setPrice(skuInfoEntity.getPrice());
            itemVo.setWeight(skuInfoEntity.getWeight());
            return skuInfoEntity;
        },threadPoolExecutor);
        CompletableFuture<Void> brandFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Long brandId = skuInfoEntity.getBrandId();
            Resp<BrandEntity> brandResp = pmsFeignClient.info(brandId);
            BrandEntity brandEntity = brandResp.getData();
            if (brandEntity != null) {
                itemVo.setBrandId(brandId);
                itemVo.setBrandName(brandEntity.getName());
            }
        },threadPoolExecutor);
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Long catalogId = skuInfoEntity.getCatalogId();
            Resp<CategoryEntity> cateResp = pmsFeignClient.catInfo(catalogId);
            CategoryEntity categoryEntity = cateResp.getData();
            if (categoryEntity != null) {
                itemVo.setCategoryId(catalogId);
                itemVo.setCategoryName(categoryEntity.getName());
            }
        },threadPoolExecutor);
        CompletableFuture<Void> spuFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Long spuId = skuInfoEntity.getSpuId();
            Resp<SpuInfoEntity> spuInfoEntityResp = pmsFeignClient.querySpuInfoBySpuId(spuId);
            SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
            if (spuInfoEntity != null) {
                itemVo.setSpuId(spuId);
                itemVo.setSpuName(spuInfoEntity.getSpuName());
            }
        },threadPoolExecutor);
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuImagesEntity>> imagesResp = pmsFeignClient.querySkuImagesBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = imagesResp.getData();
            itemVo.setImages(skuImagesEntities);
        },threadPoolExecutor);

        CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {
            Resp<List<WareSkuEntity>> wareResp = wmsFeignClient.queryWareBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareResp.getData();
            boolean flag = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
            itemVo.setStore(flag);
        },threadPoolExecutor);

        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = pmsFeignClient.querySpuDescBySpuId(skuInfoEntity.getSpuId());
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
            if (spuInfoDescEntity != null && spuInfoDescEntity.getDecript() != null) {
                String[] spuDescs = StringUtils.split(spuInfoDescEntity.getDecript(), ",");
                List<String> spuDescList = Arrays.asList(spuDescs);
                itemVo.setDesc(spuDescList);
            }
        },threadPoolExecutor);
        CompletableFuture<Void> salesFuture = CompletableFuture.runAsync(() -> {
            Resp<List<ItemSaleVo>> itemSaleResp = smsFeignClient.queryItemSaleVoBySkuId(skuId);
            List<ItemSaleVo> itemSaleVos = itemSaleResp.getData();
            itemVo.setSales(itemSaleVos);
        },threadPoolExecutor);

        CompletableFuture<Void> groupVosFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据spuid和cateid来确定属性的值
            Resp<List<ItemGroupVo>> itemGroupVoResp = pmsFeignClient.queryItemGroupVoBySpuIdAndCateId(skuInfoEntity.getSpuId(), skuInfoEntity.getCatalogId());
            List<ItemGroupVo> itemGroupVos = itemGroupVoResp.getData();
            itemVo.setGroupVos(itemGroupVos);
        },threadPoolExecutor);
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<SkuSaleAttrValueEntity>> saleAttrValuesResp = pmsFeignClient.querySkuSaleAttrValuesBySpuId(skuInfoEntity.getSpuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrValuesResp.getData();
            itemVo.setSaleAttrs(skuSaleAttrValueEntities);
        },threadPoolExecutor);
        CompletableFuture.allOf(brandFuture,categoryFuture,spuFuture,imagesFuture,storeFuture
        ,descFuture,salesFuture,groupVosFuture,saleAttrFuture).join();
        return itemVo;
    }
}
