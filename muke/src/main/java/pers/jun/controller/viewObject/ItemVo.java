/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemVo
 * Author:   俊哥
 * Date:     2019/6/12 18:59
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
@Data
public class ItemVo {

    //商品id，同id
    private Integer itemId;

    //商品名称
    private String title;

    //商品价格
    private BigDecimal price;

    //商品活动价格
    private BigDecimal promoPrice;

    //商品库存
    private Integer stock;

    //商品描述
    private String description;

    //商品销量
    private Integer sales;

    //商品图片
    private List<String> imgUrls;

    //商品图片（封面）
    private String imgUrl;

    //秒杀活动id
    private Integer promoId;

    //秒杀活动状态。0：没有活动 1：活动未开始 2：活动进行中 3：活动已结束
    private Integer status;

    //秒杀活动开始时间
    private String startDate;

    //秒杀活动的价格
    //private BigDecimal promoPrice;

    //秒杀结束时间
    private String endDate;

    //服务器当前时间


    //前端需要
    private int limitNum = 10;


}

