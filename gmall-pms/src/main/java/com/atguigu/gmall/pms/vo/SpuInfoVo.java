package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-05 13:02
 */
@Data
public class SpuInfoVo extends SpuInfoEntity {
    private List<String> spuImages;

    private List<BaseAttrVo> baseAttrs;

    private List<SkuInfoVo> skus;
}
