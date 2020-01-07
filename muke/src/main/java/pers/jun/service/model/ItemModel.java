/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemMode
 * Author:   俊哥
 * Date:     2019/6/12 16:57
 * Description: 商品模型
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈商品模型〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
@Data
public class ItemModel implements Serializable {
    private Integer itemId;

    //商品名称
    @NotBlank(message = "商品名称不能为空")
    private String title;

    //商品原价格
    @NotNull(message = "价格不能不填")
    @Min(value = 0,message = "价格不能小于0")
    private BigDecimal price;

    //商品活动价格 若存在活动则为活动价
    private BigDecimal promoPrice;

    //商品库存
    @NotNull(message = "库存不能不填")
    private Integer stock;

    //商品描述
    @NotBlank(message = "商品描述不能为空")
    private String description;

    //商品销量
    private Integer sales;

    //商品图片
    @NotBlank(message = "商品图片不能为空")
    private String imgUrl;

    //使用聚合模型，表示商品活动，如果不为空，则表示商品存在还未结束的活动
    private PromoModel promoModel;

}

