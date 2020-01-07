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
package pers.jun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import pers.jun.controller.viewObject.ItemVo;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.response.CommonReturnType;
import pers.jun.service.CacheService;
import pers.jun.service.ItemService;
import pers.jun.service.PromoService;
import pers.jun.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
@RestController
@RequestMapping("/item")
@Api(tags = "商品管理模块")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PromoService promoService;

    /**
     * 通过分类查找商品
     */
    @PostMapping("/findByCategory")
    @ApiOperation(value = "通过分类查找商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId",value = "商品分类id",required = true,paramType = "query")
    })
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
     * 获取所有商品
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取所有商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key",value = "搜索关键字",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "分页显示页码",paramType = "query"),
            @ApiImplicitParam(name = "size",value = "分页中每页显示数据",paramType = "query"),
            @ApiImplicitParam(name = "sort",value = "排序方式",paramType = "query"),
            @ApiImplicitParam(name = "priceGt",value = "按价格筛选最低价",paramType = "query"),
            @ApiImplicitParam(name = "priceLte",value = "按价格筛选最高价",paramType = "query")
    })
    public CommonReturnType itemList(@RequestParam(name = "key",required = false) String key,
                                     @RequestParam(name = "page") Integer page,
                                     @RequestParam(name = "size") Integer size,
                                     @RequestParam(name = "sort",required = false) String sort,
                                     @RequestParam(name = "priceGt",required = false) String priceGt,
                                     @RequestParam(name = "priceLte",required = false) String priceLte) throws BusinessException {

        Map<String,Object> map = new HashMap<>();
        Page<ItemModel> list = itemService.getList(key, page, size, sort, priceGt, priceLte);
        Page<ItemVo> voList = convertoItemVoList(list);
        map.put("data",voList);
        map.put("total",voList.getTotal());
        return CommonReturnType.create(map);
    }

    /**
     * 获取所有活动中的商品
     */
    @GetMapping(value = "/promoItems")
    @ApiOperation(value = "获取所有商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "分页显示页码",paramType = "query"),
            @ApiImplicitParam(name = "size",value = "分页中每页显示数据",paramType = "query"),
            @ApiImplicitParam(name = "sort",value = "排序方式",paramType = "query"),
            @ApiImplicitParam(name = "priceGt",value = "按价格筛选最低价",paramType = "query"),
            @ApiImplicitParam(name = "priceLte",value = "按价格筛选最高价",paramType = "query")
    })
    public CommonReturnType listPromoItem(@RequestParam(name = "page") Integer page,
                                     @RequestParam(name = "size") Integer size,
                                     @RequestParam(name = "sort",required = false) String sort,
                                     @RequestParam(name = "priceGt",required = false) String priceGt,
                                     @RequestParam(name = "priceLte",required = false) String priceLte) throws BusinessException {

        Map<String,Object> map = new HashMap<>();
        Page<ItemModel> list = itemService.getPromoItems(page, size, sort, priceGt, priceLte);
        Page<ItemVo> voList = convertoItemVoList(list);
        map.put("data",voList);
        map.put("total",voList.getTotal());
        return CommonReturnType.create(map);
    }

    /**
     * 创建商品
     */
    @PostMapping(value = "/create")
    @ApiOperation(value = "创建商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemModel",value = "商品信息",required = true,paramType = "query")
    })
    public Object createItem(ItemModel itemModel) throws BusinessException {

        ItemModel item = itemService.createItem(itemModel);

        ItemVo itemVo = convertToItemVO(item);

        return CommonReturnType.create(itemVo);
    }

    /**
     * 获取商品详情
     */
    @GetMapping(value = "/get")
    @ApiOperation(value = "获取商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "商品id",required = true,paramType = "query")
    })
    public Object getItem(@RequestParam(name = "id")Integer id) throws BusinessException {
        ItemModel itemModel = null;

        // 先从本地缓存中查找
        itemModel = (ItemModel) cacheService.getFromCache("item_"+id);
        if (itemModel == null) {
            // 如果本地缓存为空，从redis缓存中查找
            itemModel = (ItemModel) redisTemplate.opsForValue().get("item_"+id);
            if (itemModel == null) {
                // 如果redis缓存中没有，调用下层service
                itemModel = itemService.getById(id);
                if(itemModel == null)
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"商品id不合法，不存在该商品");
                // 存入redis缓存
                redisTemplate.opsForValue().set("item_"+id,itemModel);
                // 设置默认过期时间
                redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
            }
            //将得到的itemModel存入本地缓存
            cacheService.setCommonCache("item_"+id,itemModel);
        }

        ItemVo itemVo = convertToItemVO(itemModel);

        return CommonReturnType.create(itemVo);
    }

    /**
     * 发布活动商品
     */
    @GetMapping(value = "/publishPromo")
    @ApiOperation(value = "发布活动商品")
    @ApiImplicitParam(name = "promoId",value = "发布的活动id",required = true,paramType = "query")
    public Object publishPromo(@RequestParam Integer promoId) throws BusinessException {

        if(promoId == null || promoId < 1)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        promoService.publishPromo(promoId);

        return CommonReturnType.create(null);
    }


    /**
     * bean转换
     */
    private ItemVo convertToItemVO(ItemModel itemModel){
        if(itemModel == null)
            return null;
        ItemVo itemVo = new ItemVo();
        BeanUtils.copyProperties(itemModel,itemVo);
        itemVo.setItemId(itemModel.getItemId());
        List<String> modelImgList = Arrays.asList(itemModel.getImgUrl().split(","));
        //设置封面图片为第一张
        itemVo.setImgUrl(modelImgList.get(0));
        //设置所有图片，按照“，”分割
        itemVo.setImgUrls(modelImgList);
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

    private List<ItemVo> convertoItemVoList(List<ItemModel> itemModelList) {
        if(itemModelList == null)
            return null;
        //将List<ItemModel>转化为List<ItemVo>
        List<ItemVo> itemVoList = itemModelList.stream().map(itemModel -> {
            ItemVo itemVo = convertToItemVO(itemModel);
            return itemVo;
        }).collect(Collectors.toList());
        return itemVoList;
    }

    private Page<ItemVo> convertoItemVoList(Page<ItemModel> itemModelList) {
        if(itemModelList == null)
            return null;
        Page<ItemVo> itemVos = new Page<>();
        for (ItemModel itemModel : itemModelList) {
            itemVos.add(convertToItemVO(itemModel));
        }
        BeanUtils.copyProperties(itemModelList,itemVos);
        return itemVos;
    }
}

