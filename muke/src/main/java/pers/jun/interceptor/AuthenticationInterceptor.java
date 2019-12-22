/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AuthenticationInterceptor
 * Author:   俊哥
 * Date:     2019/12/9 9:50
 * Description: 拦截器去获取token并验证token
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pers.jun.config.PassToken;
import pers.jun.config.UserLoginToken;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.service.UserService;
import pers.jun.service.model.UserModel;
import pers.jun.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 〈HandlerInterceptor接口主要定义了三个方法
 * 1.boolean preHandle ()：
 * 预处理回调方法,实现处理器的预处理，第三个参数为响应的处理器,自定义Controller,返回值为true表示继续流程（如调用下一个拦截器或处理器）或者接着执行
 * postHandle()和afterCompletion()；false表示流程中断，不会继续调用其他的拦截器或处理器，中断执行。
 *
 * 2.void postHandle()：
 * 后处理回调方法，实现处理器的后处理（DispatcherServlet进行视图返回渲染之前进行调用），此时我们可以通过modelAndView（模型和视图对象）对模型数据进行处理或对视图进行处理，modelAndView也可能为null。
 *
 * 3.void afterCompletion():
 * 整个请求处理完毕回调方法,该方法也是需要当前对应的Interceptor的preHandle()的返回值为true时才会执行，也就是在DispatcherServlet渲染了对应的视图之后执行。用于进行资源清理。整个请求处理完毕回调方法。如性能监控中我们可以在此记录结束时间并输出消耗时间，还可以进行一些资源清理，类似于try-catch-finally中的finally，但仅调用处理器执行链中
 *
 * 主要流程:
 * 1.从 http 请求头中取出 token，
 * 2.判断是否映射到方法
 * 3.检查是否有passtoken注释，有则跳过认证
 * 4.检查有没有需要用户登录的注解，有则需要取出并验证
 * 5.认证通过则可以访问，不通过会报相关错误信息
 *
 * 链接：https://www.jianshu.com/p/e88d3f8151db
 * 来源：简书
 * 〈拦截器去获取token并验证token〉
 *
 * @author 俊哥
 * @create 2019/12/9
 * @since 1.0.0
 */
public class AuthenticationInterceptor implements HandlerInterceptor  {

    public static UserModel userModelByToken = null;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Http请求中获取token
        String token = request.getHeader("Authorization");
        //如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod))
            return true;
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if(passToken.required())
                return true;
        }

        //检查有没有需要登陆的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            //UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            //执行认证
            if(StringUtils.isBlank(token))
                throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"无token，请重新登录！");
            // 从redis得到usermodel
            UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
            userModelByToken = userModel;
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}