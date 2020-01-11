package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVo;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lzzzzz
 * @create 2020-01-10 19:07
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        try {
            SearchResponse searchResponse = highLevelClient.search(new SearchRequest(new String[]{"goods"}, build(searchParam)), RequestOptions.DEFAULT);
            SearchResponseVo searchResponseVo = parseResult(searchResponse);
            searchResponseVo.setPageNum(searchParam.getPageNum());
            searchResponseVo.setPageSize(searchParam.getPageSize());
            System.out.println(searchResponse);
            return searchResponseVo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SearchResponseVo parseResult(SearchResponse searchResponse){
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        //处理产品集
        List<Goods> goodsList = new ArrayList<>();
        for (SearchHit hitsHit : hitsHits) {
            String goodsJson = hitsHit.getSourceAsString();
            Goods goods = JSON.parseObject(goodsJson, Goods.class);
            Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
            String skuTitle = highlightFields.get("skuTitle").getFragments()[0].string();
            goods.setSkuTitle(skuTitle);
            goodsList.add(goods);
        }
        searchResponseVo.setProducts(goodsList);
        //处理品牌
        SearchResponseAttrVO brandResponseVo = new SearchResponseAttrVO();
        brandResponseVo.setProductAttributeId(null);
        brandResponseVo.setName("品牌");
        ParsedLongTerms brandIdAggs = searchResponse.getAggregations().get("brandIdAggs");
        List<? extends Terms.Bucket> buckets = brandIdAggs.getBuckets();
        if (!CollectionUtils.isEmpty(buckets)){
            List<String> brandStringList = buckets.stream().map(bucket -> {
                Object attrId = ((Terms.Bucket) bucket).getKey();
                ParsedStringTerms brandNameAggs = ((Terms.Bucket) bucket).getAggregations().get("brandNameAggs");
                String attrName = brandNameAggs.getBuckets().get(0).getKeyAsString();
                Map<String, Object> map = new HashMap<>();
                map.put("id", attrId);
                map.put("name", attrName);
                String brandString = JSON.toJSONString(map);
                return brandString;
            }).collect(Collectors.toList());
            brandResponseVo.setValue(brandStringList);
        }
        searchResponseVo.setBrand(brandResponseVo);
        //处理分类
        SearchResponseAttrVO categoryVo = new SearchResponseAttrVO();
        categoryVo.setProductAttributeId(null);
        categoryVo.setName("分类");
        ParsedLongTerms categoryIdAggs = searchResponse.getAggregations().get("categoryIdAggs");
        List<? extends Terms.Bucket> cateBuckets = categoryIdAggs.getBuckets();
        if (!CollectionUtils.isEmpty(cateBuckets)){
            List<String> cateStringList = cateBuckets.stream().map(cateBuket -> {
                String cateId = ((Terms.Bucket) cateBuket).getKeyAsString();
                ParsedStringTerms categoryNameAggs = ((Terms.Bucket) cateBuket).getAggregations().get("categoryNameAggs");
                String cateName = categoryNameAggs.getBuckets().get(0).getKeyAsString();
                Map<String, Object> map = new HashMap<>();
                map.put("id", cateId);
                map.put("name", cateName);
                String cateString = JSON.toJSONString(map);
                return cateString;
            }).collect(Collectors.toList());
            categoryVo.setValue(cateStringList);
        }
        searchResponseVo.setCatelog(categoryVo);
        //处理总页数
        searchResponseVo.setTotal(hits.getTotalHits());
        //处理属性
        ParsedNested attrAggs = searchResponse.getAggregations().get("attrAggs");
        ParsedLongTerms attrIdAggs = attrAggs.getAggregations().get("attrIdAggs");
        List<? extends Terms.Bucket> attrIdBuckets = attrIdAggs.getBuckets();
        if (!CollectionUtils.isEmpty(attrIdBuckets)){
            List<SearchResponseAttrVO> responseAttrVOS = attrIdBuckets.stream().map(bucket -> {
                SearchResponseAttrVO responseAttrVO = new SearchResponseAttrVO();
                responseAttrVO.setProductAttributeId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                ParsedStringTerms attrNameAggs = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().get("attrNameAggs");
                responseAttrVO.setName(attrNameAggs.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attrValueAggs = ((Terms.Bucket) bucket).getAggregations().get("attrValueAggs");
                List<? extends Terms.Bucket> attrValueAggsBuckets = attrValueAggs.getBuckets();
                if (!CollectionUtils.isEmpty(attrValueAggsBuckets)) {
                    List<String> valueList = attrValueAggsBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                    responseAttrVO.setValue(valueList);
                }
                return responseAttrVO;
            }).collect(Collectors.toList());
            searchResponseVo.setAttrs(responseAttrVOS);
        }

        return searchResponseVo;
    }

    public SearchSourceBuilder build(SearchParam searchParam){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String keyword = searchParam.getKeyword();
        if (StringUtils.isEmpty(keyword)){
            return sourceBuilder;
        }
        //1.构建查询条件
        //1.1 根据关键字查询结果
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",keyword).operator(Operator.AND));
        //1.2 根据brandId过滤
        Long[] brandIds = searchParam.getBrand();
        if (brandIds!=null & brandIds.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brandIds));
        }
        //1.3根据categoryId过滤
        String[] categoryIds = searchParam.getCatelog3();
        if (categoryIds!=null & categoryIds.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",categoryIds));
        }
        //1.4根据价格区间过滤
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        Double priceFrom = searchParam.getPriceFrom();
        if (priceFrom!=null & priceFrom!=0){
            rangeQueryBuilder.gt(priceFrom);
        }
        Double priceTo = searchParam.getPriceTo();
        if (priceTo!=null & priceTo!=0){
            rangeQueryBuilder.lt(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        //1.5根据属性过滤

        //1.5.1根据属性id过滤
        List<String> props = searchParam.getProps();
        if (!CollectionUtils.isEmpty(props)){
            props.forEach(prop->{
                String[] split = StringUtils.split(prop, ":");
                if (split!=null & split.length==2){
                    String attrId = split[0];
                    String attrValueString = split[1];
                    String[] attrValues = StringUtils.split(attrValueString,"-");
                    BoolQueryBuilder attrBoolQueryBuilder = QueryBuilders.boolQuery();
                    attrBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                    attrBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                    //1.5.2根据属性value过滤
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrs",attrBoolQueryBuilder, ScoreMode.None));
                }
            });
        }
        sourceBuilder.query(boolQueryBuilder);
        //2.构建排序
        String order = searchParam.getOrder();
        String[] orderString = StringUtils.split(order, ":");
        if (orderString!=null & orderString.length==2){
            String orderField = orderString[0];
            switch (orderField){
                case "0": orderField="_score";break;
                case "1": orderField="sale";break;
                case "2": orderField="price";break;
                default:  orderField="_score";break;
            }
            sourceBuilder.sort(orderField, StringUtils.equals("asc",orderString[1])?SortOrder.ASC:SortOrder.DESC);
        }
        //3.构建分页
        int pageNum = searchParam.getPageNum();
        int pageSize = searchParam.getPageSize();
        sourceBuilder.from((pageNum-1)*pageSize);
        sourceBuilder.size(pageSize);
        //4.构建高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("skuTitle").preTags("<font style='color:red'>").postTags("</font>"));
        //5.构建聚合
        //5.1品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAggs").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAggs").field("brandName")));
        //5.2分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAggs").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAggs").field("categoryName")));
        //5.3属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAggs","attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAggs").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAggs").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAggs").field("attrs.attrValue"))));
        sourceBuilder.fetchSource(new String[]{"skuId", "defaultImage", "skuTitle", "price","skuSubTitle","_score"}, null);
        System.out.println(sourceBuilder);
        return sourceBuilder;
    }

}
