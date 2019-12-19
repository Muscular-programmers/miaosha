/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderItemModel
 * Author:   俊哥
 * Date:     2019/12/16 13:38
 * Description: 订单商品
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单商品〉
 *
 * @author 俊哥
 * @create 2019/12/16
 * @since 1.0.0
 */
@Data
public class OrderItemModel {

    //订单商品列表id
    private Integer id;

    //订单商品id
    private Integer itemId;

    //商品数量
    private Integer amount;

    //商品价格（如果活动id不为空，则为活动价格）
    private BigDecimal price;

    //活动id（可能为空）
    private Integer promoId;

    //所属订单id
    private String orderId;

    //关于商品
    private String imgUrl;

    //商品名称
    private String title;

}