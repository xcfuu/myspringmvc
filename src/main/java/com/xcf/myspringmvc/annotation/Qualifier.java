package com.xcf.myspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author xuechaofu
 * @date 2018/12/27 10:41
 */
@Documented
@Target(ElementType.FIELD) //作用再字段上，实现注入
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    public String value();
}
