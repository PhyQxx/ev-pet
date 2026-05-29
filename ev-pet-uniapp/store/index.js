import { reactive } from 'vue'
import { pet as petApi, user as userApi, getUserInfo as getStoredUserInfo } from '@/utils/api.js'

const STAGE_NAMES = { 1: '幼年期', 2: '成长期', 3: '完全体' }

export const store = reactive({
  petInfo: null,
  userInfo: null,
  loading: false,

  get stageName() {
    const stage = this.petInfo?.stage || 1
    return STAGE_NAMES[stage] || '幼年期'
  },

  get petEmoji() {
    const stage = this.petInfo?.stage || 1
    return { 1: '🐣', 2: '🐥', 3: '🐦' }[stage] || '🐣'
  },

  get health() {
    return this.petInfo?.health ?? 100
  },

  get fullness() {
    return this.petInfo?.fullness ?? 100
  },

  get mood() {
    return this.petInfo?.mood ?? 100
  },

  get gold() {
    return this.petInfo?.gold ?? this.userInfo?.gold ?? 0
  },

  async loadPetInfo() {
    try {
      const data = await petApi.getInfo()
      this.petInfo = data
      return data
    } catch (e) {
      return null
    }
  },

  async loadUserInfo() {
    try {
      const data = await userApi.getInfo()
      this.userInfo = data
      return data
    } catch (e) {
      this.userInfo = getStoredUserInfo()
      return this.userInfo
    }
  },

  async init() {
    if (this.loading) return
    this.loading = true
    await Promise.all([this.loadPetInfo(), this.loadUserInfo()])
    this.loading = false
  }
})
