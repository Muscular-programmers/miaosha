/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: JwtUtil
 * Author:   俊哥
 * Date:     2019/12/9 9:09
 * Description: 生成token 的工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.jun.error.EmBusinessError;
import pers.jun.service.UserService;
import pers.jun.service.model.UserModel;

import javax.servlet.http.HttpServletRequest;


/**
 * 〈一句话功能简述〉<br> 
 * 〈生成token 的工具类〉
 *
 * @author 俊哥
 * @create 2019/12/9
 * @since 1.0.0
 */
public class JwtUtil {

    //
    //final static String base64EncodedSecretKey = "你的私钥";//私钥
    //final static long TOKEN_EXP = 1000 * 60 * 10;//过期时间,测试使用十分钟

    /**
     * Algorithm.HMAC256():使用HS256生成token,密钥则是用户的密码，唯一密钥的话可以保存在服务端。
     * withAudience()存入需要保存在token的信息，这里我把用户ID存入token中
     *
     * 链接：https://www.jianshu.com/p/e88d3f8151db
     * @param user
     * @return
     */
    public static String getToken(UserModel user) {
        String token="";
        token= JWT.create().withAudience(String.valueOf(user.getId()))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    //public UserModel userModelByToken(HttpServletRequest request) {
    //    String token = request.getHeader("token");
    //    System.out.println(token);
    //    //获取token中的userId
    //    String userId = JWT.decode(token).getAudience().get(0);
    //    System.out.println(userId);
    //    //判断用户是否存在
    //    UserModel userById = userService.getUserById(Integer.valueOf(userId));
    //    System.out.println(userById.toString());
    //    return userById;
    //
    //}


    ////获取token中的userId
    //String userId;
    //userId = JWT.decode(token).getAudience().get(0);
    ////判断用户是否存在
    //UserModel userById = userService.getUserById(Integer.valueOf(userId));
    //        if(userById == null)
    //        throw new BusinessException(EmBusinessError.USER_NOT_EXIST,"用户不存在，请重新登录");
    ////验证token
    //JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(userById.getPassword())).build();
    //        try {
    //    jwtVerifier.verify(token);
    //} catch (
    //JWTVerificationException e) {
    //    throw new RuntimeException("401");
    //}
    ////token验证成功之后将用户赋值给全局变量用于其他类获取
    //userModelByToken = userById;



}