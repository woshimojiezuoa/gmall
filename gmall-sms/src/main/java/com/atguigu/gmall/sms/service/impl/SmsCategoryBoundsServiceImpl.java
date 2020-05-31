package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SmsCategoryBoundsDao;
import com.atguigu.gmall.sms.entity.SmsCategoryBoundsEntity;
import com.atguigu.gmall.sms.service.SmsCategoryBoundsService;


@Service("smsCategoryBoundsService")
public class SmsCategoryBoundsServiceImpl extends ServiceImpl<SmsCategoryBoundsDao, SmsCategoryBoundsEntity> implements SmsCategoryBoundsService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsCategoryBoundsEntity> page = this.page(
                new Query<SmsCategoryBoundsEntity>().getPage(params),
                new QueryWrapper<SmsCategoryBoundsEntity>()
        );

        return new PageVo(page);
    }

}