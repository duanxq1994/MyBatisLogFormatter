package com.alan.plugins.MyBatisLogFormatter.utils;

import com.alan.plugins.MyBatisLogFormatter.formatter.AbstractMybatisLogSqlFormatter;
import com.alan.plugins.MyBatisLogFormatter.formatter.SqlFormatterFactory;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具类
 * 保持向后兼容，默认使用配置中的默认数据库类型
 */
public class SqlUtils {

    /**
     * 判断是否包含 MyBatis 日志关键字
     */
    public static boolean containsMybatisLog(String text) {
        return StringUtils.isNotBlank(text) && text.contains(AbstractMybatisLogSqlFormatter.PREPARING_KEY);
    }

    /**
         * 格式化 MyBatis 日志（根据数据库类型）
         * @param mybatisLog MyBatis 日志
         * @return 格式化后的 SQL
         */
        public static String formatMybatisLog(String mybatisLog) {
            return formatMybatisLog(mybatisLog, null);
        }

    /**
     * 格式化 MyBatis 日志（根据数据库类型）
     * @param mybatisLog MyBatis 日志
     * @param databaseType 数据库类型
     * @return 格式化后的 SQL
     */
    public static String formatMybatisLog(String mybatisLog, DatabaseType databaseType) {
        if (StringUtils.isBlank(mybatisLog)) {
            return "";
        }
        AbstractMybatisLogSqlFormatter formatter = SqlFormatterFactory.getFormatter(databaseType);
        return formatter.formatMybatisLog(mybatisLog);
    }


    /**
     * sql压缩
     * @param text sql
     */
    public static String compressSql(String text) {
        // 移除多余的空格和换行
        String compressedMySQL = text.replaceAll("\\s+", " ");
        // 移除单行注释
        compressedMySQL = compressedMySQL.replaceAll("--[^\\n]*", "");
        // 移除多行注释
        compressedMySQL = compressedMySQL.replaceAll("/\\*.*?\\*/", "");
        return compressedMySQL.trim();
    }

    /**
     * sql美化
     * @param text sql
     */
    public static String beautifySql(String text) {
        return SqlFormatter.format(text);
    }
}
