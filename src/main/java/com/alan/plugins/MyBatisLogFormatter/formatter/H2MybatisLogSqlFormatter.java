package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * H2 SQL 格式化器
 */
public class H2MybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char" -> formatString(value);
            case "date", "localdate" -> String.format("'%s'", value);
            case "time", "localtime" -> String.format("'%s'", value);
            case "timestamp", "localdatetime" -> String.format("'%s'", value);
            case "boolean" -> value; // H2 支持 true/false
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}
