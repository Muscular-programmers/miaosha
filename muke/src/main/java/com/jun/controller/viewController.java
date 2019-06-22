/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: viewController
 * Author:   俊哥
 * Date:     2019/6/6 16:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.jun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/6
 * @since 1.0.0
 */
@Controller
public class viewController {

    @RequestMapping("/togetopt")
    public String getopt(){
        return "getopt";
    }

}

