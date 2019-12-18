/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PassToken
 * Author:   俊哥
 * Date:     2019/12/9 9:35
 * Description: 自定义注解
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 〈一句话功能简述〉<br> 
 * 〈自定义注解〉
 *
 * @author 俊哥
 * @create 2019/12/9
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PassToken {
    boolean required() default true;
}