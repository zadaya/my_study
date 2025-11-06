package com.example;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class Product {
    private String id;
    private String name;
    private BigDecimal price;
    private String category;
    private Timestamp createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                (price == null ? product.price == null : price.compareTo(product.price) == 0) &&
                Objects.equals(category, product.category) &&
                Objects.equals(createTime, product.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, createTime);
    }
}