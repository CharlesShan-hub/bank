package com.bank.server.config;

import java.math.BigDecimal;

import com.bank.server.core.BankStore;
import com.bank.server.model.Account;
import com.bank.server.model.User;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时预置演示数据（内存版：每次重启都会从这里恢复，天然幂等）
 * 1 个管理员 + 3 个普通用户，密码统一 123456
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BankStore store;

    public DataSeeder(BankStore store) {
        this.store = store;
    }

    @Override
    public void run(String... args) {
        // 内存版每次启动都是空库，直接写入演示数据即可
        User admin = saveUser("admin", "123456", "admin");
        User zhangsan = saveUser("zhangsan", "123456", "user");
        User lisi = saveUser("lisi", "123456", "user");
        User wangwu = saveUser("wangwu", "123456", "user");

        // 给三个普通用户各开一个账户并预置余额
        saveAccount(zhangsan, new BigDecimal("5200.00"));
        saveAccount(lisi, new BigDecimal("7800.25"));
        saveAccount(wangwu, new BigDecimal("320.50"));

        System.out.println("已预置演示数据：管理员 " + admin.getUsername() + " + 3 个普通用户及其账户");
    }

    /** 新增一个用户（演示数据，密码明文仅为课程作业简化） */
    private User saveUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return store.saveUser(user);
    }

    /** 给用户开户：账号规则 = 6216 + 用户ID 六位补零（与注册自动开户一致） */
    private void saveAccount(User user, BigDecimal balance) {
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNo("6216" + String.format("%06d", user.getId()));
        account.setBalance(balance);
        store.saveAccount(account);
    }
}
