package cn.zzb.mybatis.test.loader;

import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ServiceLoader;

public class JDBCContextClassLoaderTest {

    @Test
    public void testJDBCDriverLoading() {
        System.out.println("=== JDBC 驱动加载：线程上下文类加载器的经典应用 ===\n");

        // 1. DriverManager 的类加载器
        System.out.println("1. DriverManager 的类加载器:");
        ClassLoader driverManagerLoader = DriverManager.class.getClassLoader();
        System.out.println("   " + driverManagerLoader);
        System.out.println("   说明: null 表示由 Bootstrap ClassLoader 加载");
        System.out.println("   位置: rt.jar (JDK 核心库)");

        // 2. MySQL 驱动的类加载器
        System.out.println("\n2. MySQL 驱动的类加载器:");
        try {
            Class<?> mysqlDriverClass = Class.forName("com.mysql.cj.jdbc.Driver");
            ClassLoader mysqlDriverLoader = mysqlDriverClass.getClassLoader();
            System.out.println("   " + mysqlDriverLoader);
            System.out.println("   说明: 由 Application ClassLoader 加载");
            System.out.println("   位置: classpath (Maven 依赖)");
        } catch (ClassNotFoundException e) {
            System.out.println("   MySQL 驱动未找到");
        }

        // 3. 问题分析
        System.out.println("\n3. 问题分析:");
        System.out.println("   DriverManager (Bootstrap) 需要加载 MySQL 驱动 (Application)");
        System.out.println("   但 Bootstrap ClassLoader 看不到 Application ClassLoader 的类！");
        System.out.println("   这违反了双亲委派模型（父看不到子）");

        // 4. 解决方案：使用线程上下文类加载器
        System.out.println("\n4. 解决方案:");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println("   线程上下文类加载器: " + contextClassLoader);
        System.out.println("   DriverManager 使用这个加载器来加载驱动");
        System.out.println("   这样就能加载到 Application ClassLoader 中的 MySQL 驱动了！");
    }

    @Test
    public void testServiceLoaderMechanism() {
        System.out.println("=== ServiceLoader 机制：线程上下文类加载器的应用 ===\n");

        System.out.println("1. ServiceLoader 的工作原理:");
        System.out.println("   ServiceLoader 是 Java SPI (Service Provider Interface) 的实现");
        System.out.println("   它使用线程上下文类加载器来加载服务提供者");

        System.out.println("\n2. JDBC 驱动的 SPI 配置:");
        System.out.println("   mysql-connector-j.jar");
        System.out.println("   └─ META-INF/services/");
        System.out.println("      └─ java.sql.Driver  ← 文件名是接口全名");
        System.out.println("         内容: com.mysql.cj.jdbc.Driver  ← 实现类全名");

        System.out.println("\n3. DriverManager 的加载过程:");
        System.out.println("   static {");
        System.out.println("       // 使用 ServiceLoader 加载驱动");
        System.out.println("       ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(");
        System.out.println("           Driver.class,");
        System.out.println("           Thread.currentThread().getContextClassLoader()  ← 关键！");
        System.out.println("       );");
        System.out.println("   }");

        // 实际演示
        System.out.println("\n4. 实际加载驱动:");
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class, contextCL);

        int count = 0;
        for (Driver driver : drivers) {
            count++;
            System.out.println("   [" + count + "] " + driver.getClass().getName());
            System.out.println("       加载器: " + driver.getClass().getClassLoader());
        }

        if (count == 0) {
            System.out.println("   没有找到 JDBC 驱动");
        }
    }
}
