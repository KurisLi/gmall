package com.atguigu.gmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.wms.dao.HAreaDao;
import com.atguigu.gmall.wms.entity.HAreaEntity;
import com.atguigu.gmall.wms.service.HAreaService;


@Service("hAreaService")
public class HAreaServiceImpl extends ServiceImpl<HAreaDao, HAreaEntity> implements HAreaService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<HAreaEntity> page = this.page(
                new Query<HAreaEntity>().getPage(params),
                new QueryWrapper<HAreaEntity>()
        );

        return new PageVo(page);
    }

}