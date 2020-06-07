package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient resthighlevelclient;

    public SearchResponseVO search(SearchParam searchParam) throws IOException {
        //构建DSL语句
        SearchRequest SearchRequest = buildQueryDsl(searchParam);
        SearchResponse response = resthighlevelclient.search(SearchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        SearchResponseVO searchresponse = this.parseSearchResult(response);
        searchresponse.setPageSize(searchParam.getPageSize());
        searchresponse.setPageNum(searchParam.getPageNum());
        return searchresponse;
    }

    //将SearchResponse（查询结果集）对象转换成SearchResponseVO对象
    public SearchResponseVO parseSearchResult(SearchResponse response){
        SearchResponseVO searchresponse = new SearchResponseVO();
        //1.获取总记录数
        SearchHits hits = response.getHits();
        searchresponse.setTotal(hits.getTotalHits());
        //2.品牌对象
        SearchResponseAttrVO brand = new SearchResponseAttrVO();
        brand.setName("品牌");
        //2解析所有聚合结果集
        Map<String, Aggregation> aggmap = response.getAggregations().asMap();
        //2.1.1
        ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggmap.get("brandIdAgg");
        List<String> brandValues = brandIdAgg.getBuckets().stream().map(bucket -> {
            Map<String,String> map = new HashMap<>();
            //获取品牌ID
            map.put("id", ((Terms.Bucket) bucket).getKeyAsString());
            //获取品牌名称：通过子聚合
            Map<String, Aggregation> subAggregationMap = ((Terms.Bucket) bucket).getAggregations().asMap();
            ParsedStringTerms brandNameAgg = (ParsedStringTerms)subAggregationMap.get("brandNameAgg");
            map.put("name",brandNameAgg.getBuckets().get(0).getKeyAsString());

            return JSON.toJSONString(map);
        }).collect(Collectors.toList());
        brand.setValue(brandValues);
        searchresponse.setBrand(brand);


        //3.分类对象
        SearchResponseAttrVO category = new SearchResponseAttrVO();
        brand.setName("分类");
        //3.1.1
        ParsedLongTerms cateIdAgg = (ParsedLongTerms)aggmap.get("categoryIdAgg");
        List<String> cateValues = cateIdAgg.getBuckets().stream().map(bucket -> {
            Map<String,String> map = new HashMap<>();
            //获取分类ID
            map.put("id", ((Terms.Bucket) bucket).getKeyAsString());
            //获取分类名称：通过子聚合
            Map<String, Aggregation> subAggregationMap = ((Terms.Bucket) bucket).getAggregations().asMap();
            ParsedStringTerms cateNameAgg = (ParsedStringTerms)subAggregationMap.get("categoryNameAgg");
            map.put("name",cateNameAgg.getBuckets().get(0).getKeyAsString());

            return JSON.toJSONString(map);
        }).collect(Collectors.toList());
        brand.setValue(cateValues);
        searchresponse.setCatelog(category);

        //4.解析商品列表
        SearchHit[] goodshits = hits.getHits();
        List<Goods> goodsList = new ArrayList<>();
        for (SearchHit goodshit : goodshits) {
            Goods goods = JSON.parseObject(goodshit.getSourceAsString(), Goods.class);
            goods.setTitle(goodshit.getHighlightFields().get("title").getFragments()[0].toString());

            goodsList.add(goods);
        }
        searchresponse.setProducts(goodsList);

        //规格参数
        //获取嵌套规格参数
        ParsedNested  attragg = (ParsedNested)aggmap.get("attrAgg");
        ParsedLongTerms  attrIdAgg = attragg.getAggregations().get("attrIdAgg");
        List<Terms.Bucket> buckets = ( List<Terms.Bucket>)attrIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(buckets)){
            List<SearchResponseAttrVO> collect = buckets.stream().map(bucket -> {
                SearchResponseAttrVO responseAttrVO = new SearchResponseAttrVO();
                //设置规格参数ID
                responseAttrVO.setProductAttributeId(bucket.getKeyAsNumber().longValue());
                //设置规格参数名
                List<? extends Terms.Bucket> attrNamebuckets = ((ParsedStringTerms) (bucket.getAggregations().get("attrNameAgg"))).getBuckets();
                responseAttrVO.setName(attrNamebuckets.get(0).getKeyAsString());
                //设置规格参数值
                List<? extends Terms.Bucket> attrvaluebuckets = ((ParsedStringTerms) (bucket.getAggregations().get("attrValueAgg"))).getBuckets();
                //将attrvaluebuckets转换为List<String>
                List<String> values = attrvaluebuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());

                responseAttrVO.setValue(values);
                return responseAttrVO;
            }).collect(Collectors.toList());
            searchresponse.setAttrs(collect);
        }

        return searchresponse;
    }


    public SearchRequest buildQueryDsl(SearchParam searchParam) {
        //查询关键字
        String keyword = searchParam.getKeyword();
        if(StringUtils.isAllEmpty(keyword)){
            return null;
        }
        //查询条件的构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.构建查询条件和过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1.构建查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword).operator(Operator.AND));
        //1.2.构建过滤条件
        //1.2.1构建品牌过滤
        String[] brand = searchParam.getBrand();
        if(brand!=null && brand.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brand));
        }
        //1.2.2构建分类过滤
        String[] catelog3 = searchParam.getCatelog3();
        if(catelog3!=null && catelog3.length!=0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",catelog3));
        }
        //1.2.3构建规格属性嵌套过滤
        String[] props = searchParam.getProps();
        if(props!=null && props.length!=0){
            for (String prop : props) {
                //分割字符串
                String[] split = StringUtils.split(prop, ":");
                if(split.length!=2 || split==null){
                     continue;
                }
                //分割valule
                String[] value = StringUtils.split(split[1], "-");
                //构建嵌套查询
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                //构建嵌套查询子查询
                BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                //构建子查询的过滤条件
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",value));
                //把嵌套查询放入过滤器
                boolQuery.must(QueryBuilders.nestedQuery("attrs",subBoolQuery, ScoreMode.None));
                boolQueryBuilder.filter(boolQuery);
            }

        }
        //1.2.4价格区间过滤
        Integer priceFrom = searchParam.getPriceFrom();
        Integer priceTo = searchParam.getPriceTo();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        if(priceFrom!=null){
            rangeQueryBuilder.gte(priceFrom);
        }
        if(priceTo!=null){
            rangeQueryBuilder.lte(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);


        searchSourceBuilder.query(boolQueryBuilder);

        //2.构建分页
        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        searchSourceBuilder.from((pageNum-1)*pageSize);
        searchSourceBuilder.size(pageSize);

        //3.构建排序
        String order = searchParam.getOrder();
        if(!StringUtils.isEmpty(order)){
            String[] split = StringUtils.split(order, ":");
            if(split!=null&&split.length==2){
                String field = null;
                switch (split[0]){
                    case "1": field="sale"; break;
                    case "2": field="price"; break;
                }

                searchSourceBuilder.sort(field,StringUtils.equals(split[1],"asc")? SortOrder.ASC:SortOrder.DESC);
            }


        }

        //4.构建高亮
        searchSourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));

        //5.构建聚合
        //5.1构建品牌聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));
        //5.2构建分类聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));
        //5.3构建搜索属性聚合

        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));


        searchSourceBuilder.fetchSource(new String[]{"skuid","pic","title","price"},null);
        System.out.println(searchSourceBuilder.toString());
        //查询参数
        SearchRequest searchrequest = new SearchRequest("goods");
        searchrequest.types("info");
        searchrequest.source(searchSourceBuilder);
        return searchrequest;
    }
}
