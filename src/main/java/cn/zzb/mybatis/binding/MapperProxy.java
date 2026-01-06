package cn.zzb.mybatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 1L;

    private final Class<T> mapperInterface;
    private Map<String, String> sqlSession;

    public MapperProxy(Class<T> mapperInterface, Map<String, String> sqlSession) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this, args);
        }else{
            String s = "测试代理了!" + "\n" + sqlSession.get(mapperInterface.getName() + "." + method.getName()) + "\n" + "method: " +
                    method.getName() + "(" + Arrays.toString(args) + ")";
            return s;
        }
    }
}
