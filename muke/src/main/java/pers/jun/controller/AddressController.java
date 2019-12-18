/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AddressController
 * Author:   俊哥
 * Date:     2019/12/10 14:48
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller;

import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.jun.config.UserLoginToken;
import pers.jun.controller.viewObject.AddressVo;
import pers.jun.controller.viewObject.UserVo;
import pers.jun.error.BusinessException;
import pers.jun.error.EmBusinessError;
import pers.jun.interceptor.AuthenticationInterceptor;
import pers.jun.response.CommonReturnType;
import pers.jun.service.AddressService;
import pers.jun.service.model.AddressModel;
import pers.jun.service.model.UserModel;

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
@RestController
@RequestMapping("/address")
@Api(tags = "用户地址管理模块")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")//解决跨域请求报错的问题 视频3-8
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 根据用户获取所有地址
     */
    @UserLoginToken
    @PostMapping("/getList")
    @ApiOperation(value = "根据用户获取所有地址")
    public Object getList(@RequestBody UserVo userVo) throws BusinessException {
        //判断用户是否登录
        UserModel userModel = checkUserLogin();
        List<AddressModel> list = addressService.getList(userModel.getId());
        List<AddressVo> addressVos = converToAddVoList(list);
        return CommonReturnType.create(addressVos);
    }

    /**
     * 更新用户地址信息
     */
    @UserLoginToken
    @PostMapping("/updateAddress")
    @ApiOperation(value = "更新用户地址信息")
    public CommonReturnType updateAddress(@RequestBody AddressModel addressModel) throws BusinessException {
        int result = addressService.updateAddress(addressModel);
        return CommonReturnType.create(result);
    }

    /**
     * 添加地址信息
     */
    @UserLoginToken
    @PostMapping("/addAddress")
    @ApiOperation(value = "添加地址信息")
    public CommonReturnType addAddress(@RequestBody AddressModel addressModel) throws BusinessException {
        int result = addressService.addAddress(addressModel);
        return CommonReturnType.create(result);
    }

    /**
     * 添加地址信息
     */
    @UserLoginToken
    @PostMapping("/delAddress")
    @ApiOperation(value = "添加地址信息")
    public CommonReturnType delAddress(@RequestBody AddressModel addressModel) throws BusinessException {
        int result = addressService.delAddress(addressModel);
        return CommonReturnType.create(result);
    }

    /**
     * 检查用户是否登录
     * @return
     * @throws BusinessException
     */
    private UserModel checkUserLogin() throws BusinessException {
        UserModel userModel = AuthenticationInterceptor.userModelByToken;
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        return userModel;
    }

    /**
     * bean转换
     * @param addressModel
     * @return
     */
    private AddressVo converToAddVo(AddressModel addressModel) {
        if(addressModel == null)
            return null;
        AddressVo addressVo = new AddressVo();
        BeanUtils.copyProperties(addressModel,addressVo);
        return addressVo;
    }

    private List<AddressVo> converToAddVoList(List<AddressModel> models) {
        List<AddressVo> list = new ArrayList<>();
        for (AddressModel model : models) {
            AddressVo addressVo = converToAddVo(model);
            list.add(addressVo);
        }
        return list;
    }
}