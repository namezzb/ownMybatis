package cn.zzb.mybatis.test.dao;

import cn.zzb.mybatis.test.po.Sku;
import cn.zzb.mybatis.test.po.SkuVO;
import java.util.List;

public interface ISkuDao {

    Sku querySkuById(Long skuId);

    // 使用 resultMap 映射驼峰属性
    SkuVO querySkuVOById(Long id);

    Sku querySkuByInfo(Sku sku);

    int insertSku(Sku sku);

    int updateSku(Sku sku);

    int deleteSkuById(Long id);

    // 动态 SQL：if + trim(WHERE) 按条件查询
    List<Sku> querySkuByCondition(Sku sku);

}
