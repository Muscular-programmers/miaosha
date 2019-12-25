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
package pers.jun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import pers.jun.config.UserLoginToken;
import pers.jun.controller.viewObject.CartVo;
import pers.jun.controller.viewObject.ItemVo;
import pers.jun.controller.viewObject.OrderItemVo;
import pers.jun.controller.viewObject.OrderVo;
import pers.jun.dao.ItemMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.interceptor.AuthenticationInterceptor;
import pers.jun.pojo.Cart;
import pers.jun.pojo.Item;
import pers.jun.pojo.OrderItem;
import pers.jun.response.CommonReturnType;
import pers.jun.service.AddressService;
import pers.jun.service.OrderService;
import pers.jun.service.model.AddressModel;
import pers.jun.service.model.OrderModel;
import pers.jun.service.model.UserModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.jun.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
@Api(tags = "订单管理模块")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private AddressService addressService;

    /**
     * 创建订单
     */
    @UserLoginToken
    @PostMapping(value = "/create")
    @ApiOperation(value = "创建订单")
    public Object createOrder(@RequestBody OrderModel orderModel) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();
        if(orderModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        orderService.createOrder(orderModel);
        return CommonReturnType.create(null);
    }

    /**
     * 查询订单详情
     */
    @UserLoginToken
    @GetMapping(value = "/orderDetail")
    @ApiOperation(value = "查看订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId",value = "订单id",required = true,paramType = "query")
    })
    public CommonReturnType orderDetail(@RequestParam(name = "orderId") String orderId) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        OrderModel orderModel = orderService.orderById(orderId);
        OrderVo orderVo = converToOderVo(orderModel);
        return CommonReturnType.create(orderVo);
    }

    @UserLoginToken
    @GetMapping(value = "/delOrder")
    @ApiOperation(value = "删除订单")
    public CommonReturnType delOrder(@RequestParam(name = "orderId") String orderId) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        orderService.delOrder(orderId);
        return CommonReturnType.create(null);
    }

    //@UserLoginToken
    //@RequestMapping(value = "/create")
    //@ResponseBody
    //public Object createOrder(@RequestParam(name = "itemId")Integer itemId,
    //                          @RequestParam(name = "promoId",required = false)Integer promoId,
    //                          @RequestParam(name = "amount")Integer amount) throws BusinessException {
    //    //参数校验
    //
    //    //调用service
    //    //得到用户信息
    //    Boolean is_login = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
    //    if(is_login == null || !is_login){
    //        throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
    //    }
    //    UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
    //    orderService.createOrder(userModel.getId(),itemId,promoId,amount);
    //
    //    //返回
    //    return CommonReturnType.create(null);
    //}

    /**
     * 得到订单列表
     */
    @UserLoginToken
    @GetMapping("/getList")
    @ApiOperation(value = "得到订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query"),
            @ApiImplicitParam(name = "page",value = "分页page",required = false,paramType = "query"),
            @ApiImplicitParam(name = "size",value = "分页size",required = false,paramType = "query")
        }
    )

    public Object getList(@RequestParam(name = "userId") Integer userId,
                          @RequestParam(name = "page") Integer page,
                          @RequestParam(name = "size") Integer size) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();

        //调用service
        List<OrderModel> modelList = orderService.getList(userId,page,size);
        //bean转换
        List<OrderVo> orderVos = converToOderVoList(modelList);
        return CommonReturnType.create(orderVos);
    }

    /**
     * 检查用户是否登录
     */
    private UserModel checkUserLogin() throws BusinessException {
        UserModel userModel = AuthenticationInterceptor.userModelByToken;
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        return userModel;
    }

    /**
     * bean转换
     */
    private OrderVo converToOderVo(OrderModel orderModel) throws BusinessException {
        //判空
        if(orderModel == null)
            return null;
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(orderModel,orderVo);
        // 设置价格
        orderVo.setTotalPrice(new BigDecimal(orderModel.getTotalPrice()));
        // 设置订单地址详细信息
        AddressModel addressModel = addressService.getAddress(orderModel.getAddress());
        orderVo.setAddress(addressModel);
        orderVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderModel.getCreateDate()));
        orderVo.setFinishDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderModel.getFinishDate()));
        return orderVo;
    }

    /**
     * bean转换
     */
    private List<OrderVo> converToOderVoList(List<OrderModel> orderModels) throws BusinessException {
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

