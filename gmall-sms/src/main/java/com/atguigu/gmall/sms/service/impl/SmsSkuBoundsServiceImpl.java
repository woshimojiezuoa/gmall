package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SmsSkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SmsSkuLadderDao;
import com.atguigu.gmall.sms.entity.SmsSkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SmsSkuLadderEntity;
import dto.SaleVO;
import dto.SkuSaleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SmsSkuBoundsDao;
import com.atguigu.gmall.sms.entity.SmsSkuBoundsEntity;
import com.atguigu.gmall.sms.service.SmsSkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("smsSkuBoundsService")
public class SmsSkuBoundsServiceImpl extends ServiceImpl<SmsSkuBoundsDao, SmsSkuBoundsEntity> implements SmsSkuBoundsService {
    @Autowired
    private SmsSkuFullReductionDao skuFullReductionDao;

    @Autowired
    private SmsSkuLadderDao skuLadderDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SmsSkuBoundsEntity> page = this.page(
                new Query<SmsSkuBoundsEntity>().getPage(params),
                new QueryWrapper<SmsSkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void saveSkuSaleInfo(SkuSaleDTO skuSaleDTO) {
// 3.1. 积分优惠
        SmsSkuBoundsEntity skuBoundsEntity = new SmsSkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleDTO, skuBoundsEntity);
        // 数据库保存的是整数0-15，页面绑定是0000-1111
        List<Integer> work = skuSaleDTO.getWork();
        if (!CollectionUtils.isEmpty(work)){
            skuBoundsEntity.setWork(work.get(0) * 8 + work.get(1) * 4 + work.get(2) * 2 + work.get(3));
        }
        this.save(skuBoundsEntity);

        // 3.2. 满减优惠
        SmsSkuFullReductionEntity skuFullReductionEntity = new SmsSkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleDTO, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuSaleDTO.getFullAddOther());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

        // 3.3. 数量折扣
        SmsSkuLadderEntity skuLadderEntity = new SmsSkuLadderEntity();
        BeanUtils.copyProperties(skuSaleDTO, skuLadderEntity);
        this.skuLadderDao.insert(skuLadderEntity);
    }


    public List<SaleVO> querySaleVoList(Long skuId) {
        List<SaleVO> saleVOList = new ArrayList<>();
        //查询积分信息
        SmsSkuBoundsEntity smsSkuBoundsEntity = this.getOne(new QueryWrapper<SmsSkuBoundsEntity>().eq("sku_id", skuId));
        if(smsSkuBoundsEntity!=null){
            SaleVO boudsVo = new SaleVO();
            boudsVo.setType("积分");
            StringBuffer sb = new StringBuffer();
            if(smsSkuBoundsEntity.getGrowBounds()!=null && smsSkuBoundsEntity.getGrowBounds().intValue() > 0){
                sb.append("成长积分赠送"+smsSkuBoundsEntity.getGrowBounds());
            }
            if(smsSkuBoundsEntity.getBuyBounds()!=null && smsSkuBoundsEntity.getBuyBounds().intValue() > 0){
                if(StringUtils.isNotBlank(sb)){
                    sb.append(",");
                }
                sb.append("购买积分赠送"+smsSkuBoundsEntity.getBuyBounds());
            }
            boudsVo.setDesc(sb.toString());
            saleVOList.add(boudsVo);
        }

        //查询打折
        SmsSkuLadderEntity skuLadderEntity = skuLadderDao.selectOne(new QueryWrapper<SmsSkuLadderEntity>().eq("sku_id", skuId));
        if(skuLadderEntity!=null){
            SaleVO ladderVo = new SaleVO();
            ladderVo.setType("打折");
            ladderVo.setDesc("满"+skuLadderEntity.getFullCount()+"件,打"+skuLadderEntity.getDiscount().divide(new BigDecimal(10))+"折");
            saleVOList.add(ladderVo);
        }

        //查询满减
        SmsSkuFullReductionEntity reductionEntity = this.skuFullReductionDao.selectOne(new QueryWrapper<SmsSkuFullReductionEntity>().eq("sku_id", skuId));
        if(reductionEntity!=null){
            SaleVO reducVo = new SaleVO();
            reducVo.setType("满减");
            reducVo.setDesc("满"+reductionEntity.getFullPrice()+"减"+reductionEntity.getReducePrice());
            saleVOList.add(reducVo);
        }
        return saleVOList;
    }

}