package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.CategoryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {
    ////根据spuid和cateid查组及组下规格参数
    @GetMapping("pms/attrgroup/item/group/{cid}/{spuid}")
    public Resp<List<ItemGroupVo>> queryItemGroupVoByCidAndSpuId(@PathVariable("cid")Long cid, @PathVariable("spuId")Long spuId);
    //查询海报
    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> queryImagesBySpuId(@PathVariable("spuId") Long spuId);
    //通过SPUID查询下面所有SKU的销售属性
    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrValueList(@PathVariable("skuId")Long spuId);
    //通过SKUID查询图片列表
    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<SkuImagesEntity>> querySkuImages(@PathVariable("skuId")Long skuId);
    //通过SKUID查询SKU
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuBySkuId(@PathVariable("skuId") Long skuId);
    //通过一级分类查二三级分类
    @GetMapping("pms/category/querySubCategory/{pid}")
    public Resp<List<CategoryVo>> querySubCategory(@PathVariable("pid")Long pid);
    //查询一级分类
    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> CategoryList(@RequestParam(value = "level",defaultValue = "0") Integer level,@RequestParam(value = "parentCid",required = false) Long parentCid);
    //分页查询SPU
    @PostMapping("pms/spuinfo/page")
    public Resp<List<SpuInfoEntity>> queryspuById(@RequestBody QueryCondition condition);
    //通过spuid查询spu
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    //根据SPUID查询SPU下的SKU
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId);
    //根据品牌ID生成品牌
    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);
    //根据分类ID查询分类
    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);
    //
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> queryProductAttrValueEntityById(@PathVariable("spuId") Long spuId);
}
