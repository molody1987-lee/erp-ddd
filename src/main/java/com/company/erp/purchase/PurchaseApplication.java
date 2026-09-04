package com.company.erp.purchase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 采购上下文启动类。
 */
@SpringBootApplication(scanBasePackages = "com.company.erp")
@MapperScan("com.company.erp")
public class PurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseApplication.class, args);
    }
}