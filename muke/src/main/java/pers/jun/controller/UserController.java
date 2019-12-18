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
package pers.jun.controller;

import com.alibaba.druid.util.StringUtils;
import io.swagger.annotations.*;
import pers.jun.config.UserLoginToken;
import pers.jun.controller.viewObject.UserVo;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.interceptor.AuthenticationInterceptor;
import pers.jun.response.CommonReturnType;
import pers.jun.service.model.UserModel;
import pers.jun.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pers.jun.util.JwtUtil;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
@Api(tags = "用户管理模块")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpRequest;

    /**
     * 用户登录
     */
    @PostMapping(value = "/login")
    @ApiOperation(value = "用户登录")
    public Object login(@RequestBody UserModel loginModel) throws BusinessException {
        String telephone = loginModel.getTelephone();
        String password = loginModel.getPassword();
        //参数判断
        if(StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"参数不能为空！");
        }

        //用户登录服务，校验用户登录是否合法
        UserModel userModel = userService.validataLogin(telephone, password);

        //得到token
        String token = JwtUtil.getToken(userModel);

        //将用户登录凭证添加到用户session
        //httpRequest.getSession().setAttribute("IS_LOGIN",true);
        //httpRequest.getSession().setAttribute("LOGIN_USER",userModel);

        UserVo userVo = converFromUserModel(userModel);
        HashMap<String,Object> map = new HashMap<>();
        map.put("user",userVo);
        map.put("token",token);
        return CommonReturnType.create(map);
    }

    /**
     *用户注册
     */
    @PostMapping(value = "/register",consumes = {CONTENT_TYPE_FORMED})
    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userModel",value = "用户信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "otpCode",value = "验证码",required = true,paramType = "query")
    })
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


    /**
     * checkLogin
     */
    @UserLoginToken
    @PostMapping("/checkLogin")
    @ApiOperation(value = "checkLogin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token",value = "用户token",required = true,paramType = "query")
    })
    public Object checkLogin(@RequestParam(name = "token") String token) throws BusinessException {
        UserModel userModel = AuthenticationInterceptor.userModelByToken;
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        UserVo userVo = converFromUserModel(userModel);
        return CommonReturnType.create(userVo);
    }

    /**
     * 生成验证码
     */
    @PostMapping(value = "/getopt",consumes = {CONTENT_TYPE_FORMED})
    @ApiOperation(value = "生成验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "telephone",value = "注册手机号",required = true,paramType = "query")
    })
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


    /**
     * 通过用户id获取用户详情
     */
    @PostMapping(value = "/get")
    @ApiOperation(value = "通过用户id获取用户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "query")
    })
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
     * 验证滑块
     * @return
     */
    @PostMapping(value = "/geetestInit")
    @ApiOperation(value = "验证滑块")
    public Object geetestInit() {
        HashMap<String,Object > map = new HashMap<>();
        map.put("challenge","8f491b99a96ccd6ec5f64d023bf90fd9");
        map.put("statusKey","361ffa35-920f-4c78-9129-0acc7f9e9c40");
        map.put("gt","fe8371f3c7f748bad58c2d1d9b110735");
        map.put("success",1);
        return map;
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

