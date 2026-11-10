package com.bank.bs.repository;

import java.util.List;
import java.util.Optional;

import com.bank.bs.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByAccountNo(String accountNo);

    List<Account> findAllByOrderByIdAsc();
}
