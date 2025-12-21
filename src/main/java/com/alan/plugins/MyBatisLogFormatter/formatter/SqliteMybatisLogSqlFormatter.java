package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * SQLite SQL 格式化器
 */
public class SqliteMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char" -> formatString(value);
            case "date", "localdate" -> String.format("'%s'", value);
            case "time", "localtime" -> String.format("'%s'", value);
            case "timestamp", "localdatetime" -> String.format("'%s'", value);
            case "boolean" -> formatBoolean(value); // SQLite 使用 1/0 表示布尔值
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}
