package org.cloud.demo.common.domain;

import lombok.Data;

/**
 * 商品实体类
 */
@Data
public class Store {
    private Long id;
    private String name;
    private String desc;
    private Double price;
}
