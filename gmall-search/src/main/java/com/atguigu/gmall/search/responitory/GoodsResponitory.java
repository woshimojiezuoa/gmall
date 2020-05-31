package com.atguigu.gmall.search.responitory;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsResponitory extends ElasticsearchRepository<Goods,Long> {
}
