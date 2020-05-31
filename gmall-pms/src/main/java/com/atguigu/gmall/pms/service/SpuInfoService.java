package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entityvo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 17:57:36
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuInfo(QueryCondition condition, Long catId);

    void saveSpuInfoVO(SpuInfoVO spuInfoVO);

    void saveSkuInfoWithSaleInfo(SpuInfoVO spuInfoVO);

    void saveBaseAttrs(SpuInfoVO spuInfoVO);


    void saveSpuInfo(SpuInfoVO spuInfoVO);
}

