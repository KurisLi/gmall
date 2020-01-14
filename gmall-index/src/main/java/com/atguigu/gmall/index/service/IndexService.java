package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.vo.CategoryVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-12 21:38
 */
public interface IndexService {

    List<CategoryEntity> queryLevel1Category();

    List<CategoryVo> queryCategory2and3ByPid(Long pid);

    void testLock();
}
