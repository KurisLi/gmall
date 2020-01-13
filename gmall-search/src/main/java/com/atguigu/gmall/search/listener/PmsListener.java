package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValue;
import com.atguigu.gmall.search.repositoy.GoodsRepositoy;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lzzzzz
 * @create 2020-01-12 18:28
 */
@Component
public class PmsListener {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private GoodsRepositoy goodsRepositoy;

    @Autowired
    private WmsFeignClient wmsFeignClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-SEARCH-QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true"),
            key = {"item.insert"}
    ))
    public void listener(Long spuId){
        /**
         * 根据spuid查询skuinfo的数据
         */
        Resp<List<SkuInfoEntity>> skuResp = pmsFeignClient.querySkuInfoBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
        if (!CollectionUtils.isEmpty(skuInfoEntities)){
            /**
             * 保存sku信息到es
             */
            List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                Goods goods = new Goods();
                goods.setSkuId(skuInfoEntity.getSkuId());
                goods.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                goods.setSkuTitle(skuInfoEntity.getSkuTitle());
                goods.setSkuSubTitle(skuInfoEntity.getSkuSubtitle());
                goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                goods.setSale(100l);
                /**
                 * 根据skuid查询库存
                 */
                Resp<List<WareSkuEntity>> wareResp = wmsFeignClient.queryWareBySkuId(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareResp.getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)){
                    boolean haveStock = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                    goods.setStore(haveStock);
                }
                Resp<SpuInfoEntity> spuInfoEntityResp = pmsFeignClient.querySpuInfoBySpuId(spuId);
                SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
                goods.setCreateTime(spuInfoEntity.getCreateTime());
                goods.setBrandId(skuInfoEntity.getBrandId());
                /**
                 * 根据breandid查询brand信息
                 */
                Resp<BrandEntity> brandResp = pmsFeignClient.info(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandResp.getData();
                if (brandEntity!=null){
                    goods.setBrandName(brandEntity.getName());
                }
                goods.setCategoryId(skuInfoEntity.getCatalogId());
                /**
                 * 根据catId查询category信息
                 */
                Resp<CategoryEntity> catResp = pmsFeignClient.catInfo(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = catResp.getData();
                if (categoryEntity!=null){
                    goods.setCategoryName(categoryEntity.getName());
                }
                /**
                 * 根据spuId查询普通属性信息
                 */
                Resp<List<ProductAttrValueEntity>> attrValueResp = pmsFeignClient.queryAttrBySpuId(spuInfoEntity.getId());
                List<ProductAttrValueEntity> attrvalueList = attrValueResp.getData();
                if (!CollectionUtils.isEmpty(attrvalueList)){
                    List<SearchAttrValue> searchAttrValueList = attrvalueList.stream().map(attrValuEntity -> {
                        SearchAttrValue searchAttrValue = new SearchAttrValue();
                        searchAttrValue.setAttrId(attrValuEntity.getAttrId());
                        searchAttrValue.setAttrName(attrValuEntity.getAttrName());
                        searchAttrValue.setAttrValue(attrValuEntity.getAttrValue());
                        return searchAttrValue;
                    }).collect(Collectors.toList());
                    goods.setAttrs(searchAttrValueList);
                }
                return goods;
            }).collect(Collectors.toList());
            goodsRepositoy.saveAll(goodsList);
        }
    }
}
