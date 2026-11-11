package com.bank.server.controller;

import java.math.BigDecimal;
import java.util.Map;

import com.bank.server.core.BankStore;
import com.bank.server.model.Account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 选做 2.3：普通用户主页后端（余额查询 / 存款 / 取款 / 转账）
 * 本项目未选 2.2，故不提供面向管理员的账户列表接口
 */
@RestController
@RequestMapping("/api")
public class AccountController {

    private final BankStore store;

    public AccountController(BankStore store) {
        this.store = store;
    }

    public record AmountBody(BigDecimal amount) {}

    public record TransferBody(String toAccountNo, BigDecimal amount) {}

    /** 按用户 ID 取账户，找不到直接报业务异常 */
    private Account getAccountByUserId(Long userId) {
        return store.findAccountByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("账户不存在"));
    }

    /** 2.3：当前用户账户信息（账号 + 实时余额） */
    @GetMapping("/account/{userId}")
    public Map<String, Object> mine(@PathVariable Long userId) {
        Account account = getAccountByUserId(userId);
        return Map.of(
                "id", account.getId(),
                "accountNo", account.getAccountNo(),
                "balance", account.getBalance(),
                "status", account.getStatus());
    }

    /** 2.3：存款。内存版单线程语义下天然一致，无需数据库事务 */
    @PostMapping("/account/{userId}/deposit")
    public Map<String, String> deposit(@PathVariable Long userId, @RequestBody AmountBody body) {
        checkAmount(body.amount());
        Account account = getAccountByUserId(userId);
        account.setBalance(account.getBalance().add(body.amount()));
        store.saveAccount(account);
        return Map.of("message", "存款成功");
    }

    /** 2.3：取款 */
    @PostMapping("/account/{userId}/withdraw")
    public Map<String, String> withdraw(@PathVariable Long userId, @RequestBody AmountBody body) {
        checkAmount(body.amount());
        Account account = getAccountByUserId(userId);
        // compareTo < 0 说明当前余额小于取款金额
        if (account.getBalance().compareTo(body.amount()) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        account.setBalance(account.getBalance().subtract(body.amount()));
        store.saveAccount(account);
        return Map.of("message", "取款成功");
    }

    /** 2.3：转账（同一内存仓库内一减一加，保证双方一致） */
    @PostMapping("/account/{userId}/transfer")
    public Map<String, String> transfer(@PathVariable Long userId, @RequestBody TransferBody body) {
        checkAmount(body.amount());
        Account from = getAccountByUserId(userId);
        // 收款方按账号定位，账号不存在直接报错
        Account to = store.findAccountByAccountNo(body.toAccountNo())
                .orElseThrow(() -> new IllegalArgumentException("目标账户不存在"));
        if (to.getId().equals(from.getId())) {
            throw new IllegalArgumentException("不能转账给自己");
        }
        if (from.getBalance().compareTo(body.amount()) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        // 一减一加两笔更新，顺序执行，任一步失败即整体不生效
        from.setBalance(from.getBalance().subtract(body.amount()));
        to.setBalance(to.getBalance().add(body.amount()));
        store.saveAccount(from);
        store.saveAccount(to);
        return Map.of("message", "转账成功");
    }

    /** 金额校验：非空且 > 0（signum() <= 0 覆盖 0 和负数） */
    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("金额必须大于 0");
        }
    }
}
