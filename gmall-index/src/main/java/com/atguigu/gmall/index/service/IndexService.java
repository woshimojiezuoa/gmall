package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsFeign;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class IndexService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static String KEY_PREFIX = "index:cates:";

    public List<CategoryEntity> queryLevel1Category() {
        Resp<List<CategoryEntity>> categoryResp = this.gmallPmsFeign.CategoryList(1,null);
        return categoryResp.getData();
    }
    //查询二三级分类
    public List<CategoryVo> querySubCategory(Long pid) {
        //查询缓存中是否存在
        String cacheCategories = stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //如果缓存中有则直接返回
        if(StringUtils.isNotBlank(cacheCategories)){
            List<CategoryVo> categoryVos = JSON.parseArray(cacheCategories, CategoryVo.class);
            return categoryVos;
        }

        //缓存中没有则查询数据库并存入缓存
        Resp<List<CategoryVo>> ctegoryVoResp = this.gmallPmsFeign.querySubCategory(pid);
        List<CategoryVo> ctegoryVoRespData = ctegoryVoResp.getData();
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(ctegoryVoRespData));
        return ctegoryVoRespData;
    }
}
