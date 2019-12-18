/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: GlobalExceptionHandler
 * Author:   俊哥
 * Date:     2019/11/28 21:53
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.error;

import pers.jun.response.CommonReturnType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 〈全局异常处理〉<br>
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/11/28
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest request, HttpServletResponse response, Exception ex){
        ex.printStackTrace();
        HashMap<Object, Object> responseData = new HashMap<>();

        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException)ex;
            responseData.put("errCode",businessException.getErrCode());
            responseData.put("errMsg",businessException.getErrMsg());
        }else if(ex instanceof ServletRequestBindingException){
            responseData.put("errCode",EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","URL绑定路由异常");
        }else if(ex instanceof NoHandlerFoundException){
            responseData.put("errCode",EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","没有找到对应的访问路径");
        }else{
            responseData.put("errCode",EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}