package cn.zzb.mybatis.scripting.xmltags;


import cn.zzb.mybatis.executor.parameter.ParameterHandler;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.mapping.SqlSource;
import cn.zzb.mybatis.scripting.LanguageDriver;
import cn.zzb.mybatis.scripting.defaults.DefaultParameterHandler;
import cn.zzb.mybatis.scripting.defaults.RawSqlSource;
import cn.zzb.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * XML语言驱动器
 */
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterType) {
        // 用XML脚本构建器解析
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();
    }

    /**
     * 新增方法，用于处理注解配置 SQL 语句
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        // 暂时不解析动态 SQL
        return new RawSqlSource(configuration, script, parameterType);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

}