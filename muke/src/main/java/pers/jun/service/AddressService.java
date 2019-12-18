package pers.jun.service;

import pers.jun.error.BusinessException;
import pers.jun.service.model.AddressModel;

import java.util.List;

public interface AddressService {

    //获取用户的所有地址信息
    List<AddressModel> getList(Integer userId) throws BusinessException;

    //跟新用户地址信息
    int updateAddress(AddressModel addressModel) throws BusinessException;

    //添加用户地址
    int addAddress(AddressModel addressModel) throws BusinessException;

    //删除用户地址
    int delAddress(AddressModel addressModel) throws BusinessException;
}
