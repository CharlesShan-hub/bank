package com.bank.bs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类：整个后端的入口
 * 运行后监听 6997 端口（见 application.yml），前端 Vite 把 /api 代理到这里
 */
@SpringBootApplication
public class BsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BsApplication.class, args);
    }
}
