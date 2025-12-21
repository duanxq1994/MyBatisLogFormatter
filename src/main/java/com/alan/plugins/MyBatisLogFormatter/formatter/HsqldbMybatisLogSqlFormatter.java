package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * HSQLDB SQL 格式化器
 */
public class HsqldbMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char" -> formatString(value);
            case "date", "localdate" -> String.format("DATE '%s'", value);
            case "time", "localtime" -> String.format("TIME '%s'", value);
            case "timestamp", "localdatetime" -> String.format("TIMESTAMP '%s'", value);
            case "boolean" -> value; // HSQLDB 支持 true/false
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}

