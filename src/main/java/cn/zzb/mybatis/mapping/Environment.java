package cn.zzb.mybatis.mapping;

import cn.zzb.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

public final class Environment {

    //环境id
    private final String id;
    //事务工厂
    private final TransactionFactory transactionFactory;
    //数据源
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    //构建者
    public static class Builder{
        //环境id
        private String id;
        //事务工厂
        private TransactionFactory transactionFactory;
        //数据源
        private DataSource dataSource;

        public Builder(String id){this.id = id;}

        public Builder transactionFactory(TransactionFactory transactionFactory){
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource){
            this.dataSource = dataSource;
            return this;
        }

        public Environment build(){return new Environment(this.id, this.transactionFactory, this.dataSource);}

    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
