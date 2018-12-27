package com.xcf.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author xuechaofu
 * @date 2018/12/27 10:22
 */
@Documented //JAVADOC
@Target(ElementType.TYPE) //作用再类上
@Retention(RetentionPolicy.RUNTIME) //限制注解的生命周期，自定义注解显示需要运行时保留
public @interface Controller {
    /**
     * Controller的名称属性
     * @return
     */
    public String value();
}
