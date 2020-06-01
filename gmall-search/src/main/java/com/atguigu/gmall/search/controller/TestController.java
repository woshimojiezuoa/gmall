package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.search.feign.GmallPsmClient;
import com.atguigu.gmall.search.feign.GmallWsmClient;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/search/searchTest")
public class TestController {
//    @Autowired
//    private GmallPsmClient gmallpsmclient;
    @Autowired
    private GmallWsmClient gmallwsmclient;

    @GetMapping("/test")
    public Resp<Object> controllertest(){
//        Resp<BrandEntity> brandEntityResp = gmallpsmclient.queryBrandById(5l);
//        BrandEntity brandEntity = brandEntityResp.getData();
//        String brandEntityName = brandEntity.getName();
//        System.out.println("brandEntityName"+brandEntityName);
        Resp<List<WareSkuEntity>> listResp = gmallwsmclient.queryWareSkuBySkuId(1l);
        List<WareSkuEntity> data = listResp.getData();
        System.out.println("List<WareSkuEntity>"+data.toString());
        return Resp.ok("ok");
    }

}
