package com.xy.spring.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 类扫描工具
 */
public class ClassScanner {

    /**
     * 扫描指定包下的所有类
     */
    public static List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(packagePath);
            
            if (url != null) {
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = url.getPath();
                    findClassesByFile(packageName, filePath, classes);
                }
            } else {
                System.err.println("Warning: Package not found: " + packageName);
            }
        } catch (Exception e) {
            System.err.println("Error scanning package '" + packageName + "': " + e.getMessage());
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }
        
        return classes;
    }

    /**
     * 通过文件方式扫描类
     */
    private static void findClassesByFile(String packageName, String filePath, List<Class<?>> classes) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findClassesByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    System.err.println("Warning: Could not load class '" + className + "': " + e.getMessage());
                }
            }
        }
    }
}
