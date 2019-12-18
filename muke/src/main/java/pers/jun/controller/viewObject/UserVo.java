/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: viewObject
 * Author:   俊哥
 * Date:     2019/6/5 22:29
 * Description: 用户表现层用到user字段
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户表现层用到user字段〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
@Data
public class UserVo {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;
    private String imgUrl;
}

