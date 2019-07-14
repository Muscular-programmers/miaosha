/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryController
 * Author:   俊哥
 * Date:     2019/7/10 17:48
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import com.jun.response.CommonReturnType;
import com.jun.service.CategoryService;
import com.jun.service.model.CategoryModel;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/10
 * @since 1.0.0
 */
@Controller
@RequestMapping("/category")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/getList")
    @ResponseBody
    public Object getList(){
        List<CategoryModel> list = categoryService.getList();
        return CommonReturnType.create(list);
    }

}

