import { createPinia } from 'pinia'

export const useUserStore = createPinia({
  state: () => ({
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    petInfo: JSON.parse(localStorage.getItem('petInfo') || 'null'),
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

export const usePetStore = createPinia({
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
