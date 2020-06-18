package com.nab.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String brand;
    private String color;
    private BigDecimal defaultPrice;

    //public ProductDTO() {}

//    public ProductDTO(Long id, String code, String name, String brand, String color,
//        BigDecimal defaultPrice) {
//        this.id = id;
//        this.code = code;
//        this.name = name;
//        this.brand = brand;
//        this.color = color;
//        this.defaultPrice = defaultPrice;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", brand='" + brand + '\'' +
            ", color='" + color + '\'' +
            ", defaultPrice=" + defaultPrice +
            '}';
    }
}
