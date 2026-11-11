package com.bank.server.model;

import java.time.LocalDateTime;

/**
 * 用户实体（内存版 POJO，无 JPA 注解）
 * id 自增分配；username 唯一（业务层查重保证）；role 取值 admin / user
 * 密码明文保存仅为课程作业简化
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
