/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PromoServiceImol
 * Author:   俊哥
 * Date:     2019/6/17 15:07
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.impl;

import com.jun.dao.PromoMapper;
import com.jun.pojo.Promo;
import com.jun.service.PromoService;
import com.jun.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/17
 * @since 1.0.0
 */
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoMapper promoMapper;

    /**
     *根据商品id获取商品的活动
     * @param itemId
     * @return
     */
    public PromoModel getPromoByItemId(Integer itemId) {
        Promo promo = promoMapper.selectByItemId(itemId);

        PromoModel promoModel = convertToPromoModel(promo);

        if(promoModel == null)
            return null;

        //判断当前时间与查询结果promoModel的时间得到活动状态
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }
        else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    /**
     * bean转换
     * @param promo
     * @return
     */
    private PromoModel convertToPromoModel(Promo promo){
        if(promo == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promo,promoModel);
        promoModel.setStartDate(new DateTime(promo.getStartDate()));
        promoModel.setPromoItemPrice(BigDecimal.valueOf(promo.getPromoItemPrice()));
        promoModel.setEndDate(new DateTime(promo.getEndDate()));

        return promoModel;
    }
}

