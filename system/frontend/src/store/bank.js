// 本地"数据库"：所有业务数据保存在浏览器 localStorage，刷新页面、重开浏览器都不丢失
// 结构：{ users: [...], accounts: [...], seq: { user, account } }
import { reactive, watch } from 'vue'

const KEY = 'bank_system_db'

// 预置演示数据：1 个管理员 + 3 个普通用户及账户，密码统一 123456
function seedDb() {
  return {
    users: [
      { id: 1, username: 'admin', password: '123456', role: 'admin', createdAt: '2026-09-01 08:30:00' },
      { id: 2, username: 'zhangsan', password: '123456', role: 'user', createdAt: '2026-09-01 09:10:00' },
      { id: 3, username: 'lisi', password: '123456', role: 'user', createdAt: '2026-09-02 10:20:00' },
      { id: 4, username: 'wangwu', password: '123456', role: 'user', createdAt: '2026-09-03 14:05:00' },
    ],
    accounts: [
      { id: 1, userId: 2, accountNo: '6228000002', balance: 8600.0, status: '正常', createdAt: '2026-09-01 09:10:00' },
      { id: 2, userId: 3, accountNo: '6228000003', balance: 4300.75, status: '正常', createdAt: '2026-09-02 10:20:00' },
      { id: 3, userId: 4, accountNo: '6228000004', balance: 666.66, status: '正常', createdAt: '2026-09-03 14:05:00' },
    ],
    seq: { user: 4, account: 3 },
  }
}

// 从 localStorage 恢复；首次使用（或数据损坏）时写入预置数据
function initialDb() {
  const raw = localStorage.getItem(KEY)
  if (raw) {
    try {
      return JSON.parse(raw)
    } catch {
      // JSON 解析失败则重建演示数据
    }
  }
  return seedDb()
}

export const db = reactive(initialDb())

// 任意字段变化自动持久化：deep 监听整个对象树，一处改动全量落盘
watch(db, () => localStorage.setItem(KEY, JSON.stringify(db)), { deep: true })

// 当前时间字符串（新注册用户 / 开户时间用）
export function now() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}
