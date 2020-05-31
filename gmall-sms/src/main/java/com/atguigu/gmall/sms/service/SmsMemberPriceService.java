package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SmsMemberPriceEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品会员价格
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 19:01:55
 */
public interface SmsMemberPriceService extends IService<SmsMemberPriceEntity> {

    PageVo queryPage(QueryCondition params);
}

