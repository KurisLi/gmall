package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.feignService.SaleFeignService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.service.SkuInfoService;
import org.springframework.util.CollectionUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

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
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSkuInfoWithSaleInfo(SpuInfoVo spuInfoVo, Long spuId) {
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
            SaleVo saleVo = new SaleVo();
            BeanUtils.copyProperties(skuInfoVo,saleVo);
            saleVo.setSkuId(skuId);
            saleFeignService.saveSales(saleVo);
        });
    }
}