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
package com.jun.controller.viewObject;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户表现层用到user字段〉
 *
 * @author 俊哥
 * @create 2019/6/5
 * @since 1.0.0
 */
public class UserVo {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telephone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

