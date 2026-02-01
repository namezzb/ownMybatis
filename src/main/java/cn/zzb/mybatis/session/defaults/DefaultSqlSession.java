package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.executor.Executor;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;

import java.util.List;


public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return selectOne(statement, null);
    }

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

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
}
