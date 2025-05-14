package com.example.softdeskiosk;

public class Book {
    private final String code;
    private final String name;
    private final String price;

    public Book(String code, String name, String price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
