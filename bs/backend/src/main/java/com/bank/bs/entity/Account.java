package com.bank.bs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 账户实体：对应 accounts 表
 * 一个用户一个账户（userId 关联 users.id），存款/取款/转账都围绕 balance
 */
@Entity
@Table(name = "accounts")
public class Account {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 ID（逻辑外键 → users.id） */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 账号，全局唯一；转账时用它定位收款方 */
    @Column(name = "account_no", unique = true, nullable = false, length = 30)
    private String accountNo;

    /** 余额：BigDecimal(12,2) 保证金额精度，不用 double 避免浮点误差 */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** 正常 / 注销 */
    @Column(nullable = false, length = 20)
    private String status = "正常";

    /** 开户时间 */
    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getAccountNo() { return accountNo; }

    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
