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
package com.jun.service.impl;

import com.jun.dao.OrderMapper;
import com.jun.dao.SequenceMapper;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.pojo.Order;
import com.jun.pojo.Sequence;
import com.jun.service.ItemService;
import com.jun.service.OrderService;
import com.jun.service.UserService;
import com.jun.service.model.ItemModel;
import com.jun.service.model.OrderModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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


    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        //参数校验
        if(userService.getUserById(userId) == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户不合法");
        }
        ItemModel itemModel = itemService.getById(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不合法");
        }
        if(amount < 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"下单数量不合法");
        }

        //落单减库存/还有一种方式为支付减库存
        boolean reslut = itemService.decreaseStock(itemId, amount);
        if(!reslut){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //校验活动是否正在进行
        if(promoId != null){
            if(itemModel.getPromoModel().getId() != promoId){
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");
            }
            if(itemModel.getPromoModel().getStatus() != 2){
                throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR,"活动信息不正确");
            }
        }

        //订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setUserId(userId);
        orderModel.setPromoId(promoId);
        orderModel.setAmount(amount);

        //如果活动正在进行，应该使价格为活动价格
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }
        else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));

        //交易流水号，生成id
        orderModel.setId(generateOrderNo());
        Order order = convertToOrder(orderModel);

        orderMapper.insertSelective(order);

        //更新商品销量
        boolean b = itemService.increaseSales(orderModel.getItemId(), orderModel.getAmount());
        if(!b){
            throw new BusinessException(EmBusinessError.ORDER_UNKOWN_ERROR);
        }

        //返回前端
        return orderModel;
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
        order.setItemPrice(orderModel.getItemPrice().doubleValue());
        order.setOrderPrice(orderModel.getOrderPrice().doubleValue());

        return order;
    }
}

