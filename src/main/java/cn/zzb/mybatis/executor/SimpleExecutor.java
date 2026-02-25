package cn.zzb.mybatis.executor;

import cn.zzb.mybatis.executor.statement.StatementHandler;
import cn.zzb.mybatis.mapping.BoundSql;
import cn.zzb.mybatis.mapping.MappedStatement;
import cn.zzb.mybatis.session.Configuration;
import cn.zzb.mybatis.session.ResultHandler;
import cn.zzb.mybatis.session.RowBounds;
import cn.zzb.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author 小傅哥，微信：fustack
 * @description 简单执行器
 * @date 2022/04/26
 * @github https://github.com/fuzhengwei
 * @copyright 公众号：bugstack虫洞栈 | 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    /**
     * 执行 INSERT / UPDATE / DELETE 语句。
     * <p>
     * 执行流程：
     * 1. 通过 Configuration 创建 StatementHandler（默认为 PreparedStatementHandler）
     *    - 构造期间会触发 KeyGenerator#processBefore，处理 BEFORE 类型的主键生成
     * 2. 调用 prepareStatement：获取数据库连接，创建 PreparedStatement 并绑定参数
     * 3. 调用 StatementHandler#update 执行 SQL，执行完毕后触发 KeyGenerator#processAfter
     * 4. finally 块确保 Statement 一定被关闭，避免连接资源泄漏
     *
     * @param ms        当前执行的 SQL 映射语句对象
     * @param parameter 调用方传入的参数对象
     * @return 受影响的行数
     */
    @Override
    protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
            // 准备语句
            stmt = prepareStatement(handler);
            // StatementHandler.update
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
            // 准备语句
            stmt = prepareStatement(handler);
            // 返回结果
            return handler.query(stmt, resultHandler);
        } finally {
            closeStatement(stmt);
        }
    }

    private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = transaction.getConnection();
        // 准备语句
        stmt = handler.prepare(connection);
        handler.parameterize(stmt);
        return stmt;
    }

}
