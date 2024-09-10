package org.cloud.demo.service1.feign;

import org.cloud.demo.service2.feign.StoreClient;
import org.springframework.cloud.openfeign.FeignClient;

// 还是要使用CircuitBreaker的fallback机制，不使用feign的fallback功能，因为无法做熔断和降级
@FeignClient("service2")
public interface Service2Client extends StoreClient {
}

