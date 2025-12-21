package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * MongoDB SQL 格式化器
 * 注意：MongoDB 是 NoSQL 数据库，但如果使用 MongoDB Connector for BI 或其他 SQL 接口，
 * MyBatis 可能仍会产生类似的日志格式
 */
public class MongodbMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        // MongoDB 的 SQL 接口通常与 MySQL 类似
        return switch (type.toLowerCase()) {
            case "string", "char",
                 "date", "time",
                 "localdatetime", "localdate", "timestamp" -> formatString(value);
            case "boolean" -> value; // MongoDB 支持 true/false
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}
