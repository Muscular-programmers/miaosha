/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemServiceImpl
 * Author:   俊哥
 * Date:     2019/6/12 17:24
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.service.impl;

import com.jun.dao.ItemMapper;
import com.jun.dao.ItemStockMapper;
import com.jun.error.BusinessException;
import com.jun.error.EmBusinessError;
import com.jun.pojo.Item;
import com.jun.pojo.ItemStock;
import com.jun.service.ItemService;
import com.jun.service.PromoService;
import com.jun.service.model.ItemModel;
import com.jun.service.model.PromoModel;
import com.jun.validation.ValidationResult;
import com.jun.validation.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemStockMapper stockMapper;

    @Autowired
    private PromoService promoService;

    @Override
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErr()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转换itemModel-item
        Item item = convertToItem(itemModel);

        //写入数据库
        itemMapper.insertSelective(item);
        itemModel.setId(item.getId());

        ItemStock stock = convertToItemStock(itemModel);
        stockMapper.insertSelective(stock);

        //返回创建完成的对象
        return getById(itemModel.getId());

    }


    /**
     * 查询所有
     * @return
     */
    public List<ItemModel> getList() {
        List<Item> itemList = itemMapper.selectList();
        List<ItemModel> itemModelList = itemList.stream().map(item -> {
            ItemStock stock = stockMapper.selectByItemId(item.getId());
            ItemModel itemModel = convertToItemModel(item, stock);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    /**
     * 通过id查找
     * @param id
     * @return
     */
    public ItemModel getById(Integer id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        if(item == null){
            return null;
        }

        //使用库存service得到库存
        ItemStock itemStock = stockMapper.selectByItemId(id);

        //转换相应的bean
        ItemModel itemModel = convertToItemModel(item,itemStock);

        //得到商品相应的活动
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        //表示存在还未结束的活动
        if(promoModel != null && promoModel.getStatus() != 3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;

    }

    /**
     * 通过商品id更新stock
     * @param itemId
     * @param amount
     * @return
     */
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectLine = stockMapper.updateByItemId(itemId, amount);
        if(affectLine > 0)
            return true;
        return false;
    }

    /**
     * 更新销量
     * @param id
     * @param amount
     */
    public boolean increaseSales(Integer id, Integer amount) {
        int affectLine = itemMapper.increaseSales(id, amount);
        if(affectLine > 0){
            return true;
        }
        return false;
    }

    /**
     * bean转换
     * @param itemModel
     * @return
     */
    private Item convertToItem(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemModel,item);
        item.setPrice(new Double(String.valueOf(itemModel.getPrice())));
        return item;
    }

    /**
     * bean转换
     * @param itemModel
     * @return
     */
    private ItemStock convertToItemStock(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStock itemStock = new ItemStock();
        itemStock.setStock(itemModel.getStock());
        itemStock.setItemId(itemModel.getId());
        return itemStock;
    }

    /**
     * bean转换
     * @param item
     * @param itemStock
     * @return
     */
    private ItemModel convertToItemModel(Item item,ItemStock itemStock){
        if(item == null || itemStock == null){
            return null;
        }
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(item,itemModel);
        itemModel.setPrice(new BigDecimal(item.getPrice()));
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }
}

