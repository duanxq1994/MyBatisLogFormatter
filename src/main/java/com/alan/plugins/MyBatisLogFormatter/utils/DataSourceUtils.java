package com.alan.plugins.MyBatisLogFormatter.utils;

import com.intellij.database.dataSource.LocalDataSource;

/**
 * 数据源工具类
 */
public class DataSourceUtils {

    /**
     * 从 LocalDataSource 获取数据库类型
     */
    public static DatabaseType getDatabaseType(LocalDataSource dataSource, DatabaseType defaultDatabaseType) {
        if (dataSource == null) {
            return defaultDatabaseType;
        }
        
        try {
            // 方法1: 尝试通过 getDbms() 方法获取数据库类型
            String dbms = dataSource.getDbms().getName();
            return DatabaseType.fromString(dbms);
        } catch (Exception e) {
            // 忽略异常，继续尝试其他方法
        }
        
        try {
            // 方法2: 尝试通过连接 URL 判断
            String url = dataSource.getUrl();
            if (url != null) {
                String upperUrl = url.toUpperCase();
                if (upperUrl.contains("ORACLE") || upperUrl.contains("ORACLE:THIN") || upperUrl.contains("ORACLE:OCI")) {
                    return DatabaseType.ORACLE;
                } else if (upperUrl.contains("MYSQL") || upperUrl.contains("MARIADB")) {
                    return DatabaseType.MYSQL;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        try {
            // 方法3: 尝试通过驱动类名判断
            String driverClass = dataSource.getDriverClass();
            if (driverClass != null) {
                String upperDriver = driverClass.toUpperCase();
                if (upperDriver.contains("ORACLE")) {
                    return DatabaseType.ORACLE;
                } else if (upperDriver.contains("MYSQL")) {
                    return DatabaseType.MYSQL;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return defaultDatabaseType;
    }
}

