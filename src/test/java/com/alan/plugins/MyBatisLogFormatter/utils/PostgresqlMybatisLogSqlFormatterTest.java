package com.alan.plugins.MyBatisLogFormatter.utils;

import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.POSTGRESQL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * PostgreSQL SQL 格式化器单元测试
 */
public class PostgresqlMybatisLogSqlFormatterTest {

    @Test
    public void testFormatMybatisLog_SimpleString() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: John(String)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM users WHERE name = 'John'", result);
    }

    @Test
    public void testFormatMybatisLog_Timestamp() {
        String log = "Preparing: SELECT * FROM orders WHERE create_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10.586(Timestamp)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM orders WHERE create_time = '2023-12-14 18:03:10.586'::timestamp", result);
    }

    @Test
    public void testFormatMybatisLog_Date() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14(Date)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM orders WHERE order_date = '2023-12-14'::date", result);
    }

    @Test
    public void testFormatMybatisLog_Time() {
        String log = "Preparing: SELECT * FROM orders WHERE order_time = ?\n" +
                "Parameters: 12:08:14(Time)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM orders WHERE order_time = '12:08:14'::time", result);
    }

    @Test
    public void testFormatMybatisLog_LocalDateTime() {
        String log = "Preparing: SELECT * FROM orders WHERE update_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10.0(LocalDateTime)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM orders WHERE update_time = '2023-12-14 18:03:10.0'::timestamp", result);
    }

    @Test
    public void testFormatMybatisLog_LocalDate() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14(LocalDate)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM orders WHERE order_date = '2023-12-14'::date", result);
    }

    @Test
    public void testFormatMybatisLog_Boolean() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: true(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM users WHERE active = true", result);
    }

    @Test
    public void testFormatMybatisLog_BooleanFalse() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: false(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM users WHERE active = false", result);
    }

    @Test
    public void testFormatMybatisLog_MultipleParameters() {
        String log = "Preparing: SELECT * FROM users WHERE name = ? AND age = ? AND active = ?\n" +
                "Parameters: John(String), 25(Integer), true(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM users WHERE name = 'John' AND age = 25 AND active = true", result);
    }

    @Test
    public void testFormatMybatisLog_NullValue() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: null";
        String result = SqlUtils.formatMybatisLog(log, POSTGRESQL);
        assertEquals("SELECT * FROM users WHERE name = null", result);
    }
}

