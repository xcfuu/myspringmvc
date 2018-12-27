package com.xcf.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author xuechaofu
 * @date 2018/12/27 10:50
 * url映射注解
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    public String value();
}
