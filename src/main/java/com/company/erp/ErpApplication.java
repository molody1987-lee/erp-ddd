package com.company.erp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ERP 单体应用启动类。
 *
 * <p>通过扫描 {@code com.company.erp} 一并承载采购、库存等限界上下文。
 */
@SpringBootApplication
@MapperScan("com.company.erp")
public class ErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class, args);
    }
}