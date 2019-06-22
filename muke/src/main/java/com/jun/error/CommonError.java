/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CommonError
 * Author:   俊哥
 * Date:     2019/6/5 22:49
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
public interface CommonError {

    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
