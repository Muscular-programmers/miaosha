/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemController
 * Author:   俊哥
 * Date:     2019/6/12 18:58
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import com.jun.controller.viewObject.ItemVo;
import com.jun.error.BusinessException;
import com.jun.response.CommonReturnType;
import com.jun.service.ItemService;
import com.jun.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
@Controller
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;


    /**
     * 通过分类查找
     */
    @RequestMapping("/findByCategory")
    @ResponseBody
    public Object findByCategory(@RequestParam(name = "categoryId")Integer categoryId){
        List<ItemModel> itemModels = itemService.getByCategory(categoryId);

        //将List<ItemModel>转化为List<ItemVo>
        List<ItemVo> itemVoList = itemModels.stream().map(itemModel -> {
            ItemVo itemVo = convertToItemVO(itemModel);
            return itemVo;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVoList);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object itemList(){
        List<ItemModel> itemModelList = itemService.getList();

        //将List<ItemModel>转化为List<ItemVo>
        List<ItemVo> itemVoList = itemModelList.stream().map(itemModel -> {
            ItemVo itemVo = convertToItemVO(itemModel);
            return itemVo;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVoList);
    }


    /**
     * 创建商品
     * @param itemModel
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/create",consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public Object createItem(ItemModel itemModel) throws BusinessException {

        ItemModel item = itemService.createItem(itemModel);

        ItemVo itemVo = convertToItemVO(item);

        return CommonReturnType.create(itemVo);
    }


    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public Object getItem(@RequestParam(name = "id")Integer id) throws BusinessException {

        ItemModel itemModel = itemService.getById(id);

        ItemVo itemVo = convertToItemVO(itemModel);

        return CommonReturnType.create(itemVo);
    }


    /**
     * bean转换
     * @param itemModel
     * @return
     */
    private ItemVo convertToItemVO(ItemModel itemModel){
        if(itemModel == null)
            return null;
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        if(itemModel.getPromoModel() != null){
            //如果存在有未结束的活动
            itemVo.setStatus(itemModel.getPromoModel().getStatus());
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVo.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setEndDate(itemModel.getPromoModel().getEndDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }
        else{
            itemVo.setStatus(0);
        }

        return itemVo;
    }
}

