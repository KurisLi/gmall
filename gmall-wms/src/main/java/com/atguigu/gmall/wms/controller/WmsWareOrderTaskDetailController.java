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

import com.atguigu.gmall.wms.entity.WmsWareOrderTaskDetailEntity;
import com.atguigu.gmall.wms.service.WmsWareOrderTaskDetailService;




/**
 * 库存工作单
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 15:34:20
 */
@Api(tags = "库存工作单 管理")
@RestController
@RequestMapping("wms/wmswareordertaskdetail")
public class WmsWareOrderTaskDetailController {
    @Autowired
    private WmsWareOrderTaskDetailService wmsWareOrderTaskDetailService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('wms:wmswareordertaskdetail:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = wmsWareOrderTaskDetailService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('wms:wmswareordertaskdetail:info')")
    public Resp<WmsWareOrderTaskDetailEntity> info(@PathVariable("id") Long id){
		WmsWareOrderTaskDetailEntity wmsWareOrderTaskDetail = wmsWareOrderTaskDetailService.getById(id);

        return Resp.ok(wmsWareOrderTaskDetail);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('wms:wmswareordertaskdetail:save')")
    public Resp<Object> save(@RequestBody WmsWareOrderTaskDetailEntity wmsWareOrderTaskDetail){
		wmsWareOrderTaskDetailService.save(wmsWareOrderTaskDetail);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('wms:wmswareordertaskdetail:update')")
    public Resp<Object> update(@RequestBody WmsWareOrderTaskDetailEntity wmsWareOrderTaskDetail){
		wmsWareOrderTaskDetailService.updateById(wmsWareOrderTaskDetail);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('wms:wmswareordertaskdetail:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		wmsWareOrderTaskDetailService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
