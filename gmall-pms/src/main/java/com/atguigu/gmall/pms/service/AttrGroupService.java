package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.GroupVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 13:50:33
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrGroupByCatId(Long catId, QueryCondition queryCondition);

    GroupVo queryGroupVoByGid(Long gid);

    List<GroupVo> queryAttrGroupAndAttrByCatId(Long catId);

    List<ItemGroupVo> queryItemGroupVoBySpuIdAndCateId(Long spuId, Long cateId);
}

