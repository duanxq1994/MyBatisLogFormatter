package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * DB2 SQL 格式化器
 */
public class Db2MybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char" -> formatString(value);
            case "date", "localdate" -> String.format("DATE('%s')", value);
            case "time", "localtime" -> String.format("TIME('%s')", value);
            case "timestamp", "localdatetime" -> String.format("TIMESTAMP('%s')", value);
            case "boolean" -> formatBoolean(value); // DB2 使用 1/0 表示布尔值
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}

