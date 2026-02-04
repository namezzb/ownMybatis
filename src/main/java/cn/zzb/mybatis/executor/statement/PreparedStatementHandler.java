package cn.zzb.mybatis.executor.statement;

import cn.zzb.mybatis.executor.Executor;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 预处理语句处理器（PreparedStatement Handler）
 * <p>
 * 核心职责：
 * 1. 使用 JDBC 的 PreparedStatement 执行 SQL，支持预编译和参数绑定
 * 2. 防止 SQL 注入攻击（通过 ? 占位符和参数绑定）
 * 3. 提升性能（数据库可以缓存预编译的执行计划）
 * <p>
 * 工作流程：
 * instantiateStatement() 创建 PreparedStatement →
 * parameterize() 设置参数 →
 * query() 执行查询并处理结果集
 * <p>
 * 与其他 StatementHandler 的区别：
 * - PreparedStatementHandler：使用 PreparedStatement，支持参数绑定（推荐）
 * - SimpleStatementHandler：使用 Statement，不支持参数绑定
 * - CallableStatementHandler：使用 CallableStatement，用于存储过程
 *
 * @author zzb
 */
public class PreparedStatementHandler extends BaseStatementHandler{

    /**
     * 构造函数
     *
     * @param executor        SQL 执行器
     * @param mappedStatement SQL 映射语句对象
     * @param parameterObject SQL 参数对象
     * @param resultHandler   结果处理器
     * @param boundSql        绑定的 SQL 对象
     */
    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    /**
     * 创建 PreparedStatement 对象
     * <p>
     * 从 BoundSql 中获取已处理的 SQL（#{} 已替换为 ?），
     * 然后通过数据库连接创建预编译的 PreparedStatement。
     * <p>
     * 预编译的优势：
     * 1. 数据库可以缓存执行计划，提升性能
     * 2. 防止 SQL 注入攻击
     * 3. 支持参数类型检查
     *
     * @param connection 数据库连接
     * @return PreparedStatement 对象
     * @throws SQLException SQL 异常
     */
    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        String sql = boundSql.getSql();
        return connection.prepareStatement(sql);
    }

    /**
     * 设置 SQL 参数
     * <p>
     * 将参数对象中的值绑定到 PreparedStatement 的 ? 占位符上。
     * 当前实现为简化版本，仅支持 Long 类型参数的硬编码绑定。
     * <p>
     * 完整版本应该：
     * 1. 根据 BoundSql 中的 ParameterMapping 获取参数信息
     * 2. 通过 TypeHandler 进行类型转换
     * 3. 支持多种 Java 类型到 JDBC 类型的映射
     *
     * @param statement Statement 对象（实际为 PreparedStatement）
     * @throws SQLException SQL 异常
     */
    @Override
    public void parameterize(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        // TODO: 当前为硬编码实现，应改为通过 TypeHandler 动态设置参数
        ps.setLong(1, Long.parseLong(((Object[]) parameterObject)[0].toString()));
    }

    /**
     * 执行查询并处理结果集
     * <p>
     * 执行流程：
     * 1. 将 Statement 转换为 PreparedStatement
     * 2. 调用 execute() 执行 SQL 查询
     * 3. 通过 ResultSetHandler 将 ResultSet 转换为 Java 对象列表
     * <p>
     * ResultSetHandler 的职责：
     * - 遍历 ResultSet 的每一行
     * - 根据结果映射配置创建结果对象
     * - 将列值映射到对象属性
     * - 返回结果对象列表
     *
     * @param statement     Statement 对象（实际为 PreparedStatement）
     * @param resultHandler 结果处理器（当前版本未使用）
     * @param <E>           返回结果的元素类型
     * @return 查询结果列表
     * @throws SQLException SQL 异常
     */
    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        // 执行 SQL 查询
        ps.execute();
        // 通过 ResultSetHandler 处理结果集并返回
        return resultSetHandler.<E> handleResultSets(ps);
    }

}
