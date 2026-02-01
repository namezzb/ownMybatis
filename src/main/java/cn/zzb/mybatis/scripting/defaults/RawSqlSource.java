package cn.zzb.mybatis.scripting.defaults;


import cn.zzb.mybatis.builder.SqlSourceBuilder;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.SqlSource;
import cn.zzb.mybatis.scripting.xmltags.DynamicContext;
import cn.zzb.mybatis.scripting.xmltags.SqlNode;
import cn.zzb.mybatis.session.Configuration;

import java.util.HashMap;

/**
 * 原始SQL源码，比 DynamicSqlSource 动态SQL处理快
 */
public class RawSqlSource implements SqlSource {

    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
        this(configuration, getSql(configuration, rootSqlNode), parameterType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

}
