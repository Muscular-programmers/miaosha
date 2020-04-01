/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderItemService
 * Author:   俊哥
 * Date:     2019/12/16 14:41
 * Description: 订单商品
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

import pers.jun.controller.viewObject.OrderItemVo;
import pers.jun.pojo.OrderItem;
import pers.jun.service.model.OrderItemModel;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单商品〉
 *
 * @author 俊哥
 * @create 2019/12/16
 * @since 1.0.0
 */
public interface OrderItemService {

    /**
     * 添加订单商品
     */
    int insertOrderItem(OrderItem orderItem);

    List<OrderItem> getItemByOrderId(String orderId);
}