package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author lzzzzz
 * @create 2020-01-08 16:40
 */

/**
 * 搜索后展示出来的是sku信息，所以需要一个sku的索引
 */
@Data
@Document(indexName = "goods",type = "info",shards = 3,replicas = 2)
public class Goods {
    @Id
    private Long skuId;
    @Field(type = FieldType.Keyword,index = false)
    private String defaultImage;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String skuTitle;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String skuSubTitle;
    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Long)
    private Long sale;
    @Field(type = FieldType.Boolean)
    private Boolean store;
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 根据品牌和分类来聚合
     */
    @Field(type = FieldType.Long)
    private Long brandId;
    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Long)
    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 根据属性聚合，一般会有多个属性，所以使用一个集合来接收
     */
    @Field(type = FieldType.Nested)
    private List<SearchAttrValue> attrs;

}
