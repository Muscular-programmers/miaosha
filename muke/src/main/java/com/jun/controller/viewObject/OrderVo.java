/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderVo
 * Author:   俊哥
 * Date:     2019/7/11 16:13
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller.viewObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/11
 * @since 1.0.0
 */
public class OrderVo {

    //订单id
    private String id;

    //商品id
    private Integer itemId;

    //商品名称
    private String title;

    //下单时间
    private String orderTime;

    //秒杀活动id,如果promoId不为空，则价格为秒杀价格
    private Integer promoId;

    //秒杀活动状态
    private Integer status;

    //下单时商品的价格,如果promoId不为空，则价格为秒杀价格
    private BigDecimal itemPrice;

    //下单数量
    private Integer amount;

    //下单总价,如果promoId不为空，则价格为秒杀价格
    private  BigDecimal orderPrice;

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}

