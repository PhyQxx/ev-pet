import axios from 'axios'

const BASE_URL = '/api'

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('adminToken')
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
      localStorage.removeItem('adminToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ============ 认证相关 ============
export const auth = {
  login(data) {
    return api.post('/auth/login', data)
  },
  verify() {
    return api.get('/auth/verify')
  }
}

// ============ 后台管理 API ============

// 统计概览
export const stats = {
  getOverview() {
    return api.get('/admin/stats/overview')
  },
  getDauTrend(params) {
    return api.get('/admin/stats/dau', { params })
  },
  getRevenueTrend(params) {
    return api.get('/admin/stats/revenue', { params })
  }
}

// 用户管理
export const userAdmin = {
  list(params) {
    return api.get('/admin/users', { params })
  },
  get(id) {
    return api.get(`/admin/users/${id}`)
  },
  update(id, data) {
    return api.put(`/admin/users/${id}`, data)
  },
  ban(id) {
    return api.post(`/admin/users/${id}/ban`)
  },
  unban(id) {
    return api.post(`/admin/users/${id}/unban`)
  }
}

// 商品/道具管理
export const itemAdmin = {
  list(params) {
    return api.get('/admin/items', { params })
  },
  create(data) {
    return api.post('/admin/items', data)
  },
  update(id, data) {
    return api.put(`/admin/items/${id}`, data)
  },
  delete(id) {
    return api.delete(`/admin/items/${id}`)
  },
  toggleStatus(id) {
    return api.post(`/admin/items/${id}/toggle`)
  }
}

// 系统配置
export const systemConfig = {
  getAll() {
    return api.get('/admin/config')
  },
  save(configMap) {
    return api.post('/admin/config', configMap)
  },
  testAI() {
    return api.post('/admin/config/test-ai')
  }
}

// 公告管理
export const announcementAdmin = {
  list(params) {
    return api.get('/admin/announcements', { params })
  },
  create(data) {
    return api.post('/admin/announcements', data)
  },
  update(id, data) {
    return api.put(`/admin/announcements/${id}`, data)
  },
  delete(id) {
    return api.delete(`/admin/announcements/${id}`)
  },
  publish(id) {
    return api.post(`/admin/announcements/${id}/publish`)
  },
  withdraw(id) {
    return api.post(`/admin/announcements/${id}/withdraw`)
  }
}

// 活动管理
export const activityAdmin = {
  list(params) {
    return api.get('/admin/activities', { params })
  },
  create(data) {
    return api.post('/admin/activities', data)
  },
  update(id, data) {
    return api.put(`/admin/activities/${id}`, data)
  },
  delete(id) {
    return api.delete(`/admin/activities/${id}`)
  },
  end(id) {
    return api.post(`/admin/activities/${id}/end`)
  }
}

// 内容审核
export const contentReview = {
  list(params) {
    return api.get('/admin/reviews', { params })
  },
  approve(id) {
    return api.post(`/admin/reviews/${id}/approve`)
  },
  reject(id, reason) {
    return api.post(`/admin/reviews/${id}/reject`, { reason })
  }
}

// 管理员账号
export const adminAccount = {
  list() {
    return api.get('/admin/admins')
  },
  create(data) {
    return api.post('/admin/admins', data)
  },
  update(id, data) {
    return api.put(`/admin/admins/${id}`, data)
  },
  delete(id) {
    return api.delete(`/admin/admins/${id}`)
  }
}

// 角色权限
export const roleAdmin = {
  list() {
    return api.get('/admin/roles')
  },
  update(id, data) {
    return api.put(`/admin/roles/${id}`, data)
  }
}

export default api
