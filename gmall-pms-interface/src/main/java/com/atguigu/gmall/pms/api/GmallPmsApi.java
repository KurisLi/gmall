package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuInfoBySpuId(@PathVariable("id") Long id);

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> getCategoriesByLevelOrPid(@RequestParam(value = "level",defaultValue = "0")Integer level,
                                                                @RequestParam(value = "parentCid",required = false) Long pid);

    @GetMapping("pms/category/cates/{pid}")
    public Resp<List<CategoryVo>> queryCategory2and3ByPid(@PathVariable("pid") Long pid);
}
