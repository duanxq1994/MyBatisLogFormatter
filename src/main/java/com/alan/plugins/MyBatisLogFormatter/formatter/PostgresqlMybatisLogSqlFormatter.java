package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * PostgreSQL SQL 格式化器
 */
public class PostgresqlMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char" -> formatString(value);
            case "date", "localdate" -> String.format("'%s'::date", value);
            case "time", "localtime" -> String.format("'%s'::time", value);
            case "timestamp", "localdatetime" -> String.format("'%s'::timestamp", value);
            case "boolean" -> value; // PostgreSQL 支持 true/false
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}
