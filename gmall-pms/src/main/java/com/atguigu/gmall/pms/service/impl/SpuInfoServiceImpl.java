package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feignService.SaleFeignService;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;

    @Autowired
    private SaleFeignService saleFeignService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo getSpuInfoByCatId(QueryCondition queryCondition, Long catId) {
        String key = queryCondition.getKey();
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (catId != 0) {
            wrapper.eq("catalog_id", catId);
        }
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(queryWrapper -> queryWrapper.eq("id", key).or().like("spu_name", key));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                wrapper
        );
        return new PageVo(page);
    }

    @Override
    public void bigSave(SpuInfoVo spuInfoVo) {
        //1.保存spu相关信息
        //1.1保存spuinfo信息
        spuInfoVo.setCreateTime(new Date());
        spuInfoVo.setUodateTime(spuInfoVo.getCreateTime());
        this.save(spuInfoVo);
        Long spuId = spuInfoVo.getId();
        //1.2保存spu详细信息
        List<String> spuImages = spuInfoVo.getSpuImages();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        if (!CollectionUtils.isEmpty(spuImages)) {
            spuInfoDescEntity.setDecript(StringUtils.join(spuImages, ","));
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescDao.insert(spuInfoDescEntity);
        }
        //1.3保存spu基本属性
        List<BaseAttrVo> baseAttrs = spuInfoVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttrVo -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                BeanUtils.copyProperties(baseAttrVo, productAttrValueEntity);
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setAttrSort(0);
                productAttrValueEntity.setQuickShow(1);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveBatch(productAttrValueEntities);
        }

        List<SkuInfoVo> skus = spuInfoVo.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }
        skus.forEach(skuInfoVo -> {
            //2.保存sku相关信息
            //2.1保存sku相关信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVo, skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setCatalogId(spuInfoVo.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoVo.getBrandId());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            List<String> images = skuInfoVo.getImages();
            if (!CollectionUtils.isEmpty(images)) {
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg() == null ? images.get(0) : skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //2.2保存sku图片
            if (!CollectionUtils.isEmpty(images)) {
                List<SkuImagesEntity> imagesEntities = images.stream().map(imageUrl -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(imageUrl);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuImagesEntity.getImgUrl(), imageUrl) ? 1 : 0);
                    skuImagesEntity.setImgSort(0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
            }
            //2.3保存sku销售属性
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVo.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(skuSaleAttrValueEntity -> {
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    skuSaleAttrValueEntity.setAttrSort(0);
                });
                saleAttrValueService.saveBatch(saleAttrs);
            }
         /*   if (!CollectionUtils.isEmpty(saleAttrs)){
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = saleAttrs.stream().map(skuSaleAttrValueEntity -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(skuSaleAttrValueEntity, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    //saleAttrValueEntity.setAttrSort(0);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                saleAttrValueService.saveBatch(saleAttrValueEntities);
            }*/
            SaleVo saleVo = new SaleVo();
            BeanUtils.copyProperties(skuInfoVo,saleVo);
            saleVo.setSkuId(skuId);
            saleFeignService.saveSales(saleVo);
        });
    }

}