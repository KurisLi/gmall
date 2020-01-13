package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.vo.CategoryVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-12 21:20
 */
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 查询一级菜单
     */
    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryLevel1Category(){
        List<CategoryEntity> categoryEntities = indexService.queryLevel1Category();
        return Resp.ok(categoryEntities);
    }

    /**
     * 根据一级分类的id查询二级分类和三级分类
     */
    @GetMapping("cates/{pid}")
    public Resp<List<CategoryVo>> queryCategory2and3ByPid(@PathVariable("pid") Long pid){
        List<CategoryVo> categoryVos = indexService.queryCategory2and3ByPid(pid);
        return Resp.ok(categoryVos);
    }
}
