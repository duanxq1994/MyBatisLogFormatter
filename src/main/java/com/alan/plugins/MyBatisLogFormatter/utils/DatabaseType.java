package com.alan.plugins.MyBatisLogFormatter.utils;

import com.intellij.database.dataSource.LocalDataSource;
import org.jetbrains.annotations.Nullable;

/**
 * 数据库类型枚举
 */
public enum DatabaseType {
    MYSQL("MySQL", new String[]{"MYSQL"}, new String[]{"mysql", "jdbc:mysql"}, new String[]{"com.mysql", "mysql"}),
    ORACLE("Oracle", new String[]{"ORACLE"}, new String[]{"oracle", "jdbc:oracle"}, new String[]{"oracle.jdbc", "oracle"}),
    POSTGRESQL("PostgreSQL", new String[]{"POSTGRESQL", "POSTGRES"}, new String[]{"postgresql", "postgres", "jdbc:postgresql"}, new String[]{"org.postgresql", "postgresql"}),
    SQL_SERVER("SQL Server", new String[]{"SQLSERVER", "SQL_SERVER", "MSSQL"}, new String[]{"sqlserver", "mssql", "jdbc:sqlserver", "jdbc:microsoft"}, new String[]{"com.microsoft.sqlserver", "sqlserver"}),
    MONGODB("MongoDB", new String[]{"MONGODB", "MONGO"}, new String[]{"mongodb", "mongo"}, new String[]{"mongodb", "com.mongodb"}),
    SQLITE("SQLite", new String[]{"SQLITE"}, new String[]{"sqlite", "jdbc:sqlite"}, new String[]{"org.sqlite", "sqlite"}),
    H2("H2", new String[]{"H2"}, new String[]{"h2", "jdbc:h2"}, new String[]{"org.h2", "h2"}),
    MARIADB("MariaDB", new String[]{"MARIADB"}, new String[]{"mariadb", "jdbc:mariadb"}, new String[]{"org.mariadb", "mariadb"}),
    DB2("DB2", new String[]{"DB2"}, new String[]{"db2", "jdbc:db2"}, new String[]{"com.ibm.db2", "db2"}),
    HSQLDB("HSQLDB", new String[]{"HSQLDB", "HSQL"}, new String[]{"hsqldb", "jdbc:hsqldb"}, new String[]{"org.hsqldb", "hsqldb"});

    /**
     * 默认数据库类型
     */
    public static final DatabaseType DEFAULT_DATABASE_TYPE = MYSQL;

    private final String displayName;
    private final String[] dbmsKeywords;
    private final String[] urlKeywords;
    private final String[] driverKeywords;

    DatabaseType(String displayName, String[] dbmsKeywords, String[] urlKeywords, String[] driverKeywords) {
        this.displayName = displayName;
        this.dbmsKeywords = dbmsKeywords;
        this.urlKeywords = urlKeywords;
        this.driverKeywords = driverKeywords;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 检查给定的字符串是否匹配此数据库类型的关键字
     */
    private boolean matches(String text, String[] keywords) {
        if (text == null || keywords == null) {
            return false;
        }
        String upperText = text.toUpperCase();
        for (String keyword : keywords) {
            if (upperText.contains(keyword.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查数据库管理系统名称是否匹配
     */
    private boolean matchesDbms(String dbms) {
        return matches(dbms, dbmsKeywords);
    }

    /**
     * 检查连接 URL 是否匹配
     */
    private boolean matchesUrl(String url) {
        return matches(url, urlKeywords);
    }

    /**
     * 检查驱动类名是否匹配
     */
    private boolean matchesDriver(String driverClass) {
        return matches(driverClass, driverKeywords);
    }

    /**
     * 根据数据库名称字符串获取数据库类型
     */
    private static DatabaseType fromDbmsName(String dbName) {
        if (dbName == null) {
            return null;
        }
        for (DatabaseType type : values()) {
            if (type.matchesDbms(dbName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据连接 URL 获取数据库类型
     */
    private static DatabaseType fromUrl(String url) {
        if (url == null) {
            return null;
        }
        for (DatabaseType type : values()) {
            if (type.matchesUrl(url)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据驱动类名获取数据库类型
     */
    private static DatabaseType fromDriverClass(String driverClass) {
        if (driverClass == null) {
            return null;
        }
        for (DatabaseType type : values()) {
            if (type.matchesDriver(driverClass)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 从 LocalDataSource 获取数据库类型
     * 会尝试多种方法：DBMS 名称、连接 URL、驱动类名
     *
     * @param dataSource          数据源，可以为 null
     * @param defaultDatabaseType
     * @return 识别到的数据库类型，如果无法识别则返回默认类型
     */
    public static DatabaseType fromDataSource(@Nullable LocalDataSource dataSource, DatabaseType defaultDatabaseType) {
        if (dataSource == null) {
            return defaultDatabaseType;
        }
        DatabaseType result;
        if((result = fromDbmsName(dataSource.getDbms().getName())) != null) {
            return result;
        }
        if((result = fromUrl(dataSource.getUrl())) != null) {
            return result;
        }
        if((result = fromDriverClass(dataSource.getDriverClass())) != null) {
            return result;
        }
        return defaultDatabaseType;
    }
}

