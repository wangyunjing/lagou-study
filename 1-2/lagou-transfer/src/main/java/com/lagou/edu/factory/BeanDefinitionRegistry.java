package com.lagou.edu.factory;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(BeanDefinition beanDefinition);

    int size();

    List<BeanDefinition> getBeanDefinitions();

    BeanDefinition getBeanDefinition(String beanName);

    String[] getBeanNamesForType(Class<?> clazz);
}
