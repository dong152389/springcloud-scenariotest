package org.cloud.demo.workflow.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth")
public interface AuthFeignClient extends org.cloud.demo.auth.domain.feign.AuthFeignClient {
}
