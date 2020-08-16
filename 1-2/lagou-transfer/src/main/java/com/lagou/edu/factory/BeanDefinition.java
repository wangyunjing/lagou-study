package com.lagou.edu.factory;

import java.lang.reflect.Field;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class BeanDefinition {

    private String beanName;

    private Class<?> clazz;

    private String[] dependsOn;
    private Field[] fields;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }
}
