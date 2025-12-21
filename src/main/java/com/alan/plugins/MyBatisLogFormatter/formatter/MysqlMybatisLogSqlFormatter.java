package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * MySQL SQL 格式化器
 */
public class MysqlMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char",
                 "date", "time",
                 "localdatetime", "localdate", "timestamp" -> formatString(value);
            default ->
                // 数字
                // 复杂类型。比如 ByteArrayInputStream
                    value;
        };
    }
}

