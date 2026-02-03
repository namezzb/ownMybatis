package cn.zzb.mybatis.session;

import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.datasource.druid.DruidDataSourceFactory;
import cn.zzb.mybatis.datasource.pooled.PooledDataSourceFactory;
import cn.zzb.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import cn.zzb.mybatis.executor.Executor;
import cn.zzb.mybatis.executor.SimpleExecutor;
import cn.zzb.mybatis.executor.resultset.DefaultResultSetHandler;
import cn.zzb.mybatis.executor.resultset.ResultSetHandler;
import cn.zzb.mybatis.executor.statement.PreparedStatementHandler;
import cn.zzb.mybatis.executor.statement.StatementHandler;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.Environment;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.reflection.MetaObject;
import cn.zzb.mybatis.reflection.factory.DefaultObjectFactory;
import cn.zzb.mybatis.reflection.factory.ObjectFactory;
import cn.zzb.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.zzb.mybatis.reflection.wrapper.ObjectWrapperFactory;
import cn.zzb.mybatis.scripting.LanguageDriverRegistry;
import cn.zzb.mybatis.scripting.xmltags.XMLLanguageDriver;
import cn.zzb.mybatis.transaction.Transaction;
import cn.zzb.mybatis.transaction.jdbc.JdbcTransactionFactory;
import cn.zzb.mybatis.type.TypeAliasRegistry;
import cn.zzb.mybatis.type.TypeHandlerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * MyBatis 全局配置中心
 * <p>
 * 核心职责：
 * 1. 管理所有配置信息：环境配置、类型别名、类型处理器、语言驱动等
 * 2. 注册和管理 Mapper 接口与 MappedStatement（SQL 映射语句）
 * 3. 提供工厂方法：创建 Executor、StatementHandler、ResultSetHandler 等核心组件
 * 4. 作为各个组件之间的协调者和配置提供者
 * <p>
 * 设计模式：
 * - 单例模式：全局唯一的配置对象
 * - 工厂模式：提供各种处理器的创建方法
 * - 注册器模式：管理类型别名、类型处理器、Mapper 等的注册
 * <p>
 * 生命周期：
 * 应用启动时创建 → 解析配置文件填充 → 整个应用生命周期内使用 → 应用关闭时销毁
 *
 * @author zzb
 */
public class Configuration {

    /** 环境配置：包含数据源和事务管理器 */
    protected Environment environment;

    /** Mapper 注册器：管理 Mapper 接口的注册和代理对象创建 */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /** MappedStatement 存储容器：key 为 namespace.id，value 为 SQL 映射语句对象 */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    /** 类型别名注册器：管理 Java 类型的简短别名（如 string -> java.lang.String） */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /** 语言驱动注册器：管理不同的 SQL 语言驱动（XML、注解等） */
    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

    /** 类型处理器注册器：管理 Java 类型与 JDBC 类型之间的转换 */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

    /** 对象工厂：用于创建结果对象实例 */
    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    /** 对象包装器工厂：用于创建对象包装器，统一处理 Bean/Map/Collection */
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    /** 已加载的资源集合：防止重复加载同一个 Mapper.xml 文件 */
    protected final Set<String> loadedResources = new HashSet<>();

    /** 数据库厂商标识：用于支持多数据库 SQL 方言 */
    protected String databaseId;

    /**
     * 构造函数：初始化默认配置
     * <p>
     * 注册内置的类型别名：
     * - JDBC：JDBC 事务工厂
     * - DRUID/UNPOOLED/POOLED：三种数据源工厂
     * <p>
     * 设置默认的语言驱动为 XMLLanguageDriver
     */
    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);

        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
    }

    /**
     * 批量注册指定包下的所有 Mapper 接口
     *
     * @param packageName 包名，如 "cn.zzb.mybatis.dao"
     */
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    /**
     * 注册单个 Mapper 接口
     *
     * @param type Mapper 接口的 Class 对象
     * @param <T>  Mapper 接口类型
     */
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    /**
     * 获取 Mapper 接口的代理对象
     * <p>
     * 通过 JDK 动态代理为 Mapper 接口生成代理实现，
     * 代理对象会拦截方法调用并转换为 SQL 执行。
     *
     * @param type       Mapper 接口的 Class 对象
     * @param sqlSession SQL 会话对象
     * @param <T>        Mapper 接口类型
     * @return Mapper 接口的代理对象
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    /**
     * 判断是否已注册指定的 Mapper 接口
     *
     * @param type Mapper 接口的 Class 对象
     * @return 如果已注册返回 true，否则返回 false
     */
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    /**
     * 添加 MappedStatement 到配置中
     * <p>
     * MappedStatement 封装了一条 SQL 语句的所有配置信息，
     * 包括 SQL 类型、参数映射、结果映射等。
     *
     * @param ms MappedStatement 对象
     */
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    /**
     * 根据 statement id 获取 MappedStatement
     *
     * @param id statement 的唯一标识（namespace.id），如 "cn.zzb.mybatis.dao.IUserDao.queryUserInfoById"
     * @return MappedStatement 对象
     */
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    /**
     * 获取类型别名注册器
     *
     * @return TypeAliasRegistry 对象
     */
    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    /**
     * 获取环境配置
     *
     * @return Environment 对象，包含数据源和事务管理器
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * 设置环境配置
     *
     * @param environment Environment 对象
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 获取数据库厂商标识
     *
     * @return 数据库 ID，如 "mysql"、"oracle" 等
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * 创建结果集处理器（工厂方法）
     * <p>
     * ResultSetHandler 负责将 JDBC ResultSet 转换为 Java 对象。
     * 当前实现返回 DefaultResultSetHandler，支持基本的结果映射。
     *
     * @param executor        SQL 执行器
     * @param mappedStatement SQL 映射语句对象
     * @param boundSql        绑定的 SQL 对象（包含最终 SQL 和参数映射）
     * @return ResultSetHandler 实例
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 创建 SQL 执行器（工厂方法）
     * <p>
     * Executor 是 SQL 执行的核心组件，负责：
     * 1. 创建 StatementHandler 并执行 SQL
     * 2. 调用 ResultSetHandler 处理结果集
     * 3. 管理一级缓存（当前简化版未实现）
     * <p>
     * 当前实现返回 SimpleExecutor，每次执行都创建新的 Statement。
     * 未来可扩展为 ReuseExecutor（复用 Statement）或 BatchExecutor（批处理）。
     *
     * @param transaction 事务对象，用于获取数据库连接
     * @return Executor 实例
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器（工厂方法）
     * <p>
     * StatementHandler 负责：
     * 1. 创建 JDBC Statement 对象（PreparedStatement/Statement/CallableStatement）
     * 2. 设置 SQL 参数
     * 3. 执行 SQL 语句
     * 4. 封装执行结果
     * <p>
     * 当前实现返回 PreparedStatementHandler，使用预编译的 PreparedStatement。
     *
     * @param executor        SQL 执行器
     * @param mappedStatement SQL 映射语句对象
     * @param parameter       SQL 参数对象
     * @param resultHandler   结果处理器（当前版本未使用）
     * @param boundSql        绑定的 SQL 对象
     * @return StatementHandler 实例
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

    /**
     * 创建元对象（MetaObject）
     * <p>
     * MetaObject 是对任意对象的反射操作封装，提供统一的属性访问接口。
     * 支持复杂属性路径访问，如 "user.address.city"。
     *
     * @param object 需要包装的对象
     * @return MetaObject 实例
     */
    // 创建元对象
    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
    }

    /**
     * 获取类型处理器注册器
     *
     * @return TypeHandlerRegistry 对象
     */
    // 类型处理器注册机
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    /**
     * 判断指定资源是否已加载
     * <p>
     * 用于防止重复加载同一个 Mapper.xml 文件。
     *
     * @param resource 资源路径，如 "mapper/UserMapper.xml"
     * @return 如果已加载返回 true，否则返回 false
     */
    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    /**
     * 标记资源为已加载
     * <p>
     * 在解析完 Mapper.xml 文件后调用，防止重复解析。
     *
     * @param resource 资源路径
     */
    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    /**
     * 获取语言驱动注册器
     * <p>
     * LanguageDriverRegistry 管理不同的 SQL 语言驱动，
     * 如 XMLLanguageDriver（解析 XML 中的 SQL）、RawLanguageDriver（注解 SQL）等。
     *
     * @return LanguageDriverRegistry 对象
     */
    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

}
