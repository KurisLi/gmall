package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttrValue;
import com.atguigu.gmall.search.repositoy.GoodsRepositoy;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Autowired
    private GoodsRepositoy goodsRepositoy;

    @Autowired
    private WmsFeignClient wmsFeignClient;
    @Test
    void contextLoads() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
    }

    /**
     * 将数据库中的信息导入到es中
     */
    @Test
    void importGoodsInfo(){
        /**
         * 假设传过来5页，每页100条记录，
         * 需要每页循环来导入
         */
        long pageNum = 1;
        long pageSize = 100;
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setLimit(pageSize);
        int currentSize;
        do {
            queryCondition.setPage(pageNum);
            /**
             * 根据页码查询spu的数据
             */
            Resp<List<SpuInfoEntity>> spuResp = pmsFeignClient.querySpuList(queryCondition);
            List<SpuInfoEntity> spuInfoEntities = spuResp.getData();
            if (CollectionUtils.isEmpty(spuInfoEntities)){
                return;
            }
            /**
             * 遍历spu，获取每个spu下的sku
             */
            spuInfoEntities.forEach(spuInfoEntity -> {
                /**
                 * 根据spuid查询skuinfo的数据
                 */
                Resp<List<SkuInfoEntity>> skuResp = pmsFeignClient.querySkuInfoBySpuId(spuInfoEntity.getId());
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
            });
            currentSize = spuInfoEntities.size();
            pageNum++;
        }while (currentSize==100);
    }
}
