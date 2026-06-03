package com.example.aijobhelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI求职助手 - 启动类
 */
@SpringBootApplication
@MapperScan("com.example.aijobhelper.mapper")
public class AiJobHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiJobHelperApplication.class, args);
    }
}
