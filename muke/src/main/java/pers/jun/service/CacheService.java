/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CacheService
 * Author:   俊哥
 * Date:     2019/12/26 15:16
 * Description: 封装本地缓存
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

/**
 * 〈一句话功能简述〉<br> 
 * 〈封装本地缓存〉
 *
 * @author 俊哥
 * @create 2019/12/26
 * @since 1.0.0
 */
public interface CacheService {

    // 存方法
    void setCommonCache(String key,Object value);

    // 取方法
    Object getFromCache(String key);

}