/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderItemVo
 * Author:   俊哥
 * Date:     2019/12/17 17:02
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/12/17
 * @since 1.0.0
 */
@Data
public class OrderItemVo {

    //
    private Integer id;

    //商品id
    private Integer itemId;

    //关于商品
    private String imgUrl;

    //商品名称
    private String title;

    //商品原来价格
    private BigDecimal price;

    //商品数量
    private Integer amount;

    //秒杀活动id
    private Integer promoId;

    //秒杀活动的价格
    //private BigDecimal promoPrice;

}