package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feignService.SaleFeignService;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.atguigu.gmall.sms.vo.SaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private AmqpTemplate amqpTemplate;

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
    @GlobalTransactional
    public void bigSave(SpuInfoVo spuInfoVo) {
        /**
         * 1.1保存spu相关信息
         */
        Long spuId = this.saveSpuinfo(spuInfoVo);
        /**
         * 1.2保存spu详细信息
         */
        spuInfoDescService.saveSpuinfoDesc(spuInfoVo, spuId);

        /**
         * 1.3保存spu基本属性
         */
        attrValueService.saveAttrValue(spuInfoVo, spuId);

        /**
         * 2.保存sku相关信息，和销售相关信息
         */
        skuInfoService.saveSkuInfoWithSaleInfo(spuInfoVo, spuId);

        sendMsg("insert",spuId);
    }

    private void sendMsg(String type,Long spuId) {
        amqpTemplate.convertAndSend("GMALL-PMS-EXCHANGE","item."+type,spuId);
    }

    public Long saveSpuinfo(SpuInfoVo spuInfoVo) {
        //1.保存spu相关信息
        //1.1保存spuinfo信息
        spuInfoVo.setCreateTime(new Date());
        spuInfoVo.setUodateTime(spuInfoVo.getCreateTime());
        this.save(spuInfoVo);
        return spuInfoVo.getId();
    }

    @Override
    public List<SpuInfoEntity> querySpuList(QueryCondition queryCondition) {
        IPage<SpuInfoEntity> page = spuInfoService.page(new Query<SpuInfoEntity>().getPage(queryCondition),
                new QueryWrapper<SpuInfoEntity>().eq("publish_status", 1));
        return page.getRecords();
    }
}