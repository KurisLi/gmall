package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.HAreaEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 全国省市区信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 15:34:20
 */
public interface HAreaService extends IService<HAreaEntity> {

    PageVo queryPage(QueryCondition params);
}

