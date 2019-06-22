/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CommonReturnType
 * Author:   俊哥
 * Date:     2019/6/5 22:34
 * Description: http请求返回类型，加上一个字段用于表示服务器是否请求成功
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.response;

/**
 * 〈一句话功能简述〉<br> 
 * 〈http请求返回类型，加上一个字段用于表示服务器是否请求成功〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
public class CommonReturnType {
    //表明请求的返回处理结果 "success"或"fail"
    private String status;
    private Object data;

    //定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

