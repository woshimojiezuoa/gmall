package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.CategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;




/**
 * 商品三级分类
 *
 * @author yuanbowang
 * @email 634215228@qq.com
 * @date 2020-05-11 17:57:36
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:category:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = categoryService.queryPage(queryCondition);

        return Resp.ok(page);
    }
    //通过一节分类ID查询二三级分类
    @ApiOperation("父id查询二级分类及其子分类")
    @GetMapping("querySubCategory/{pid}")
    public Resp<List<CategoryVo>> querySubCategory(@PathVariable("pid")Long pid){
        List<CategoryVo> categoryEntityList = this.categoryService.querySubCategory(pid);
        return Resp.ok(categoryEntityList);
    }
    @GetMapping()
    public Resp<List<CategoryEntity>> CategoryList(@RequestParam(value = "level",defaultValue = "0") Integer level,@RequestParam(value = "parentCid",required = false) Long parentCid){
        List<CategoryEntity> categoryentitylist =categoryService.queryCategoryList(level,parentCid);
        return Resp.ok(categoryentitylist);
    }
    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("{catId}")
    public Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return Resp.ok(category);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:category:save')")
    public Resp<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:category:update')")
    public Resp<Object> update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:category:delete')")
    public Resp<Object> delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return Resp.ok(null);
    }

}
