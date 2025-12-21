package com.alan.plugins.MyBatisLogFormatter.utils;

import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.DB2;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * DB2 SQL 格式化器单元测试
 */
public class Db2MybatisLogSqlFormatterTest {

    @Test
    public void testFormatMybatisLog_SimpleString() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: John(String)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM users WHERE name = 'John'", result);
    }

    @Test
    public void testFormatMybatisLog_Timestamp() {
        String log = "Preparing: SELECT * FROM orders WHERE create_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10.586(Timestamp)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM orders WHERE create_time = TIMESTAMP('2023-12-14 18:03:10.586')", result);
    }

    @Test
    public void testFormatMybatisLog_Date() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14(Date)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM orders WHERE order_date = DATE('2023-12-14')", result);
    }

    @Test
    public void testFormatMybatisLog_Time() {
        String log = "Preparing: SELECT * FROM orders WHERE order_time = ?\n" +
                "Parameters: 12:08:14(Time)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM orders WHERE order_time = TIME('12:08:14')", result);
    }

    @Test
    public void testFormatMybatisLog_Boolean() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: true(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM users WHERE active = 1", result);
    }

    @Test
    public void testFormatMybatisLog_BooleanFalse() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: false(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM users WHERE active = 0", result);
    }

    @Test
    public void testFormatMybatisLog_MultipleParameters() {
        String log = "Preparing: SELECT * FROM users WHERE name = ? AND age = ? AND active = ?\n" +
                "Parameters: John(String), 25(Integer), true(Boolean)";
        String result = SqlUtils.formatMybatisLog(log, DB2);
        assertEquals("SELECT * FROM users WHERE name = 'John' AND age = 25 AND active = 1", result);
    }
}

