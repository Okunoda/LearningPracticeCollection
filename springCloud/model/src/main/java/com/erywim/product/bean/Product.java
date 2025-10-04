package com.erywim.product.bean;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Product {
    private Long id;
    private BigDecimal price;
    private String name;
    private int num;
}

