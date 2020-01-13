package com.atguigu.gmall.index.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.vo.CategoryVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-12 21:39
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private PmsFeignClient pmsFeignClient;

    @Override
    public List<CategoryEntity> queryLevel1Category() {
        Resp<List<CategoryEntity>> categoriesResp = pmsFeignClient.getCategoriesByLevelOrPid(1, null);
        List<CategoryEntity> categoryEntities = categoriesResp.getData();
        return categoryEntities;
    }

    @Override
    public List<CategoryVo> queryCategory2and3ByPid(Long pid) {
        Resp<List<CategoryVo>> categoryVoResp = pmsFeignClient.queryCategory2and3ByPid(pid);
        List<CategoryVo> categoryVos = categoryVoResp.getData();
        return categoryVos;
    }
}
