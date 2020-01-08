package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * sku信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 13:50:33
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSkuInfoWithSaleInfo(SpuInfoVo spuInfoVo, Long spuId);

}

