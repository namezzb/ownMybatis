package cn.zzb.mybatis.test.loader;

import org.junit.Test;

public class ThreadContextClassLoaderTest {

    @Test
    public void testThreadContextClassLoader() {
        System.out.println("=== 线程上下文类加载器详解 ===\n");

        // 1. 获取当前线程的上下文类加载器
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();

        System.out.println("1. 当前线程信息:");
        System.out.println("   线程名称: " + currentThread.getName());
        System.out.println("   线程上下文类加载器: " + contextClassLoader);
        System.out.println("   类型: " + contextClassLoader.getClass().getName());

        // 2. 对比不同的类加载器
        System.out.println("\n2. 对比不同的类加载器:");

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("   系统类加载器: " + systemClassLoader);

        ClassLoader thisClassLoader = this.getClass().getClassLoader();
        System.out.println("   当前类的加载器: " + thisClassLoader);

        System.out.println("\n   是否相同:");
        System.out.println("   线程上下文 == 系统类加载器: " + (contextClassLoader == systemClassLoader));
        System.out.println("   线程上下文 == 当前类加载器: " + (contextClassLoader == thisClassLoader));

        // 3. 查看父加载器链
        System.out.println("\n3. 线程上下文类加载器的父加载器链:");
        ClassLoader loader = contextClassLoader;
        int level = 0;
        while (loader != null) {
            System.out.println("   [" + level + "] " + loader);
            loader = loader.getParent();
            level++;
        }
        System.out.println("   [" + level + "] null (Bootstrap ClassLoader)");
    }

    @Test
    public void testSetContextClassLoader() {
        System.out.println("=== 动态设置线程上下文类加载器 ===\n");

        Thread currentThread = Thread.currentThread();

        // 1. 获取原始的上下文类加载器
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        System.out.println("1. 原始上下文类加载器:");
        System.out.println("   " + originalClassLoader);

        // 2. 创建一个新的类加载器
        ClassLoader newClassLoader = new CustomClassLoader();
        System.out.println("\n2. 创建新的类加载器:");
        System.out.println("   " + newClassLoader);

        // 3. 设置新的上下文类加载器
        currentThread.setContextClassLoader(newClassLoader);
        System.out.println("\n3. 设置后的上下文类加载器:");
        System.out.println("   " + currentThread.getContextClassLoader());
        System.out.println("   是否是新的: " + (currentThread.getContextClassLoader() == newClassLoader));

        // 4. 恢复原始的类加载器
        currentThread.setContextClassLoader(originalClassLoader);
        System.out.println("\n4. 恢复后的上下文类加载器:");
        System.out.println("   " + currentThread.getContextClassLoader());
        System.out.println("   是否恢复: " + (currentThread.getContextClassLoader() == originalClassLoader));
    }

    // 自定义类加载器
    static class CustomClassLoader extends ClassLoader {
        public CustomClassLoader() {
            super(ClassLoader.getSystemClassLoader());
        }

        @Override
        public String toString() {
            return "CustomClassLoader@" + Integer.toHexString(hashCode());
        }
    }
}
