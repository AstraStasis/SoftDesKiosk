package com.example.softdeskiosk;

public class Uniform {
    private String code;
    private String classification;
    private String size;
    private String price;

    public Uniform(String code, String name, String price) {
        this.code = code;
        String[] parts = name.split("(?i) (?=Small|Medium|Large|XL|2XL|3XL|4XL)");
        this.classification = parts[0].trim();
        this.size = parts.length > 1 ? parts[1].trim() : "";
        this.price = price;
    }

    public String getCode() { return code; }
    public String getClassification() { return classification; }
    public String getSize() { return size; }
    public String getPrice() { return price; }
}
