/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserService
 * Author:   俊哥
 * Date:     2019/6/5 21:35
 * Description: 用户服务层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service;

import pers.jun.error.BusinessException;
import pers.jun.service.model.UserModel;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户服务层〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
public interface UserService {

    UserModel getUserById(Integer id);

    // 从缓存中查询用户
    UserModel getUserByIdIncache(Integer userId);

    void register(UserModel userModel) throws BusinessException;

    UserModel validataLogin(String telephone,String password) throws BusinessException;

    //保存验证码
    void saveOpt(String phone,String opt);

    boolean checkOpt(String phone,String opt) throws BusinessException;


}
