package com.bank.bs.repository;

import java.util.Optional;

import com.bank.bs.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户表数据访问：继承 JpaRepository 即免费获得全套 CRUD
 * 查询方法按 Spring Data 命名规则自动生成 SQL，无需写实现
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 按用户名查找（登录用） */
    Optional<User> findByUsername(String username);

    /** 用户名是否已存在（注册查重、编辑查重） */
    boolean existsByUsername(String username);
}
