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
import java.math.BigDecimal;

@Slf4j
public class CrudTest {

    @Test
    public void test_insert() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao userDao = sqlSession.getMapper(ISkuDao.class);

        Sku sku = new Sku();
        sku.setSource("s02");
        sku.setGoods_id("G004");
        sku.setGoods_name("测试slelectKeys");
        sku.setChannel("app");
        sku.setOriginal_price(new BigDecimal("99.99"));

        int rows = userDao.insertSku(sku);
        sqlSession.commit();
        log.info("insert 影响行数：{}", rows);
        log.info("返回的原始对象: {}", JSON.toJSONString(sku));
    }

    @Test
    public void test_update() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao userDao = sqlSession.getMapper(ISkuDao.class);

        Sku sku = new Sku();
        sku.setId(1L);
        sku.setGoods_name("更新后的商品名");
        sku.setOriginal_price(new BigDecimal("199.99"));

        int rows = userDao.updateSku(sku);
        sqlSession.commit();
        log.info("update 影响行数：{}", rows);
    }

    @Test
    public void test_delete() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao userDao = sqlSession.getMapper(ISkuDao.class);

        int rows = userDao.deleteSkuById(1L);
        sqlSession.commit();
        log.info("delete 影响行数：{}", rows);
    }
}