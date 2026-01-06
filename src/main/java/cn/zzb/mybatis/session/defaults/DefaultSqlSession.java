package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.session.SqlSession;

public class DefaultSqlSession implements SqlSession {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("被代理了!!方法是: selectOne(String statement), sqlid: " + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return (T) ("被代理了!!方法是: selectOne(String statement, Object parameter), sqlid: " +
                statement + ", parameter: " + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type, this);
    }
}
