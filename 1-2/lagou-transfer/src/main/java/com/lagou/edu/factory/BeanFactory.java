package com.lagou.edu.factory;

/**
 * @author 应癫
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
public interface BeanFactory {
    Object getBean(String beanName);

    <T> T getBean(Class<T> clazz);

    <T> T getBean(String beanName, Class<T> clazz);
}
