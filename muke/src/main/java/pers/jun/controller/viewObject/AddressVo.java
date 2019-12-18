/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AddressVo
 * Author:   俊哥
 * Date:     2019/12/10 14:44
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.controller.viewObject;

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
public class AddressVo {

    private Integer userId;

    private String userName;

    private String telephone;

    private String address;

    private Integer addressId;

    private Boolean isDefault;
}