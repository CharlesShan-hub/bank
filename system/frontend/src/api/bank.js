// 模拟接口层：本项目没有真实后端，这里用 Promise + 延时模拟网络请求
// 每个导出函数对应一个"接口"，业务失败抛 Error(message)，由本文件统一弹窗提示
// （角色等价于 axios 项目里的响应拦截器，视图层 catch 留空即可）
import { ElMessage } from 'element-plus'
import { db, now } from '../store/bank'

// 模拟网络延迟，让页面的 loading 动画有机会展示
const delay = (ms = 200) => new Promise((resolve) => setTimeout(resolve, ms))

// 统一调用包装：成功返回数据；失败弹窗提示后继续抛出
async function call(fn) {
  await delay()
  try {
    return await fn()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
    throw e
  }
}

// 金额保留两位小数（规避浮点误差累计）
const round2 = (n) => Math.round(n * 100) / 100

// ---------------- 必做：注册 / 登录 ----------------

/** 注册：非空校验 → 用户名查重 → 建用户 → 自动开户 */
export function register({ username, password }) {
  return call(async () => {
    if (!username?.trim() || !password?.trim()) throw new Error('用户名和密码不能为空')
    if (db.users.some((u) => u.username === username)) throw new Error('用户名已存在')
    const id = ++db.seq.user
    db.users.push({ id, username, password, role: 'user', createdAt: now() })
    // 注册自动开立账户：账号 = 6228 + 用户ID 六位补零，初始余额 0
    db.accounts.push({
      id: ++db.seq.account,
      userId: id,
      accountNo: '6228' + String(id).padStart(6, '0'),
      balance: 0,
      status: '正常',
      createdAt: now(),
    })
    return { message: '注册成功' }
  })
}

/** 登录：查不到用户与密码错误报同一句话，避免暴露「该用户名是否存在」 */
export function login({ username, password }) {
  return call(async () => {
    const user = db.users.find((u) => u.username === username)
    if (!user || user.password !== password) throw new Error('用户名或密码错误')
    return { id: user.id, username: user.username, role: user.role }
  })
}

// ---------------- 选做 2.1：用户管理 ----------------

/** 用户列表：手动挑字段返回，不把 password 带出去 */
export function listUsers() {
  return call(async () => db.users.map(({ password, ...rest }) => rest))
}

/** 编辑用户：用户名改动时查重（排除自己）；密码为空则不动 */
export function updateUser(id, { username, password }) {
  return call(async () => {
    const user = db.users.find((u) => u.id === id)
    if (!user) throw new Error('用户不存在')
    if (username?.trim()) {
      if (db.users.some((u) => u.username === username && u.id !== id)) {
        throw new Error('用户名已存在')
      }
      user.username = username
    }
    if (password?.trim()) user.password = password
    return { message: '修改成功' }
  })
}

/** 删除用户：admin 保护；级联删除名下账户，避免孤儿数据 */
export function deleteUser(id) {
  return call(async () => {
    const user = db.users.find((u) => u.id === id)
    if (!user) throw new Error('用户不存在')
    if (user.username === 'admin') throw new Error('不允许删除管理员')
    db.accounts = db.accounts.filter((a) => a.userId !== id)
    db.users = db.users.filter((u) => u.id !== id)
    return { message: '删除成功' }
  })
}

// ---------------- 选做 2.2：账户管理 ----------------

/** 账户列表：联查用户名，管理员可直观看到账户归属 */
export function listAccounts() {
  return call(async () =>
    db.accounts.map((a) => ({
      ...a,
      username: db.users.find((u) => u.id === a.userId)?.username || '-',
    }))
  )
}

/** 编辑账户：修改余额（非负）与状态 */
export function updateAccount(id, { balance, status }) {
  return call(async () => {
    const acc = db.accounts.find((a) => a.id === id)
    if (!acc) throw new Error('账户不存在')
    const val = Number(balance)
    if (Number.isNaN(val) || val < 0) throw new Error('余额不能为负数')
    acc.balance = round2(val)
    if (status) acc.status = status
    return { message: '修改成功' }
  })
}

/** 注销账户：软删除，仅把状态置为「注销」——用户仍然存在，账户信息不应物理删除 */
export function closeAccount(id) {
  return call(async () => {
    const acc = db.accounts.find((a) => a.id === id)
    if (!acc) throw new Error('账户不存在')
    acc.status = '注销'
    return { message: '注销成功' }
  })
}

// ---------------- 选做 2.3：普通用户主页 ----------------

/** 当前用户账户信息（账号 + 实时余额） */
export function getMyAccount(userId) {
  return call(async () => {
    const acc = db.accounts.find((a) => a.userId === userId)
    if (!acc) throw new Error('账户不存在')
    return { ...acc }
  })
}

/** 金额校验：非空且 > 0 */
function checkAmount(amount) {
  const val = Number(amount)
  if (!val || val <= 0) throw new Error('金额必须大于 0')
  return val
}

/** 取自己的账户并要求状态正常（已注销的账户不允许存取转） */
function requireActive(userId) {
  const acc = db.accounts.find((a) => a.userId === userId)
  if (!acc) throw new Error('账户不存在')
  if (acc.status !== '正常') throw new Error('账户已注销，无法操作')
  return acc
}

/** 存款 */
export function deposit(userId, amount) {
  return call(async () => {
    const val = checkAmount(amount)
    const acc = requireActive(userId)
    acc.balance = round2(acc.balance + val)
    return { message: '存款成功' }
  })
}

/** 取款：余额不足直接拒绝 */
export function withdraw(userId, amount) {
  return call(async () => {
    const val = checkAmount(amount)
    const acc = requireActive(userId)
    if (acc.balance < val) throw new Error('余额不足')
    acc.balance = round2(acc.balance - val)
    return { message: '取款成功' }
  })
}

/** 转账：同一数据源内一减一加同步完成，一致性天然保证 */
export function transfer(userId, { toAccountNo, amount }) {
  return call(async () => {
    const val = checkAmount(amount)
    const from = requireActive(userId)
    const to = db.accounts.find((a) => a.accountNo === toAccountNo)
    if (!to) throw new Error('目标账户不存在')
    if (to.id === from.id) throw new Error('不能转账给自己')
    if (from.balance < val) throw new Error('余额不足')
    if (to.status !== '正常') throw new Error('目标账户已注销，无法收款')
    from.balance = round2(from.balance - val)
    to.balance = round2(to.balance + val)
    return { message: '转账成功' }
  })
}
