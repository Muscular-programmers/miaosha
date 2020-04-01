/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: HomeController
 * Author:   俊哥
 * Date:     2019/12/7 0:24
 * Description: 主页
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.jun.controller.viewObject.ItemVo;
import pers.jun.pojo.ItemScroll;
import pers.jun.response.CommonReturnType;
import pers.jun.service.ItemService;
import pers.jun.service.PromoService;
import pers.jun.service.model.ItemModel;
import pers.jun.service.model.PromoModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br> 
 * 〈主页〉
 *
 * @author 俊哥
 * @create 2019/12/7
 * @since 1.0.0
 */
@RestController
@RequestMapping("/goods")
@Api(tags = "HomeController")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class HomeController extends BaseController{

    private static final int POP_ITEM = 2;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    /**
     * 获取首页所有数据（活动，热销，秒杀商品）
     * @return
     */
    @GetMapping("/home")
    @ApiOperation(value = "获取首页所有数据（活动，热销，秒杀商品）")
    public Object home() {
        List<HashMap<String,Object>> list = new ArrayList<>();

        /**
         * 1. 得到轮播图
         */
        HashMap<String,Object> scrollMap = new HashMap<>();

        // 设置参考值
        scrollMap.put("id",7);
        scrollMap.put("limitNum",5);
        scrollMap.put("name","轮播图");
        scrollMap.put("position",0);
        scrollMap.put("sortOrder",0);
        scrollMap.put("status",1);
        scrollMap.put("type",0);

        // 得到轮播图的商品信息
        List<ItemScroll> homeScroll = itemService.getHomeScroll();
        //List<ItemVo> scrollItemVoList = convertToItemVOList(homeScroll);
        scrollMap.put("panelContents",homeScroll);

        //加入结果list
        list.add(scrollMap);

        /**
         * 2. 得到热门商品
         */
        HashMap<String,Object> popMap = new HashMap<>();
        // 设置参考值
        popMap.put("id",1);
        popMap.put("limitNum",3);
        popMap.put("name","热门商品");
        popMap.put("position",0);
        popMap.put("sortOrder",2);
        popMap.put("status",1);
        popMap.put("type",2);
        List<ItemModel> popularItems = itemService.getPopular(POP_ITEM);
        List<ItemVo> popItemVoList = convertToItemVOList(popularItems);
        popMap.put("panelContents",popItemVoList);
        list.add(popMap);

        /**
         * 3. 得到秒杀进行中的商品
         */
        HashMap<String,Object> promoMap = new HashMap<>();
        // 设置参考值
        promoMap.put("id",2);
        promoMap.put("limitNum",8);
        promoMap.put("name","秒杀进行中");
        promoMap.put("position",0);
        promoMap.put("sortOrder",3);
        promoMap.put("status",1);
        promoMap.put("type",3);

        //根据活动信息中的商品id查询商品信息
        List<ItemModel> itemModels = itemService.getPromoHotItems();
        List<ItemVo> promoItemsVO = convertToItemVOList(itemModels);
        promoMap.put("panelContents",promoItemsVO);
        list.add(promoMap);

        return CommonReturnType.create(list);
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
        itemVo.setImgUrl(itemModel.getImgUrl().split("\\*\\*\\*")[0]);
        //List<String> modelImgList = Arrays.asList(itemModel.getImgUrl().split("\\*\\*\\*"));
        //设置封面图片为第一张
        //itemVo.setImgUrl(modelImgList.get(0));
        //设置所有图片，按照“，”分割
        //itemVo.setImgUrls(modelImgList);

        if(itemModel.getPromoModel() != null){
            //如果存在有未结束的活动
            itemVo.setStatus(itemModel.getPromoModel().getStatus());
            itemVo.setPromoId(itemModel.getPromoModel().getId());
            itemVo.setPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVo.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVo.setEndDate(itemModel.getPromoModel().getEndDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }
        else{
            itemVo.setStatus(0);
        }

        return itemVo;
    }

    private List<ItemVo> convertToItemVOList(List<ItemModel> itemModels){
        if(itemModels == null)
            return null;
        List<ItemVo> list = itemModels.stream().map(itemModel -> {
            ItemVo itemVo = convertToItemVO(itemModel);
            return itemVo;
        }).collect(Collectors.toList());
        return list;
    }

}