/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryModel
 * Author:   俊哥
 * Date:     2019/7/10 17:09
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.model;

import com.jun.pojo.Category;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/7/10
 * @since 1.0.0
 */
public class CategoryModel {

    /**
    分类id
    */
    private Integer categoryId;

    /**
    分类名称
    */
    private String categoryName;

    /**
    分类描述
    */
    private String categoryDesc;

    /**
    分类父id
    */
    private Integer parentId;

    /**
     * 所有子分类
     */
    private List<CategoryModel> childs;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<CategoryModel> getChilds() {
        return childs;
    }

    public void setChilds(List<CategoryModel> childs) {
        this.childs = childs;
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryDesc='" + categoryDesc + '\'' +
                ", parentId=" + parentId +
                ", childs=" + childs +
                '}';
    }
}

