package com.bank.bs.controller;

import java.util.Map;

import com.bank.bs.entity.Account;
import com.bank.bs.entity.User;
import com.bank.bs.repository.AccountRepository;
import com.bank.bs.repository.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册 / 登录
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AuthController(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public record RegisterBody(String username, String password) {}

    public record LoginBody(String username, String password) {}

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterBody body) {
        if (body.username() == null || body.username().isBlank()
                || body.password() == null || body.password().isBlank()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        if (userRepository.existsByUsername(body.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(body.username());
        user.setPassword(body.password());
        user.setRole("user");
        userRepository.save(user);

        // 注册自动开立账户，账号 = 622202 + 用户ID 六位补零
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNo("622202" + String.format("%06d", user.getId()));
        accountRepository.save(account);

        return Map.of("message", "注册成功");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginBody body) {
        User user = userRepository.findByUsername(body.username())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!user.getPassword().equals(body.password())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole());
    }
}
