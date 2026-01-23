package cn.zzb.mybatis.transaction.jdbc;

import cn.zzb.mybatis.session.TransactionIsolationLevel;
import cn.zzb.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTransaction implements Transaction {

    protected Connection conn;
    protected DataSource dataSource;
    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;
    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }
    public JdbcTransaction(Connection conn) {
        this.conn = conn;
    }


    @Override
    public Connection getConnection() throws SQLException {
        conn = dataSource.getConnection();
        conn.setTransactionIsolation(level.getLevel());
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    @Override
    public void commit() throws SQLException {
        if(conn != null && !conn.getAutoCommit()){
            conn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if(conn != null && !conn.getAutoCommit()){
            conn.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if(conn != null && !conn.getAutoCommit()){
            conn.close();
        }
    }
}
