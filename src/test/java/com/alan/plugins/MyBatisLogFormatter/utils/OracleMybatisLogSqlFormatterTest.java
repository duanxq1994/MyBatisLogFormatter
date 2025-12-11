package com.alan.plugins.MyBatisLogFormatter.utils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * OracleMybatisLogSqlFormatter 单元测试
 */
public class OracleMybatisLogSqlFormatterTest {

    private OracleMybatisLogSqlFormatter formatter;

    @Before
    public void setUp() {
        formatter = new OracleMybatisLogSqlFormatter();
    }

    @Test
    public void testFormatMybatisLog_SimpleString() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: John(String)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE name = 'John'", result);
    }

    @Test
    public void testFormatMybatisLog_Timestamp() {
        String log = "Preparing: SELECT * FROM orders WHERE create_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10.586(Timestamp)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM orders WHERE create_time = to_timestamp('2023-12-14 18:03:10.586', 'yyyy-MM-dd HH24:MI:ss.ff')", result);
    }

    @Test
    public void testFormatMybatisLog_Date() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14 18:03:10(Date)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM orders WHERE order_date = to_date('2023-12-14 18:03:10', 'yyyy-MM-dd HH24:MI:ss')", result);
    }

    @Test
    public void testFormatMybatisLog_LocalDateTime() {
        String log = "Preparing: SELECT * FROM orders WHERE update_time = ?\n" +
                "Parameters: 2023-12-14 18:03:10(LocalDateTime)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM orders WHERE update_time = to_date('2023-12-14 18:03:10', 'yyyy-MM-dd HH24:MI:ss')", result);
    }

    @Test
    public void testFormatMybatisLog_LocalDate() {
        String log = "Preparing: SELECT * FROM orders WHERE order_date = ?\n" +
                "Parameters: 2023-12-14(LocalDate)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM orders WHERE order_date = to_date('2023-12-14', 'yyyy-MM-dd HH24:MI:ss')", result);
    }

    @Test
    public void testFormatMybatisLog_Boolean() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: true(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE active = 1", result);
    }

    @Test
    public void testFormatMybatisLog_BooleanFalse() {
        String log = "Preparing: SELECT * FROM users WHERE active = ?\n" +
                "Parameters: false(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE active = 0", result);
    }

    @Test
    public void testFormatMybatisLog_Long() {
        String log = "Preparing: SELECT * FROM users WHERE id = ?\n" +
                "Parameters: 12345(Long)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE id = 12345", result);
    }

    @Test
    public void testFormatMybatisLog_Integer() {
        String log = "Preparing: SELECT * FROM users WHERE age = ?\n" +
                "Parameters: 25(Integer)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE age = 25", result);
    }

    @Test
    public void testFormatMybatisLog_MultipleParameters() {
        String log = "Preparing: SELECT * FROM users WHERE name = ? AND age = ? AND active = ?\n" +
                "Parameters: John(String), 25(Integer), true(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE name = 'John' AND age = 25 AND active = 1", result);
    }

    @Test
    public void testFormatMybatisLog_ValueWithParentheses() {
        String log = "Preparing: SELECT * FROM tags WHERE name = ?\n" +
                "Parameters: Best Area(New)(String)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM tags WHERE name = 'Best Area(New)'", result);
    }

    @Test
    public void testFormatMybatisLog_NullValue() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: null";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE name = null", result);
    }

    @Test
    public void testFormatMybatisLog_EmptyString() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: (String)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE name = ''", result);
    }

    @Test
    public void testFormatMybatisLog_ComplexExample() {
        String log = "Preparing: select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = ? AND o.TAG_NAME = ? AND ( 1=1 and (o.MAIN_OBJECT_ID in ( ? ,? ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = ? AND o.updated = ? ORDER BY o.UPDATED_TIME DESC\n" +
                "Parameters: JS_RANGE_DEF(String), Best Area(New)(String), 99559(Long), 0(Long), 01(String), true(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = 'JS_RANGE_DEF' AND o.TAG_NAME = 'Best Area(New)' AND ( 1=1 and (o.MAIN_OBJECT_ID in ( 99559 ,0 ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = '01' AND o.updated = 1 ORDER BY o.UPDATED_TIME DESC", result);
    }

    @Test
    public void testFormatMybatisLog_ComplexExample_1() {
        String log = "Preparing: select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = ? AND o.TAG_NAME = ? AND ( 1=1 and (o.MAIN_OBJECT_ID in ( ? ,? ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = ? AND o.updated = ? ORDER BY o.UPDATED_TIME DESC\n" +
                "Parameters: JS_RANGE_DEF(String), Best Area, (New)(String), 99559(Long), 0(Long), 01(String), true(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = 'JS_RANGE_DEF' AND o.TAG_NAME = 'Best Area, (New)' AND ( 1=1 and (o.MAIN_OBJECT_ID in ( 99559 ,0 ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = '01' AND o.updated = 1 ORDER BY o.UPDATED_TIME DESC", result);
    }

    @Test
    @Ignore
    public void testFormatMybatisLog_ComplexExample_2() {
        String log = "Preparing: select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = ? AND o.TAG_NAME = ? AND ( 1=1 and (o.MAIN_OBJECT_ID in ( ? ,? ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = ? AND o.updated = ? ORDER BY o.UPDATED_TIME DESC\n" +
                "Parameters: JS_RANGE_DEF(String), Best Area(String), (New)(String), 99559(Long), 0(Long), 01(String), true(Boolean)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("select count(1) FROM JS_TABLE_TAG o WHERE 1=1 AND o.TABLE_NAME = 'JS_RANGE_DEF' AND o.TAG_NAME = 'Best Area(String), (New)' AND ( 1=1 and (o.MAIN_OBJECT_ID in ( 99559 ,0 ) ) OR DEFAULT_AREA = 1 ) AND o.AREA_TYPE = '01' AND o.updated = 1 ORDER BY o.UPDATED_TIME DESC", result);
    }

    @Test
    public void testFormatMybatisLog_Float() {
        String log = "Preparing: SELECT * FROM products WHERE price = ?\n" +
                "Parameters: 99.99(Float)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM products WHERE price = 99.99", result);
    }

    @Test
    public void testFormatMybatisLog_Double() {
        String log = "Preparing: SELECT * FROM products WHERE price = ?\n" +
                "Parameters: 99.999(Double)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM products WHERE price = 99.999", result);
    }

    @Test
    public void testFormatMybatisLog_BigDecimal() {
        String log = "Preparing: SELECT * FROM products WHERE price = ?\n" +
                "Parameters: 99.99(BigDecimal)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM products WHERE price = 99.99", result);
    }

    @Test
    public void testFormatMybatisLog_Char() {
        String log = "Preparing: SELECT * FROM users WHERE status = ?\n" +
                "Parameters: A(Char)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE status = 'A'", result);
    }

    @Test
    public void testFormatMybatisLog_CaseInsensitive() {
        String log = "Preparing: SELECT * FROM users WHERE name = ?\n" +
                "Parameters: John(STRING)";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users WHERE name = 'John'", result);
    }

    @Test
    public void testFormatMybatisLog_EmptyLog() {
        String result = formatter.formatMybatisLog("");
        assertEquals("", result);
    }

    @Test
    public void testFormatMybatisLog_NoParameters() {
        String log = "Preparing: SELECT * FROM users";
        String result = formatter.formatMybatisLog(log);
        assertEquals("SELECT * FROM users", result);
    }
}

