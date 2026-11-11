package com.bank.bs.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 用户实体：对应 users 表（JPA 启动时自动建表）
 * 注册、登录、用户管理都围绕这张表
 */
@Entity
@Table(name = "users")
public class User {

    /** 主键，数据库自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名，全局唯一，登录凭据 */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /** 密码（课程作业为简化存明文，实际项目应存哈希） */
    @Column(nullable = false, length = 100)
    private String password;

    /** admin / user */
    @Column(nullable = false, length = 20)
    private String role = "user";

    /** 注册时间；@JsonFormat 让 JSON 输出为 yyyy-MM-dd HH:mm:ss */
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
