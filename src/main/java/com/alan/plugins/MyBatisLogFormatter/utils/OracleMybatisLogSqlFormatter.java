package com.alan.plugins.MyBatisLogFormatter.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Oracle SQL 格式化器
 */
public class OracleMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        switch (type.toLowerCase()) {
            case "timestamp":
                // Oracle 时间戳需要使用 to_timestamp 函数
                return String.format("to_timestamp('%s', 'yyyy-MM-dd HH24:MI:ss.ff')", value);
            case "date":
                // Oracle 日期需要使用 to_date 函数
                return String.format("to_date('%s', 'yyyy-MM-dd HH24:MI:ss')", value);
            case "localdatetime":
            case "localdate":
                // LocalDateTime 和 LocalDate 也使用 to_date
                return String.format("to_date('%s', 'yyyy-MM-dd HH24:MI:ss')", value);
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
            case "char":
            default:
                return formatString(value);
        }
    }
}

