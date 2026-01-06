package cn.zzb.mybatis.binding;

import java.lang.reflect.Proxy;
import java.util.Map;

public class MapperProxyFactory<T> {
    private final Class<T> mapperInteface;

    public MapperProxyFactory(Class<T> mapperInteface) {
        this.mapperInteface = mapperInteface;
    }

    public T newInstance(Map<String, String> sqlSession) {
        return (T) Proxy.newProxyInstance(mapperInteface.getClassLoader(),
                new Class[]{mapperInteface},
                new MapperProxy(mapperInteface, sqlSession));
    }

}
