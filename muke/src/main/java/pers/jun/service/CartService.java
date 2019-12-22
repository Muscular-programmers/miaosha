/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartService
 * Author:   俊哥
 * Date:     2019/7/14 20:15
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;
import pers.jun.error.BusinessException;
import pers.jun.service.model.CartModel;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/14
 * @since 1.0.0
 */
public interface CartService {

    //根据用户查询所有购物车条目
    List<CartModel> selectAll(Integer userId);

    //添加至购物车
    void addCart(Integer itemId,Integer amount,Integer userId) throws BusinessException;

    //编辑购物车条目数量
    int editCart(Integer userId, Integer itmeId, Integer productNum) throws BusinessException;

    //从购物车删除
    int deleteCart(Integer userId, Integer itemId) throws BusinessException;

    CartModel getCartByUserAndItem(Integer userId, Integer itemId);
}
