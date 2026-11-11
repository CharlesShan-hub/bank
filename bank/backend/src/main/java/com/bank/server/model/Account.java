package com.bank.server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户实体（内存版 POJO，无 JPA 注解）
 * balance 用 BigDecimal 避免浮点误差；accountNo 全局唯一，用于转账定位
 * status 取值「正常 / 注销」，注销为软删除标记
 */
public class Account {

    private Long id;
    private Long userId;
    private String accountNo;
    private BigDecimal balance = BigDecimal.ZERO;
    private String status = "正常";
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
