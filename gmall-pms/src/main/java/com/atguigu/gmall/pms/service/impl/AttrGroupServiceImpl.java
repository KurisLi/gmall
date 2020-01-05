package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.vo.GroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrDao attrDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupByCatId(Long catId, QueryCondition queryCondition) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        return new PageVo(page);
    }

    @Override
    public GroupVo queryGroupVoByGid(Long gid) {
        GroupVo groupVo = new GroupVo();
        //根据gid查询组信息
        AttrGroupEntity attrGroup = attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroup,groupVo);
        //根据gid查询关联表信息
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        groupVo.setRelations(relations);
        //判断relations是否为空
        if (CollectionUtils.isEmpty(relations)){
            return groupVo;
        }
        //根据关联表中attrid查询属性信息
        List<Long> attrIdList = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIdList);
        groupVo.setAttrEntities(attrEntities);
        return groupVo;
    }

    @Override
    public List<GroupVo> queryAttrGroupAndAttrByCatId(Long catId) {
        //根据catId查出group信息
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //根据group组信息查询出所有属性信息
        List<GroupVo> groupVos = attrGroupEntities.stream().map(attrGroupEntity ->queryGroupVoByGid(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());
        return groupVos;
        /*
            自己的
        //先根据catid查询所有的组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
            //获取到所有的组id
        List<Long> groupIdList = attrGroupEntities.stream().map(attrGroupEntity -> attrGroupEntity.getAttrGroupId()).collect(Collectors.toList());
        //根据组id在关联表中查出所有的skuid
        List<GroupVo> groupVos = groupIdList.stream().map(groupId -> queryGroupVoByGid(groupId)).collect(Collectors.toList());
        //根据skuid查询所有的sku信息
        return groupVos;*/

    }
}