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

import com.jun.controller.viewObject.OrderVo;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.response.CommonReturnType;
import com.jun.service.OrderService;
import com.jun.service.model.OrderModel;
import com.jun.service.model.UserModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping("/getList")
    @ResponseBody
    public Object getList(){
        //调用service
        List<OrderModel> list = orderService.getList();

        //bean转换
        List<OrderVo> orderVos = converToOderVoList(list);

        return CommonReturnType.create(orderVos);
    }

    /**
     * bean转换
     */
    private OrderVo converToOderVo(OrderModel orderModel){
        //判空
        if(orderModel == null)
            return null;

        OrderVo orderVo = new OrderVo();

        BeanUtils.copyProperties(orderModel,orderVo);

        orderVo.setOrderTime(orderModel.getOrderTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));

        return orderVo;
    }

    /**
     * bean转换
     */
    private List<OrderVo> converToOderVoList(List<OrderModel> orderModels){
        //判空
        if(orderModels == null)
            return null;

        List<OrderVo> orderVos = new ArrayList<>();
        for (OrderModel orderModel : orderModels) {
            OrderVo orderVo = converToOderVo(orderModel);
            orderVos.add(orderVo);
        }

        return orderVos;
    }

}

