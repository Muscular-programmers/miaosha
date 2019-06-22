/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserServiceImpl
 * Author:   俊哥
 * Date:     2019/6/5 21:38
 * Description: 用户服务层实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.impl;

import com.jun.dao.UserMapper;
import com.jun.dao.UserPasswordMapper;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.pojo.User;
import com.jun.pojo.UserPassword;
import com.jun.service.model.UserModel;
import com.jun.service.UserService;
import com.jun.validation.ValidationResult;
import com.jun.validation.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 〈一句话功能简述〉<br>
 * 〈用户服务层实现类〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPasswordMapper passwordMapper;

    @Autowired
    private ValidatorImpl validator;
    /**
     * 通过uid查询
     * @param id
     * @return
     */
    public UserModel getUserById(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);

        UserPassword password = passwordMapper.selectByUserId(id);
        return convertFromUser(user,password);

    }

    /**
     * 注册
     * @param userModel
     */
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        //参数校验
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

        }

        ValidationResult result = validator.validate(userModel);
        if(result.isHasErr()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //插入到用户表
        User user = convertFromUserModel(userModel);
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号重复注册！");
        }

        userModel.setId(user.getId());

        //插入到密码表
        UserPassword password = convertpassFromUsermodel(userModel);
        passwordMapper.insertSelective(password);

        return;
    }

    /**
     * 验证登录
     * @param telephone
     * @param password
     */
    public UserModel validataLogin(String telephone, String password) throws BusinessException {
        //根据用户电话拿到用户信息
        User user = userMapper.selectByTelephone(telephone);

        if(user == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户手机号或密码错误！");
        }
        //根据用户id拿到用户密码
        UserPassword userPassword = passwordMapper.selectByUserId(user.getId());
        UserModel userModel = convertFromUser(user,userPassword);

        //根据用户信息判断密码是否正确
        if(!StringUtils.equals(password,userPassword.getPassword())){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户手机号或密码错误！");
        }
        return userModel;
    }

    /**
     * 两个bean之间的属性映射
     * @param userModel
     * @return
     */
    private User convertFromUserModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        User user= new User();
        BeanUtils.copyProperties(userModel,user);

        return user;
    }

    /**
     * 两个bean之间的属性映射
     * @param userModel
     * @return
     */
    private UserPassword convertpassFromUsermodel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPassword userPassword = new UserPassword();
        userPassword.setPassword(userModel.getPassword());
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }

    /**
     * 正常的服务层操作（视频中由于用户信息和用户密码在两个不同的表，所以在service层必须返回一个信息完整的user
     * 故用一个新的类UserModel将user和password的信息合并在一起
     * @param user
     * @return
     */
    private UserModel convertFromUser(User user,UserPassword password){
        if(user == null)
            return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(user,userModel);
        if(password != null){
            userModel.setPassword(password.getPassword());
        }

        return userModel;
    }
}

