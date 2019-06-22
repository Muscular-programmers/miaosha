/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: EmBusinessError
 * Author:   俊哥
 * Date:     2019/6/5 22:50
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.error;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
public enum EmBusinessError implements CommonError {
    //通用错误类型
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),

    //位置错误
    UNKNOWN_ERROR(10002,"未知错误"),

    //10000开头为用户相关错误
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_NOT_LOGIN(20002,"用户未登录"),

    //30000开头为交易错误
    STOCK_NOT_ENOUGH(30001,"库存不足"),
    ORDER_UNKOWN_ERROR(30002,"交易未知错误")
    ;

    EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;

    }
}
