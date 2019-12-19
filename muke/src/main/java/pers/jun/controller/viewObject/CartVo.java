/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartVo
 * Author:   俊哥
 * Date:     2019/7/14 20:39
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

import lombok.Data;
import pers.jun.pojo.Item;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/14
 * @since 1.0.0
 */
@Data
public class CartVo {

    /**
     id
     */
    private Integer id;

    //商品id
    private Integer itemId;

    //关于商品
    private String imgUrl;

    //商品名称
    private String title;

    //商品价格
    private BigDecimal price;

    //商品数量
    private Integer amount;

    //秒杀活动id
    private Integer promoId;

    //秒杀活动的价格
    //private BigDecimal promoPrice;

    /**
     购物车商品
     */
    //private Item item;

    //前端商品默认是否选中
    private String checked = "1";

    //前端购物车可添加最大数值
    private int limitNum = 10;




}

