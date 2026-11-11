package com.bank.bs.repository;

import java.util.List;
import java.util.Optional;

import com.bank.bs.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 账户表数据访问：继承 JpaRepository 获得全套 CRUD
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /** 按用户 ID 找账户（用户主页查询、删除用户时级联清理） */
    Optional<Account> findByUserId(Long userId);

    /** 按账号找账户（转账时定位收款方） */
    Optional<Account> findByAccountNo(String accountNo);

    /** 全部账户按 ID 升序（账户列表页展示顺序稳定） */
    List<Account> findAllByOrderByIdAsc();
}
