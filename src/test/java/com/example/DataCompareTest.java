package com.example;

import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.Assert.*;

public class DataCompareTest {

    @Test
    public void testProductEquals() {
        // 创建两个完全相同的Product对象
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("测试产品");
        product1.setPrice(new BigDecimal(100.00));
        product1.setCategory("电子产品");
        Timestamp timestamp = new Timestamp(new Date().getTime());
        product1.setCreateTime(timestamp);

        Product product2 = new Product();
        product2.setId("1");
        product2.setName("测试产品");
        product2.setPrice(new BigDecimal(100.00));
        product2.setCategory("电子产品");
        product2.setCreateTime(timestamp);

        // 验证相等
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());

        // 修改其中一个属性
        product2.setPrice(new BigDecimal(200.00));
        // 验证不相等
        assertNotEquals(product1, product2);

        // 修改回相同的值
        product2.setPrice(new BigDecimal(100.00));
        // 验证相等
        assertEquals(product1, product2);
    }

    @Test
    public void testProductHashCode() {
        // 创建两个完全相同的Product对象
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("测试产品");
        product1.setPrice(new BigDecimal(100.00));
        product1.setCategory("电子产品");
        product1.setCreateTime(new Timestamp(new Date().getTime()));

        Product product2 = new Product();
        product2.setId("1");
        product2.setName("测试产品");
        product2.setPrice(new BigDecimal(100.00));
        product2.setCategory("电子产品");
        product2.setCreateTime(product1.getCreateTime());

        // 验证hashCode相等
        assertEquals(product1.hashCode(), product2.hashCode());

        // 修改ID
        product2.setId("2");
        // 验证hashCode不相等
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    public void testCompareResult() {
        CompareResult result = new CompareResult();

        // 初始状态验证
        assertEquals(0, result.getMatchedCount());
        assertEquals(0, result.getOnlyInA());
        assertEquals(0, result.getOnlyInB());
        assertEquals(0, result.getMismatched());

        // 增加统计
        result.incrementMatchedCount();
        result.incrementOnlyInA();
        result.incrementOnlyInB(2);
        result.incrementMismatched();

        // 验证统计结果
        assertEquals(1, result.getMatchedCount());
        assertEquals(1, result.getOnlyInA());
        assertEquals(2, result.getOnlyInB());
        assertEquals(1, result.getMismatched());

        // 创建另一个结果对象
        CompareResult result2 = new CompareResult();
        result2.incrementMatchedCount();
        result2.incrementOnlyInB(1);

        // 合并结果
        result.add(result2);

        // 验证合并后的结果
        assertEquals(2, result.getMatchedCount());
        assertEquals(1, result.getOnlyInA());
        assertEquals(3, result.getOnlyInB());
        assertEquals(1, result.getMismatched());
    }
}