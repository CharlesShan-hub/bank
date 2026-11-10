"""银行管理系统后端：FastAPI + SQLite（单文件实现，无 ORM）

模块职责：
  1. 启动时自动建表、预置演示数据（init_db），
     幂等设计：已有数据则跳过，重复启动不重复插入；
  2. 提供注册 / 登录接口（必做题）；
  3. 提供用户管理接口（选做 2.1）；
  4. 提供账户管理接口（选做 2.2）。

启动：python -m uvicorn main:app --reload --port 6998
"""
import sqlite3
from contextlib import closing
from typing import Optional

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

# SQLite 数据库文件路径
# 与 main.py 同目录，首次运行时自动创建
DB_PATH = "bank.db"


def get_conn():
    """打开一个数据库连接。

    row_factory 设为 sqlite3.Row，
    查询结果即可按列名取值，如 row["username"]。
    调用方统一用 with closing(...) 包裹，
    保证用完即关闭连接，不占用文件句柄。
    """
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    """建表并预置演示数据。

    幂等设计：
    - 建表用 IF NOT EXISTS，表已存在不报错；
    - 插数据前先查行数，表非空则跳过。
    """
    with closing(get_conn()) as conn:
        # users：登录账号表
        # role 字段区分管理员(admin)与普通用户(user)
        # accounts：银行账户表
        # user_id 关联 users.id；
        # 注销为软删除：只改 status，不删记录
        conn.executescript(
            """
            -- 用户表：role 区分管理员(admin)与普通用户(user)
            CREATE TABLE IF NOT EXISTS users (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                username   TEXT UNIQUE NOT NULL,
                password   TEXT NOT NULL,
                role       TEXT NOT NULL DEFAULT 'user',
                created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
            );
            -- 账户表：user_id 关联 users；注销为软删除（只改 status）
            CREATE TABLE IF NOT EXISTS accounts (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id    INTEGER NOT NULL REFERENCES users(id),
                account_no TEXT UNIQUE NOT NULL,
                balance    REAL NOT NULL DEFAULT 0,
                status     TEXT NOT NULL DEFAULT '正常',
                created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
            );
            """
        )
        # 表为空才插入预置数据（幂等）：
        # 保证重复启动不会重复插入演示账号
        if conn.execute("SELECT COUNT(*) FROM users").fetchone()[0] == 0:
            # 预置 1 个管理员 + 3 个普通用户，密码统一 123456
            conn.executemany(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                [
                    ("admin", "123456", "admin"),
                    ("zhangsan", "123456", "user"),
                    ("lisi", "123456", "user"),
                    ("wangwu", "123456", "user"),
                ],
            )
        # 预置 3 个账户；
        # wangwu 的账户为「注销」状态，便于演示
        if conn.execute("SELECT COUNT(*) FROM accounts").fetchone()[0] == 0:
            conn.executemany(
                "INSERT INTO accounts (user_id, account_no, balance, status) VALUES (?, ?, ?, ?)",
                [
                    (2, "622202000001", 8500.00, "正常"),
                    (3, "622202000002", 12300.50, "正常"),
                    (4, "622202000003", 66.66, "注销"),
                ],
            )
        conn.commit()


# ---------------- 请求体模型 ----------------
# Pydantic 模型负责请求体的类型校验：
# 字段缺失或类型不符时，自动返回 422 错误；
# Optional 字段缺省表示「该项不修改」（部分更新）

class RegisterBody(BaseModel):
    """注册请求体：用户名 + 密码"""
    username: str
    password: str


class LoginBody(BaseModel):
    """登录请求体：用户名 + 密码"""
    username: str
    password: str


class UserUpdateBody(BaseModel):
    """编辑用户请求体：未传的字段不修改"""
    username: Optional[str] = None
    password: Optional[str] = None


class AccountUpdateBody(BaseModel):
    """编辑账户请求体：未传的字段不修改"""
    balance: Optional[float] = None
    status: Optional[str] = None


# 创建 FastAPI 应用实例
app = FastAPI(title="Bank Server")
# 模块导入时即建表、预置数据，保证接口可用
init_db()


# ---------------- 注册 / 登录 ----------------

@app.post("/api/register")
def register(body: RegisterBody):
    """用户注册（必做题）。

    流程：
    1. 按用户名查重，重复返回 400；
    2. 写入 users 表，role 固定为普通用户。
    """
    with closing(get_conn()) as conn:
        # 先查重，避免触发唯一索引异常变成 500
        if conn.execute("SELECT 1 FROM users WHERE username = ?", (body.username,)).fetchone():
            raise HTTPException(400, "用户名已存在")
        # 通过注册入口创建的账号一律是普通用户
        conn.execute(
            "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')",
            (body.username, body.password),
        )
        conn.commit()
    return {"message": "注册成功"}


@app.post("/api/login")
def login(body: LoginBody):
    """用户登录。

    - 校验用户名 + 密码
    - 成功返回 { id, username, role }
      供前端按角色分流到不同主页
    """
    with closing(get_conn()) as conn:
        # 按用户名 + 密码精确匹配
        row = conn.execute(
            "SELECT id, username, role FROM users WHERE username = ? AND password = ?",
            (body.username, body.password),
        ).fetchone()
    if not row:
        raise HTTPException(400, "用户名或密码错误")
    return {"id": row["id"], "username": row["username"], "role": row["role"]}


# ---------------- 用户管理（2.1） ----------------

@app.get("/api/users")
def list_users():
    """用户列表（选做 2.1）。

    返回全部用户的 ID / 用户名 / 角色 / 注册时间
    """
    with closing(get_conn()) as conn:
        rows = conn.execute(
            "SELECT id, username, role, created_at FROM users ORDER BY id"
        ).fetchall()
    # sqlite3.Row 转 dict，由 FastAPI 自动序列化为 JSON
    return [dict(r) for r in rows]


@app.put("/api/users/{user_id}")
def update_user(user_id: int, body: UserUpdateBody):
    """编辑用户（选做 2.1）。

    只更新请求中传入且非空的字段（用户名 / 密码）
    """
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM users WHERE id = ?", (user_id,)).fetchone():
            raise HTTPException(404, "用户不存在")
        # Optional 字段为空时跳过对应字段，实现「部分更新」
        if body.username:
            conn.execute("UPDATE users SET username = ? WHERE id = ?", (body.username, user_id))
        if body.password:
            conn.execute("UPDATE users SET password = ? WHERE id = ?", (body.password, user_id))
        conn.commit()
    return {"message": "修改成功"}


@app.delete("/api/users/{user_id}")
def delete_user(user_id: int):
    """删除用户（选做 2.1）。

    - 内置管理员不可删除
    - 用户名下账户一并级联删除
    """
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM users WHERE id = ?", (user_id,)).fetchone():
            raise HTTPException(404, "用户不存在")
        # 保护内置管理员账号
        # 防止误删后无法进入管理端
        if conn.execute(
            "SELECT 1 FROM users WHERE id = ? AND username = 'admin'", (user_id,)
        ).fetchone():
            raise HTTPException(400, "不允许删除管理员")
        conn.execute("DELETE FROM users WHERE id = ?", (user_id,))
        # 级联删除：该用户名下的账户记录一并清除
        conn.execute("DELETE FROM accounts WHERE user_id = ?", (user_id,))
        conn.commit()
    return {"message": "删除成功"}


# ---------------- 账户管理（2.2） ----------------

@app.get("/api/accounts")
def list_accounts():
    """账户列表（选做 2.2）。

    JOIN users 联查，带出所属用户名。
    """
    with closing(get_conn()) as conn:
        # a 为账户表别名，u 为用户表别名
        rows = conn.execute(
            """
            SELECT a.id, a.user_id, u.username, a.account_no, a.balance, a.status, a.created_at
            FROM accounts a JOIN users u ON a.user_id = u.id
            ORDER BY a.id
            """
        ).fetchall()
    return [dict(r) for r in rows]


@app.put("/api/accounts/{account_id}")
def update_account(account_id: int, body: AccountUpdateBody):
    """编辑账户（选做 2.2）。

    只更新传入的字段（余额 / 状态）。
    """
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM accounts WHERE id = ?", (account_id,)).fetchone():
            raise HTTPException(404, "账户不存在")
        # 余额用 is not None 判断：允许改成 0
        # 状态用非空判断：空串视为不修改
        if body.balance is not None:
            conn.execute(
                "UPDATE accounts SET balance = ? WHERE id = ?", (body.balance, account_id)
            )
        if body.status:
            conn.execute(
                "UPDATE accounts SET status = ? WHERE id = ?", (body.status, account_id)
            )
        conn.commit()
    return {"message": "修改成功"}


@app.delete("/api/accounts/{account_id}")
def close_account(account_id: int):
    """注销账户（选做 2.2）。

    软删除：仅把 status 置为「注销」，
    保留记录，改回「正常」即可恢复。
    """
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM accounts WHERE id = ?", (account_id,)).fetchone():
            raise HTTPException(404, "账户不存在")
        conn.execute("UPDATE accounts SET status = '注销' WHERE id = ?", (account_id,))
        conn.commit()
    return {"message": "注销成功"}
