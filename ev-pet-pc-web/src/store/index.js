import { defineStore } from 'pinia'

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
    },
    async fetchPetInfo() {
      const { pet } = await import('../api').then(m => m.pet.getInfo())
      this.updatePet(pet)
      return pet
    },
    async fetchUserProfile() {
      const profile = await import('../api').then(m => m.user.getProfile())
      this.updateUser(profile)
      return profile
    },
    async feed() {
      const { pet } = await import('../api').then(m => m.pet.feed())
      this.updatePet(pet)
      return pet
    },
    async bath() {
      const { pet } = await import('../api').then(m => m.pet.bath())
      this.updatePet(pet)
      return pet
    },
    async play() {
      const { pet } = await import('../api').then(m => m.pet.play())
      this.updatePet(pet)
      return pet
    }
  }
})

// Backward compatibility — usePetStore is deprecated, use useUserStore instead
export const usePetStore = useUserStore
