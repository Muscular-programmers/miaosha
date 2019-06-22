/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ValidationResult
 * Author:   俊哥
 * Date:     2019/6/12 16:10
 * Description: 校验结果
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈校验结果〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
public class ValidationResult {
    private boolean hasErr = false;
    private Map<String,String> errMsgMap = new HashMap<>();

    public boolean isHasErr() {
        return hasErr;
    }

    public void setHasErr(boolean hasErr) {
        this.hasErr = hasErr;
    }

    public Map<String, String> getErrMsgMap() {
        return errMsgMap;
    }

    public void setErrMsgMap(Map<String, String> errMsgMap) {
        this.errMsgMap = errMsgMap;
    }

    //通用的返回错误信息
    public String getErrMsg(){
        return StringUtils.join(errMsgMap.values().toArray(),",");
    }
}

