package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-09 15:23
 */
public interface GmallPmsApi {
    @PostMapping("pms/spuinfo/query")
    public Resp<List<SpuInfoEntity>> querySpuList(@RequestBody QueryCondition queryCondition);

    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuInfoBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> info(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> catInfo(@PathVariable("catId") Long catId);

    @GetMapping("pms/productattrvalue/queryAttrBySpuId/{spuId}")
    public Resp<List<ProductAttrValueEntity>> queryAttrBySpuId(@PathVariable("spuId") long spuId);
}
