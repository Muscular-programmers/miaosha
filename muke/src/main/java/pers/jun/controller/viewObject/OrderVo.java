/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderVo
 * Author:   俊哥
 * Date:     2019/7/11 16:13
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

import lombok.Data;
import org.joda.time.DateTime;
import pers.jun.pojo.Address;
import pers.jun.pojo.OrderItem;
import pers.jun.service.model.AddressModel;
import pers.jun.service.model.OrderItemModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/11
 * @since 1.0.0
 */
@Data
public class OrderVo {

    //订单id
    private String id;

    //下单商品
    private List<OrderItemModel> orderItems;

    //订单总价格
    private BigDecimal totalPrice;

    //下单时间
    private String createDate;

    //订单完成时间
    private String finishDate;

    //订单状态 1表示已付款 2表示已发货 3表示收获 4表示已售后退货 5表示完成 默认为0
    private Integer status = 0;

    //订单地址
    private AddressModel address;

    ////下单数量
    //private Integer amount;

    ////商品id
    //private Integer itemId;
    //
    ////商品名称
    //private String title;
    //
    ////下单时间
    //private String orderTime;
    //
    ////秒杀活动id,如果promoId不为空，则价格为秒杀价格
    //private Integer promoId;
    //
    ////秒杀活动状态
    //private Integer status;
    //
    ////下单时商品的价格,如果promoId不为空，则价格为秒杀价格
    //private BigDecimal itemPrice;
    //

    //
    ////下单总价,如果promoId不为空，则价格为秒杀价格
    //private  BigDecimal orderPrice;


}

