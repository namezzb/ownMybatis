package cn.zzb.mybatis.binding;

import cn.zzb.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Mapper 接口的动态代理类
 * <p>
 * 核心职责：
 * 1. 实现 JDK 动态代理的 InvocationHandler 接口，拦截 Mapper 接口方法调用
 * 2. 将 Mapper 接口方法调用转换为 SQL 执行
 * 3. 缓存 MapperMethod 对象，避免重复创建，提升性能
 * <p>
 * 工作流程：
 * 用户调用 Mapper 方法 → invoke() 拦截 → 获取/创建 MapperMethod → 执行 SQL → 返回结果
 *
 * @param <T> Mapper 接口类型
 * @author zzb
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 1L;

    /** Mapper 接口的 Class 对象 */
    private final Class<T> mapperInterface;

    /** SQL 会话对象，用于执行数据库操作 */
    private SqlSession sqlSession;

    /** MapperMethod 缓存，key 为 Method 对象，value 为对应的 MapperMethod */
    private final Map<Method, MapperMethod> methodCache;

    /**
     * 构造函数
     *
     * @param mapperInterface Mapper 接口的 Class 对象
     * @param sqlSession      SQL 会话对象
     * @param methodCache     方法缓存 Map
     */
    public MapperProxy(Class<T> mapperInterface, SqlSession sqlSession, Map<Method, MapperMethod> methodCache) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
        this.methodCache = methodCache;
    }

    /**
     * 代理方法调用的核心逻辑
     * <p>
     * 拦截 Mapper 接口的方法调用，并将其转换为 SQL 执行。
     * 对于 Object 类的方法（如 toString、hashCode、equals），直接调用原方法。
     * 对于 Mapper 接口的方法，通过 MapperMethod 执行 SQL 操作。
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 方法执行结果
     * @throws Throwable 执行过程中可能抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            // Object 类的方法直接调用，不走 SQL 执行流程
            return method.invoke(this, args);
        }else{
            // Mapper 接口方法：获取 MapperMethod 并执行 SQL
            MapperMethod mapperMethod = cachedMapperMethod(method);
            return mapperMethod.execute(sqlSession, args);
        }
    }


    /**
     * 从缓存中获取 MapperMethod，如果不存在则创建并缓存
     * <p>
     * 性能优化策略：
     * 1. 首次调用时创建 MapperMethod 对象并放入缓存
     * 2. 后续调用直接从缓存获取，避免重复创建
     * 3. MapperMethod 封装了 SQL 命令类型和方法签名信息
     *
     * @param method Mapper 接口的方法对象
     * @return 对应的 MapperMethod 对象
     */
    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod == null) {
            //找不到才去new
            mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
            methodCache.put(method, mapperMethod);
        }
        return mapperMethod;
    }
}
