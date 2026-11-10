import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例：
// baseURL 统一带 /api 前缀；
// 开发环境由 Vite 把 /api 代理到后端 6998 端口，
// 前端直接请求同源地址，无需处理跨域
const request = axios.create({ baseURL: '/api', timeout: 5000 })

// 响应拦截：直接返回 data；错误统一弹窗提示，页面里只需 catch 后什么都不做
request.interceptors.response.use(
  // 成功：剥掉 axios 外层，页面拿到的直接是后端 JSON
  (res) => res.data,
  // 失败：统一用 ElMessage 弹出错误信息
  (err) => {
    // 优先展示后端返回的 detail（如「用户名已存在」）
    const msg = err.response?.data?.detail || '网络请求失败，请检查后端是否启动'
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)

export default request
