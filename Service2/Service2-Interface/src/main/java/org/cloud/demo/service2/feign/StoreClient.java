package org.cloud.demo.service2.feign;

import org.cloud.demo.service2.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface StoreClient {
    @GetMapping("store/stores")
    List<Product> getStores();
}
