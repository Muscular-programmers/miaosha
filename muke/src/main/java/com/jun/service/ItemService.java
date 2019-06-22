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
package com.jun.service;

import com.jun.error.BusinessException;
import com.jun.service.model.ItemModel;

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

    //查看商品详情
    ItemModel getById(Integer id);

    //更新库存
    boolean decreaseStock(Integer itemId,Integer amount);

    //更新销量
    boolean increaseSales(Integer id,Integer amount);
}
