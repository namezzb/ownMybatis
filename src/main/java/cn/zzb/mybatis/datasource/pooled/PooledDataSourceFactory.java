package cn.zzb.mybatis.datasource.pooled;


import cn.zzb.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 有连接池的数据源工厂
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
            this.dataSource = new PooledDataSource();
        }

}
