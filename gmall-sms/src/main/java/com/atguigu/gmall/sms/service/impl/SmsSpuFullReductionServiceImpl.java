package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SmsSpuFullReductionDao;
import com.atguigu.gmall.sms.entity.SmsSpuFullReductionEntity;
import com.atguigu.gmall.sms.service.SmsSpuFullReductionService;


@Service("smsSpuFullReductionService")
public class SmsSpuFullReductionServiceImpl extends ServiceImpl<SmsSpuFullReductionDao, SmsSpuFullReductionEntity> implements SmsSpuFullReductionService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsSpuFullReductionEntity> page = this.page(
                new Query<SmsSpuFullReductionEntity>().getPage(params),
                new QueryWrapper<SmsSpuFullReductionEntity>()
        );

        return new PageVo(page);
    }

}