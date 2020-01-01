package com.atguigu.gmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.WmsFeightTemplateDao;
import com.atguigu.gmall.wms.entity.WmsFeightTemplateEntity;
import com.atguigu.gmall.wms.service.WmsFeightTemplateService;


@Service("wmsFeightTemplateService")
public class WmsFeightTemplateServiceImpl extends ServiceImpl<WmsFeightTemplateDao, WmsFeightTemplateEntity> implements WmsFeightTemplateService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WmsFeightTemplateEntity> page = this.page(
                new Query<WmsFeightTemplateEntity>().getPage(params),
                new QueryWrapper<WmsFeightTemplateEntity>()
        );

        return new PageVo(page);
    }

}