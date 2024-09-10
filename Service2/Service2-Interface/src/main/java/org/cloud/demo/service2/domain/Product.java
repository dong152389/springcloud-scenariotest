package org.cloud.demo.service2.domain;

import lombok.Data;

/**
 * 商品实体类
 */
@Data
public class Product {
    private Long id;
    private String name;
    private String desc;
    private Double price;
}
