package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPsmClient;
import com.atguigu.gmall.search.feign.GmallWsmClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.responitory.GoodsResponitory;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoodsListener {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchresttemplate;
    @Autowired
    private GmallPsmClient pmsClient;
    @Autowired
    private GmallWsmClient wmsClient;
    @Autowired
    private GoodsResponitory goodsresponitory;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "gmall.item.create.queue", durable = "true"),
            exchange = @Exchange(
                    value = "GMALL-PMS-EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}))
    public void insertEsmethod(Long spuId){
        Resp<List<SkuInfoEntity>> skuResp = this.pmsClient.querySkuBySpuId(spuId);
        List<SkuInfoEntity> skus = skuResp.getData();
        //如果skus不为空则
        if(!CollectionUtils.isEmpty(skus)){
            //把SKUS集合转化为Goods集合
            List<Goods> goodSList = skus.stream().map(skuInfoEntity -> {
                Goods goods = new Goods();
                //将skuInfoEntity取值付给goods
                //1.用feign查询属性
                Resp<List<ProductAttrValueEntity>> attrValueResp = this.pmsClient.queryProductAttrValueEntityById(spuId);
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


                Resp<SpuInfoEntity> spuInfoEntityResp = pmsClient.querySpuById(spuId);
                SpuInfoEntity data = spuInfoEntityResp.getData();
                goods.setCreateTime(data.getCreateTime());
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
    }
}
