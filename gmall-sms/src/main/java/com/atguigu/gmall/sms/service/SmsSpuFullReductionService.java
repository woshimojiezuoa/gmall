package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SmsSpuFullReductionEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品满减信息
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 19:01:54
 */
public interface SmsSpuFullReductionService extends IService<SmsSpuFullReductionEntity> {

    PageVo queryPage(QueryCondition params);
}

