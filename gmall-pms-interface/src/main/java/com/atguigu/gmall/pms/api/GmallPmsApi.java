package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {
    //分页查询SPU
    @PostMapping("pms/spuinfo/page")
    public Resp<List<SpuInfoEntity>> queryspuById(@RequestBody QueryCondition condition);

    //根据SPUID查询SPU下的SKU
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable("spuId")Long spuId);
    //根据品牌ID生成品牌
    @GetMapping("pms/brand/{brandId}")
    public Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);
    //根据分类ID查询分类
    @GetMapping("pms/category/{catId}")
    public Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);
    //
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> queryProductAttrValueEntityById(@PathVariable("spuId") Long spuId);
}
