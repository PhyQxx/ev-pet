import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const username = ref(localStorage.getItem('adminUsername') || '')
  const role = ref(localStorage.getItem('adminRole') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setLogin(data) {
    token.value = data.token
    username.value = data.username
    role.value = data.role || 'admin'
    localStorage.setItem('adminToken', data.token)
    localStorage.setItem('adminUsername', data.username)
    localStorage.setItem('adminRole', data.role || 'admin')
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUsername')
    localStorage.removeItem('adminRole')
  }

  return { token, username, role, isLoggedIn, setLogin, logout }
})
