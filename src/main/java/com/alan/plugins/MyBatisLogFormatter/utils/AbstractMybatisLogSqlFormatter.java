package com.alan.plugins.MyBatisLogFormatter.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽象 SQL 格式化器基类
 */
public abstract class AbstractMybatisLogSqlFormatter {

    protected static final String PREPARING_KEY = "Preparing: ";
    protected static final String PARAMETERS_KEY = "Parameters: ";
    protected static final String SQL_PLACEHOLDER = "#占位符";

    /**
     * 格式化 MyBatis 日志
     */
    public String formatMybatisLog(String mybatisLog) {
        String[] mybatisLogArray = splitMybatisLog(mybatisLog);
        if (mybatisLogArray == null || mybatisLogArray.length == 0) {
            return "";
        }
        String sql = mybatisLogArray[0];
        String parameters = "";
        if (mybatisLogArray.length == 2) {
            parameters = mybatisLogArray[1];
        }
        if (StringUtils.isNotBlank(parameters)) {
            parameters = parameters.replace(PARAMETERS_KEY, "");
            // 替换？占位符
            sql = sql.replace("?", SQL_PLACEHOLDER);
            // 匹配参数并替换
            sql = replaceParameters(sql, parameters);
        }
        return sql;
    }

    /**
     * 分割 MyBatis 日志，提取 SQL 和参数
     */
    protected String[] splitMybatisLog(String mybatisLog) {
        if (StringUtils.isBlank(mybatisLog)) {
            return null;
        }
        String[] logSplitArr = mybatisLog.split("\n");
        String sqlLog = "";
        String paramsLog = "";
        if (logSplitArr.length > 2) {
            for (String row : logSplitArr) {
                if (row.contains(PREPARING_KEY)) {
                    sqlLog = extractSql(row);
                } else if (row.contains(PARAMETERS_KEY)) {
                    paramsLog = extractParameters(row);
                }
            }
        } else if (logSplitArr.length == 2) {
            sqlLog = extractSql(logSplitArr[0]);
            paramsLog = extractParameters(logSplitArr[1]);
        } else if (logSplitArr.length == 1) {
            if (logSplitArr[0].contains(PREPARING_KEY)) {
                sqlLog = extractSql(logSplitArr[0]);
            }
        }
        return new String[]{sqlLog, paramsLog};
    }

    /**
     * 提取 SQL 语句
     */
    protected String extractSql(String row) {
        Pattern pattern = Pattern.compile(PREPARING_KEY + "(.*)");
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * 提取参数
     */
    protected String extractParameters(String row) {
        Pattern pattern = Pattern.compile(PARAMETERS_KEY + "(.*)");
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * 替换 SQL 中的参数占位符
     * 参考 Python 代码逻辑：
     * - 使用正则表达式匹配参数：value(type) 格式
     * - 参考 Python: re.sub("(?P<value>.*)\((?P<type>\w+)\)", group1, p)
     * - Python 使用贪婪匹配 .* 来匹配值部分，这样可以处理值中包含括号的情况
     *   例如：Best Area(New)(String) 应该匹配为 value="Best Area(New)", type="String"
     */
    protected String replaceParameters(String sql, String parameters) {
        if (StringUtils.isBlank(parameters)) {
            return sql;
        }
        
        // 参考 Python 代码：param.split(", ")
        // 使用正则表达式分割参数，处理逗号前后的空格
        String[] paramArray = parameters.split(", ");
        
        // 匹配参数格式：value(type)，参考 Python: (.*)\((\w+)\)
        // Python 使用贪婪匹配 .* 来匹配值部分，这样可以处理值中包含括号的情况
        // 例如：Best Area(New)(String) 应该匹配为 value="Best Area(New)", type="String"
        // 使用贪婪匹配 .* 而不是非贪婪匹配 .*?，贪婪匹配会自动匹配到最后一个 (type)
        Pattern paramPattern = Pattern.compile("(.*)\\((\\w+)\\)");
        
        for (String param : paramArray) {
            param = param.trim();
            if (StringUtils.isBlank(param)) {
                continue;
            }
            
            // 处理 null 值
            if (param.equalsIgnoreCase("null")) {
                sql = sql.replaceFirst(Pattern.quote(SQL_PLACEHOLDER), "null");
                continue;
            }
            
            // 匹配 value(type) 格式
            // 贪婪匹配会自动找到最后一个 (type) 格式，这样可以处理值中包含括号的情况
            Matcher matcher = paramPattern.matcher(param);
            if (matcher.find()) {
                String value = matcher.group(1).trim();
                String type = matcher.group(2).trim();
                
                // 调用子类实现的格式化方法
                String formattedValue = formatParameterValue(value, type);
                
                // 替换第一个占位符
                sql = sql.replaceFirst(Pattern.quote(SQL_PLACEHOLDER), Matcher.quoteReplacement(formattedValue));
            } else {
                // 如果没有匹配到格式，可能是空字符串或其他格式
                // 参考 Python 代码中对空字符串的处理
                if (param.isEmpty()) {
                    sql = sql.replaceFirst(Pattern.quote(SQL_PLACEHOLDER), "''");
                }
            }
        }
        return sql;
    }

    /**
     * 格式化参数值，子类实现具体的格式化逻辑
     */
    protected abstract String formatParameterValue(String value, String type);

    /**
     * 处理字符串类型
     */
    protected String formatString(String value) {
        if (StringUtils.isBlank(value)) {
            return "''";
        }
        return "'" + value + "'";
    }

    /**
     * 处理布尔类型
     */
    protected String formatBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return "1";
        } else if ("false".equalsIgnoreCase(value)) {
            return "0";
        }
        return value;
    }
}

