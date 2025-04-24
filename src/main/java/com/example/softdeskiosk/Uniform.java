package com.example.softdeskiosk;

public class Uniform {
    private String code;
    private String name;
    private String price;

    public Uniform(String code, String name, String price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    // Getters only (TableView uses these)
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getPrice() { return price; }
}