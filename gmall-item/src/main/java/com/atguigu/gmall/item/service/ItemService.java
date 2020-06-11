package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import dto.SaleVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;

    public ItemVO queryItemVo(Long skuId) {
        ItemVO itemVO = new ItemVO();
        itemVO.setSkuId(skuId);

        //根据skuid查询sku
        Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuBySkuId(skuId);
        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        if(skuInfoEntity!=null){
            itemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
            itemVO.setSkuSubtitle(skuInfoEntity.getSkuSubtitle());
            itemVO.setPrice(skuInfoEntity.getPrice());
            itemVO.setWeight(skuInfoEntity.getWeight());
        }

        Long spuId = skuInfoEntity.getSpuId();
        //根据SKUID查询SPU
        itemVO.setSpuId(spuId);
        Resp<SpuInfoEntity> spuInfoEntityResp = pmsClient.querySpuById(spuId);
        SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
        if(spuInfoEntity!=null){
            itemVO.setSpuName(spuInfoEntity.getSpuName());
        }


        //根据skuid查图片列表
        Resp<List<SkuImagesEntity>> SkuImagesEntityList = pmsClient.querySkuImages(skuId);
        itemVO.setPics(SkuImagesEntityList.getData());

        //根据sku中的cid和brandid查

        Resp<BrandEntity> brandEntityResp = pmsClient.queryBrandById(skuInfoEntity.getBrandId());
        Resp<CategoryEntity> categoryEntityResp = pmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
        itemVO.setCategoryentity(categoryEntityResp.getData());
        itemVO.setBrandentity(brandEntityResp.getData());

        //根据skuid查询营销信息
        Resp<List<SaleVO>> saleVoList = smsClient.querySaleVoList(skuId);
        itemVO.setSales(saleVoList.getData());
        //查库存
        Resp<List<WareSkuEntity>> wareSkuBySkuId = wmsClient.queryWareSkuBySkuId(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuBySkuId.getData();
        itemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()>0));


        //根据spuid查所有skuids，再去查销售属性
        Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrValueList = pmsClient.querySkuSaleAttrValueList(spuId);
        itemVO.setSaleAttrs(skuSaleAttrValueList.getData());
        //spuid查询海报
        Resp<SpuInfoDescEntity> spuInfoDescEntityResp = pmsClient.queryImagesBySpuId(spuId);
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
        if(spuInfoDescEntity!=null){
            String decript = spuInfoDescEntity.getDecript();
            String[] split = StringUtils.split(decript, ",");
            itemVO.setImages(Arrays.asList(split));
        }

        //根据spuid和cateid查组及组下规格参数
        Resp<List<ItemGroupVo>> listResp = pmsClient.queryItemGroupVoByCidAndSpuId(skuInfoEntity.getCatalogId(), spuId);

        itemVO.setGroups(listResp.getData());

        return itemVO;
    }
}
