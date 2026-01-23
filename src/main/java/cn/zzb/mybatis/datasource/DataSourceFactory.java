package cn.zzb.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceFactory {

    void setProperties(Properties prop);

    DataSource getDataSource();
}
