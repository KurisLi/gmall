package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuFullReductionDao fullReductionDao;

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Autowired
    private SkuBoundsDao skuBoundsDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void saveSales(SaleVo saleVo) {
        //3.保存营销相关信息
            //3.1保存满减信息
        SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(saleVo,fullReductionEntity);
        fullReductionEntity.setAddOther(saleVo.getFullAddOther());
        fullReductionDao.insert(fullReductionEntity);
            //3.2保存打折信息
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(saleVo,ladderEntity);
        ladderEntity.setAddOther(saleVo.getLadderAddOther());
        skuLadderDao.insert(ladderEntity);
            //3.3保存优惠信息
        SkuBoundsEntity boundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(saleVo,boundsEntity);
        List<Integer> works = saleVo.getWork();
        if (!CollectionUtils.isEmpty(works)){
            boundsEntity.setWork(works.get(0)+works.get(1)*2+works.get(2)*4+works.get(3)*8);
        }
        skuBoundsDao.insert(boundsEntity);
    }

    @Override
    public List<ItemSaleVo> queryItemSaleVoBySkuId(Long skuId) {
        List<ItemSaleVo> itemSaleVos = new ArrayList<>();
        SkuFullReductionEntity skuFullEntity = this.getOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (skuFullEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            itemSaleVo.setSaleInfo("满"+skuFullEntity.getFullPrice()+"减"+skuFullEntity.getReducePrice());
            itemSaleVos.add(itemSaleVo);
        }
        SkuBoundsEntity skuBoundsEntity = skuBoundsDao.selectOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            itemSaleVo.setSaleInfo("增加成长积分："+skuBoundsEntity.getGrowBounds()+",购物积分："+skuBoundsEntity.getBuyBounds());
            itemSaleVos.add(itemSaleVo);
        }
        SkuLadderEntity skuLadderEntity = skuLadderDao.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            itemSaleVo.setSaleInfo("满"+skuLadderEntity.getFullCount()+"件，打"+skuLadderEntity.getDiscount().divide(new BigDecimal(10))+"折");
            itemSaleVos.add(itemSaleVo);
        }
        return itemSaleVos;
    }
}