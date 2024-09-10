package org.cloud.demo.service2.controller;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.service2.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("store")
public class StoreController {
    @GetMapping("/stores")
    List<Product> getStores() {
        log.info("我被调用了");
        int code = RandomUtil.randomInt();
        if (code % 2 == 0) {
            throw new RuntimeException("服务异常");
        }
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(4, 8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Product> products = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setId((long) i);
            product.setName("商品" + i);
            product.setDesc("商品" + i + "真便宜");
            product.setPrice(RandomUtil.randomDouble(1000, 2, RoundingMode.DOWN));
            products.add(product);
        }
        return products;
    }
}
