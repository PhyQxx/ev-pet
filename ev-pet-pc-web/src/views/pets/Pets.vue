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
import { ref, computed } from 'vue'

const pets = ref([
  { id: 1, name: '小福', icon: '🦊', level: 12, state: 'normal', stateText: '正常', mood: 85, hunger: 72, mutation: false, value: 2800 },
  { id: 2, name: '小白', icon: '🐰', level: 8, state: 'normal', stateText: '正常', mood: 60, hunger: 45, mutation: false, value: 1500 },
  { id: 3, name: '雷神', icon: '⚡', level: 15, state: 'mutation', stateText: '变异', mood: 95, hunger: 88, mutation: true, value: 8800 },
])

const mutationCount = computed(() => pets.value.filter(p => p.mutation).length)
const totalValue = computed(() => pets.value.reduce((s, p) => s + p.value, 0).toLocaleString())
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
.pet-state.mutation { background: #FFE5A0; color: #7d4e00; }
.pet-mood, .pet-hunger { font-size: 12px; color: var(--text-secondary, #7A6B8A); }
</style>
