package cn.zzb.mybatis.binding;

import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.mapping.SqlCommandType;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.SqlSession;

import java.lang.reflect.Method;

public class MapperMethod {

    private final SqlCommand sqlCommand;


    public MapperMethod(Class<?> mapperClass, Method method, Configuration configuration) {
        sqlCommand = new SqlCommand(mapperClass, method, configuration);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (sqlCommand.getType()) {
            case INSERT:
                break;
            case DELETE:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(sqlCommand.getName(), args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + sqlCommand.getName());
        }
        return result;
    }

    public static class SqlCommand {

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Class<?> mapperClass, Method method, Configuration configuration) {
            String statementName = mapperClass.getName() + "." + method.getName();
            MappedStatement ms = configuration.getMappedStatement(statementName);
            type = ms.getSqlCommandType();
            name = ms.getId();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }
}
