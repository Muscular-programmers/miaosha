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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.tools.corba.se.idl.constExpr.Or;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import pers.jun.controller.viewObject.OrderVo;
import pers.jun.dao.OrderItemMapper;
import pers.jun.dao.OrderMapper;
import pers.jun.dao.SequenceMapper;
import pers.jun.dao.StockLogMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.pojo.*;
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

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private StockLogMapper stockLogMapper;


    /**
     * 创建订单，此方法用于普通商品和购物车商品
     * Transactional:https://www.jianshu.com/p/9098372c108a
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderModel orderModel) throws BusinessException {
        System.out.println(orderModel);

        // 用户合法性验证
        UserModel userModel = userService.getUserByIdIncache(orderModel.getUserId());
        if(userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不合法");
        }

        // 商品合法性验证
        List<OrderItemModel> orderItemModels = orderModel.getOrderItems();
        for (OrderItemModel orderItemModel : orderItemModels) {
            if(itemService.getByIdIncache(orderItemModel.getItemId()) == null) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");
            }
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
        if(insert < 1) {
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单未知错误");
        }

        // 3.减库存 AND 添加订单item AND 更新商品销量 AND 从购物车删除

        // 3.减库存
        boolean decreaseResult = itemService.decreaseStock(orderItemModels);
        if(!decreaseResult) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

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
            orderItemService.insertOrderItem(converrToOrderItem(orderItem));

            // 5. 更新商品销量
            itemService.increaseSales(orderItem.getItemId(), orderItem.getAmount());

            // 6.从购物车删除
            CartModel cartModel = cartService.getCartByUserAndItem(orderModel.getUserId(),orderItem.getItemId());
            if(cartModel != null){
                int deleteCart = cartService.deleteCart(orderModel.getUserId(), orderItem.getItemId());
                if(deleteCart < 1) {
                    throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"删除购物车未知错误");
                }
            }

        }
    }

    /**
     * 秒杀活动商品下单操作
     */
    @Override
    public void createOrderPromo(OrderModel orderModel, String stockLogId) throws BusinessException {
        // 用户合法性验证
        //UserModel userModel = userService.getUserByIdIncache(orderModel.getUserId());
        //if(userModel == null)
        //    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不合法");

        // 商品合法性验证，同时之能秒杀一个商品
        OrderItemModel orderItemModel = orderModel.getOrderItems().get(0);
        Integer itemId = orderItemModel.getItemId();
        Integer amount = orderItemModel.getAmount();
        //if(itemService.getByIdIncache(itemId) == null)
        //    throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");

        //默认每个用户只能下单十个
        if(amount < 0 || amount > 10){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"下单数量不合法");
        }

        //校验活动是否正在进行，这里必须是活动中商品
        //if(orderItemModel.getPromoId() == null)
        //    throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");
        //
        ////获取订单中这个商品的活动信息，如果这个商品没有活动或者活动状态不为“正在活动中”则不合法
        PromoModel promoByItemId = promoService.getPromoByItemId(itemId);
        //if(promoByItemId == null || promoByItemId.getStatus() != 2)
        //    throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");

        //落单减库存/还有一种方式为支付减库存
        boolean result = itemService.decreaseStockIncache(itemId, amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //设置订单默认状态和入库时间
        orderModel.setStatus(0);
        orderModel.setCreateDate(new Date());

        // 交易流水号，生成id
        String generateOrderNo = generateOrderNo();
        orderModel.setId(generateOrderNo);
        // 订单入库
        int insert = orderMapper.insertSelective(convertToOrder(orderModel));
        if(insert < 1) {
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"添加订单未知错误");
        }


        //添加订单条目

        // 设置所属订单id
        orderItemModel.setOrderId(generateOrderNo);

        // 设置价格
        orderItemModel.setPrice(promoByItemId.getPromoItemPrice().doubleValue());

        // 4.添加订单item
        orderItemService.insertOrderItem(converrToOrderItem(orderItemModel));

        // 5.更新商品销量
        itemService.increaseSales(itemId, amount);

        //if(true)
        //throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);

        // 6.将库存流水状态更新为2，即下单成功
        StockLog stockLog = stockLogMapper.selectByPrimaryKey(stockLogId);
        if(stockLog == null) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        stockLog.setStatus(2);
        stockLogMapper.updateByPrimaryKeySelective(stockLog);



        //try {
        //    Thread.sleep(3000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}

        // 订单入库的所有操作都完成以后，再通过rocketMQ发送消息异步更新数据库库存
        // 所有操作完成以后，事务提交也有可能会出错，所以可以将rocketMQ消息放在事务提交以后再发送
        // springboot的@Transaction也提供给了我们这样的操作----TransactionSynchronizationManager
        //TransactionSynchronizationManager.regiasyncReduceStocksterSynchronization(new TransactionSynchronizationAdapter() {
        //    @Override
        //    // 这个方法会在最近的@Transaction成功commit后才会去执行
        //    public void afterCommit() {
        //        boolean decreaseStock = itemService.asyncDecreaseStock(itemId, amount1);
        //        // 但是如果发送失败，也就没办法回滚了
        //        //if (!decreaseStock) {
        //        //    itemService.increaseStock(itemId, amount1);
        //        //    throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
        //        //}
        //    }
        //});


    }

    /**
     * 查询所有订单
     */
    @Override
    public Page<OrderModel> getList(Integer userId, Integer page, Integer size) throws BusinessException {
        if(page == null || size == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"分页参数不合法");
        }
        //查询订单信息
        PageHelper.startPage(page,size);
        PageHelper.orderBy("create_date desc");
        Page<Order> orders = orderMapper.selectAll(userId);

        //为每个订单设置其订单条目
        Page<OrderModel> orderModels = new Page<>();
        for (Order order : orders) {
            //根据订单id查询订单所包含的商品
            List<OrderItem> itemByOrderId = orderItemService.getItemByOrderId(order.getId());
            //bean转换
            List<OrderItemModel> orderItemModels = convertToItemModelList(itemByOrderId);

            OrderModel orderModel = convertToOrderModel(order, orderItemModels);
            orderModels.add(orderModel);
        }
        BeanUtils.copyProperties(orders,orderModels);
        return orderModels;
    }

    /**
     * 根据id查询id
     */
    @Override
    public OrderModel orderById(String orderId) throws BusinessException {
        if(StringUtils.isBlank(orderId)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        OrderModel orderAndDetail = orderMapper.getOrderAndDetail(orderId);
        List<OrderItemModel> orderItems = orderAndDetail.getOrderItems();
        for (OrderItemModel orderItem : orderItems) {
            orderItem.setImgUrl(orderItem.getImgUrl().split("\\*\\*\\*")[0]);
        }
        return orderAndDetail;
    }

    /**
     * 删除订单
     */
    @Override
    public void delOrder(String orderId) throws BusinessException {
        if(StringUtils.isBlank(orderId)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        int result = orderMapper.deleteByPrimaryKey(orderId);
        if(result < 1) {
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR);
        }
    }


    /**
     * 生成订单编号
     * 此处的propagation = Propagation.REQUIRES_NEW表示：即使这个私有方法隶属于上面的createOrder这个标注了事务的方法中，
     * 但是由于注解使得不管上面方法是否执行成功，我对应的事务执行成功就会提交掉
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo(){
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

        //3.最后两位为分库分表位（写死，暂时不开发）
        stringBuilder.append(00);

        return stringBuilder.toString();
    }

    /**
     * bean转换
     */
    private Order convertToOrder(OrderModel orderModel){
        if(orderModel == null) {
            return null;
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderModel,order);
        // 设置创建时间为当前时间
        order.setCreateDate(orderModel.getCreateDate());
        // 设置订单状态为1
        order.setStatus(1);
        // 如果ordermodel不存在完成时间，将完成时间设置为创建时间
        if(orderModel.getFinishDate() == null) {
            order.setFinishDate(order.getCreateDate());
        }
        return order;
    }

    private OrderItem converrToOrderItem(OrderItemModel orderItemModel) {
        if(orderItemModel == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(orderItemModel,orderItem);
        orderItem.setPrice(orderItemModel.getPrice());
        return orderItem;
    }

    /**
     * bean转换
     */
    private OrderModel convertToOrderModel(Order order,List<OrderItemModel> orderItemModels){
        if(order == null || orderItemModels == null) {
            return null;
        }
        OrderModel orderModel = new OrderModel();

        BeanUtils.copyProperties(order,orderModel);

        orderModel.setOrderItems(orderItemModels);

        //orderModel.setItemPrice(BigDecimal.valueOf(order.getItemPrice()));
        //orderModel.setOrderPrice(BigDecimal.valueOf(order.getOrderPrice()));
        //
        ////设置下单时间
        //orderModel.setOrderTime(new DateTime(order.getOrderTime()));
        //
        ////设置名称和活动状态
        //if(itemModel.getPromoModel() != null)
        //    orderModel.setStatus(itemModel.getPromoModel().getStatus());
        //else
        //    orderModel.setStatus(0);
        //orderModel.setTitle(itemModel.getTitle());

        return orderModel;
    }

    private List<OrderItemModel> convertToItemModelList(List<OrderItem> orderItems){
        if(orderItems == null){
            return null;
        }
        List<OrderItemModel> orderItemModels = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemModel orderItemModel = new OrderItemModel();
            BeanUtils.copyProperties(orderItem,orderItemModel);
            //得到商品信息
            ItemModel byId = itemService.getById(orderItem.getItemId());
            //设置商品图片（第一张）
            orderItemModel.setImgUrl(byId.getImgUrl().split("\\*\\*\\*")[0]);
            //设置商品名称
            orderItemModel.setTitle(byId.getTitle());
            orderItemModels.add(orderItemModel);
        }
        return orderItemModels;
    }
}

