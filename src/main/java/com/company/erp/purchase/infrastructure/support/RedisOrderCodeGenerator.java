package com.company.erp.purchase.infrastructure.support;

import com.company.erp.purchase.application.port.OrderCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 基于 Redis INCR 的订单编码生成器，生成形如 PO20260904000001 的编码。
 */
@Component
@RequiredArgsConstructor
public class RedisOrderCodeGenerator implements OrderCodeGenerator {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String nextCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long seq = redisTemplate.opsForValue().increment("purchase:order:sequence:" + date);
        return "PO" + date + String.format("%06d", seq);
    }
}