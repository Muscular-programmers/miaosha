/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartVo
 * Author:   俊哥
 * Date:     2019/7/14 20:39
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller.viewObject;

import com.jun.pojo.Item;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/14
 * @since 1.0.0
 */
public class CartVo {

    /**
     id
     */
    private Integer id;

    /**
     购物车商品
     */
    private Item item;

    /**
     商品数量
     */
    private Integer amount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}

