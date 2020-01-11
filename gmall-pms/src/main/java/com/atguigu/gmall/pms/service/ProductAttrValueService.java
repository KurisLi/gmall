package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * spu属性值
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 13:50:33
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    void saveAttrValue(SpuInfoVo spuInfoVo, Long spuId);

    PageVo queryPage(QueryCondition params);

    List<ProductAttrValueEntity> queryAttrBySpuId(long spuId);
}

