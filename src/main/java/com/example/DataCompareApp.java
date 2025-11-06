package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class DataCompareApp {
    private static final Logger logger = LoggerFactory.getLogger(DataCompareApp.class);

    // 数据库配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // 表名
    private static final String TABLE_A = "product_a";
    private static final String TABLE_B = "product_b";

    // 批量大小
    private static final int BATCH_SIZE = 10000;
    // 线程池大小
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 获取表的总行数
            long totalRowsA = getTotalRows(conn, TABLE_A);
            long totalRowsB = getTotalRows(conn, TABLE_B);

            logger.info("表 {} 共有 {} 条记录", TABLE_A, totalRowsA);
            logger.info("表 {} 共有 {} 条记录", TABLE_B, totalRowsB);

            // 计算需要多少批次
            int batchesA = (int) Math.ceil((double) totalRowsA / BATCH_SIZE);
            int batchesB = (int) Math.ceil((double) totalRowsB / BATCH_SIZE);

            // 并行处理数据比对
            List<Future<CompareResult>> futures = new ArrayList<>();

            // 处理表A的每个批次
            for (int i = 0; i < batchesA; i++) {
                long offset = i * BATCH_SIZE;
                futures.add(executor.submit(() -> compareBatch(conn, TABLE_A, TABLE_B, offset, BATCH_SIZE)));
            }

            // 处理表B中可能超出表A的批次
            if (batchesB > batchesA) {
                for (int i = batchesA; i < batchesB; i++) {
                    long offset = i * BATCH_SIZE;
                    futures.add(executor.submit(() -> compareBatch(conn, TABLE_A, TABLE_B, offset, BATCH_SIZE)));
                }
            }

            // 收集比对结果
            CompareResult totalResult = new CompareResult();
            for (Future<CompareResult> future : futures) {
                CompareResult result = future.get();
                totalResult.add(result);
            }

            // 输出结果
            logger.info("比对完成！总耗时: {} 毫秒", System.currentTimeMillis() - startTime);
            logger.info("匹配的记录数: {}", totalResult.getMatchedCount());
            logger.info("表A独有的记录数: {}", totalResult.getOnlyInA());
            logger.info("表B独有的记录数: {}", totalResult.getOnlyInB());
            logger.info("不匹配的记录数: {}", totalResult.getMismatched());

        } finally {
            executor.shutdown();
        }
    }

    /**
     * 获取表的总行数
     */
    private static long getTotalRows(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * 比对指定批次的数据
     */
    private static CompareResult compareBatch(Connection conn, String tableA, String tableB, long offset, int limit) throws SQLException {
        CompareResult result = new CompareResult();

        // 查询表A的批次数据
        Map<String, Product> mapA = queryBatch(conn, tableA, offset, limit);
        // 查询表B的批次数据
        Map<String, Product> mapB = queryBatch(conn, tableB, offset, limit);

        // 查找表A独有的记录和匹配但不相同的记录
        for (Map.Entry<String, Product> entry : mapA.entrySet()) {
            String id = entry.getKey();
            Product productA = entry.getValue();

            if (!mapB.containsKey(id)) {
                result.incrementOnlyInA();
            } else {
                Product productB = mapB.get(id);
                if (!productA.equals(productB)) {
                    result.incrementMismatched();
                } else {
                    result.incrementMatchedCount();
                }
                // 从mapB中移除已处理的记录
                mapB.remove(id);
            }
        }

        // 剩下的mapB中的记录都是表B独有的
        result.incrementOnlyInB(mapB.size());

        return result;
    }

    /**
     * 查询指定批次的数据
     */
    private static Map<String, Product> queryBatch(Connection conn, String tableName, long offset, int limit) throws SQLException {
        Map<String, Product> result = new HashMap<>();

        String sql = "SELECT id, name, price, category, create_time FROM " + tableName + " LIMIT ? OFFSET ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setLong(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getString("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setCategory(rs.getString("category"));
                    product.setCreateTime(rs.getTimestamp("create_time"));
                    result.put(product.getId(), product);
                }
            }
        }

        return result;
    }
}