package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzzzzz
 * @create 2020-01-10 19:06
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 使用es查询方法
     */
    @GetMapping("/search")
    public Resp<SearchResponseVo> search(SearchParam searchParam){
        SearchResponseVo searchResponseVo = searchService.search(searchParam);
        return Resp.ok(searchResponseVo);
    }
}
