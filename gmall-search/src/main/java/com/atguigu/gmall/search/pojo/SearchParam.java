package com.atguigu.gmall.search.pojo;


/**
 * @author lzzzzz
 * @create 2020-01-10 18:31
 */

import lombok.Data;

import java.util.List;

/**
 * 用来接收页面输入的查询条件
 */
@Data
public class SearchParam {
    private String keyword;
    //可能是多个brand和多个category
    private Long[] brand;
    private String[] catelog3;
    private Double priceFrom;
    private Double priceTo;
    //0：综合排序  1：销量  2：价格  controller=1:asc 冒号前是排序字段，冒号后是排序规则
    private String order;
    //检索属性的集合，格式：2:win10-android-ios  冒号前是属性的id，冒号后是属性值（多个以”-“分隔）
    private List<String> props;
    //当前页码，设定初始值
    private int pageNum = 1;
    //每页大小，一般设为固定值，不需要更改
    private final int pageSize = 60;
    //是否有货
    private boolean store ;
}
