package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * Oracle SQL 格式化器
 */
public class OracleMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "time", "localtime" -> String.format("to_date('1970-01-01 %s', 'yyyy-MM-dd HH24:MI:ss')", value);
            case "timestamp", "localdatetime" ->
                // Oracle 时间戳需要使用 to_timestamp 函数
                    String.format("to_timestamp('%s', 'yyyy-MM-dd HH24:MI:ss.ff')", value);
            case "date", "localdate" ->
                // Oracle 日期需要使用 to_date 函数
                    String.format("to_date('%s', 'yyyy-MM-dd')", value);
            case "boolean" -> formatBoolean(value);
            case "string", "char" -> formatString(value);
            default ->
                // 数字
                // 复杂类型。比如 ByteArrayInputStream
                    value;
        };
    }
}

