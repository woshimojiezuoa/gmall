package com.atguigu.gmall.search;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPsmClient;
import com.atguigu.gmall.search.feign.GmallWsmClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.responitory.GoodsResponitory;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchresttemplate;
    @Autowired
    private GmallWsmClient wmsClient;
    @Autowired
    private GmallPsmClient pmsClient;
    @Autowired
    private GoodsResponitory goodsresponitory;
    @Test
    void contextLoads() {
        elasticsearchresttemplate.createIndex(Goods.class);
        elasticsearchresttemplate.putMapping(Goods.class);
    }

    @Test
    void inportData(){
//        System.out.println("8888");
//        System.out.println(wmsClient.toString());
//        System.out.println(pmsClient.toString());
        Long pageNum = 1l;
        Long pageSize = 100l;
        do{
            //分页查询SPU
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(pageNum);
            queryCondition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> queryspu = this.pmsClient.queryspuById(queryCondition);
            List<SpuInfoEntity> spus = queryspu.getData();
            //便利spu查询SKU
            spus.forEach(spuInfoEntity -> {
                 //通过FEIGN和SPUID查询所有SPU
                Resp<List<SkuInfoEntity>> skuResp = this.pmsClient.querySkuBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skus = skuResp.getData();
                //如果skus不为空则
                if(!CollectionUtils.isEmpty(skus)){
                    //把SKUS集合转化为Goods集合
                    List<Goods> goodSList = skus.stream().map(skuInfoEntity -> {
                        Goods goods = new Goods();
                        //将skuInfoEntity取值付给goods
                        //1.用feign查询属性
                        Resp<List<ProductAttrValueEntity>> attrValueResp = this.pmsClient.queryProductAttrValueEntityById(spuInfoEntity.getId());
                        List<ProductAttrValueEntity> attrValueList = attrValueResp.getData();
                        if(!CollectionUtils.isEmpty(attrValueList)){
                            List<SearchAttr> searchAttrList =  attrValueList.stream().map(productAttrValueEntity -> {
                                SearchAttr searchattr = new SearchAttr();
                                searchattr.setAttrId(productAttrValueEntity.getAttrId());
                                searchattr.setAttrName(productAttrValueEntity.getAttrName());
                                searchattr.setAttrValue(productAttrValueEntity.getAttrValue());
                                return searchattr;
                            }).collect(Collectors.toList());
                            goods.setAttrs(searchAttrList);
                        }
                        //2.用feign查询品牌
                        Resp<BrandEntity> brandEntityResp = this.pmsClient.queryBrandById(skuInfoEntity.getBrandId());
                        BrandEntity brandEntityRespData = brandEntityResp.getData();
                        if(brandEntityRespData!=null){
                            goods.setBrandId(skuInfoEntity.getBrandId());
                            goods.setBrandName(brandEntityRespData.getName());
                        }


                        //3.用feign查询分类
                        Resp<CategoryEntity> categoryEntityResp = this.pmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
                        CategoryEntity categoryEntity = categoryEntityResp.getData();
                        if(categoryEntity!=null){

                            goods.setCategoryId(skuInfoEntity.getCatalogId());
                            goods.setCategoryName(categoryEntity.getName());
                        }



                        goods.setCreateTime(spuInfoEntity.getCreateTime());
                        goods.setPic(skuInfoEntity.getSkuDefaultImg());
                        goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                        goods.setSale(0l);
                        goods.setSkuId(skuInfoEntity.getSkuId());

                        //4.用feign查询库存
                        Resp<List<WareSkuEntity>> listResp = this.wmsClient.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
                        List<WareSkuEntity> wareSkuEntities = listResp.getData();
                        if(!CollectionUtils.isEmpty(wareSkuEntities)){
                            boolean flag = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                            goods.setStore(flag);
                        }

                        goods.setTitle(skuInfoEntity.getSkuTitle());


                        return goods;
                    }).collect(Collectors.toList());
                    //导入索引库
                    goodsresponitory.saveAll(goodSList);
                }

            });


            pageSize=(long)spus.size();
            pageNum++;
        }while (pageSize==100);
    }
}
