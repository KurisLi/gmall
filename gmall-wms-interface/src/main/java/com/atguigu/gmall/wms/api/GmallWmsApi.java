package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-09 16:17
 */
public interface GmallWmsApi {
    @GetMapping("wms/waresku/{skuId}")
    public Resp<List<WareSkuEntity>> queryWareBySkuId(@PathVariable("skuId") Long skuId);

    @PostMapping("wms/waresku")
    public Resp<List<SkuLockVo>> checkAndLock(@RequestBody List<SkuLockVo> skuLockVos);
}
