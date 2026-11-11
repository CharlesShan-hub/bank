package com.bank.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bank.server.core.BankStore;
import com.bank.server.model.Account;
import com.bank.server.model.User;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 选做 2.1：用户管理
 * - GET    /api/users      用户列表
 * - PUT    /api/users/{id} 编辑（用户名查重；密码留空表示不改）
 * - DELETE /api/users/{id} 删除（admin 保护；级联删除名下账户）
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final BankStore store;

    public UserController(BankStore store) {
        this.store = store;
    }

    public record UserUpdateBody(String username, String password) {}

    /** 用户列表：手动挑字段返回，不把 password 带出去 */
    @GetMapping
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : store.allUsers()) {
            result.add(Map.of(
                    "id", u.getId(),
                    "username", u.getUsername(),
                    "role", u.getRole(),
                    "createdAt", u.getCreatedAt().toString()));
        }
        return result;
    }

    /** 编辑用户：用户名改动时查重（排除自己）；密码为空则不动 */
    @PutMapping("/{id}")
    public Map<String, String> update(@PathVariable Long id, @RequestBody UserUpdateBody body) {
        User user = store.findUser(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (body.username() != null && !body.username().isBlank()) {
            // 查到同名用户且不是自己 → 拒绝修改
            store.findUserByUsername(body.username())
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(other -> { throw new IllegalArgumentException("用户名已存在"); });
            user.setUsername(body.username());
        }
        if (body.password() != null && !body.password().isBlank()) {
            user.setPassword(body.password());
        }
        store.saveUser(user);
        return Map.of("message", "修改成功");
    }

    /** 删除用户：admin 保护；先删名下账户再删用户（级联） */
    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        User user = store.findUser(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 演示数据里管理员用户名固定为 admin，不允许删掉
        if ("admin".equals(user.getUsername())) {
            throw new IllegalArgumentException("不允许删除管理员");
        }
        // 级联删除：先清掉名下账户，避免留下孤儿数据
        store.findAccountByUserId(id).ifPresent(account -> store.deleteAccount(account.getId()));
        store.deleteUser(id);
        return Map.of("message", "删除成功");
    }
}
