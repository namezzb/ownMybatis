package cn.zzb.mybatis.test.dao;

import cn.zzb.mybatis.test.po.Sku;
import cn.zzb.mybatis.test.po.SkuVO;

public interface ISkuDao {

    Sku querySkuById(Long skuId);

    // 使用 resultMap 映射驼峰属性
    SkuVO querySkuVOById(Long id);

    Sku querySkuByInfo(Sku sku);

    int insertSku(Sku sku);

    int updateSku(Sku sku);

    int deleteSkuById(Long id);
}
