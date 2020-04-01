/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderService
 * Author:   俊哥
 * Date:     2019/6/16 16:51
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

import com.github.pagehelper.Page;
import pers.jun.error.BusinessException;
import pers.jun.service.model.OrderModel;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/16
 * @since 1.0.0
 */
public interface OrderService {

    /**
     * 创建订单，下单普通商品
     */
    void createOrder(OrderModel orderModel) throws BusinessException;

    /**
     * 创建订单，下单活动商品
     */
    void createOrderPromo(OrderModel orderModel,String stockLogId) throws BusinessException;

    /**
     * 查询所有订单
     */
    Page<OrderModel> getList(Integer userId, Integer page, Integer size) throws BusinessException;

    /**
     * 根据id查询订单
     */
    OrderModel orderById(String orderId) throws BusinessException;

    /**
     * 删除订单
     */
    void delOrder(String orderId) throws BusinessException;
}
