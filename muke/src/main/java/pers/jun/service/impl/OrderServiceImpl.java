/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderServiceImpl
 * Author:   俊哥
 * Date:     2019/6/16 16:52
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.impl;

import com.sun.tools.corba.se.idl.constExpr.Or;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import pers.jun.dao.OrderMapper;
import pers.jun.dao.SequenceMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.pojo.Order;
import pers.jun.pojo.OrderItem;
import pers.jun.pojo.Sequence;
import pers.jun.response.CommonReturnType;
import pers.jun.service.*;
import pers.jun.service.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/16
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SequenceMapper sequenceMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PromoService promoService;


    /**
     * 创建订单，此方法用于普通商品和购物车商品
     */
    @Transactional
    public void createOrder(OrderModel orderModel) throws BusinessException {
        System.out.println(orderModel);

        // 用户合法性验证
        UserModel userModel = userService.getUserByIdIncace(orderModel.getUserId());
        if(userModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不合法");

        // 商品合法性验证
        List<OrderItemModel> orderItemModels = orderModel.getOrderItems();
        for (OrderItemModel orderItemModel : orderItemModels) {
            if(itemService.getByIdIncache(orderItemModel.getItemId()) == null)
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");
        }

        // 2.添加订单
        // 默认订单状态为0
        orderModel.setStatus(0);
        orderModel.setCreateDate(new Date());

        // 交易流水号，生成id
        String generateOrderNo = generateOrderNo();
        orderModel.setId(generateOrderNo);
        System.out.println(convertToOrder(orderModel));

        int insert = orderMapper.insertSelective(convertToOrder(orderModel));
        if(insert < 1)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单未知错误");

        // 3.减库存 AND 添加订单item AND 更新商品销量 AND 从购物车删除

        // 3.减库存
        boolean decreaseResult = itemService.decreaseStock(orderItemModels);
        if(!decreaseResult)
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);

        for (OrderItemModel orderItem : orderItemModels) {
            // 设置所属订单id
            orderItem.setOrderId(generateOrderNo);
            // 根据商品id得到商品，判断商品是否处于活动当中
            ItemModel itemModel = itemService.getById(orderItem.getItemId());
            if(itemModel.getPromoModel() != null){
                // 设置商品活动id
                orderItem.setPromoId(itemModel.getPromoModel().getId());
                // 设置商品活动价格
                orderItem.setPrice(itemModel.getPrice().doubleValue());
            }
            // 4.添加订单item
            int result = orderItemService.insertOrderItem(converrToOrderItem(orderItem));
            if(result < 1)
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单商品未知错误");

            // 5. 更新商品销量
            boolean increaseSales = itemService.increaseSales(orderItem.getItemId(), orderItem.getAmount());
            if(!increaseSales)
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"更新销量未知错误");

            // 6.从购物车删除
            CartModel cartModel = cartService.getCartByUserAndItem(orderModel.getUserId(),orderItem.getItemId());
            if(cartModel != null){
                int deleteCart = cartService.deleteCart(orderModel.getUserId(), orderItem.getItemId());
                if(deleteCart < 1)
                    throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"删除购物车未知错误");
            }

        }
    }

    /**
     * 秒杀活动商品下单操作
     */
    public void createOrderPromo(OrderModel orderModel) throws BusinessException {
        // 用户合法性验证
        UserModel userModel = userService.getUserByIdIncace(orderModel.getUserId());
        if(userModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不合法");

        // 商品合法性验证，同时之能秒杀一个商品
        OrderItemModel orderItemModel = orderModel.getOrderItems().get(0);
        if(itemService.getByIdIncache(orderItemModel.getItemId()) == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");

        //默认每个用户只能下单十个
        int amount = orderItemModel.getAmount();
        if(amount < 0 || amount > 10){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"下单数量不合法");
        }

        //落单减库存/还有一种方式为支付减库存
        boolean reslut = itemService.decreaseStockIncache(orderItemModel.getItemId(), amount);
        if(!reslut){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //校验活动是否正在进行，这里必须是活动中商品
        if(orderItemModel.getPromoId() == null)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");

        //获取订单中这个商品的活动信息，如果这个商品没有活动或者活动状态不为“正在活动中”则不合法
        PromoModel promoByItemId = promoService.getPromoByItemId(orderItemModel.getItemId());
        if(promoByItemId == null || promoByItemId.getStatus() != 2)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");

        //设置订单默认状态和入库时间
        orderModel.setStatus(0);
        orderModel.setCreateDate(new Date());

        // 交易流水号，生成id
        String generateOrderNo = generateOrderNo();
        orderModel.setId(generateOrderNo);
        // 订单入库
        int insert = orderMapper.insertSelective(convertToOrder(orderModel));
        if(insert < 1)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单未知错误");


        //添加订单条目

        // 设置所属订单id
        orderItemModel.setOrderId(generateOrderNo);

        // *****这个有没有必要
        orderItemModel.setPrice(promoByItemId.getPromoItemPrice().doubleValue());

        // 4.添加订单item
        int result = orderItemService.insertOrderItem(converrToOrderItem(orderItemModel));
        if(result < 1)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单商品未知错误");

        // 5. 更新商品销量
        boolean increaseSales = itemService.increaseSales(orderItemModel.getItemId(), orderItemModel.getAmount());
        if(!increaseSales)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"更新销量未知错误");

    }

    /**
     * 查询所有订单
     */
    public List<OrderModel> getList(Integer userId,Integer page,Integer size) throws BusinessException {
        if(page == null || size == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"分页参数不合法");
        //查询订单信息
        // 得到关于分页的参数：(page - 1) * size & size
        int temp = (page - 1) * size;
        temp = temp > 0 ? temp : 0;
        System.out.println(userId + "," + temp + "," + size);
        List<OrderModel> orders = orderMapper.selectAll(userId,temp,size);
        // 关于图片，只要第一张
        for (OrderModel order : orders) {
            List<OrderItemModel> orderItems = order.getOrderItems();
            for (OrderItemModel orderItem : orderItems) {
                orderItem.setImgUrl(orderItem.getImgUrl().split(",")[0]);
            }
        }
        System.out.println(orders.toString());
        return orders;

        //------------------teacher
        //return convertToOrderModelList(orders);
        //List<OrderModel> orderModels = new ArrayList<>();
        //for (Order order : orders) {
        //
        //    ItemModel nameAndPromo = itemService.getNameAndPromo(order.getItemId());
        //    //OrderModel orderModel = convertToOrderModel(order, nameAndPromo);
        //    orderModels.add(orderModel);
        //}
        //
        //return orderModels;
    }

    /**
     * 根据id查询id
     */
    public OrderModel orderById(String orderId) throws BusinessException {
        if(StringUtils.isBlank(orderId))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        OrderModel orderAndDetail = orderMapper.getOrderAndDetail(orderId);
        return orderAndDetail;
    }

    /**
     * 删除订单
     */
    public void delOrder(String orderId) throws BusinessException {
        if(StringUtils.isBlank(orderId))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        int result = orderMapper.deleteByPrimaryKey(orderId);
        if(result < 1)
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR);
    }


    //此处的propagation = Propagation.REQUIRES_NEW表示：即使这个私有方法隶属于上面的createOrder这个标注了事务的方法中，
    // 但是由于注解使得不管上面方法是否执行成功，我对应的事务执行成功就会提交掉
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo(){
        StringBuilder stringBuilder = new StringBuilder();
        //订单16位
        //1.前八位为时间
        LocalDateTime now = LocalDateTime.now();
        //now.format(DateTimeFormatter.ISO_DATE)输出为2019-6-16
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");

        stringBuilder.append(nowDate);

        //2.中间6位为自增序列
        int sequence = 0;
        Sequence sequenceByName = sequenceMapper.getSequenceByName("order_info");
        sequence = sequenceByName.getCurrentValue();

        sequenceByName.setCurrentValue(sequence + sequenceByName.getStep());
        sequenceMapper.updateByPrimaryKeySelective(sequenceByName);

        //拼接成6位
        String sequenceStr = String.valueOf(sequence);
        for (int i = 0; i < (6-sequenceStr.length()); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        //3.最后两位为分库分表位（写死，暂时不讨论）
        stringBuilder.append(00);

        return stringBuilder.toString();
    }

    /**
     * bean转换
     * @param orderModel
     * @return
     */
    private Order convertToOrder(OrderModel orderModel){
        if(orderModel == null)
            return null;
        Order order = new Order();
        BeanUtils.copyProperties(orderModel,order);
        // 设置创建时间为当前时间
        order.setCreateDate(orderModel.getCreateDate());
        // 设置订单状态为1
        order.setStatus(1);
        // 如果ordermodel不存在完成时间，将完成时间设置为创建时间
        if(orderModel.getFinishDate() == null)
            order.setFinishDate(order.getCreateDate());
        return order;
    }


    private OrderItem converrToOrderItem(OrderItemModel orderItemModel) {
        if(orderItemModel == null)
            return null;
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(orderItemModel,orderItem);
        orderItem.setPrice(orderItemModel.getPrice());
        return orderItem;
    }

    /**
     * bean转换
     */
    //private OrderModel convertToOrderModel(Order order,ItemModel itemModel){
    //    if(order == null || itemModel == null)
    //        return null;
    //    OrderModel orderModel = new OrderModel();
    //
    //    BeanUtils.copyProperties(order,orderModel);
    //
    //    orderModel.setItemPrice(BigDecimal.valueOf(order.getItemPrice()));
    //    orderModel.setOrderPrice(BigDecimal.valueOf(order.getOrderPrice()));
    //
    //    //设置下单时间
    //    orderModel.setOrderTime(new DateTime(order.getOrderTime()));
    //
    //    //设置名称和活动状态
    //    if(itemModel.getPromoModel() != null)
    //        orderModel.setStatus(itemModel.getPromoModel().getStatus());
    //    else
    //        orderModel.setStatus(0);
    //    orderModel.setTitle(itemModel.getTitle());
    //
    //    return orderModel;
    //}
}

