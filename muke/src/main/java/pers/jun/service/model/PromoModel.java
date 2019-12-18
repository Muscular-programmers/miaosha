/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PromoModel
 * Author:   俊哥
 * Date:     2019/6/17 14:20
 * Description: 秒杀模型
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈秒杀模型〉
 *
 * @author 俊哥
 * @create 2019/6/17
 * @since 1.0.0
 */
@Data
public class PromoModel {

    //秒杀活动id
    private Integer id;

    //秒杀活动名称
    private String promoName;

    //秒杀活动状态。0：没有活动 1：活动未开始 2：活动进行中 3：活动已结束
    private int status;

    //开始时间
    private DateTime startDate;

    //开始时间
    private DateTime endDate;

    //包含的商品（此处假设只有一个商品）
    private Integer itemId;

    //活动时商品的价格
    private BigDecimal promoItemPrice;


}

