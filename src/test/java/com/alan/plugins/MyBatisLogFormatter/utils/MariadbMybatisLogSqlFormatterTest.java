package com.alan.plugins.MyBatisLogFormatter.utils;

import static com.alan.plugins.MyBatisLogFormatter.utils.DatabaseType.MARIADB;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * MariaDB SQL 格式化器单元测试
 */
public class MariadbMybatisLogSqlFormatterTest {

    @Test
    public void testFormatMybatisLog_SimpleString() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: John(String)";
        String result = SqlUtils.formatMybatisLog(log, MARIADB);
        assertEquals("SELECT * FROM users WHERE name = 'John'", result);
    }

    @Test
    public void testFormatMybatisLog_Timestamp() {
        String log = "Preparing: SELECT * FROM orders WHERE create_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10.586(Timestamp)";
        String result = SqlUtils.formatMybatisLog(log, MARIADB);
        assertEquals("SELECT * FROM orders WHERE create_time = '2023-12-14 18:03:10.586'", result);
    }

    @Test
    public void testFormatMybatisLog_Date() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14(Date)";
        String result = SqlUtils.formatMybatisLog(log, MARIADB);
        assertEquals("SELECT * FROM orders WHERE order_date = '2023-12-14'", result);
    }

    @Test
    public void testFormatMybatisLog_MultipleParameters() {
        String log = "Preparing: SELECT * FROM users WHERE name = ? AND age = ?\n" +
                "Parameters: John(String), 25(Integer)";
        String result = SqlUtils.formatMybatisLog(log, MARIADB);
        assertEquals("SELECT * FROM users WHERE name = 'John' AND age = 25", result);
    }

    @Test
    public void testFormatMybatisLog_Long() {
        String log = "Preparing: SELECT * FROM users WHERE id = ?\n" +
                "Parameters: 12345(Long)";
        String result = SqlUtils.formatMybatisLog(log, MARIADB);
        assertEquals("SELECT * FROM users WHERE id = 12345", result);
    }
}

