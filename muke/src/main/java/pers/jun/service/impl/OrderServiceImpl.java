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
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.OrderItemModel;
import pers.jun.service.model.OrderModel;
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


    /**
     * 创建订单
     */
    @Transactional
    public OrderModel createOrder(OrderModel orderModel) throws BusinessException {
        System.out.println(orderModel);
        // 1.参数校验
        if(orderModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

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
        //List<OrderItem> orderItems = orderModel.getOrderItems();
        List<OrderItemModel> orderItemModels = orderModel.getOrderItems();
        for (OrderItemModel orderItem : orderItemModels) {
            // 落单减库存/还有一种方式为支付减库存
            boolean decreaseStock = itemService.decreaseStock(orderItem.getItemId(),orderItem.getAmount());
            if(!decreaseStock){
                throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
            }
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
            int result = orderItemService.insertOrderItem(converrToOrderItem(orderItem));
            if(result < 1)
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单商品未知错误");

            // 5. 更新商品销量
            boolean increaseSales = itemService.increaseSales(orderItem.getItemId(), orderItem.getAmount());
            if(!increaseSales)
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"更新销量未知错误");

            // 6.从购物车删除
            System.out.println(orderModel.getUserId()+","+orderItem.getItemId());
            int deleteCart = cartService.deleteCart(orderModel.getUserId(), orderItem.getItemId());
            if(deleteCart < 1)
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"删除购物车未知错误");
        }
        return orderModel;

        //参数校验
        //if(userService.getUserById(userId) == null){
        //    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户不合法");
        //}
        //ItemModel itemModel = itemService.getById(itemId);
        //if(itemModel == null){
        //    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");
        //}
        //if(amount < 0 || amount > 99){
        //    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"下单数量不合法");
        //}
        //
        ////落单减库存/还有一种方式为支付减库存
        //boolean reslut = itemService.decreaseStock(itemId, amount);
        //if(!reslut){
        //    throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        //}
        //
        ////校验活动是否正在进行
        //if(promoId != null){
        //    if(itemModel.getPromoModel().getId() != promoId){
        //        throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");
        //    }
        //    if(itemModel.getPromoModel().getStatus() != 2){
        //        throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");
        //    }
        //}
        //
        ////订单入库
        //OrderModel orderModel = new OrderModel();
        //orderModel.setItemId(itemId);
        //orderModel.setUserId(userId);
        //orderModel.setPromoId(promoId);
        //orderModel.setAmount(amount);
        ////得到当前时间
        //Date date = new Date();
        //System.out.println(date);
        //DateTime now = new DateTime(date.getTime());//2013-01-14 22:45:36.484
        //System.out.println(now);
        //orderModel.setOrderTime(now);
        //
        ////如果活动正在进行，应该使价格为活动价格
        //if(promoId != null){
        //    orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        //}
        //else{
        //    orderModel.setItemPrice(itemModel.getPrice());
        //}
        //orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
        //
        ////交易流水号，生成id
        //orderModel.setId(generateOrderNo());
        //
        ////bean转换
        //Order order = convertToOrder(orderModel);
        //
        //orderMapper.insertSelective(order);
        //
        ////更新商品销量
        //boolean b = itemService.increaseSales(orderModel.getItemId(), orderModel.getAmount());
        //if(!b){
        //    throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR);
        //}
        //
        ////返回前端
        //return orderModel;
    }

    /**
     * 查询所有订单
     */
    public List<OrderModel> getList(Integer userId) {
        //查询订单信息
        List<OrderModel> orders = orderMapper.selectAll(userId);
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
        // 设置价格
        order.setTotalPrice(orderModel.getTotalPrice().doubleValue());
        // 如果ordermodel不存在完成时间，将完成时间设置为创建时间
        if(orderModel.getFinishDate() == null)
            order.setFinishDate(order.getCreateDate());
        return order;
    }

    /**
     * bean转换
     */
    private OrderModel convertToOrderModel(Order order){
        if(order == null)
            return null;
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(order,orderModel);
        // 设置价格
        orderModel.setTotalPrice(order.getTotalPrice());
        return orderModel;
    }
    private List<OrderModel> convertToOrderModelList(List<Order> orders){
        if(orders == null)
            return null;
        List<OrderModel> orderModels = new ArrayList<>();
        for (Order order : orders) {
            orderModels.add(convertToOrderModel(order));
        }
        return orderModels;
    }

    private OrderItem converrToOrderItem(OrderItemModel orderItemModel) {
        if(orderItemModel == null)
            return null;
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(orderItemModel,orderItem);
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

