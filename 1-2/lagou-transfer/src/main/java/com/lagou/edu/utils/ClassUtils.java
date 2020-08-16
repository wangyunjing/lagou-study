package com.lagou.edu.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class ClassUtils {

    public static Set<Class<?>> getClasses(String pack, Predicate<Class<?>> predicate) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, classes, predicate);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        addClasses(classes, packageName + '.' + className, predicate);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }


    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                         Set<Class<?>> classes, Predicate<Class<?>> predicate) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes, predicate);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    addClasses(classes, packageName + '.' + className, predicate);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addClasses(Set<Class<?>> classes, String clazz, Predicate<Class<?>> predicate) throws ClassNotFoundException {
        Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(clazz);
        if (predicate.test(aClass)) {
            classes.add(aClass);
        }
    }

    public static Field[] getAllField(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> list = new ArrayList<>(32);
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = clazz.getDeclaredFields();
            list.addAll(Arrays.asList(fields));
            searchType = searchType.getSuperclass();
        }
        list = list.stream().filter(predicate).collect(Collectors.toList());
        return list.toArray(new Field[list.size()]);
    }


    public static Class[] getAllSuperClassAndInterfaces(Class<?> clazz) {
        Queue<Class<?>> queue = new LinkedList<>();
        queue.add(clazz);
        Set<Class> setClass = new HashSet<>();
        setClass.add(clazz);
        while (!queue.isEmpty()) {
            clazz = queue.poll();
            if (clazz.getSuperclass() != null && !setClass.contains(clazz.getSuperclass())) {
                queue.offer(clazz.getSuperclass());
                setClass.add(clazz.getSuperclass());
            }
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (!setClass.contains(anInterface)) {
                    queue.offer(anInterface);
                    setClass.add(anInterface);
                }
            }
        }
        return setClass.toArray(new Class[setClass.size()]);
    }
}
