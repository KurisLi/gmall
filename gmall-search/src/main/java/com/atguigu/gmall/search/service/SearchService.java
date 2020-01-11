package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVo;

/**
 * @author lzzzzz
 * @create 2020-01-10 19:06
 */
public interface SearchService {
    SearchResponseVo search(SearchParam searchParam);
}
