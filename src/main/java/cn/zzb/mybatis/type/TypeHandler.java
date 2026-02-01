package cn.zzb.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 类型处理器
 */
public interface TypeHandler<T> {

    /**
     * 设置参数
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

}
