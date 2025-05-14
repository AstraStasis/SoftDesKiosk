package com.example.softdeskiosk;

import java.math.BigDecimal;

public class Uniform {
    private String code;
    private String classification;
    private String size;
    private BigDecimal price;
    private int stock;

    /**
     * @param code           Unique item code
     * @param classification Classification or category name
     * @param size           Size (e.g. S, M, L, XL)
     * @param price          Price as BigDecimal
     * @param stock          Available stock count
     */
    public Uniform(String code, String classification, String size, BigDecimal price, int stock) {
        this.code = code;
        this.classification = classification;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() {
        return code;
    }

    public String getClassification() {
        return classification;
    }

    public String getSize() {
        return size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    /**
     * Decrease stock by one when an order is placed.
     */
    public void decrementStock() {
        if (stock > 0) {
            stock--;
        }
    }
}
