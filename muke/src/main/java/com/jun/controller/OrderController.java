/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderController
 * Author:   俊哥
 * Date:     2019/6/16 22:55
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.response.CommonReturnType;
import com.jun.service.OrderService;
import com.jun.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/16
 * @since 1.0.0
 */
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value = "/create",consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public Object createOrder(@RequestParam(name = "itemId")Integer itemId,
                              @RequestParam(name = "promoId",required = false)Integer promoId,
                              @RequestParam(name = "amount")Integer amount) throws BusinessException {
        //参数校验

        //调用service
        //得到用户信息
        Boolean is_login = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(is_login == null || !is_login){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        orderService.createOrder(userModel.getId(),itemId,promoId,amount);

        //返回
        return CommonReturnType.create(null);
    }

}

