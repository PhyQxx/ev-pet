<template>
  <div class="pets-page">
    <div class="page-header">
      <h2>🐾 我的宠物</h2>
      <p class="page-sub">管理和查看您的宠物</p>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon">🐱</div>
        <div class="stat-info">
          <div class="stat-num">{{ pets.length }}</div>
          <div class="stat-label">我的宠物</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <div class="stat-num">{{ mutationCount }}</div>
          <div class="stat-label">变异宠物</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-num">{{ totalValue }}</div>
          <div class="stat-label">宠物总价值</div>
        </div>
      </div>
    </div>

    <div class="pets-grid">
      <div v-for="pet in pets" :key="pet.id" class="pet-card" :class="{ mutation: pet.mutation }">
        <div class="pet-avatar">{{ pet.icon }}</div>
        <div class="pet-info">
          <h4>{{ pet.name }}</h4>
          <div class="pet-status">
            <span class="pet-level">Lv.{{ pet.level }}</span>
            <span class="pet-state" :class="pet.state">{{ pet.stateText }}</span>
          </div>
          <div class="pet-mood">心情: {{ pet.mood }}/100</div>
          <div class="pet-hunger">饥饿: {{ pet.hunger }}/100</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../store'
import { pet as petApi } from '../../api'

const userStore = useUserStore()
const loading = ref(true)

const petData = ref(null)

const stageEmojis = { 1: '🥚', 2: '🐱', 3: '😺', 4: '👑' }

const petIcon = computed(() => stageEmojis[petData.value?.stage] || '🐱')
const petStateText = computed(() => {
  if (!petData.value) return '加载中'
  const m = petData.value.mood || 0
  if (m >= 80) return '开心'
  if (m >= 50) return '正常'
  if (m >= 20) return '一般'
  return '不开心'
})
const petStateClass = computed(() => {
  const text = petStateText.value
  if (text === '开心') return 'happy'
  if (text === '正常') return 'normal'
  return 'low'
})

const pets = computed(() => petData.value ? [{
  id: petData.value.id,
  name: petData.value.name || '小可爱',
  icon: petIcon.value,
  level: petData.value.level || 1,
  state: petStateClass.value,
  stateText: petStateText.value,
  mood: petData.value.mood || 0,
  hunger: petData.value.fullness || 0,
  mutation: false,
  value: (petData.value.level || 1) * 500
}] : [])

const mutationCount = computed(() => pets.value.filter(p => p.mutation).length)
const totalValue = computed(() => pets.value.reduce((s, p) => s + p.value, 0).toLocaleString())

onMounted(async () => {
  try {
    const data = await petApi.getInfo()
    petData.value = data
    userStore.updatePet(data)
  } catch (err) {
    console.error('Failed to load pet info:', err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.pets-page { padding: 0; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 20px; font-weight: 600; margin: 0 0 4px 0; }
.page-sub { font-size: 13px; color: var(--text-secondary, #7A6B8A); margin: 0; }
.stats-row { display: flex; gap: 16px; margin-bottom: 24px; }
.stat-card { flex: 1; background: #FAF8FF; border-radius: 16px; padding: 16px 20px; display: flex; align-items: center; gap: 14px; border: 1px solid #F0E8F8; }
.stat-icon { font-size: 32px; }
.stat-num { font-size: 22px; font-weight: 700; }
.stat-label { font-size: 12px; color: var(--text-secondary, #7A6B8A); }
.pets-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.pet-card { background: #fff; border-radius: 16px; padding: 20px; border: 1px solid #F0E8F8; display: flex; flex-direction: column; align-items: center; gap: 12px; transition: all 0.2s; }
.pet-card:hover { box-shadow: 0 4px 16px rgba(180,150,200,0.12); }
.pet-card.mutation { background: linear-gradient(135deg, #FFF9E6, #FFF0F5); border-color: #FFE5A0; }
.pet-avatar { font-size: 48px; width: 80px; height: 80px; background: #F5F0FF; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.pet-info { width: 100%; text-align: center; }
.pet-info h4 { font-size: 16px; font-weight: 600; margin: 0 0 8px 0; }
.pet-status { display: flex; gap: 8px; justify-content: center; margin-bottom: 6px; }
.pet-level { background: linear-gradient(135deg, #FFB3C6, #D5AAFF); color: #fff; font-size: 12px; padding: 2px 8px; border-radius: 10px; font-weight: 600; }
.pet-state { font-size: 12px; padding: 2px 8px; border-radius: 10px; }
.pet-state.normal { background: #B8F1CC; color: #2e7d32; }
.pet-state.happy { background: #B8F1CC; color: #2e7d32; }
.pet-state.low { background: #FFE5E5; color: #c62828; }
.pet-state.mutation { background: #FFE5A0; color: #7d4e00; }
.pet-mood, .pet-hunger { font-size: 12px; color: var(--text-secondary, #7A6B8A); }
</style>
