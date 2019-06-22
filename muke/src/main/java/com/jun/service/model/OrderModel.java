/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderModel
 * Author:   俊哥
 * Date:     2019/6/16 16:19
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.model;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/16
 * @since 1.0.0
 */
public class OrderModel {

    //订单id
    private String id;

    //用户id
    private Integer userId;

    //商品id
    private Integer itemId;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    //秒杀活动id,如果promoId不为空，则价格为秒杀价格
    private Integer promoId;

    //下单时商品的价格,如果promoId不为空，则价格为秒杀价格
    private BigDecimal itemPrice;

    //下单数量
    private Integer amount;

    //下单总价,如果promoId不为空，则价格为秒杀价格
    private  BigDecimal orderPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}

