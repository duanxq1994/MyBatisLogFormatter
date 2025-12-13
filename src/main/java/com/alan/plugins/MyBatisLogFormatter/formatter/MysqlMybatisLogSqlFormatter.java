package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * MySQL SQL 格式化器
 */
public class MysqlMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        switch (type.toLowerCase()) {
            case "string", "char":
            case "date", "time", "localdatetime", "localdate", "timestamp":
                return formatString(value);
            default:
                // 数字
                // 复杂类型。比如 ByteArrayInputStream
                return value;
        }
    }
}

