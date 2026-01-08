package cn.zzb.mybatis.session.defaults;

import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
