package cn.zzb.mybatis.scripting.xmltags;


import cn.zzb.mybatis.mapping.SqlSource;
import cn.zzb.mybatis.scripting.LanguageDriver;
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

}