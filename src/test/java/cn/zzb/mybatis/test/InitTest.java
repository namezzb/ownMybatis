package cn.zzb.mybatis.test;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.test.dao.IUserDao;
import cn.zzb.mybatis.test.po.Sku;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
@Slf4j
public class InitTest {


    @Test
    public void test_SignalSqlSessionFactory() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        Sku sku = userDao.querySkuById(1L);
        log.info("测试结果：{}", JSONUtil.toJsonStr(sku, JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss")));
    }


    @Test
    public void test_SqlSessionFactory() throws IOException {
        // 1. 从SqlSessionFactory中获取SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2. 获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        for (int i = 0; i < 50; i++) {
            Sku sku = userDao.querySkuById(1L);
            log.info("测试结果：{}", JSONUtil.toJsonStr(sku, JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss")));
        }
    }

}
