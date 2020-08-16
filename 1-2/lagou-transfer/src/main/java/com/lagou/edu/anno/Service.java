package com.lagou.edu.anno;

import java.lang.annotation.*;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
