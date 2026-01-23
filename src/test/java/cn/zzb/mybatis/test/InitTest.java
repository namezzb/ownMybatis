package cn.zzb.mybatis.test;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.binding.MapperProxyFactory;
import cn.zzb.mybatis.binding.MapperRegistry;
import cn.zzb.mybatis.builder.xml.XMLConfigBuilder;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.session.defaults.DefaultSqlSession;
import cn.zzb.mybatis.session.defaults.DefaultSqlSessionFactory;
import cn.zzb.mybatis.test.dao.IUserDao;
import cn.zzb.mybatis.test.po.Sku;
import cn.zzb.mybatis.test.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


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
        Sku sku = userDao.querySkuById(1L);

        log.info("测试结果：{}", JSONUtil.toJsonStr(sku));
    }

    @Test
    public void test_selectOne() throws IOException {
        // 解析 XML
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 获取 DefaultSqlSession
        SqlSession sqlSession = new DefaultSqlSession(configuration);

        // 执行查询：默认是一个集合参数
        Object[] req = {1L};
        Object res = sqlSession.selectOne("cn.zzb.mybatis.test.dao.IUserDao.querySkuById", req);
        log.info("测试结果：{}", JSONUtil.toJsonStr(res));
    }

}
