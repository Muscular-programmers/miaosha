/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AddressModel
 * Author:   俊哥
 * Date:     2019/12/10 14:42
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.service.model;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/12/10
 * @since 1.0.0
 */
@Data
public class AddressModel {

    private Integer addressId;

    private Integer userId;

    private String address;

    private String userName;

    private String telephone;

    private Boolean isDefault;

}