package cn.zzb.mybatis.test;

import cn.zzb.mybatis.binding.MapperProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class InitTest {

    @Test
    public void tets1(){
        Map<String, String> sqlSession = new HashMap<>();
        sqlSession.put("cn.zzb.mybatis.test.TestDao.Test1", "执行的是Test1的sql语句!");
        MapperProxyFactory<TestDao> mapperProxyFactory = new MapperProxyFactory<>(TestDao.class);
        TestDao testDao = mapperProxyFactory.newInstance(sqlSession);
        log.info(testDao.Test1("测试传入参数"));
    }
}
