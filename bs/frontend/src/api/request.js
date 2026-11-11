// 统一的 axios 实例：所有接口调用都走它
// baseURL 用相对路径 /api，由 Vite 代理转发到 6997 后端，天然规避跨域
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

// 统一响应处理：成功直接返回 data；失败弹窗提示（题目要求“失败弹窗显示提示信息”）
request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const msg = err.response?.data?.message || err.response?.data?.detail || '网络请求失败'
    ElMessage.error(msg)
    return Promise.reject(new Error(msg))
  }
)

export default request
