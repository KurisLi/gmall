package com.atguigu.gmall.wms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.wms.entity.HAreaEntity;
import com.atguigu.gmall.wms.service.HAreaService;




/**
 * 全国省市区信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 15:34:20
 */
@Api(tags = "全国省市区信息 管理")
@RestController
@RequestMapping("wms/harea")
public class HAreaController {
    @Autowired
    private HAreaService hAreaService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('wms:harea:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = hAreaService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('wms:harea:info')")
    public Resp<HAreaEntity> info(@PathVariable("id") Integer id){
		HAreaEntity hArea = hAreaService.getById(id);

        return Resp.ok(hArea);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('wms:harea:save')")
    public Resp<Object> save(@RequestBody HAreaEntity hArea){
		hAreaService.save(hArea);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('wms:harea:update')")
    public Resp<Object> update(@RequestBody HAreaEntity hArea){
		hAreaService.updateById(hArea);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('wms:harea:delete')")
    public Resp<Object> delete(@RequestBody Integer[] ids){
		hAreaService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
