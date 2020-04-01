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

import org.springframework.beans.BeansException;
import org.springframework.data.redis.core.RedisTemplate;
import pers.jun.dao.PromoMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.pojo.Promo;
import pers.jun.service.ItemService;
import pers.jun.service.PromoService;
import pers.jun.service.UserService;
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.OrderItemModel;
import pers.jun.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.jun.service.model.UserModel;
import sun.nio.cs.KOI8_R;

import java.math.BigDecimal;
import java.nio.Buffer;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private UserService userService;

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


    @Override
    public List<PromoModel> getPromoItems(){
        List<Promo> list = promoMapper.getList();
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
    public void publishPromo(Integer promoId) throws BusinessException {
        Promo promo = promoMapper.selectByPrimaryKey(promoId);
        if(promo == null) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"活动id不合法");
        }
        ItemModel itemModel = itemService.getById(promo.getItemId());

        //在发布活动商品的时候，将商品库存存入缓存
        redisTemplate.opsForValue().set("promo_itemStock_id_"+itemModel.getItemId(),itemModel.getStock());

        //将大闸限制数字设置到redis内,库存 * 某个系数
        redisTemplate.opsForValue().set("promo_door_count_"+promoId,itemModel.getStock() * 3);
    }

    /**
     * 生成秒杀令牌
     */
    @Override
    public String generateSecondKillToken(Integer promoId, Integer userId, Integer itemId) {
        // 先判断该商品是否已售罄，若已卖完直接返回
        if (redisTemplate.hasKey("promo_itemStock_id_over_"+itemId)) {
            return null;
        }

        Promo promo = promoMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertToPromoModel(promo);
        if(promoModel == null || !promoModel.getItemId().equals(itemId)) {
            return null;
        }

        // 判断秒杀活动状态
        promoModel.setStatus(setPromoStatus(promoModel));
        if(promoModel.getStatus() != 2) {
            return null;
        }

        // 用户合法性验证
        UserModel userModel = userService.getUserByIdIncache(userId);
        if(userModel == null) {
            return null;
        }

        // 商品合法性验证
        ItemModel itemModel = itemService.getByIdIncache(itemId);
        if(itemModel == null){
            return null;
        }

        //获取秒杀大闸的count数量
        Long doorCount = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
        if (doorCount < 0) {
            return null;
        }

        // 生成token并且存入redis缓存，设置五分钟过期时间
        String token = UUID.randomUUID().toString().replaceAll("-","");
        redisTemplate.opsForValue().set("promo_token_"+promoId+"_userId_"+userId+"_itemId_"+itemId,token);
        redisTemplate.expire("promo_token_"+promoId+"_userId_"+userId+"_itemId_"+itemId,5, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 判断活动时间，设置活动状态
     */
    private int setPromoStatus(PromoModel promoModel) {
        if(promoModel == null) {
            return 0;
        }
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
    private PromoModel convertToPromoModel(Promo promo) {
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

