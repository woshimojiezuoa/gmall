package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import dto.SaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemVO {

    //1、当前sku的基本信息
    private Long skuId;

    private CategoryEntity categoryentity;
    private BrandEntity brandentity;
    private Long spuId;
    private String spuName;


    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal price;
    private BigDecimal weight;

    //2、sku的所有图片
    private List<SkuImagesEntity> pics;

    //3、sku的所有促销信息
    private List<SaleVO> sales;

    private Boolean store;//是否有货

    private List<SkuSaleAttrValueEntity> saleAttrs;//销售属性
    private List<String> images;//spud的海报
    private List<ItemGroupVo> groups;//规格参数组，及组下的规格参数



}