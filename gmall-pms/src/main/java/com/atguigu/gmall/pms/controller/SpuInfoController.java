package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;




/**
 * spu信息
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2020-01-01 13:50:33
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spuinfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 通过catId查询spu信息
     */
    @GetMapping
    public Resp<PageVo> getSpuInfoByCatId(QueryCondition queryCondition
            , @RequestParam(value = "catId" ,defaultValue = "0") Long catId){
        PageVo pageVo = spuInfoService.getSpuInfoByCatId(queryCondition,catId);
        return Resp.ok(pageVo);
    }
    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = spuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }

    /**
     * 分页查询spu信息
     */
    @PostMapping("/query")
    public Resp<List<SpuInfoEntity>> querySpuList(@RequestBody QueryCondition queryCondition){
        List<SpuInfoEntity> spuInfoEntities = spuInfoService.querySpuList(queryCondition);
        return  Resp.ok(spuInfoEntities);
    }

    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('pms:spuinfo:info')")
    public Resp<SpuInfoEntity> info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return Resp.ok(spuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:spuinfo:save')")
    public Resp<Object> save(@RequestBody SpuInfoVo spuInfoVo){
		//spuInfoService.save(spuInfoVo);
        this.spuInfoService.bigSave(spuInfoVo);
        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:spuinfo:update')")
    public Resp<Object> update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);
        //假设修改spu时发送消息给mq，用来同步购物的价格
        amqpTemplate.convertAndSend("GMALL-PMS-EXCHANGE","cart.update",spuInfo.getId());
        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:spuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));
        return Resp.ok(null);
    }
}
