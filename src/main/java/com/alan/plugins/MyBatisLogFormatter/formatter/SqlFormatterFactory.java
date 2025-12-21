package com.alan.plugins.MyBatisLogFormatter.formatter;

import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.*;

import java.util.HashMap;
import java.util.Map;

import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;

/**
 * SQL 格式化器工厂
 */
public class SqlFormatterFactory {

    private static final Map<DatabaseType, AbstractMybatisLogSqlFormatter> cache = new HashMap<>();

    static {
        cache.put(MYSQL, new MysqlMybatisLogSqlFormatter());
        cache.put(ORACLE, new OracleMybatisLogSqlFormatter());
        cache.put(POSTGRESQL, new PostgresqlMybatisLogSqlFormatter());
        cache.put(SQL_SERVER, new SqlServerMybatisLogSqlFormatter());
        cache.put(MONGODB, new MongodbMybatisLogSqlFormatter());
        cache.put(SQLITE, new SqliteMybatisLogSqlFormatter());
        cache.put(H2, new H2MybatisLogSqlFormatter());
        cache.put(MARIADB, new MariadbMybatisLogSqlFormatter());
        cache.put(DB2, new Db2MybatisLogSqlFormatter());
        cache.put(HSQLDB, new HsqldbMybatisLogSqlFormatter());
    }

    /**
     * 根据数据库类型获取对应的 SQL 格式化器
     */
    public static AbstractMybatisLogSqlFormatter getFormatter(DatabaseType databaseType) {
        if (databaseType == null) {
            databaseType = MYSQL; // 默认 MySQL
        }
        return cache.getOrDefault(databaseType, cache.get(MYSQL));
    }
}

