package com.alan.plugins.MyBatisLogFormatter.formatter;

/**
 * MariaDB SQL 格式化器
 * MariaDB 与 MySQL 兼容，格式化方式相同
 */
public class MariadbMybatisLogSqlFormatter extends AbstractMybatisLogSqlFormatter {

    @Override
    protected String formatParameterValue(String value, String type) {
        return switch (type.toLowerCase()) {
            case "string", "char",
                 "date", "time",
                 "localdatetime", "localdate", "timestamp" -> formatString(value);
            default ->
                // 数字类型和其他复杂类型
                    value;
        };
    }
}
