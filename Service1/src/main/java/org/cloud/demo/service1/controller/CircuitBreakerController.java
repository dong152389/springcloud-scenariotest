package org.cloud.demo.service1.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.cloud.demo.common.domain.Store;
import org.cloud.demo.service1.feign.StoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 服务间调用的熔断测试
 */
@RestController
@RequestMapping("circuitBreaker")
public class CircuitBreakerController {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerController.class);
    private final StoreClient storeClient;

    public CircuitBreakerController(@Qualifier("org.cloud.demo.service1.feign.StoreClient") StoreClient storeClient) {
        this.storeClient = storeClient;
    }
    //熔断配置，如果对面服务多次失败，需要快速熔断，否则时间长了一定出现服务雪崩
    @CircuitBreaker(name = "service2", fallbackMethod = "fallback")
    //我这里是为了省事，真实情况需要分开，因为TimeLimiter是超时配置，超时并不代表服务不可用了，只是为了防止服务长时间不返回影响到本服务
    @TimeLimiter(name = "service2", fallbackMethod = "fallback")
    @GetMapping("list")
    public CompletionStage<List<Store>> getDataFromService2() {
        return CompletableFuture.supplyAsync(storeClient::getStores);
    }

    public CompletionStage<List<Store>> fallback(Throwable throwable) {
        log.error(throwable.getMessage());
        return CompletableFuture.completedFuture(List.of());
    }


}
