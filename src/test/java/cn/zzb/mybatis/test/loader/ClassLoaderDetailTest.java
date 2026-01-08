package cn.zzb.mybatis.test.loader;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderDetailTest {

    @Test
    public void testApplicationClassLoaderDetails() {
        System.out.println("=== ApplicationClassLoader 详细分析 ===\n");

        // 1. 获取 ApplicationClassLoader
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("1. ApplicationClassLoader 类型:");
        System.out.println("   " + appClassLoader.getClass().getName());
        System.out.println("   实例: " + appClassLoader);

        // 2. 查看它能加载的所有路径（URL）
        System.out.println("\n2. ApplicationClassLoader 能加载的所有路径:");
        if (appClassLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) appClassLoader;
            URL[] urls = urlClassLoader.getURLs();
            for (int i = 0; i < urls.length; i++) {
                System.out.println("   [" + i + "] " + urls[i]);
            }
        } else {
            // Java 9+ 不再是 URLClassLoader
            System.out.println("   (Java 9+ 使用内部实现，通过 java.class.path 查看)");
        }

        // 3. 通过系统属性查看 classpath
        System.out.println("\n3. 通过 java.class.path 查看:");
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(File.pathSeparator);
        for (int i = 0; i < paths.length; i++) {
            File file = new File(paths[i]);
            System.out.println("   [" + i + "] " + paths[i]);
            System.out.println("       类型: " + (file.isDirectory() ? "目录" : "JAR文件"));
            System.out.println("       存在: " + file.exists());
        }

        // 4. 测试能加载的内容类型
        System.out.println("\n4. ApplicationClassLoader 能加载的内容类型:");
        testLoadClass();
        testLoadResource();
    }

    private void testLoadClass() {
        System.out.println("\n   A. 加载 .class 文件:");

        // 测试加载自己的类
        try {
            Class<?> clazz = Class.forName("cn.zzb.mybatis.test.loader.ClassLoaderDetailTest");
            System.out.println("      ✅ 加载测试类: " + clazz.getName());
            System.out.println("         位置: " + clazz.getResource("ClassLoaderDetailTest.class"));
        } catch (ClassNotFoundException e) {
            System.out.println("      ❌ 加载失败");
        }

        // 测试加载主代码类
        try {
            Class<?> clazz = Class.forName("cn.zzb.mybatis.io.Resources");
            System.out.println("      ✅ 加载主代码类: " + clazz.getName());
            System.out.println("         位置: " + clazz.getResource("Resources.class"));
        } catch (ClassNotFoundException e) {
            System.out.println("      ❌ 加载失败");
        }

        // 测试加载依赖 jar 中的类
        try {
            Class<?> clazz = Class.forName("org.junit.Test");
            System.out.println("      ✅ 加载依赖类: " + clazz.getName());
            System.out.println("         位置: " + clazz.getProtectionDomain().getCodeSource().getLocation());
        } catch (ClassNotFoundException e) {
            System.out.println("      ❌ 加载失败");
        }
    }

    private void testLoadResource() {
        System.out.println("\n   B. 加载资源文件:");

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        // 测试加载 XML 文件
        URL xmlResource = cl.getResource("mapper/User_Mapper.xml");
        if (xmlResource != null) {
            System.out.println("      ✅ 加载 XML: mapper/User_Mapper.xml");
            System.out.println("         位置: " + xmlResource);
        } else {
            System.out.println("      ❌ 找不到 XML");
        }

        // 测试加载 properties 文件（如果有）
        URL propsResource = cl.getResource("application.properties");
        if (propsResource != null) {
            System.out.println("      ✅ 加载 properties: application.properties");
            System.out.println("         位置: " + propsResource);
        } else {
            System.out.println("      ℹ️  没有 application.properties");
        }
    }

    @Test
    public void testClasspathRules() {
        System.out.println("\n=== Classpath 查找规则 ===\n");

        System.out.println("1. 目录结构:");
        System.out.println("   target/test-classes/");
        System.out.println("   ├─ cn/zzb/mybatis/test/ClassLoaderDetailTest.class");
        System.out.println("   └─ mapper/User_Mapper.xml");
        System.out.println("");
        System.out.println("   target/classes/");
        System.out.println("   └─ cn/zzb/mybatis/io/Resources.class");

        System.out.println("\n2. 类名到文件路径的转换规则:");
        System.out.println("   类名: cn.zzb.mybatis.test.loader.ClassLoaderDetailTest");
        System.out.println("   转换: cn/zzb/mybatis/test/ClassLoaderDetailTest.class");
        System.out.println("   查找: classpath根目录/cn/zzb/mybatis/test/ClassLoaderDetailTest.class");

        System.out.println("\n3. 资源路径规则:");
        System.out.println("   资源: mapper/User_Mapper.xml");
        System.out.println("   查找: classpath根目录/mapper/User_Mapper.xml");
        System.out.println("   注意: 资源路径不需要转换，直接拼接");

        System.out.println("\n4. 查找顺序:");
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(File.pathSeparator);
        System.out.println("   按 classpath 顺序依次查找:");
        for (int i = 0; i < Math.min(3, paths.length); i++) {
            System.out.println("   [" + i + "] " + paths[i]);
        }
        System.out.println("   找到第一个匹配的就返回，不再继续查找");
    }
}
