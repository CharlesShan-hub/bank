package com.bank.server.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.bank.server.model.Account;
import com.bank.server.model.User;

import org.springframework.stereotype.Component;

/**
 * 内存数据仓库：整个后端的"数据库"
 * 用 ConcurrentHashMap 保存用户与账户，线程安全；重启后恢复为 DataSeeder 的预置数据（不落盘）
 */
@Component
public class BankStore {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong userSeq = new AtomicLong();
    private final AtomicLong accountSeq = new AtomicLong();

    // ---------------- 用户 ----------------

    /** 新增或更新用户；无 id 时自动分配（自增） */
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userSeq.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /** 按用户名精确查找 */
    public Optional<User> findUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findUserByUsername(username).isPresent();
    }

    /** 全部用户，按 id 升序（模拟数据库自增主键的自然顺序） */
    public Collection<User> allUsers() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    public long userCount() {
        return users.size();
    }

    public void deleteUser(Long id) {
        users.remove(id);
    }

    // ---------------- 账户 ----------------

    /** 新增或更新账户；无 id 时自动分配（自增） */
    public Account saveAccount(Account account) {
        if (account.getId() == null) {
            account.setId(accountSeq.incrementAndGet());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findAccount(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    /** 按所属用户找账户（本系统一个用户只有一个账户） */
    public Optional<Account> findAccountByUserId(Long userId) {
        return accounts.values().stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst();
    }

    /** 按账号找账户（转账时定位收款方） */
    public Optional<Account> findAccountByAccountNo(String accountNo) {
        return accounts.values().stream()
                .filter(a -> a.getAccountNo().equals(accountNo))
                .findFirst();
    }

    public void deleteAccount(Long id) {
        accounts.remove(id);
    }
}
