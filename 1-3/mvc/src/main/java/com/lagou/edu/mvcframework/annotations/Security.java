package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @author yunjing.wang
 * @date 2020/8/30
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Security {
    String[] value();
}
