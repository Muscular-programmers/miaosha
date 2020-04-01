/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderItemServiceImpl
 * Author:   俊哥
 * Date:     2019/12/16 14:44
 * Description: 订单商品实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pers.jun.controller.viewObject.OrderItemVo;
import pers.jun.dao.ItemMapper;
import pers.jun.dao.OrderItemMapper;
import pers.jun.pojo.Item;
import pers.jun.pojo.OrderItem;
import pers.jun.service.model.OrderItemModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单商品实现类〉
 *
 * @author 俊哥
 * @create 2019/12/16
 * @since 1.0.0
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public int insertOrderItem(OrderItem orderItem) {
        return orderItemMapper.insertSelective(orderItem);
    }

    /**
     * 根据订单id查询所有订单商品
     */
    @Override
    public List<OrderItem> getItemByOrderId(String orderId) {
        List<OrderItem> orderItems = orderItemMapper.getItemByOrderId(orderId);
        //List<OrderItemVo> orderItemVos = convertToVoList(orderItems);
        return orderItems;
    }

    /**
     * bean转换
     */
    private List<OrderItemVo> convertToVoList(List<OrderItem> orderItems) {
        if(orderItems == null)
            return null;
        List<OrderItemVo> itemVos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            itemVos.add(convertToOrderVo(orderItem));
        }
        return itemVos;
    }

    private OrderItemVo convertToOrderVo(OrderItem orderItem) {
        if(orderItem == null)
            return null;
        OrderItemVo itemVo = new OrderItemVo();
        BeanUtils.copyProperties(orderItem,itemVo);
        itemVo.setPrice(new BigDecimal(orderItem.getPrice()));
        Item item = itemMapper.selectByPrimaryKey(orderItem.getItemId());
        itemVo.setImgUrl(item.getImgUrl());
        itemVo.setTitle(item.getTitle());
        return itemVo;
    }

    /**
     * bean转换
     */
    private OrderItemModel convertToOrderItemModel(OrderItem orderItem) {
        if(orderItem == null)
            return null;
        OrderItemModel orderItemModel = new OrderItemModel();
        BeanUtils.copyProperties(orderItem,orderItemModel);
        orderItemModel.setPrice(orderItem.getPrice());
        return orderItemModel;
    }

}