/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: BaseController
 * Author:   俊哥
 * Date:     2019/6/6 14:53
 * Description: 处理未被controller吸收的异常
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller;

/**
 * 〈一句话功能简述〉<br> 
 * 〈处理未被controller吸收的异常〉
 *
 * @author 俊哥
 * @create 2019/6/6
 * @since 1.0.0
 */
public class BaseController {
    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";

    //@ExceptionHandler(Exception.class)
    //@ResponseStatus(HttpStatus.OK)
    //@ResponseBody
    //public Object exceptionHandler(HttpServletRequest req, Exception ex){
    //    Map<String,Object> hashMap = new HashMap<>();
    //    if(ex instanceof BusinessException){
    //        BusinessException bx = (BusinessException)ex;
    //
    //        hashMap.put("errCode",bx.getErrCode());
    //        hashMap.put("errMsg",bx.getErrMsg());
    //    }else{
    //        hashMap.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
    //        hashMap.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
    //    }
    //
    //    return CommonReturnType.create(hashMap,"fail");
    //}

}

