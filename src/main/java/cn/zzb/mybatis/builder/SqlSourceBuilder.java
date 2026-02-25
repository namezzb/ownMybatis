package cn.zzb.mybatis.builder;


import cn.zzb.mybatis.mapping.ParameterMapping;
import cn.zzb.mybatis.mapping.SqlSource;
import cn.zzb.mybatis.parsing.GenericTokenParser;
import cn.zzb.mybatis.parsing.TokenHandler;
import cn.zzb.mybatis.reflection.MetaClass;
import cn.zzb.mybatis.reflection.MetaObject;
import cn.zzb.mybatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL 源码构建器
 */
public class SqlSourceBuilder extends BaseBuilder {

    private static Logger logger = LoggerFactory.getLogger(SqlSourceBuilder.class);

    private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    /**
     * 将含有 #{} 占位符的原始 SQL 解析为 StaticSqlSource。
     * <p>
     * 解析过程：
     * 1. 创建 ParameterMappingTokenHandler，负责将每个 #{} 替换为 ? 并收集参数映射信息
     * 2. 使用 GenericTokenParser 扫描 SQL 字符串，遇到 #{...} 时回调 handler
     * 3. 解析完成后，SQL 中所有 #{} 均已替换为 ?，参数映射列表也已构建完毕
     * 4. 将最终 SQL 和参数映射封装为 StaticSqlSource 返回
     *
     * @param originalSql          含有 #{} 占位符的原始 SQL 字符串
     * @param parameterType        参数对象的 Java 类型
     * @param additionalParameters 额外的上下文参数（如动态 SQL 绑定变量）
     * @return 解析完成的 StaticSqlSource，包含可直接执行的 SQL 和参数映射列表
     */
    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        // 返回静态 SQL
        return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;
        private MetaObject metaParameters;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
            this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        /**
         * 设置ParameterMapping
         * @param content
         * @return
         */
        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        // 构建参数映射
        private ParameterMapping buildParameterMapping(String content) {
            // 先解析参数映射,就是转化成一个 HashMap | #{favouriteSection,jdbcType=VARCHAR}
            Map<String, String> propertiesMap = new ParameterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType;
            if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
                propertyType = parameterType;
            } else if (property != null) {
                MetaClass metaClass = MetaClass.forClass(parameterType);
                if (metaClass.hasGetter(property)) {
                    propertyType = metaClass.getGetterType(property);
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }

            logger.info("构建参数映射 property：{} propertyType：{}", property, propertyType);
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            return builder.build();
        }

    }

}