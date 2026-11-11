package com.bank.server.controller;

import java.math.BigDecimal;
import java.util.Map;

import com.bank.server.core.BankStore;
import com.bank.server.model.Account;
import com.bank.server.model.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 必做要求：注册 + 登录
 * - POST /api/register：用户名查重后建用户，并自动开立一个账户
 * - POST /api/login：校验密码，返回 { id, username, role } 供前端按角色分流
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final BankStore store;

    public AuthController(BankStore store) {
        this.store = store;
    }

    // 请求体用 record 定义：不可变、自动生成访问器，Java 17 的简洁写法
    public record RegisterBody(String username, String password) {}

    public record LoginBody(String username, String password) {}

    /** 注册：非空校验 → 用户名查重 → 建用户 → 自动开户 */
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterBody body) {
        // 参数非法直接抛异常，由 GlobalExceptionHandler 统一转成 400 + message
        if (body.username() == null || body.username().isBlank()
                || body.password() == null || body.password().isBlank()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        if (store.existsByUsername(body.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(body.username());
        user.setPassword(body.password());
        user.setRole("user");
        store.saveUser(user);

        // 注册自动开立账户，账号 = 6216 + 用户ID 六位补零，初始余额 0
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNo("6216" + String.format("%06d", user.getId()));
        account.setBalance(BigDecimal.ZERO);
        store.saveAccount(account);

        return Map.of("message", "注册成功");
    }

    /** 登录：按用户名查人 → 比对密码 → 返回用户信息（不返回密码） */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginBody body) {
        // 查不到用户与密码错误都报同一句话，避免暴露「该用户名是否存在」
        User user = store.findUserByUsername(body.username())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!user.getPassword().equals(body.password())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole());
    }
}
