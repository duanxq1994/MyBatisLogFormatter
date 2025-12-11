package com.alan.plugins.MyBatisLogFormatter.utils;

/**
 * 数据库类型枚举
 */
public enum DatabaseType {
    MYSQL("MySQL"),
    ORACLE("Oracle");

    private final String name;

    DatabaseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据数据库名称字符串获取数据库类型
     */
    public static DatabaseType fromString(String dbName) {
        if (dbName == null) {
            return MYSQL; // 默认 MySQL
        }
        String upperName = dbName.toUpperCase();
        if (upperName.contains("ORACLE")) {
            return ORACLE;
        } else if (upperName.contains("MYSQL")) {
            return MYSQL;
        }
        return MYSQL; // 默认 MySQL
    }
}

