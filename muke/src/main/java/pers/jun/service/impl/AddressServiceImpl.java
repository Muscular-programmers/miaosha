/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AddressServiceImpl
 * Author:   俊哥
 * Date:     2019/12/10 14:53
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.jun.dao.AddressMapper;
import pers.jun.dao.UserMapper;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.pojo.Address;
import pers.jun.pojo.User;
import pers.jun.service.AddressService;
import pers.jun.service.model.AddressModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/12/10
 * @since 1.0.0
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取用户所有地址信息
     * @return
     */
    public List<AddressModel> getList(Integer userId) throws BusinessException {
        if(userId == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        List<Address> addressList = addressMapper.getList(userId);
        return converToAddressModelList(addressList);
    }

    /**
     * 修改地址信息
     * @param addressModel
     * @return
     * @throws BusinessException
     */
    @Transactional
    public int updateAddress(AddressModel addressModel) throws BusinessException {
        if(addressModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        //判断默认地址信息
        if(updateIsDefault(addressModel) < 1)
            throw new BusinessException(EmBusinessError.ADDRESS_UNKONW_ERROR);
        int result = addressMapper.updateByPrimaryKeySelective(converToAddress(addressModel));
        if(result < 1)
            throw new BusinessException(EmBusinessError.ADDRESS_UNKONW_ERROR);
        return result;
    }

    /**
     * 添加地址信息
     */
    @Transactional
    public int addAddress(AddressModel addressModel) throws BusinessException {
        if(addressModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        //判断默认地址信息
        if(updateIsDefault(addressModel) < 1)
            throw new BusinessException(EmBusinessError.ADDRESS_UNKONW_ERROR);
        int result = addressMapper.insertSelective(converToAddress(addressModel));
        if(result < 1)
            throw new BusinessException(EmBusinessError.ADDRESS_UNKONW_ERROR);
        return result;
    }

    /**
     * 删除用户地址
     * @param addressModel
     * @return
     */
    public int delAddress(AddressModel addressModel) throws BusinessException {
        if(addressModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        int result = addressMapper.deleteByPrimaryKey(addressModel.getAddressId());
        if(result < 1)
            throw new BusinessException(EmBusinessError.ADDRESS_UNKONW_ERROR);
        return result;
    }

    /**
     * 关于更新默认地址的方法（修改和添加地址）
     * @param addressModel
     * @return
     */
    private int updateIsDefault(AddressModel addressModel) {
        //如果更改为默认地址，需将以前的默认地址设置为非默认
        int result = 1;
        if (addressModel.getIsDefault()) {
            Address address = addressMapper.selectByIdDefault(addressModel.getUserId(), true);
            //查询默认的地址和当前的地址不相等，则修改原来的默认地址isDefault为false
            if (address != null && -address.getId() != addressModel.getAddressId()) {
                address.setIsDefault(false);
                result = addressMapper.updateByPrimaryKeySelective(address);
            }
        }
        return result;
    }

    /**
     * bean转换
     * @param address
     * @return
     */
    private AddressModel converToAddressModel(Address address) {
        if(address == null)
            return null;
        AddressModel addressModel = new AddressModel();
        //映射属性
        BeanUtils.copyProperties(address,addressModel);
        addressModel.setAddressId(address.getId());
        addressModel.setUserName(address.getName());
        return addressModel;
    }
    private List<AddressModel> converToAddressModelList(List<Address> addresses) {
        if(addresses == null)
            return null;
        List<AddressModel> list = new ArrayList<>();
        for (Address address : addresses) {
            AddressModel addressModel = converToAddressModel(address);
            list.add(addressModel);
        }
        return list;
    }

    private Address converToAddress(AddressModel addressModel) {
        if(addressModel == null)
            return null;
        Address address = new Address();
        BeanUtils.copyProperties(addressModel,address);
        address.setId(addressModel.getAddressId());
        address.setName(addressModel.getUserName());
        return address;
    }
}