/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryServiceImpl
 * Author:   俊哥
 * Date:     2019/7/10 17:16
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.impl;

import com.jun.dao.CategoryMapper;
import com.jun.pojo.Category;
import com.jun.service.CategoryService;
import com.jun.service.model.CategoryModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/10
 * @since 1.0.0
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询所有分类
     * @return
     */
    public List<CategoryModel> getList() {
        //查询所有父分类
        List<Category> list = categoryMapper.selectParents();
        //bean转换
        List<CategoryModel> parents = converToCategoryModel(list);

        for (CategoryModel parent : parents) {
            List<CategoryModel> childs = findByParent(parent);
            parent.setChilds(childs);
        }
        return parents;
    }

    /**
     * bean转换
     * @param parents
     * @return
     */
    private List<CategoryModel> converToCategoryModel(List<Category> parents) {
        if(parents == null){
            return null;
        }
        List<CategoryModel> modelList = new ArrayList<>();
        for (Category parent : parents) {
            CategoryModel categoryModel = new CategoryModel();
            BeanUtils.copyProperties(parent,categoryModel);
            modelList.add(categoryModel);
        }
        return modelList;
    }

    /**
     * 根据父id查询所有子分类
     * @param parent
     * @return
     */
    private List<CategoryModel> findByParent(CategoryModel parent) {
        List<Category> list = categoryMapper.selectByParent(parent.getCategoryId());
        List<CategoryModel> childs = converToCategoryModel(list);
        return childs;
    }
}

