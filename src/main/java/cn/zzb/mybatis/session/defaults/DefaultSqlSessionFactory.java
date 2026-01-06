package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
