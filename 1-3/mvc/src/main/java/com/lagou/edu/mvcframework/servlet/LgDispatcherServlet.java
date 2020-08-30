package com.lagou.edu.mvcframework.servlet;

import com.lagou.edu.mvcframework.annotations.*;
import com.lagou.edu.mvcframework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LgDispatcherServlet extends HttpServlet {


    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<String, Object>();

    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    public void init(ServletConfig config) {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        // 1 加载配置文件
        doLoadConfig(contextConfigLocation);

        // 2 扫描相关的类，扫描注解
        doScan(properties.getProperty("scanPackage"));

        // 3 初始化bean对象（实现ioc容器，基于注解）
        doInstance();

        // 4 实现依赖注入
        doAutoWired();

        // 5 构造一个HandlerMapping处理器映射器，将配置好的url和Method建立映射关系
        initHandlerMapping();
    }

    /**
     * 构造一个HandlerMapping处理器映射器
     * 最关键的环节
     * 目的：将url和method建立关联
     */
    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(LagouController.class)) {
                continue;
            }
            String baseUrl = "";
            if (aClass.isAnnotationPresent(LagouRequestMapping.class)) {
                LagouRequestMapping annotation = aClass.getAnnotation(LagouRequestMapping.class);
                baseUrl = annotation.value();
            }
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (!method.isAnnotationPresent(LagouRequestMapping.class)) {
                    continue;
                }
                LagouRequestMapping annotation = method.getAnnotation(LagouRequestMapping.class);
                String methodUrl = annotation.value();
                String url = baseUrl + methodUrl;
                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(url));
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];

                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), j);
                    } else {
                        handler.getParamIndexMapping().put(parameter.getName(), j);
                    }
                }
                doSecurity(entry.getValue(), method, handler);
                handlerMapping.add(handler);
            }
        }
    }

    private void doSecurity(Object obj, Method method, Handler handler) {
        Security security = obj.getClass().getAnnotation(Security.class);
        if (security != null) {
            String[] values = security.value();
            for (String value : values) {
                if (!"".equals(value.trim())) {
                    handler.getSecurityList().add(value);
                }
            }
        }
        security = method.getAnnotation(Security.class);
        if (security != null) {
            String[] values = security.value();
            for (String value : values) {
                if (!"".equals(value.trim())) {
                    handler.getSecurityList().add(value);
                }
            }
        }
    }

    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Field declaredField = declaredFields[i];
                if (!declaredField.isAnnotationPresent(LagouAutowired.class)) {
                    continue;
                }
                LagouAutowired annotation = declaredField.getAnnotation(LagouAutowired.class);
                String beanName = annotation.value();
                if ("".equals(beanName.trim())) {
                    beanName = declaredField.getType().getName();
                }
                // 开启赋值
                declaredField.setAccessible(true);
                try {
                    declaredField.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.size() == 0) {
            return;
        }
        try {
            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i);
                Class<?> aClass = Class.forName(className);
                if (aClass.isAnnotationPresent(LagouController.class)) {
                    String simpleName = aClass.getSimpleName();
                    String lowerFirstSimpleName = lowerFirst(simpleName);
                    Object o = aClass.newInstance();
                    ioc.put(lowerFirstSimpleName, o);
                } else if (aClass.isAnnotationPresent(LagouService.class)) {
                    LagouService annotation = aClass.getAnnotation(LagouService.class);
                    String beanName = annotation.value();
                    if ("".equals(beanName.trim())) {
                        beanName = lowerFirst(aClass.getSimpleName());
                    }
                    ioc.put(beanName, aClass.newInstance());
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        ioc.put(anInterface.getName(), aClass.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lowerFirst(String str) {
        char[] chars = str.toCharArray();
        if ('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + scanPackage.replaceAll("\\.", "/");
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();

        for (File file : files) {
            if (file.isDirectory()) { // 子package
                // 递归
                doScan(scanPackage + "." + file.getName());  // com.lagou.demo.controller
            } else if (file.getName().endsWith(".class")) {
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-type", "text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 not found");
            return;
        }
        if (!handler.security(req.getParameter("username"))) {

            resp.getWriter().write("没有访问权限");
            return;
        }
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        Object[] paraValues = new Object[parameterTypes.length];

        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            if (!handler.getParamIndexMapping().containsKey(param.getKey())) {
                continue;
            }
            String value = StringUtils.join(param.getValue(), ",");
            Integer index = handler.getParamIndexMapping().get(param.getKey());
            paraValues[index] = value;
        }

        Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
        if (requestIndex != null) {
            paraValues[requestIndex] = req;
        }

        Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        if (responseIndex != null) {
            paraValues[responseIndex] = resp;
        }
        try {
            handler.getMethod().invoke(handler.getController(), paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        resp.getWriter().write(req.getParameter("username") + " 访问【" + req.getRequestURI() + "】成功");
    }

    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }
}
