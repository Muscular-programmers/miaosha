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
package pers.jun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import pers.jun.response.CommonReturnType;
import pers.jun.service.CategoryService;
import pers.jun.service.model.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/10
 * @since 1.0.0
 */
@RestController
@RequestMapping("/category")
@Api(tags = "商品分类管理")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询所有分类")
    public Object getList(){
        List<CategoryModel> list = categoryService.getList();
        return CommonReturnType.create(list);
    }

}

