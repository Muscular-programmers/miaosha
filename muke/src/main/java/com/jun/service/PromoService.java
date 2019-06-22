/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PromoService
 * Author:   俊哥
 * Date:     2019/6/17 15:06
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service;

import com.jun.service.model.PromoModel;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/17
 * @since 1.0.0
 */
public interface PromoService {

    PromoModel getPromoByItemId(Integer itemId);
}
