package cn.zzb.mybatis.mapping;

import cn.zzb.mybatis.executor.keygen.Jdbc3KeyGenerator;
import cn.zzb.mybatis.executor.keygen.KeyGenerator;
import cn.zzb.mybatis.executor.keygen.NoKeyGenerator;
import cn.zzb.mybatis.scripting.LanguageDriver;
import cn.zzb.mybatis.session.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author 小傅哥，微信：fustack
 * @description 映射语句类
 * @date 2022/04/06
 * @github https://github.com/fuzhengwei
 * @copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class MappedStatement {

    private String resource;
    private Configuration configuration;
    private String id;
    private SqlCommandType sqlCommandType;
    private SqlSource sqlSource;
    Class<?> resultType;
    private LanguageDriver lang;
    private List<ResultMap> resultMaps;
    //一级缓存相关
    private boolean flushCacheRequired;
    // step-14 新增
    private KeyGenerator keyGenerator;
    private String[] keyProperties;
    private String[] keyColumns;

    MappedStatement() {
        // constructor disabled
    }

    /**
     * 根据参数对象生成可执行的 BoundSql。
     * <p>
     * 对于静态 SQL（RawSqlSource），每次返回相同的 SQL 结构；
     * 对于动态 SQL（DynamicSqlSource），每次调用都会重新计算 if/where/foreach 等标签，
     * 根据当前参数值生成不同的 SQL 片段。
     *
     * @param parameterObject 调用 Mapper 方法时传入的参数对象
     * @return BoundSql，包含最终可执行的 SQL 字符串和对应的参数映射列表
     */
    public BoundSql getBoundSql(Object parameterObject) {
        // 调用 SqlSource#getBoundSql
        return sqlSource.getBoundSql(parameterObject);
    }

    /**
     * 建造者
     */
    public static class Builder {

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, SqlSource sqlSource, Class<?> resultType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.resultType = resultType;
            mappedStatement.keyGenerator = configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? new Jdbc3KeyGenerator() : new NoKeyGenerator();
            mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
            return mappedStatement;
        }

        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public String id() {
            return mappedStatement.id;
        }

        public Builder resultMaps(List<ResultMap> resultMaps) {
            mappedStatement.resultMaps = resultMaps;
            return this;
        }

        public Builder keyGenerator(KeyGenerator keyGenerator) {
            mappedStatement.keyGenerator = keyGenerator;
            return this;
        }

        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyProperties = delimitedStringToArray(keyProperty);
            return this;
        }

    }

    private static String[] delimitedStringToArray(String in) {
        if (in == null || in.trim().length() == 0) {
            return null;
        } else {
            return in.split(",");
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public LanguageDriver getLang() {
        return lang;
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public String[] getKeyProperties() {
        return keyProperties;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public String getResource() {
        return resource;
    }

    public boolean isFlushCacheRequired() {
        return flushCacheRequired;
    }

}
