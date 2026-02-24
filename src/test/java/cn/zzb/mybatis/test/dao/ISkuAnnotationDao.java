package cn.zzb.mybatis.test.dao;

import cn.zzb.mybatis.annotations.Delete;
import cn.zzb.mybatis.annotations.Insert;
import cn.zzb.mybatis.annotations.Select;
import cn.zzb.mybatis.annotations.Update;
import cn.zzb.mybatis.test.po.Sku;

public interface ISkuAnnotationDao {

    @Select("SELECT id, source, goods_id, goods_name, original_price, create_time, update_time FROM sku WHERE id = #{id}")
    Sku querySkuById(Long id);

    @Insert("INSERT INTO sku (source, channel, goods_id, goods_name, original_price) VALUES (#{source}, #{channel}, #{goods_id}, #{goods_name}, #{original_price})")
    int insertSku(Sku sku);

    @Update("UPDATE sku SET goods_name = #{goods_name}, original_price = #{original_price} WHERE id = #{id}")
    int updateSku(Sku sku);

    @Delete("DELETE FROM sku WHERE id = #{id}")
    int deleteSkuById(Long id);
}