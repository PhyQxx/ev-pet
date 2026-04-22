import { defineStore } from 'pinia'

// 安全解析 localStorage JSON，避免 "undefined" 字符串导致 JSON.parse 报错
const safeParse = (str, fallback) => {
  if (!str || str === 'undefined' || str === 'null') return fallback
  try { return JSON.parse(str) } catch { return fallback }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: safeParse(localStorage.getItem('userInfo'), null),
    petInfo: safeParse(localStorage.getItem('petInfo'), null),
    token: localStorage.getItem('token') || ''
  }),
  actions: {
    setUser(user, pet, token) {
      this.userInfo = user
      this.petInfo = pet
      this.token = token
      localStorage.setItem('userInfo', JSON.stringify(user))
      localStorage.setItem('petInfo', JSON.stringify(pet))
      localStorage.setItem('token', token)
    },
    logout() {
      this.userInfo = null
      this.petInfo = null
      this.token = ''
      localStorage.clear()
    },
    updatePet(pet) {
      this.petInfo = pet
      localStorage.setItem('petInfo', JSON.stringify(pet))
    },
    updateUser(user) {
      this.userInfo = user
      localStorage.setItem('userInfo', JSON.stringify(user))
    }
  }
})

export const usePetStore = defineStore('pet', {
  state: () => ({
    petInfo: null,
    messages: []
  }),
  actions: {
    async fetchPetInfo() {
      const { pet } = await import('../api').then(m => m.pet.getInfo())
      this.petInfo = pet
      return pet
    },
    async feed() {
      const { pet } = await import('../api').then(m => m.pet.feed())
      this.petInfo = pet
      return pet
    },
    async bath() {
      const { pet } = await import('../api').then(m => m.pet.bath())
      this.petInfo = pet
      return pet
    },
    async play() {
      const { pet } = await import('../api').then(m => m.pet.play())
      this.petInfo = pet
      return pet
    }
  }
})
