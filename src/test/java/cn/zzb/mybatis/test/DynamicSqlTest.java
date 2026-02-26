package cn.zzb.mybatis.test;

import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.test.dao.ISkuDao;
import cn.zzb.mybatis.test.po.Sku;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DynamicSqlTest {

    /**
     * 动态查询：所有条件都有值，三个 if 全部生效
     */
    @Test
    public void test_queryByCondition_allParams() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao skuDao = sqlSession.getMapper(ISkuDao.class);

        Sku condition = new Sku();
        condition.setSource("s01");
        condition.setChannel("c01");
        condition.setGoods_name("《手写MyBatis：渐进式源码实践》");

        List<Sku> result = skuDao.querySkuByCondition(condition);
        log.info("全条件查询结果：{}", JSON.toJSONString(result));
    }

    /**
     * 动态查询：只传 source，其余 if 不生效
     */
    @Test
    public void test_queryByCondition_partialParams() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao skuDao = sqlSession.getMapper(ISkuDao.class);

        Sku condition = new Sku();
        condition.setSource("s01");

        List<Sku> result = skuDao.querySkuByCondition(condition);
        log.info("单条件查询结果：{}", JSON.toJSONString(result));
    }

    /**
     * 动态查询：所有条件为空，trim 不生成 WHERE，查全表
     */
    @Test
    public void test_queryByCondition_noParams() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao skuDao = sqlSession.getMapper(ISkuDao.class);

        List<Sku> result = skuDao.querySkuByCondition(new Sku());
        log.info("无条件查询结果（全表）：{}", JSON.toJSONString(result));
    }
}