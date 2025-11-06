# 数据比对工具

一个高效的Java工具，用于比对MySQL数据库中两个表的数据。

## 功能特点

- 支持百万级数据快速比对
- 并行处理，提高比对效率
- 精确比对记录内容
- 提供详细的比对结果统计
- 配置简单，易于使用

## 技术实现

- **Java 8**：使用Java 8编写，确保性能和兼容性
- **MySQL驱动**：使用MySQL官方JDBC驱动连接数据库
- **日志记录**：使用SLF4J进行日志管理
- **并行处理**：使用Java并发包提高比对速度

## 表结构要求

被比对的两个表需要满足以下结构：

```sql
CREATE TABLE `product_a` (
  `id` varchar(50) NOT NULL PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category` varchar(50) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `product_b` (
  `id` varchar(50) NOT NULL PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category` varchar(50) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 配置说明

在`DataCompareApp.java`中可以配置以下参数：

- **数据库连接**：`DB_URL`、`DB_USER`、`DB_PASSWORD`
- **表名**：`TABLE_A`、`TABLE_B`
- **批量大小**：`BATCH_SIZE`（默认10000，可根据内存调整）
- **线程池大小**：`THREAD_POOL_SIZE`（默认CPU核心数×2）

## 编译和运行

### 编译项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包项目

```bash
mvn package
```

### 运行程序

```bash
java -jar target/my_study-1.0-SNAPSHOT.jar
```

## 比对结果

程序运行后会输出以下结果：

- 匹配的记录数：两个表中ID存在且内容完全相同的记录数
- 表A独有的记录数：仅存在于表A中的记录数
- 表B独有的记录数：仅存在于表B中的记录数
- 不匹配的记录数：ID存在于两个表中但内容不同的记录数

## 性能优化建议

1. 为表的`id`列创建主键或索引
2. 根据服务器内存调整`BATCH_SIZE`参数
3. 确保网络连接稳定
4. 适当调整线程池大小

## 依赖

- `mysql-connector-java`：MySQL JDBC驱动
- `slf4j-api`：SLF4J日志API
- `slf4j-simple`：SLF4J简单实现
- `junit`：单元测试框架

## 版本要求

- Java 8+ 
- MySQL 5.7+ 
- Maven 3.0+