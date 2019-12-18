/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: VadatorImpl
 * Author:   俊哥
 * Date:     2019/6/12 16:13
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package pers.jun.validation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 俊哥
 * @create 2019/6/12
 * @since 1.0.0
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        ValidationResult validationResult = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintValidateSet = validator.validate(bean);
        if(constraintValidateSet.size() > 0){
            //有错误
            validationResult.setHasErr(true);
            constraintValidateSet.forEach(constrainValidate->{
                String errMsg = constrainValidate.getMessage();
                String propertyName = constrainValidate.getPropertyPath().toString();
                validationResult.getErrMsgMap().put(propertyName,errMsg);
            });
        }
        return validationResult;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂初始化的方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }
}

