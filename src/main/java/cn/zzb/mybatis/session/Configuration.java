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
import cn.zzb.mybatis.transaction.Transaction;
import cn.zzb.mybatis.transaction.jdbc.JdbcTransactionFactory;
import cn.zzb.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    //环境
    protected Environment environment;

    // 映射注册机
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    // 映射的语句，存在Map里
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    // 类型别名注册机
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }


    //Mapper
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }


    //MapperStatement
    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    //Envviroment
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnviroment(Environment environment) {
        this.environment = environment;
    }

    //TypeAliasRegistry
    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }



    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    /**
     * 生产执行器
     */
    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this, transaction);
    }

    /**
     * 创建语句处理器
     */
    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        return new PreparedStatementHandler(executor, mappedStatement, parameter, resultHandler, boundSql);
    }

}
