package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entityvo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 17:57:36
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryByCidPage(Long cid, QueryCondition condition);

    AttrGroupVO queryById(Long gid);

    List<AttrGroupVO> queryByCid(Long cid);

    List<ItemGroupVo> queryItemGroupVoByCidAndSpuId(Long cid, Long spuId);
}

