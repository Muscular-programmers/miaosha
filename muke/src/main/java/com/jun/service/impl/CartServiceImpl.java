/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartServiceImpl
 * Author:   俊哥
 * Date:     2019/7/14 20:16
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.impl;

import com.jun.dao.CartMapper;
import com.jun.dao.ItemMapper;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.pojo.Cart;
import com.jun.pojo.Item;
import com.jun.service.CartService;
import com.jun.service.model.CartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/14
 * @since 1.0.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 根据用户查询所有
     * @param userId
     * @return
     */
    public List<CartModel> selectAll(Integer userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartModel> cartModels = convertToCartModelList(cartList);
        return cartModels;
    }

    /**
     * 添加至购物车
     * @param itemId
     * @param amount
     * @param userId
     */
    public void addCart(Integer itemId, Integer amount, Integer userId) throws BusinessException {
        //参数验证
        if(itemId == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");
        }
        if(amount == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        if(userId == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        Cart cart = new Cart();

        //判断该用户是否拥有该商品，如果存在，则更新数据，如果不存在则添加数据
        Cart cart1 = cartMapper.selectByUserAndItem(itemId, userId);
        if (cart1 != null) {
            //该用户存在该商品
            int id = cart1.getId();
            cart.setItemId(itemId);
            cart.setUserId(userId);
            cart.setId(id);
            cart.setAmount(cart1.getAmount() + 1);
            cartMapper.updateByPrimaryKeySelective(cart);
            return;
        }

        //如果不存在则插入
        cart.setItemId(itemId);
        cart.setAmount(amount);
        cart.setUserId(userId);

        cartMapper.insertSelective(cart);
        return ;

    }


    /**
     * bean转换
     * @param cartList
     * @return
     */
    private List<CartModel> convertToCartModelList(List<Cart> cartList){
        if(cartList == null)
            return null;

        List<CartModel> cartModels = new ArrayList<>();
        for (Cart cart : cartList) {
            CartModel cartModel = convertToCartModel(cart);
            cartModels.add(cartModel);
        }

        return cartModels;
    }

    private CartModel convertToCartModel(Cart cart) {
        if(cart == null)
            return null;

        CartModel cartModel = new CartModel();
        BeanUtils.copyProperties(cart,cartModel);

        //根据itemId查询item
        Item item = itemMapper.selectByPrimaryKey(cart.getItemId());
        cartModel.setItem(item);

        return cartModel;

    }
}

