import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 5000 })

// 响应拦截：直接返回 data；错误统一弹窗提示，页面里只需 catch 后什么都不做
request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const msg = err.response?.data?.detail || '网络请求失败，请检查后端是否启动'
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)

export default request
