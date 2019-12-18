/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderModel
 * Author:   俊哥
 * Date:     2019/6/16 16:19
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import pers.jun.controller.viewObject.OrderItemVo;
import pers.jun.pojo.OrderItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/16
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    //订单id
    private String id;

    //用户id
    private Integer userId;

    //下单商品 orderItems
    private List<OrderItemModel> orderItems;

    //订单总价格
    private Double totalPrice;

    //下单时间
    private Date createDate;

    //订单完成时间
    private Date finishDate;

    //订单状态 1表示已付款 2表示已发货 3表示收获 4表示已售后退货 5表示完成 默认为0
    private Integer status = 0;

    //订单地址id
    private Integer address;

    ////下单数量
    //private Integer amount;

    ////秒杀活动id,如果promoId不为空，则价格为秒杀价格
    //private Integer promoId;
    //
    ////秒杀活动状态
    //private Integer promo_status;
    //
    ////下单时商品的价格,如果promoId不为空，则价格为秒杀价格
    //private BigDecimal itemPrice;
    //

    //
    ////下单总价,如果promoId不为空，则价格为秒杀价格
    //private  BigDecimal orderPrice;


}

