/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: model
 * Author:   俊哥
 * Date:     2019/6/5 21:36
 * Description: 用户
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
@Data
public class UserModel {

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotNull(message = "性别不能不填")
    private Byte gender;

    @NotNull(message = "年龄不能不填")
    @Min(value = 0,message = "年龄必须大于0")
    @Max(value = 100,message = "年龄必须小于100")
    private Integer age;

    @NotBlank(message = "电话不能为空")
    private String telephone;
    private String registModel;
    private String thirdPartyId;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String imgUrl;



}

