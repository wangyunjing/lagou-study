package com.lagou.edu.factory;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class AnnotationApplicationContext implements BeanFactory {
    String pack;
    AnnotationBeanFactory beanFactory;

    public AnnotationApplicationContext(String pack) {
        this.pack = pack;
    }

    public void refresh() {
        beanFactory = new AnnotationBeanFactory();
        AnnotationBeanDefinitionReader reader = new AnnotationBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(pack);
        registerBeanPostProcessors();
        beanFactory.instantiateBean();
    }

    private void registerBeanPostProcessors() {
        String[] beanNamesForType = beanFactory.getBeanNamesForType(BeanPostProcessor.class);
        for (String beanName : beanNamesForType) {
            BeanPostProcessor beanPostProcessor = beanFactory.getBean(beanName, BeanPostProcessor.class);
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        return beanFactory.getBean(beanName, clazz);
    }

}
