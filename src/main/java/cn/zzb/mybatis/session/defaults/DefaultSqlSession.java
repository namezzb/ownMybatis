package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("拿到了sqlId是" + statement + ", sql是" + mappedStatement.getSql());
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("拿到了sqlId是" + statement + ", sql是" + mappedStatement.getSql() + ", parameter是" + parameter);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
