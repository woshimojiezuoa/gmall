package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SmsSkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import dto.SaleVO;
import dto.SkuSaleDTO;

import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 19:01:54
 */
public interface SmsSkuBoundsService extends IService<SmsSkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSkuSaleInfo(SkuSaleDTO skuSaleDTO);

    List<SaleVO> querySaleVoList(Long skuId);
}

