package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.SmsCouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 19:01:55
 */
@Mapper
public interface SmsCouponDao extends BaseMapper<SmsCouponEntity> {
	
}
