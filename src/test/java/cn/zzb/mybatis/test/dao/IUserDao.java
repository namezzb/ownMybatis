package cn.zzb.mybatis.test.dao;

import cn.zzb.mybatis.test.po.Sku;
import cn.zzb.mybatis.test.po.User;

public interface IUserDao {

    User queryUserInfoById(Long userId);

    Sku querySkuById(Long skuId);
}
