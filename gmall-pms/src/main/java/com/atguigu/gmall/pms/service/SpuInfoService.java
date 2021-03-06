package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * spu信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 13:50:33
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo getSpuInfoByCatId(QueryCondition queryCondition, Long catId);

    void bigSave(SpuInfoVo spuInfo);



    Long saveSpuinfo(SpuInfoVo spuInfoVo);

    List<SpuInfoEntity> querySpuList(QueryCondition queryCondition);
}

