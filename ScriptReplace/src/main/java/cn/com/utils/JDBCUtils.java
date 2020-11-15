package cn.com.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtils {
    static ComboPooledDataSource cpds = new ComboPooledDataSource();

    // 链接数据池
    public static DataSource getDataSource() {
        return cpds;
    }

    // 数据数据库
    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

}
