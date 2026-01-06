package cn.zzb.mybatis.test;

import cn.zzb.mybatis.binding.MapperProxyFactory;
import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.defaults.DefaultSqlSessionFactory;
import cn.zzb.mybatis.test.dao.IUserDao;
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
//        TestDao testDao = mapperProxyFactory.newInstance(sqlSession);
//        log.info(testDao.Test1("测试传入参数"));
    }

    @Test
    public void test2(){
        //Registry
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.addMappers("cn.zzb.mybatis.test.dao");
        //Get sqlSession
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(mapperRegistry);
        SqlSession sqlSession = defaultSqlSessionFactory.openSession();
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        //Test
        String result = mapper.selectUserNameById(1);
        log.info("result:{}", result);
    }
}
