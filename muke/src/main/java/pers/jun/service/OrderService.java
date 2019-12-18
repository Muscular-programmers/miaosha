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

    OrderModel createOrder(OrderModel orderModel) throws BusinessException;

    //查询所有订单
    List<OrderModel> getList(Integer userId);
}
