package com.alan.plugins.MyBatisLogFormatter.utils;

/**
 * SQL 格式化器工厂
 */
public class SqlFormatterFactory {

    /**
     * 根据数据库类型获取对应的 SQL 格式化器
     */
    public static AbstractMybatisLogSqlFormatter getFormatter(DatabaseType databaseType) {
        if (databaseType == null) {
            return new MysqlMybatisLogSqlFormatter(); // 默认 MySQL
        }

        return switch (databaseType) {
            case ORACLE -> new OracleMybatisLogSqlFormatter();
            default -> new MysqlMybatisLogSqlFormatter();
        };
    }
}

