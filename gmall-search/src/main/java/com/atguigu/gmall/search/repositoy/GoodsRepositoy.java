package com.atguigu.gmall.search.repositoy;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author lzzzzz
 * @create 2020-01-09 15:55
 */
public interface GoodsRepositoy extends ElasticsearchRepository<Goods,Long> {
}
