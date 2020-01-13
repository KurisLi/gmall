package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-12 22:07
 */
@Data
public class CategoryVo extends CategoryEntity {
    private List<CategoryEntity> subs;
}
