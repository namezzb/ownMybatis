package cn.zzb.mybatis.transaction.jdbc;

import cn.zzb.mybatis.session.TransactionIsolationLevel;
import cn.zzb.mybatis.transaction.Transaction;
import cn.zzb.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {


    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
