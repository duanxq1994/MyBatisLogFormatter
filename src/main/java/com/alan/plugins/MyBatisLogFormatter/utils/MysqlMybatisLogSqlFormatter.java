package com.alan.plugins.MyBatisLogFormatter.utils;

/**
 * MySQL SQL 格式化器
 */
public class MysqlMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        switch (type.toLowerCase()) {
            case "boolean":
                return formatBoolean(value);
            case "integer":
            case "int":
            case "long":
            case "float":
            case "double":
            case "bigdecimal":
                return value;
            case "string":
            case "char", "date", "localdatetime", "localdate", "timestamp":
            default:
                return formatString(value);
        }
    }
}

