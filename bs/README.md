# Bank BS — 银行管理系统（Spring Boot + MySQL 版）

前后端分离：Vue3 + Element Plus 前端（`frontend`），Spring Boot 3 + Spring Data JPA + MySQL 后端（`backend`，兼容 scoop 安装的 MariaDB 服务器）。

## 一、功能范围

| 模块 | 类型 | 要求要点 |
| :--- | :--- | :--- |
| 用户注册 | 必做 | RegisterView.vue，el-form（用户名/密码/确认密码），v-model 双向绑定，axios 提交；成功跳登录页，失败弹窗提示；注册自动开立账户 |
| 用户登录 | 隐含必备 | 返回用户角色；admin 进管理主页，普通用户进用户主页 |
| 用户管理 | 选做 2.1 | admin 登录后，左侧菜单切换，el-table 用户列表 + 删除/编辑按钮 |
| 账户管理 | 选做 2.2(1) | admin 左侧菜单切换，el-table 展示所有账户信息（按要求去掉 2.2(2) 的注销/编辑按钮） |
| 普通用户主页 | 选做 2.3 | 显示当前用户账户余额；el-button 提供存款/取款/转账；点击不同按钮显示不同表单弹窗 |

## 二、技术栈

- **前端**：Vue3 + Vite + Element Plus + Vue Router + Axios（yarn，端口 7997）
- **后端**：JDK 17 + Spring Boot 3.4 + Spring Data JPA + mysql-connector-j（端口 6997）
- **数据库**：MariaDB 13（MySQL 协议兼容，库名 `bank_bs` 由连接串自动创建，JPA 自动建表并预置数据）
- **联调**：Vite 把 `/api` 代理到 `http://localhost:6997`，后端零跨域代码

## 三、目录结构

```
bs/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/bank/bs/
│       │   ├── BsApplication.java          # 启动类
│       │   ├── config/DataSeeder.java      # 首次启动预置演示数据
│       │   ├── config/GlobalExceptionHandler.java  # 业务异常 → 400 + message
│       │   ├── entity/User.java / Account.java
│       │   ├── repository/UserRepository.java / AccountRepository.java
│       │   └── controller/AuthController.java / UserController.java / AccountController.java
│       └── resources/application.yml
└── frontend/
    ├── src/views/  LoginView / RegisterView / AdminHome / UserManageView / AccountManageView / UserHomeView
    ├── src/router/index.js
    ├── src/api/request.js
    └── vite.config.js
```

## 四、数据库设计（JPA 自动建表）

- **users**：id, username(唯一), password, role('admin'/'user'), created_at
- **accounts**：id, user_id, account_no(唯一), balance DECIMAL(12,2), status('正常'/'注销'), created_at
- 一个用户对应一个账户；注册时自动生成账号（622202 + 用户ID 六位补零）

## 五、API 设计（前缀 /api，错误统一返回 400 + {message}）

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | /api/register | 注册（查重 + 自动开户） |
| POST | /api/login | 登录，返回 { id, username, role } |
| GET | /api/users | 用户列表（2.1） |
| PUT | /api/users/{id} | 编辑用户 |
| DELETE | /api/users/{id} | 删除用户（admin 不可删，级联删账户） |
| GET | /api/accounts | 账户列表，联查用户名（2.2(1)） |
| GET | /api/account/{userId} | 当前用户账户信息（2.3） |
| POST | /api/account/{userId}/deposit | 存款 |
| POST | /api/account/{userId}/withdraw | 取款（余额不足返回 400） |
| POST | /api/account/{userId}/transfer | 转账（事务：余额校验 + 双方账户更新） |

## 六、快速启动

```bash
# 1. 后端（先启，首次运行会自动下载依赖、建库建表并预置数据）
cd bs/backend
mvn spring-boot:run

# 2. 前端
cd bs/frontend
yarn install
yarn dev
# 浏览器打开 http://localhost:7997
```

## 七、预置测试账号

| 用户名 | 密码 | 角色 | 账户余额 |
| :--- | :--- | :--- | :--- |
| admin | 123456 | 管理员 | - |
| zhangsan | 123456 | 普通用户 | ¥ 8500.00 |
| lisi | 123456 | 普通用户 | ¥ 12300.50 |
| wangwu | 123456 | 普通用户 | ¥ 66.66 |
