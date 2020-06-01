package com.atguigu.gmall.search.feign;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("pms-service")
public interface GmallPsmClient extends GmallPmsApi{
}
