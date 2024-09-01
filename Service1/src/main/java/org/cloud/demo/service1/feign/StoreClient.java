package org.cloud.demo.service1.feign;

import org.cloud.demo.common.domain.Store;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// 还是要使用CircuitBreaker的fallback机制，不使用feign的fallback功能，因为无法做熔断和降级
@FeignClient("service2")
public interface StoreClient {
    @GetMapping("store/stores")
    List<Store> getStores();
}

