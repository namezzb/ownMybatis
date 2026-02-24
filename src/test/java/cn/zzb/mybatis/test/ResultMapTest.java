package cn.zzb.mybatis.test;

import cn.zzb.mybatis.SqlSessionFactoryBuilder;
import cn.zzb.mybatis.io.Resources;
import cn.zzb.mybatis.session.SqlSession;
import cn.zzb.mybatis.session.SqlSessionFactory;
import cn.zzb.mybatis.test.dao.ISkuDao;
import cn.zzb.mybatis.test.po.SkuVO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class ResultMapTest {

    @Test
    public void test_querySkuVOById() throws IOException {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        ISkuDao skuDao = sqlSession.getMapper(ISkuDao.class);
        SkuVO skuVO = skuDao.querySkuVOById(1L);
        log.info("resultMap 查询结果：{}", JSON.toJSONStringWithDateFormat(skuVO, "yyyy-MM-dd HH:mm:ss"));
    }

}