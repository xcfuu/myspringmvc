package com.xcf.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author xuechaofu
 * @date 2018/12/27 10:52
 * 业务层注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public String value();
}
