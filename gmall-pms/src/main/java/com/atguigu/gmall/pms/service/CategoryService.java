package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 17:57:36
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntity> queryCategoryList(Integer level, Long parentCid);

    List<CategoryVo> querySubCategory(Long pid);
}

