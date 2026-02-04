package cn.zzb.mybatis.executor;



import cn.zzb.mybatis.executor.statement.StatementHandler;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.ResultHandler;
import cn.zzb.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 简单执行器（Simple Executor）
 * <p>
 * 核心职责：
 * 1. 继承 BaseExecutor，实现 SQL 查询的具体执行逻辑
 * 2. 每次执行都创建新的 Statement 对象，执行完毕后关闭
 * 3. 不复用 Statement，适合执行频率较低的场景
 * <p>
 * 执行流程：
 * 创建 StatementHandler → 获取数据库连接 → 创建 Statement →
 * 设置参数 → 执行 SQL → 处理结果集 → 返回结果
 * <p>
 * 与其他执行器的区别：
 * - SimpleExecutor：每次创建新 Statement（当前实现）
 * - ReuseExecutor：复用 Statement，减少预编译开销
 * - BatchExecutor：批量执行，适合批量插入/更新
 *
 * @author zzb
 */
public class SimpleExecutor extends BaseExecutor {

    /**
     * 构造函数
     *
     * @param configuration 全局配置对象
     * @param transaction   事务对象，用于获取数据库连接
     */
    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    /**
     * 执行查询操作的核心方法
     * <p>
     * 完整的 SQL 执行流程：
     * 1. 从 Configuration 创建 StatementHandler（语句处理器）
     * 2. 从 Transaction 获取数据库连接
     * 3. 通过 StatementHandler 创建 Statement 对象
     * 4. 设置 SQL 参数（将 #{} 占位符替换为实际值）
     * 5. 执行 SQL 查询
     * 6. 通过 ResultSetHandler 将 ResultSet 转换为 Java 对象
     * 7. 返回结果列表
     * <p>
     * 注意：每次调用都会创建新的 Statement，不会复用
     *
     * @param ms            SQL 映射语句对象，包含 SQL 配置信息
     * @param parameter     SQL 参数对象
     * @param resultHandler 结果处理器（当前版本未使用）
     * @param boundSql      绑定的 SQL 对象，包含最终 SQL 和参数映射
     * @param <E>           返回结果的元素类型
     * @return 查询结果列表，如果发生异常则返回 null
     */
    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            // 1. 获取全局配置对象
            Configuration configuration = ms.getConfiguration();
            // 2. 创建语句处理器（封装 Statement 的创建和执行）
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            // 3. 从事务中获取数据库连接
            Connection connection = transaction.getConnection();
            // 4. 创建 Statement 对象（PreparedStatement）
            Statement stmt = handler.prepare(connection);
            // 5. 设置 SQL 参数（将参数绑定到 ? 占位符）
            handler.parameterize(stmt);
            // 6. 执行查询并处理结果集，返回结果列表
            return handler.query(stmt, resultHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
