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
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.omg.CORBA.COMM_FAILURE;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import pers.jun.config.UserLoginToken;
import pers.jun.controller.viewObject.CartVo;
import pers.jun.controller.viewObject.ItemVo;
import pers.jun.controller.viewObject.OrderItemVo;
import pers.jun.controller.viewObject.OrderVo;
import pers.jun.dao.ItemMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.interceptor.AuthenticationInterceptor;
import pers.jun.mq.MqProducer;
import pers.jun.pojo.Cart;
import pers.jun.pojo.Item;
import pers.jun.pojo.OrderItem;
import pers.jun.response.CommonReturnType;
import pers.jun.service.AddressService;
import pers.jun.service.ItemService;
import pers.jun.service.OrderService;
import pers.jun.service.PromoService;
import pers.jun.service.model.AddressModel;
import pers.jun.service.model.OrderItemModel;
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
import java.util.Map;

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
@Api(tags = "OrderController")
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

    @Autowired
    private PromoService promoService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成秒杀令牌
     */
    @UserLoginToken
    @PostMapping(value = "/generateToken")
    @ApiOperation(value = "创建订单")
    public Object generateToken(@RequestParam(name="itemId")Integer itemId,
                                @RequestParam(name="promoId")Integer promoId) throws BusinessException {
        //判断用户是否登录
        UserModel userModel = checkUserLogin();

        //生成秒杀令牌
        String secondKillToken = promoService.generateSecondKillToken(promoId, userModel.getId(), itemId);

        if(secondKillToken == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌生成失败");

        return CommonReturnType.create(secondKillToken);
    }

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

        //通过订单中包含的商品调用不用的下层service，多个商品或者一个没有活动的商品
        //List<OrderItemModel> orderItems = orderModel.getOrderItems();
        //if(orderItems.size() > 1 || orderItems.get(0).getPromoId() == null)
        //    orderService.createOrder(orderModel);
        //else{
        //    //校验秒杀令牌是否正确
        //    //redisTemplate.opsForValue().get("promo_token_"+promo)
        //
        //    // 先判断该商品是否已卖完，若已卖完直接返回
        //    if (redisTemplate.hasKey("promo_item_id_over_"+orderItems.get(0).getItemId())) {
        //        throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        //    }
        //
        //    //orderService.createOrderPromo(orderModel);
        //    //在下单之前，加入库存流水初始化
        //    String stockLog = itemService.initStockLog(orderItems.get(0).getItemId(), orderItems.get(0).getAmount());
        //
        //    //再去完成对应的下单事务型消息机制
        //    boolean result = mqProducer.transactionAsyncReduceStock(orderModel, stockLog);
        //    if(!result)
        //        throw new BusinessException(EmBusinessError.MQ_SEND_FAIL,"下单失败，库存不足或同步消息失败");
        //}
        return CommonReturnType.create(null);
    }

    /**
     * 创建秒杀订单
     */
    @UserLoginToken
    @PostMapping(value = "/createPromo")
    @ApiOperation(value = "创建秒杀订单")
    public Object createPromo(@RequestBody OrderModel orderModel,
                              @RequestParam(name = "promoToken")String promoToken) throws BusinessException {
        //判断用户是否登录
        UserModel userModel = checkUserLogin();

        OrderItemModel itemModel = orderModel.getOrderItems().get(0);
        Integer itemId = itemModel.getItemId();
        Integer promoId = itemModel.getPromoId();
        Integer amount = itemModel.getAmount();
        Integer userId = userModel.getId();
        //校验秒杀令牌是否正确
        String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_"+promoId+"_userId_"+userId+"_itemId_"+itemId);
        if(inRedisPromoToken == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
        }

        if (!StringUtils.equals(promoToken, inRedisPromoToken)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"秒杀令牌校验失败");
        }

        // 先判断该商品是否已售罄，若已卖完直接返回
        if (redisTemplate.hasKey("promo_item_id_over_"+itemId)) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //orderService.createOrderPromo(orderModel);
        //在下单之前，加入库存流水初始化
        String stockLog = itemService.initStockLog(itemId, amount);

        //再去完成对应的下单事务型消息机制
        boolean result = mqProducer.transactionAsyncReduceStock(orderModel, stockLog);
        if(!result) {
            throw new BusinessException(EmBusinessError.MQ_SEND_FAIL,"下单失败，库存不足或同步消息失败");
        }

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

    @UserLoginToken
    @GetMapping(value = "/payOrder")
    @ApiOperation(value = "支付订单")
    public CommonReturnType payOrder(@RequestParam(name = "orderId") String orderId) throws BusinessException {
        //判断用户是否登录
        checkUserLogin();
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("qrCode","");
        map.put("id","");
        return CommonReturnType.create(map);
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
        Page<OrderModel> modelList = orderService.getList(userId,page,size);
        //bean转换
        Page<OrderVo> orderVos = converToOderVoList(modelList);
        Map<String,Object> map = new HashMap<>();
        map.put("data",orderVos);
        map.put("total",orderVos.getTotal());
        return CommonReturnType.create(map);
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
    private OrderVo converToOderVo(OrderModel orderModel) throws BusinessException{
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
    private Page<OrderVo> converToOderVoList(Page<OrderModel> orderModels) throws BusinessException {
        //判空
        if(orderModels == null) {
            return null;
        }
        Page<OrderVo> orderVos = new Page<>();
        for (OrderModel orderModel : orderModels) {
            orderVos.add(converToOderVo(orderModel));
        }
        BeanUtils.copyProperties(orderModels,orderVos);
        return orderVos;
    }

}

