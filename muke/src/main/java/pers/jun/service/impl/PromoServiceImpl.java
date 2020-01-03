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
package pers.jun.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import pers.jun.dao.PromoMapper;
import pers.jun.pojo.Promo;
import pers.jun.service.ItemService;
import pers.jun.service.PromoService;
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

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

        int status = setPromoStatus(promoModel);
        promoModel.setStatus(status);
        return promoModel;
    }

    /**
     * 查询所有活动中的商品或者查询指定数量处于活动中的商品
     * @param count
     * @return
     */
    public List<PromoModel> getPromoItems(int count){
        List<Promo> list = promoMapper.getList(count);
        List<PromoModel> modelList = convertToPromoModelList(list);

        //使用迭代器在遍历list的时候删除
        Iterator<PromoModel> iterator = modelList.iterator();
        while (iterator.hasNext()) {
            PromoModel next = iterator.next();
            int status = setPromoStatus(next);
            //状态为2表示正处于活动中
            if(status != 2)
                iterator.remove();
        }
        return modelList;

    }

    @Override
    public void publishPromo(Integer promoId) {
        Promo promo = promoMapper.selectByPrimaryKey(promoId);
        ItemModel itemModel = itemService.getById(promo.getItemId());

        //在发布活动商品的时候，将商品库存存入缓存
        redisTemplate.opsForValue().set("promo_item_id_"+itemModel.getItemId(),itemModel.getStock());
    }

    /**
     * 判断活动时间，设置活动状态
     */
    private int setPromoStatus(PromoModel promoModel) {
        if(promoModel == null)
            return 0;
        //判断当前时间与查询结果promoModel的时间得到活动状态
        if(promoModel.getStartDate().isAfterNow()){
            return 1;
        }
        else if(promoModel.getEndDate().isBeforeNow()){
            return 3;
        }else{
            return 2;
        }
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

    private List<PromoModel> convertToPromoModelList(List<Promo> list){
        List<PromoModel> modelList = list.stream().map(item->{
            PromoModel promoModel = convertToPromoModel(item);
            return promoModel;
        }).collect(Collectors.toList());
        return modelList;
    }
}

