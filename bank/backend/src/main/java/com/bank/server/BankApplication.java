package com.bank.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类：整个后端的入口
 * 运行后监听 6995 端口（见 application.yml），前端 Vite 把 /api 代理到这里
 * 特点：不连接任何数据库，数据保存在内存（core/BankStore）
 */
@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}
