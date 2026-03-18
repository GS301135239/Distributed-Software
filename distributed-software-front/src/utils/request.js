import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance
const service = axios.create({
  baseURL: 'http://localhost:8087', // Replace with your actual backend URL
  timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // You can add token here if needed
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // You may want to check custom code here
    return res
  },
  error => {
    console.log('err' + error)
    ElMessage({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service

