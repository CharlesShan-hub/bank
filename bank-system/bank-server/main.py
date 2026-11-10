"""银行管理系统后端：FastAPI + SQLite（单文件实现，无 ORM）

启动：python -m uvicorn main:app --reload --port 8000
"""
import sqlite3
from contextlib import closing

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

DB_PATH = "bank.db"


def get_conn():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    """建表并预置演示数据（幂等：已有数据则跳过）"""
    with closing(get_conn()) as conn:
        conn.executescript(
            """
            CREATE TABLE IF NOT EXISTS users (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                username   TEXT UNIQUE NOT NULL,
                password   TEXT NOT NULL,
                role       TEXT NOT NULL DEFAULT 'user',
                created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
            );
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
        if conn.execute("SELECT COUNT(*) FROM users").fetchone()[0] == 0:
            conn.executemany(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                [
                    ("admin", "123456", "admin"),
                    ("zhangsan", "123456", "user"),
                    ("lisi", "123456", "user"),
                    ("wangwu", "123456", "user"),
                ],
            )
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

class RegisterBody(BaseModel):
    username: str
    password: str


class LoginBody(BaseModel):
    username: str
    password: str


class UserUpdateBody(BaseModel):
    username: str | None = None
    password: str | None = None


class AccountUpdateBody(BaseModel):
    balance: float | None = None
    status: str | None = None


app = FastAPI(title="Bank Server")
init_db()


# ---------------- 注册 / 登录 ----------------

@app.post("/api/register")
def register(body: RegisterBody):
    with closing(get_conn()) as conn:
        if conn.execute("SELECT 1 FROM users WHERE username = ?", (body.username,)).fetchone():
            raise HTTPException(400, "用户名已存在")
        conn.execute(
            "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')",
            (body.username, body.password),
        )
        conn.commit()
    return {"message": "注册成功"}


@app.post("/api/login")
def login(body: LoginBody):
    with closing(get_conn()) as conn:
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
    with closing(get_conn()) as conn:
        rows = conn.execute(
            "SELECT id, username, role, created_at FROM users ORDER BY id"
        ).fetchall()
    return [dict(r) for r in rows]


@app.put("/api/users/{user_id}")
def update_user(user_id: int, body: UserUpdateBody):
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM users WHERE id = ?", (user_id,)).fetchone():
            raise HTTPException(404, "用户不存在")
        if body.username:
            conn.execute("UPDATE users SET username = ? WHERE id = ?", (body.username, user_id))
        if body.password:
            conn.execute("UPDATE users SET password = ? WHERE id = ?", (body.password, user_id))
        conn.commit()
    return {"message": "修改成功"}


@app.delete("/api/users/{user_id}")
def delete_user(user_id: int):
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM users WHERE id = ?", (user_id,)).fetchone():
            raise HTTPException(404, "用户不存在")
        if conn.execute(
            "SELECT 1 FROM users WHERE id = ? AND username = 'admin'", (user_id,)
        ).fetchone():
            raise HTTPException(400, "不允许删除管理员")
        conn.execute("DELETE FROM users WHERE id = ?", (user_id,))
        conn.execute("DELETE FROM accounts WHERE user_id = ?", (user_id,))
        conn.commit()
    return {"message": "删除成功"}


# ---------------- 账户管理（2.2） ----------------

@app.get("/api/accounts")
def list_accounts():
    with closing(get_conn()) as conn:
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
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM accounts WHERE id = ?", (account_id,)).fetchone():
            raise HTTPException(404, "账户不存在")
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
    """注销 = 软删除，仅把状态置为「注销」"""
    with closing(get_conn()) as conn:
        if not conn.execute("SELECT 1 FROM accounts WHERE id = ?", (account_id,)).fetchone():
            raise HTTPException(404, "账户不存在")
        conn.execute("UPDATE accounts SET status = '注销' WHERE id = ?", (account_id,))
        conn.commit()
    return {"message": "注销成功"}
