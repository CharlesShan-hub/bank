package com.bank.bs.config;

import java.math.BigDecimal;
import java.util.List;

import com.bank.bs.entity.Account;
import com.bank.bs.entity.User;
import com.bank.bs.repository.AccountRepository;
import com.bank.bs.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 首次启动时预置演示数据（users 表为空才执行，幂等）
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public DataSeeder(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) {
        // users 表有数据就跳过，保证幂等（重启不会重复插入）
        if (userRepository.count() > 0) {
            return;
        }
        // 1 个管理员 + 3 个普通用户，密码统一 123456
        User admin = saveUser("admin", "123456", "admin");
        User zhangsan = saveUser("zhangsan", "123456", "user");
        User lisi = saveUser("lisi", "123456", "user");
        User wangwu = saveUser("wangwu", "123456", "user");

        // 给三个普通用户各开一个账户并预置余额
        saveAccount(zhangsan, new BigDecimal("8500.00"));
        saveAccount(lisi, new BigDecimal("12300.50"));
        saveAccount(wangwu, new BigDecimal("66.66"));

        System.out.println("已预置演示数据：管理员 " + admin.getUsername() + " + 3 个普通用户及其账户");
    }

    /** 新增一个用户（演示数据，密码明文仅为课程作业简化） */
    private User saveUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return userRepository.save(user);
    }

    /** 给用户开户：账号规则 = 622202 + 用户ID 六位补零（与注册自动开户一致） */
    private void saveAccount(User user, BigDecimal balance) {
        Account account = new Account();
        account.setUserId(user.getId());
        account.setAccountNo("622202" + String.format("%06d", user.getId()));
        account.setBalance(balance);
        accountRepository.save(account);
    }
}
