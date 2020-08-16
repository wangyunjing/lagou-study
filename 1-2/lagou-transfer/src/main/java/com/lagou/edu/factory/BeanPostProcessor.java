package com.lagou.edu.factory;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public interface BeanPostProcessor {

    Object after(Object bean, String beanName);

    Object getEarlyBeanReference(Object bean, String beanName);
}
