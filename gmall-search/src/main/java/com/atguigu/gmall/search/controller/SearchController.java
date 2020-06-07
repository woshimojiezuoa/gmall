package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("search")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchservice;

    @GetMapping
    public Resp<SearchResponseVO> search(SearchParam searchParam) throws IOException {

        SearchResponseVO searchresponse = this.searchservice.search(searchParam);

        return Resp.ok(searchresponse);
    }
}