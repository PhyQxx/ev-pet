import axios from 'axios'

const BASE_URL = '/api'

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 认证
export const auth = {
  login(data) {
    return api.post('/auth/login', data)
  },
  verify() {
    return api.get('/auth/verify')
  }
}

// 宠物
export const pet = {
  getInfo() { return api.get('/pet/info') },
  feed() { return api.post('/pet/feed') },
  bath() { return api.post('/pet/bath') },
  play() { return api.post('/pet/play') },
  updateName(name) { return api.put('/pet/name', { name }) }
}

// AI对话
export const chat = {
  send(content) { return api.post('/chat/send', { content }) },
  getHistory() { return api.get('/chat/history') }
}

// 商店
export const shop = {
  getItems(category) { return api.get('/shop/items', { params: { category } }) },
  getFreeItems() { return api.get('/shop/free') },
  getBackpack() { return api.get('/shop/backpack') },
  buy(itemId, quantity = 1) { return api.post('/shop/buy', { itemId, quantity }) },
  equip(itemId) { return api.post('/shop/equip', { itemId }) },
  use(itemId) { return api.post('/shop/use', { itemId }) }
}

export default api
