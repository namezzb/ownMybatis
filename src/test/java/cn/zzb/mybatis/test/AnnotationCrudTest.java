package cn.zzb.mybatis.test;

import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.test.dao.ISkuAnnotationDao;
import cn.zzb.mybatis.test.po.Sku;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
public class AnnotationCrudTest {

    @Test
    public void test_select() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuAnnotationDao dao = sqlSession.getMapper(ISkuAnnotationDao.class);
        Sku sku = dao.querySkuById(1L);
        log.info("select 结果：{}", JSON.toJSONString(sku));
    }

    @Test
    public void test_insert() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuAnnotationDao dao = sqlSession.getMapper(ISkuAnnotationDao.class);
        Sku sku = new Sku();
        sku.setSource("anno");
        sku.setChannel("web");
        sku.setGoods_id("G002");
        sku.setGoods_name("注解测试商品");
        sku.setOriginal_price(new BigDecimal("88.88"));

        int rows = dao.insertSku(sku);
        sqlSession.commit();
        log.info("insert 影响行数：{}", rows);
    }

    @Test
    public void test_update() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuAnnotationDao dao = sqlSession.getMapper(ISkuAnnotationDao.class);
        Sku sku = new Sku();
        sku.setId(1L);
        sku.setGoods_name("注解更新商品名");
        sku.setOriginal_price(new BigDecimal("188.88"));

        int rows = dao.updateSku(sku);
        sqlSession.commit();
        log.info("update 影响行数：{}", rows);
    }

    @Test
    public void test_delete() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuAnnotationDao dao = sqlSession.getMapper(ISkuAnnotationDao.class);
        int rows = dao.deleteSkuById(1L);
        sqlSession.commit();
        log.info("delete 影响行数：{}", rows);
    }
}