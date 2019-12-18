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
package pers.jun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pers.jun.config.UserLoginToken;
import pers.jun.controller.viewObject.CartVo;
import pers.jun.controller.viewObject.UserVo;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.interceptor.AuthenticationInterceptor;
import pers.jun.pojo.Cart;
import pers.jun.pojo.Item;
import pers.jun.response.CommonReturnType;
import pers.jun.service.CartService;
import pers.jun.service.ItemService;
import pers.jun.service.UserService;
import pers.jun.service.model.CartModel;
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pers.jun.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
@Api(tags = "购物车管理模块")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class CartController extends BaseController{

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ItemService itemService;

    /**
     * 添加至购物车
     */
    @UserLoginToken
    @PostMapping(value = "/addCart")
    @ApiOperation(value = "添加至购物车")
    public Object addCart(@RequestBody CartVo cartVo) throws BusinessException {
        //检查用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();
        cartService.addCart(cartVo.getItemId(),cartVo.getAmount(),userId);
        return CommonReturnType.create(null);

    }

    /**
     * 查询购物车列表
     */
    @UserLoginToken
    @PostMapping(value = "/getList")
    @ApiOperation(value = "查询购物车列表")
    public Object getList() throws BusinessException {
        // 判断用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();
        List<CartModel> cartModels = cartService.selectAll(userId);

        List<CartVo> cartVos = convertToCartVoList(cartModels);
        return CommonReturnType.create(cartVos);
    }

    /**
     * 编辑购物车条目
     */
    @UserLoginToken
    @PostMapping("/cartEdit")
    @ApiOperation(value = "编辑购物车条目")
    public Object cartEdit(@RequestBody CartVo cartVo) throws BusinessException {
        //检查用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();
        //更新商品数量(三个参数:用户id,商品id,更新数量)
        cartService.editCart(userId,cartVo.getItemId(),cartVo.getAmount());
        return new CommonReturnType().create(null);
    }

    /**
     * 全选购物车条目
     */
    @UserLoginToken
    @PostMapping("/editCheckAll")
    @ApiOperation(value = "全选购物车条目")
    public Object editCheckAll(@RequestBody CartVo cartVo) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();
        return new CommonReturnType().create(null);
    }

    /**
     * 删除购物车条目
     */
    @UserLoginToken
    @PostMapping("/cartDel")
    @ApiOperation(value = "删除购物车条目")
    public Object cartDel(@RequestBody CartVo cartVo) throws BusinessException {
        //判断用户是否登录
        UserModel userModel = checkUserLogin();
        Integer userId = userModel.getId();
        cartService.deleteCart(userId,cartVo.getItemId());
        return new CommonReturnType().create(null);
    }

    /**
     * 检查用户是否登录
     * @return
     * @throws BusinessException
     */
    private UserModel checkUserLogin() throws BusinessException {
        UserModel userModel = AuthenticationInterceptor.userModelByToken;
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
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
        cartVo.setItemId(cartModel.getItem().getId());
        cartVo.setTitle(cartModel.getItem().getTitle());
        cartVo.setImgUrl(cartModel.getItem().getImgUrl().split(",")[0]);
        cartVo.setPrice(new BigDecimal(cartModel.getItem().getPrice()));
        if (cartModel.getPromoModel() != null) {
            cartVo.setPromoId(cartModel.getPromoModel().getId());
            cartVo.setPrice(cartModel.getPromoModel().getPromoItemPrice());
        }
        return cartVo;
    }


}

