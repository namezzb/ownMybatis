package cn.zzb.mybatis;

import cn.zzb.mybatis.builder.xml.XMLConfigBuilder;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;
import java.net.PortUnreachableException;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }


    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
