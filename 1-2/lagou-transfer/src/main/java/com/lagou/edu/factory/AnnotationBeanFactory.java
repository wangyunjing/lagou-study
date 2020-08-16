package com.lagou.edu.factory;

import com.lagou.edu.utils.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class AnnotationBeanFactory implements BeanDefinitionRegistry, BeanFactory {

    private List<BeanDefinition> beanDefinitionList = new ArrayList<>();
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, List<String>> classBeanDefinitionMap = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private Set<String> creating = new HashSet<>();
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, Object> earlyBeans = new HashMap<>();
    private Map<String, Supplier<?>> beanFactories = new HashMap<>();

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new RuntimeException("重复定义 " + beanName);
        }
        beanDefinitionList.add(beanDefinition);
        beanDefinitionMap.put(beanName, beanDefinition);
        Class[] allSuperClassAndInterfaces = ClassUtils.getAllSuperClassAndInterfaces(beanDefinition.getClazz());
        for (Class<?> clazz : allSuperClassAndInterfaces) {
            if (!classBeanDefinitionMap.containsKey(clazz.getName())) {
                classBeanDefinitionMap.put(clazz.getName(), new ArrayList<>());
            }
            List<String> values = classBeanDefinitionMap.get(clazz.getName());
            values.add(beanDefinition.getBeanName());
        }
    }

    public void instantiateBean() {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            getBean(beanDefinition.getBeanName());
        }
    }

    private Object doGetBean(String originalBeanName) {
        BeanDefinition beanDefinition = getBeanDefinition(originalBeanName);
        if (beanDefinition == null) {
            throw new RuntimeException("bean不存在 " + originalBeanName);
        }
        String beanName = beanDefinition.getBeanName();
        Object singleBean = getSingleBean(beanName, true);
        if (singleBean != null) {
            return singleBean;
        }

        Class<?> clazz = beanDefinition.getClazz();
        try {
            creating.add(beanName);
            // 创建实例
            Object bean = clazz.newInstance();
            Object exposedBean = bean;

            beanFactories.put(beanName, () -> {
                Object value = bean;
                for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                    value = beanPostProcessor.getEarlyBeanReference(value, beanName);
                }
                return value;
            });

            // 依赖
            if (beanDefinition.getDependsOn() != null) {
                String[] dependsOn = beanDefinition.getDependsOn();
                Field[] fields = beanDefinition.getFields();
                for (int i = 0; i < dependsOn.length; i++) {
                    Object value;
                    Field field = fields[i];
                    Class<?> type = field.getType();
                    if (List.class.isAssignableFrom(type)) {
                        List<String> strings = classBeanDefinitionMap.get(getGeneric(field));
                        value = strings.stream()
                                .map(this::getBean)
                                .collect(Collectors.toList());
                    } else if (Set.class.isAssignableFrom(type)) {
                        List<String> strings = classBeanDefinitionMap.get(getGeneric(field));
                        value = strings.stream()
                                .map(this::getBean)
                                .collect(Collectors.toSet());
                    } else {
                        value = getBean(dependsOn[i]);
                    }
                    fields[i].setAccessible(true);
                    fields[i].set(bean, value);
                }
            }

            // 后处理
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                exposedBean = beanPostProcessor.after(exposedBean, beanName);
            }
            Object earlySingletonRef = getSingleBean(beanName, false);

            if (earlySingletonRef != null && exposedBean == bean) {
                exposedBean = earlySingletonRef;
            }

            // 注册
            registerBean(beanName, exposedBean);
            return exposedBean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            creating.remove(beanName);
        }

    }

    private String getGeneric(Field field) {
        return ((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getName();
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    private Object getSingleBean(String beanName, boolean allowEarlyRef) {
        Object bean =  beans.get(beanName);
        if (bean == null && creating.contains(beanName)) {
            bean = earlyBeans.get(beanName);
            if (bean == null && allowEarlyRef) {
                Supplier<?> singletonFactory = this.beanFactories.get(beanName);
                if (singletonFactory != null) {
                    bean = singletonFactory.get();
                    this.beanFactories.remove(beanName);
                    this.earlyBeans.put(beanName, bean);
                }
            }
        }
        return bean;
    }

    private void registerBean(String beanName, Object bean) {
        beans.put(beanName, bean);
        earlyBeans.remove(beanName);
        beanFactories.remove(beanName);
    }

    @Override
    public int size() {
        return beanDefinitionList.size();
    }

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitionList;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition != null) {
            return beanDefinition;
        }
        List<String> strings = classBeanDefinitionMap.get(beanName);
        if (strings == null || strings.isEmpty()) {
            return null;
        }
        if (strings.size() > 1) {
            throw new RuntimeException("beanNam存在多个" + beanName);
        }
        return beanDefinitionMap.get(strings.get(0));
    }

    @Override
    public String[] getBeanNamesForType(Class<?> clazz) {
        List<String> strings = classBeanDefinitionMap.get(clazz.getName());
        if (strings == null) {
            return new String[0];
        }
        return strings.toArray(new String[strings.size()]);
    }

    @Override
    public Object getBean(String beanName) {
        return doGetBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        String[] strings = getBeanNamesForType(clazz);
        if (strings == null || strings.length == 0) {
            return null;
        }
        if (strings.length > 1) {
            throw new RuntimeException("存在多个相同的bean");
        }
        return (T) getBean(strings[0]);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        return (T) getBean(beanName);
    }
}
