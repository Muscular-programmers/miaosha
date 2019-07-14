/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserController
 * Author:   俊哥
 * Date:     2019/6/5 21:50
 * Description: 用户表现层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import com.alibaba.druid.util.StringUtils;
import com.jun.controller.viewObject.UserVo;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.response.CommonReturnType;
import com.jun.service.model.UserModel;
import com.jun.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户表现层〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */

@RestController
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpRequest;


    /**
     * 用户登录
     * @param telephone
     * @param password
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/login",consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public Object login(@RequestParam(name = "telephone")String telephone,
                        @RequestParam(name = "password")String password) throws BusinessException {
        //参数判断
        if(StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"参数不能为空！");
        }

        //用户登录服务，校验用户登录是否合法
        UserModel userModel = userService.validataLogin(telephone, password);

        //将用户登录凭证添加到用户session
        httpRequest.getSession().setAttribute("IS_LOGIN",true);
        httpRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }

    /**
     *用户注册
     * @return
     */
    @RequestMapping(value = "/register",consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public Object register(UserModel userModel,@RequestParam(name = "otpCode")String optCode) throws BusinessException {
        //验证输入验证码是否正确
        /*String sessionOtpCode = (String) httpRequest.getSession().getAttribute(userModel.getTelephone());
        if(!StringUtils.equals(sessionOtpCode,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }*/

        userService.checkOpt(userModel.getTelephone(), optCode);

        //执行注册操作
        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/getopt",consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public Object getopt(@RequestParam(name="telephone")String telephone){
        //生成验证码
        Random random = new Random();
        int number = random.nextInt(9000);
        number += 1000;
        String opt = String.valueOf(number);

        //将生成的验证码保存到redis，并设置过期时间
        userService.saveOpt(telephone,opt);

        //与手机号码绑定（手机号与验证码绑定以键值对的形式绑定，适合与redis，此处只用httpSession模拟）
        //httpRequest.getSession().setAttribute(telephone,opt);
        System.out.println("手机号："+telephone+"，验证码为："+opt);

        //通过短信通道发送给用户（省略）

        return CommonReturnType.create(null);
    }


    @RequestMapping(value = "/get")
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        UserVo userVo =  converFromUserModel(userModel);

        if(userVo == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //返回通用的字段
        return CommonReturnType.create(userVo);
    }

    /**
     * md5加密字符串
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    private UserVo converFromUserModel(UserModel userModel){
        if(userModel == null)
            return null;
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userModel,userVo);
        return userVo;
    }


}

