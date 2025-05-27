import axios from 'axios'
import { ElMessage } from 'element-plus'
import qs from 'qs'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8001/system-api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('adminToken')
    console.log(token);
    
    if (token) {
      config.headers['AdminToken'] = token
    }
    
    // 将 POST、PUT 请求的数据转换为 form-data 格式
    if (
      (config.method === 'post' || config.method === 'put') &&
      !(config.data instanceof FormData)
    ) {
      config.data = qs.stringify(config.data)
      config.headers['Content-Type'] = 'application/x-www-form-urlencoded'
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 显示错误消息
          ElMessage.error('登录已过期，请重新登录')
          // 清除token
          localStorage.removeItem('adminToken')
          // 跳转到401页面
          router.push('/401')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

export default service 