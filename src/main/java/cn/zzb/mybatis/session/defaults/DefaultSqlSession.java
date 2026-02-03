package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.executor.Executor;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;

import java.util.List;

/**
 * SqlSession 的默认实现类
 * <p>
 * 核心职责：
 * 1. 作为 MyBatis 操作数据库的统一入口，提供查询、更新等数据库操作方法
 * 2. 持有 Configuration 全局配置对象和 Executor 执行器
 * 3. 协调 Configuration、Executor、MappedStatement 完成 SQL 执行流程
 * <p>
 * 工作流程：
 * 用户调用 selectOne() → 获取 MappedStatement → 生成 BoundSql →
 * 调用 Executor 执行查询 → 返回结果
 * <p>
 * 设计模式：
 * - 外观模式：封装底层复杂的 SQL 执行流程，提供简洁的 API
 * - 委托模式：将实际的 SQL 执行委托给 Executor
 *
 * @author zzb
 */
public class DefaultSqlSession implements SqlSession {

    /** 全局配置对象，包含所有配置信息和注册器 */
    private Configuration configuration;

    /** SQL 执行器，负责实际的 SQL 执行和结果处理 */
    private Executor executor;

    /**
     * 构造函数
     *
     * @param configuration 全局配置对象
     * @param executor      SQL 执行器
     */
    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    /**
     * 查询单条记录（无参数版本）
     * <p>
     * 委托给带参数的 selectOne 方法，参数传 null
     *
     * @param statement SQL 语句的唯一标识（namespace.id）
     * @param <T>       返回结果类型
     * @return 查询结果对象
     */
    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

    /**
     * 查询单条记录（带参数版本）
     * <p>
     * 核心执行流程：
     * 1. 根据 statement 从 Configuration 获取 MappedStatement（包含 SQL 配置信息）
     * 2. 通过 SqlSource.getBoundSql() 生成可执行的 BoundSql（包含 SQL 和参数映射）
     * 3. 调用 Executor.query() 执行查询
     * 4. 从结果列表中取第一条记录返回
     *
     * @param statement SQL 语句的唯一标识（namespace.id），如 "cn.zzb.mybatis.dao.IUserDao.queryUserInfoById"
     * @param parameter SQL 参数对象，可以是基本类型、POJO 或 Map
     * @param <T>       返回结果类型
     * @return 查询结果对象，如果发生异常则返回 null
     */
    @Override
    public <T> T selectOne(String statement, Object parameter){
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            List<T> query = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getSqlSource().getBoundSql(parameter));
            return query.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取全局配置对象
     *
     * @return Configuration 对象
     */
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 获取 Mapper 接口的代理对象
     * <p>
     * 通过动态代理技术，为 Mapper 接口生成代理实现类。
     * 当调用 Mapper 接口方法时，会被 MapperProxy 拦截并转换为 SQL 执行。
     *
     * @param type Mapper 接口的 Class 对象
     * @param <T>  Mapper 接口类型
     * @return Mapper 接口的代理对象
     */
    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
