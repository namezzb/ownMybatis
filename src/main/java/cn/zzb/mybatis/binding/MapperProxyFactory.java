package cn.zzb.mybatis.binding;

import cn.zzb.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

public class MapperProxyFactory<T> {
    private final Class<T> mapperInteface;

    public MapperProxyFactory(Class<T> mapperInteface) {
        this.mapperInteface = mapperInteface;
    }

    public T newInstance(SqlSession sqlSession) {
        return (T) Proxy.newProxyInstance(mapperInteface.getClassLoader(),
                new Class[]{mapperInteface},
                new MapperProxy(mapperInteface, sqlSession));
    }

}
