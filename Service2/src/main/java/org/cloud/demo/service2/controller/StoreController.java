package org.cloud.demo.service2.controller;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.cloud.demo.common.domain.Store;
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
    List<Store> getStores() {
        log.info("我被调用了");
        int code = RandomUtil.randomInt();
        if (code % 2 == 0) {
            throw new RuntimeException("服务异常");
        }
        try {
            TimeUnit.SECONDS.sleep(RandomUtil.randomInt(4,8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Store> stores = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            Store store = new Store();
            store.setId((long) i);
            store.setName("商品" + i);
            store.setDesc("商品" + i + "真便宜");
            store.setPrice(RandomUtil.randomDouble(1000, 2, RoundingMode.DOWN));
            stores.add(store);
        }
        return stores;
    }
}
