/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PromoModel
 * Author:   俊哥
 * Date:     2019/6/17 14:20
 * Description: 秒杀模型
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.model;

import com.jun.pojo.Item;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈秒杀模型〉
 *
 * @author 俊哥
 * @create 2019/6/17
 * @since 1.0.0
 */
public class PromoModel {

    //秒杀活动id
    private Integer id;

    //秒杀活动名称
    private String promoName;

    //秒杀活动状态。0：没有活动 1：活动未开始 2：活动进行中 3：活动已结束
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    //开始时间
    private DateTime startDate;

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    //开始时间
    private DateTime endDate;

    //包含的商品（此处假设只有一个商品）
    private Integer itemId;

    //活动时商品的价格
    private BigDecimal promoItemPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPromoItemPrice() {
        return promoItemPrice;
    }

    public void setPromoItemPrice(BigDecimal promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }
}

