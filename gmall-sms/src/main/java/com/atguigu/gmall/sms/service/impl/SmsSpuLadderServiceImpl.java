package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SmsSpuLadderDao;
import com.atguigu.gmall.sms.entity.SmsSpuLadderEntity;
import com.atguigu.gmall.sms.service.SmsSpuLadderService;


@Service("smsSpuLadderService")
public class SmsSpuLadderServiceImpl extends ServiceImpl<SmsSpuLadderDao, SmsSpuLadderEntity> implements SmsSpuLadderService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsSpuLadderEntity> page = this.page(
                new Query<SmsSpuLadderEntity>().getPage(params),
                new QueryWrapper<SmsSpuLadderEntity>()
        );

        return new PageVo(page);
    }

}