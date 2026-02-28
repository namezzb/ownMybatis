package cn.zzb.mybatis.test.plugin;

import cn.zzb.mybatis.executor.statement.StatementHandler;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.plugin.Interceptor;
import cn.zzb.mybatis.plugin.Intercepts;
import cn.zzb.mybatis.plugin.Invocation;
import cn.zzb.mybatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * 测试plugin的类
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class TestPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // 获取SQL信息
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        // 输出SQL
        System.out.println("拦截到了方法: " + "StatementHandler.prepare");
        System.out.println("拦截SQL：" + sql);
        // 放行
        return invocation.proceed();
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("参数输出：" + properties.getProperty("test00"));
    }

}