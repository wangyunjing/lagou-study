package com.lagou.edu.factory;

import com.lagou.edu.anno.Autowired;
import com.lagou.edu.anno.Service;
import com.lagou.edu.anno.Transactional;
import com.lagou.edu.utils.ClassUtils;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yunjing.wang
 * @date 2020/8/17
 */
@Service
public class TransactionBeanPostProcessor implements BeanPostProcessor {

    Map<String, Object> cache = new HashMap<>();

    @Autowired
    TransactionManager transactionManager;

    @Override
    public Object after(Object bean, String beanName) {
        if (!isExist(bean)) {
            return bean;
        }
        if (cache.remove(beanName) != bean) {
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if (interfaces == null || interfaces.length == 0) {
                return getCglibProxy(bean);
            }
            return getJdkProxy(bean);
        }
        return bean;
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        if (!isExist(bean)) {
            return bean;
        }
        cache.put(beanName, bean);
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return getCglibProxy(bean);
        }
        return getJdkProxy(bean);
    }

    private boolean isExist(Object bean) {
        Class[] allSuperClassAndInterfaces = ClassUtils.getAllSuperClassAndInterfaces(bean.getClass());
        for (Class<?> clazz : allSuperClassAndInterfaces) {
            if (clazz.getAnnotation(Transactional.class) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Jdk动态代理
     */
    private Object getJdkProxy(Object obj) {

        // 获取代理对象
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;

                        try {
                            // 开启事务(关闭事务的自动提交)
                            transactionManager.beginTransaction();

                            result = method.invoke(obj, args);

                            // 提交事务
                            transactionManager.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 回滚事务
                            transactionManager.rollback();
                            // 抛出异常便于上层servlet捕获
                            throw e;
                        }
                        return result;
                    }
                });
    }

    /**
     * 使用cglib动态代理生成代理对象
     */
    private Object getCglibProxy(Object obj) {
        return Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try {
                    // 开启事务(关闭事务的自动提交)
                    transactionManager.beginTransaction();

                    result = method.invoke(obj, objects);

                    // 提交事务
                    transactionManager.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback();

                    // 抛出异常便于上层servlet捕获
                    throw e;
                }
                return result;
            }
        });
    }
}
