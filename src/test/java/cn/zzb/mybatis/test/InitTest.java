package cn.zzb.mybatis.test;

import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.binding.MapperProxyFactory;
import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.session.defaults.DefaultSqlSessionFactory;
import cn.zzb.mybatis.test.dao.IUserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class InitTest {

    @Test
    public void init() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);
        String result = iUserDao.selectUserNameById(1);
        System.out.println(result);
    }
}
