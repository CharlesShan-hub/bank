package com.bank.bs.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bank.bs.entity.Account;
import com.bank.bs.repository.AccountRepository;
import com.bank.bs.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户管理（2.2(1)：仅列表展示）+ 普通用户主页（2.3：余额 / 存款 / 取款 / 转账）
 */
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountController(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public record AmountBody(BigDecimal amount) {}

    public record TransferBody(String toAccountNo, BigDecimal amount) {}

    /** 2.2(1)：账户列表（联查用户名），无注销/编辑操作 */
    @GetMapping("/accounts")
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Account a : accountRepository.findAllByOrderByIdAsc()) {
            // 逐条联查用户名——课程作业数据量小，直接循环查询足够清晰
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("userId", a.getUserId());
            m.put("username", userRepository.findById(a.getUserId())
                    .map(u -> u.getUsername()).orElse("-"));
            m.put("accountNo", a.getAccountNo());
            m.put("balance", a.getBalance());
            m.put("status", a.getStatus());
            m.put("createdAt", a.getCreatedAt().toString());
            result.add(m);
        }
        return result;
    }

    /** 按用户 ID 取账户，找不到直接报业务异常 */
    private Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("账户不存在"));
    }

    /** 2.3：当前用户账户信息 */
    @GetMapping("/account/{userId}")
    public Map<String, Object> mine(@PathVariable Long userId) {
        Account account = getAccountByUserId(userId);
        return Map.of(
                "id", account.getId(),
                "accountNo", account.getAccountNo(),
                "balance", account.getBalance(),
                "status", account.getStatus());
    }

    /** 2.3：存款 */
    @Transactional
    @PostMapping("/account/{userId}/deposit")
    public Map<String, String> deposit(@PathVariable Long userId, @RequestBody AmountBody body) {
        checkAmount(body.amount());
        Account account = getAccountByUserId(userId);
        account.setBalance(account.getBalance().add(body.amount()));
        accountRepository.save(account);
        return Map.of("message", "存款成功");
    }

    /** 2.3：取款 */
    @Transactional
    @PostMapping("/account/{userId}/withdraw")
    public Map<String, String> withdraw(@PathVariable Long userId, @RequestBody AmountBody body) {
        checkAmount(body.amount());
        Account account = getAccountByUserId(userId);
        // compareTo < 0 说明当前余额小于取款金额
        if (account.getBalance().compareTo(body.amount()) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        account.setBalance(account.getBalance().subtract(body.amount()));
        accountRepository.save(account);
        return Map.of("message", "取款成功");
    }

    /** 2.3：转账（事务保证双方账户一致性） */
    @Transactional
    @PostMapping("/account/{userId}/transfer")
    public Map<String, String> transfer(@PathVariable Long userId, @RequestBody TransferBody body) {
        checkAmount(body.amount());
        Account from = getAccountByUserId(userId);
        // 收款方按账号定位，账号不存在直接报错
        Account to = accountRepository.findByAccountNo(body.toAccountNo())
                .orElseThrow(() -> new IllegalArgumentException("目标账户不存在"));
        if (to.getId().equals(from.getId())) {
            throw new IllegalArgumentException("不能转账给自己");
        }
        if (from.getBalance().compareTo(body.amount()) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        // 一减一加两笔更新；@Transactional 保证要么都成功、要么都回滚
        from.setBalance(from.getBalance().subtract(body.amount()));
        to.setBalance(to.getBalance().add(body.amount()));
        accountRepository.save(from);
        accountRepository.save(to);
        return Map.of("message", "转账成功");
    }

    /** 金额校验：非空且 > 0（signum() <= 0 覆盖 0 和负数） */
    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("金额必须大于 0");
        }
    }
}
