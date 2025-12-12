package com.alan.plugins.MyBatisLogFormatter.formatter;

import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.MYSQL;
import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.ORACLE;

import java.util.HashMap;
import java.util.Map;

import com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType;

/**
 * SQL 格式化器工厂
 */
public class SqlFormatterFactory {

    private static final Map<DatabaseType, AbstractMybatisLogSqlFormatter> cache = new HashMap<>();

    static {
        cache.put(ORACLE, new OracleMybatisLogSqlFormatter());
        cache.put(MYSQL, new MysqlMybatisLogSqlFormatter());
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

