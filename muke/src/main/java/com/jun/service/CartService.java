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
package com.jun.service;
import com.jun.error.BusinessException;
import com.jun.service.model.CartModel;

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
}
