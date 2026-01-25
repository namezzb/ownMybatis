package cn.zzb.mybatis.test;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.datasource.pooled.PooledDataSource;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.test.dao.IUserDao;
import cn.zzb.mybatis.test.po.Sku;
import cn.zzb.mybatis.test.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 小傅哥，微信：fustack
 * @description 单元测试
 * @date 2022/3/26
 * @github https://github.com/fuzhengwei
 * @Copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
@Slf4j
public class InitTest {

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

    @Test
    public void test_pooled() throws SQLException, InterruptedException {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver("com.mysql.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true");
        pooledDataSource.setUsername("root");
        pooledDataSource.setPassword("123456");
        // 持续获得链接
        while (true){
            Connection connection = pooledDataSource.getConnection();
            System.out.println(connection);
            Thread.sleep(1000);
            connection.close();
        }
    }

}
