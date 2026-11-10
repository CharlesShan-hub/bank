# Bank System — 银行管理系统（课程大作业）

前后端分离架构：Vue3 + Element Plus 前端（`bank-front`），FastAPI + SQLite 轻量后端（`bank-server`）。

## 一、功能范围

| 模块 | 类型 | 要求要点 |
| :--- | :--- | :--- |
| 用户注册 | 必做 | RegisterView.vue，el-form（用户名/密码/确认密码），v-model 双向绑定，axios 提交；成功跳登录页，失败弹窗提示 |
| 用户登录 | 隐含必备 | 校验账号密码，返回用户角色；admin 进管理主页，普通用户进用户主页 |
| 用户管理 | 选做 2.1 | admin 登录后，左侧菜单切换，el-table 显示用户列表，提供删除/编辑按钮 |
| 账户管理 | 选做 2.2 | 同上布局，el-table 显示账户列表，提供注销/编辑按钮 |
| 普通用户主页 | 预留 | 普通用户登录后进入，仅含欢迎信息与退出入口，业务功能（存款/取款/转账）暂未实现 |

## 二、技术栈

- **前端**：Vue3 + Vite + Element Plus + Vue Router + Axios，包管理用 yarn（`yarn install` → `yarn dev` 一键启动）
- **后端**：Python（scoop 安装）+ FastAPI + uvicorn + sqlite3 标准库，单文件 `main.py`，不用 ORM
- **联调**：Vite dev server（7998）把 `/api` 代理到 `http://localhost:6998`，后端零跨域代码

## 三、目录结构

```
bank-system/
├── bank-front/
│   ├── src/
│   │   ├── views/
│   │   │   ├── LoginView.vue          # 登录
│   │   │   ├── RegisterView.vue       # 注册（必做）
│   │   │   ├── AdminHome.vue          # 管理主页：左侧 el-menu + 右侧 router-view
│   │   │   ├── UserManageView.vue     # 2.1 用户列表 + 删除/编辑弹窗
│   │   │   ├── AccountManageView.vue  # 2.2 账户列表 + 注销/编辑弹窗
│   │   │   └── UserHomeView.vue       # 普通用户主页（预留：欢迎信息 + 退出）
│   │   ├── router/index.js            # /login /register /admin 子路由 + 登录守卫
│   │   ├── api/request.js             # axios 实例，baseURL = '/api'，错误统一弹窗
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── bank-server/
    ├── main.py                        # 建表 + 预置数据 + 全部接口
    └── requirements.txt               # fastapi、uvicorn
```

## 四、数据库设计（SQLite，首次启动自动建表并预置数据）

- **users**：id, username, password, role('admin'/'user'), created_at
- **accounts**：id, user_id, account_no, balance, status('正常'/'注销'), created_at
- 注销为**软删除**：仅把 status 置为「注销」，保留记录
- 删除用户时级联删除其名下账户

## 五、API 设计（前缀 /api）

| 方法 | 路径 | 说明 |
| :--- | :--- | :--- |
| POST | /api/register | 注册，用户名查重，重复返回 400 |
| POST | /api/login | 校验密码，返回 { id, username, role } |
| GET | /api/users | 用户列表 |
| PUT | /api/users/{id} | 编辑用户（用户名/密码） |
| DELETE | /api/users/{id} | 删除用户（管理员不可删） |
| GET | /api/accounts | 账户列表（JOIN users 带出用户名） |
| PUT | /api/accounts/{id} | 编辑账户（余额/状态） |
| DELETE | /api/accounts/{id} | 注销账户（status 置为「注销」） |

## 六、快速启动

```bash
# 1. 启动后端（先启）
cd bank-server
python3 -m pip install -r requirements.txt
python3 -m uvicorn main:app --reload --port 6998

# 2. 启动前端
cd bank-front
yarn install
yarn dev
# 浏览器打开 http://localhost:7998
```

## 七、预置测试账号

| 用户名 | 密码 | 角色 |
| :--- | :--- | :--- |
| admin | 123456 | 管理员 |
| zhangsan / lisi / wangwu | 123456 | 普通用户 |

## 八、开发顺序备忘

1. ~~Vite 脚手架 + Element Plus / Router / Axios~~
2. ~~后端 main.py：建表预置数据 + register / login 接口~~
3. ~~RegisterView、LoginView 页面并联调（覆盖必做题全部得分点）~~
4. ~~AdminHome 布局 + UserManageView / AccountManageView（表格 + 弹窗复用）~~
5. 整体截图，按 framework.md 模板完成报告
