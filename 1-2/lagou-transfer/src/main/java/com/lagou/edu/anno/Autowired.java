package com.lagou.edu.anno;

import java.lang.annotation.*;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
