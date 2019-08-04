/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartController
 * Author:   俊哥
 * Date:     2019/7/14 20:29
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import com.jun.controller.viewObject.CartVo;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.response.CommonReturnType;
import com.jun.service.CartService;
import com.jun.service.model.CartModel;
import com.jun.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
@RestController
@RequestMapping("/cart")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class CartController extends BaseController{

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletRequest servletRequest;

    /**
     * 添加至购物车
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/addCart")
    @ResponseBody
    public Object addCart(Integer itemId, Integer amount) throws BusinessException {
        //检查用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();

        cartService.addCart(itemId,amount,userId);

        return CommonReturnType.create(null);

    }

    @RequestMapping(value = "/getList")
    @ResponseBody
    public Object getList() throws BusinessException {

        //判断用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();
        List<CartModel> cartModels = cartService.selectAll(userId);

        List<CartVo> cartVos = convertToCartVoList(cartModels);
        return CommonReturnType.create(cartVos);
    }

    /**
     * 检查用户是否登录
     * @return
     * @throws BusinessException
     */
    private UserModel checkUserLogin() throws BusinessException {
        //判断用户是否登录
        Boolean is_login = (Boolean) servletRequest.getSession().getAttribute("IS_LOGIN");
        if (is_login == null || !is_login) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel)servletRequest.getSession().getAttribute("LOGIN_USER");
        return userModel;
    }

    /**
     * bean转换
     * @param cartModels
     * @return
     */
    private List<CartVo> convertToCartVoList(List<CartModel> cartModels) {
        if (cartModels == null) {
            return null;
        }

        List<CartVo> cartVos = new ArrayList<>();
        for (CartModel cartModel : cartModels) {
            CartVo cartVo = convertToCartVo(cartModel);
            cartVos.add(cartVo);
        }

        return cartVos;

    }

    private CartVo convertToCartVo(CartModel cartModel) {
        if (cartModel == null) {
            return null;
        }

        CartVo cartVo = new CartVo();
        BeanUtils.copyProperties(cartModel,cartVo);

        return cartVo;
    }


}

