package com.lagou.edu.factory;

import com.lagou.edu.anno.Autowired;
import com.lagou.edu.anno.Qualifier;
import com.lagou.edu.anno.Service;
import com.lagou.edu.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class AnnotationBeanDefinitionReader {

    private BeanDefinitionRegistry registry;

    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public int loadBeanDefinitions(String pack) {
        int size = registry.size();
        Set<Class<?>> classes = ClassUtils.getClasses(pack, new BeanPredicate());
        for (Class<?> clazz : classes) {
            BeanDefinition beanDefinition = createBeanDefinition(clazz);
            registry.registerBeanDefinition(beanDefinition);
        }
        return registry.size() - size;
    }

    private BeanDefinition createBeanDefinition(Class<?> clazz) {
        Service service = clazz.getDeclaredAnnotation(Service.class);
        String beanName = service.value();
        if (beanName.equals("")) {
            beanName = generateBeanName(clazz);
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setClazz(clazz);
        Field[] fields = ClassUtils.getAllField(clazz, new DependPredicate());
        List<String> dependList = new ArrayList<>(fields.length);
        List<Field> fieldList = new ArrayList<>(fields.length);
        for (Field field : fields) {
            Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
            if (autowired == null) {
                continue;
            }
            Qualifier qualifier = field.getDeclaredAnnotation(Qualifier.class);
            String dependBeanName = qualifier == null ? "" : qualifier.value();
            if (dependBeanName.equals("")) {
                Class<?> type = field.getType();
                if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
                    Class actualType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    dependBeanName = actualType.getName();
                } else {
                    dependBeanName = type.getName();
                }
            }
            dependList.add(dependBeanName);
            fieldList.add(field);
        }
        beanDefinition.setDependsOn(dependList.toArray(new String[dependList.size()]));
        beanDefinition.setFields(fieldList.toArray(new Field[fieldList.size()]));
        return beanDefinition;
    }

    private String generateBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        String s = simpleName.substring(0, 1).toLowerCase();
        return s + simpleName.substring(1);
    }

    private class BeanPredicate implements Predicate<Class<?>> {
        @Override
        public boolean test(Class aClass) {
            Annotation service = aClass.getDeclaredAnnotation(Service.class);
            return service != null;
        }
    }

    private class DependPredicate implements Predicate<Field> {
        @Override
        public boolean test(Field field) {
            Autowired autowired = field.getDeclaredAnnotation(Autowired.class);
            return autowired != null;
        }
    }
}
