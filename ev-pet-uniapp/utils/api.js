const BASE_URL = 'http://localhost:19081/api'

// 保存token
export function setToken(token) {
  uni.setStorageSync('token', token)
}

export function getToken() {
  return uni.getStorageSync('token')
}

export function removeToken() {
  uni.removeStorageSync('token')
}

// 保存用户信息
export function setUserInfo(userInfo) {
  uni.setStorageSync('userInfo', userInfo)
}

export function getUserInfo() {
  return uni.getStorageSync('userInfo')
}

// API请求封装
function request(url, method, data, needAuth = true) {
  return new Promise((resolve, reject) => {
    const header = { 'Content-Type': 'application/json' }
    
    if (needAuth && getToken()) {
      header['Authorization'] = 'Bearer ' + getToken()
    }
    
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else {
          uni.showToast({ title: res.data.message || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// 认证相关
export const auth = {
  login(data) { return request('/auth/login', 'POST', data, false) },
  register(data) { return request('/auth/register', 'POST', data, false) },
  verify() { return request('/auth/verify', 'GET', {}) }
}

// 宠物相关
export const pet = {
  getInfo() { return request('/pet/info', 'GET', {}) },
  feed() { return request('/pet/feed', 'POST', {}) },
  bath() { return request('/pet/bath', 'POST', {}) },
  play() { return request('/pet/play', 'POST', {}) },
  rest() { return request('/pet/rest', 'POST', {}) },
  updateName(name) { return request('/pet/name', 'PUT', { name }) }
}

// AI对话
export const chat = {
  send(content) { return request('/chat/send', 'POST', { content }) },
  getHistory() { return request('/chat/history', 'GET', {}) }
}

// 商店
export const shop = {
  getItems(category) { return request('/shop/items', 'GET', { category }) },
  getFreeItems() { return request('/shop/free', 'GET', {}) },
  getBackpack() { return request('/shop/backpack', 'GET', {}) },
  buy(itemId, quantity) { return request('/shop/buy', 'POST', { itemId, quantity }) },
  equip(itemId) { return request('/shop/equip', 'POST', { itemId }) },
  use(itemId) { return request('/shop/use', 'POST', { itemId }) }
}

// 打工
export const work = {
  getInfo() { return request('/work/info', 'GET', {}) },
  start(workId) { return request('/work/start', 'POST', { workId }) },
  claim(recordId) { return request('/work/claim', 'POST', { recordId }) }
}

// 社交
export const social = {
  getFriends() { return request('/social/friends', 'GET', {}) },
  getPosts(filter) { return request('/social/posts', 'GET', { filter }) },
  getRankings(type) { return request('/social/rankings', 'GET', { type }) },
  addFriend(friendId) { return request('/social/friend/add', 'POST', { friendId }) },
  acceptFriend(requestId) { return request('/social/friend/accept', 'POST', { requestId }) },
  rejectFriend(requestId) { return request('/social/friend/reject', 'POST', { requestId }) },
  publishPost(content) { return request('/social/post/publish', 'POST', { content }) },
  likePost(postId) { return request('/social/post/like', 'POST', { postId }) }
}

// 成就
export const achievement = {
  getList() { return request('/achievement/list', 'GET', {}) },
  claimReward(achievementId) { return request('/achievement/claim', 'POST', { achievementId }) }
}

// 用户
export const user = {
  getInfo() { return request('/user/info', 'GET', {}) },
  updateInfo(data) { return request('/user/info', 'PUT', data) },
  getSettings() { return request('/user/settings', 'GET', {}) },
  updateSettings(data) { return request('/user/settings', 'PUT', data) }
}
