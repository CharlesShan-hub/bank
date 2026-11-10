package com.bank.bs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bank.bs.entity.User;
import com.bank.bs.repository.AccountRepository;
import com.bank.bs.repository.UserRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理（2.1）
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserController(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public record UserUpdateBody(String username, String password) {}

    @GetMapping
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            result.add(Map.of(
                    "id", u.getId(),
                    "username", u.getUsername(),
                    "role", u.getRole(),
                    "createdAt", u.getCreatedAt().toString()));
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, String> update(@PathVariable Long id, @RequestBody UserUpdateBody body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (body.username() != null && !body.username().isBlank()) {
            userRepository.findByUsername(body.username())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(other -> { throw new IllegalArgumentException("用户名已存在"); });
            user.setUsername(body.username());
        }
        if (body.password() != null && !body.password().isBlank()) {
            user.setPassword(body.password());
        }
        userRepository.save(user);
        return Map.of("message", "修改成功");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if ("admin".equals(user.getUsername())) {
            throw new IllegalArgumentException("不允许删除管理员");
        }
        accountRepository.findByUserId(id).ifPresent(accountRepository::delete);
        userRepository.delete(user);
        return Map.of("message", "删除成功");
    }
}
