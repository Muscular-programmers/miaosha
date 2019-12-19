/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemService
 * Author:   俊哥
 * Date:     2019/6/12 17:22
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

import pers.jun.controller.viewObject.ItemVo;
import pers.jun.error.BusinessException;
import pers.jun.pojo.ItemScroll;
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.OrderItemModel;
import pers.jun.service.model.PromoModel;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //浏览商品
    List<ItemModel> getList();

    //通过分类查找查找商品
    List<ItemModel> getByCategory(Integer categoryId);

    //查看商品详情
    ItemModel getById(Integer id);

    //得到名称和活动（订单调用）
    ItemModel getNameAndPromo(Integer id);

    //更新库存
    //boolean decreaseStock(Integer itemId,Integer amount);
    boolean decreaseStock(List<OrderItemModel> orderItemModels);

    //更新销量
    boolean increaseSales(Integer id,Integer amount);

    //查询热门商品
    List<ItemModel> getPopular(int count);

    //根据活动进行查询商品
    List<ItemModel> getPromoItems(List<PromoModel> promoItems);

    //查询图片轮播的商品
    List<ItemScroll> getHomeScroll();

    //根据名字模糊查找
    List<ItemModel> getListByName(String key);
}
