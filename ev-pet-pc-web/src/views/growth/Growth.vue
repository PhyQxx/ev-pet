<template>
  <div class="growth-page">
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="pet-display-card card">
          <div class="pet-stage">
            <span class="stage-badge">第{{ petInfo?.stage || 1 }}阶段</span>
          </div>
          <div class="pet-avatar">
            <span class="pet-emoji">{{ petEmoji }}</span>
          </div>
          <div class="pet-info">
            <h2 class="pet-name">{{ petInfo?.name || '小可爱' }}</h2>
            <div class="pet-level">Lv.{{ petInfo?.level || 1 }}</div>
          </div>
          
          <div class="evolution-progress">
            <div class="progress-header">
              <span>进化进度</span>
              <span>{{ petInfo?.exp || 0 }} / {{ petInfo?.nextEvolutionExp || 100 }}</span>
            </div>
            <el-progress :percentage="expPercent" :stroke-width="12" color="#D5AAFF" />
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="care-actions-card card">
          <h3>养成操作</h3>
          <div class="actions-grid">
            <div class="action-item" @click="feed">
              <div class="action-icon feed">🍖</div>
              <div class="action-content">
                <span class="action-name">喂食</span>
                <span class="action-desc">恢复饱食度 +20</span>
              </div>
              <el-progress :percentage="petInfo?.fullness || 0" :stroke-width="6" :show-text="false" color="#FFD166" />
            </div>
            <div class="action-item" @click="bath">
              <div class="action-icon bath">🛁</div>
              <div class="action-content">
                <span class="action-name">洗澡</span>
                <span class="action-desc">恢复体力 +20</span>
              </div>
              <el-progress :percentage="petInfo?.health || 0" :stroke-width="6" :show-text="false" color="#FF6B6B" />
            </div>
            <div class="action-item" @click="play">
              <div class="action-icon play">🎮</div>
              <div class="action-content">
                <span class="action-name">陪玩</span>
                <span class="action-desc">恢复心情 +20</span>
              </div>
              <el-progress :percentage="petInfo?.mood || 0" :stroke-width="6" :show-text="false" color="#4ECDC4" />
            </div>
            <div class="action-item" @click="rest">
              <div class="action-icon rest">💤</div>
              <div class="action-content">
                <span class="action-name">休息</span>
                <span class="action-desc">恢复体力 +10</span>
              </div>
              <el-progress :percentage="petInfo?.health || 0" :stroke-width="6" :show-text="false" color="#4ECDC4" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="24" class="mt-6">
      <el-col :span="24">
        <div class="evolution-card card">
          <h3>进化阶段</h3>
          <div class="evolution-stages">
            <div class="stage-item" :class="{ active: (petInfo?.stage || 1) >= 1, current: petInfo?.stage === 1 }">
              <div class="stage-icon">🐣</div>
              <div class="stage-info">
                <span class="stage-name">幼年期</span>
                <span class="stage-exp">0 exp</span>
              </div>
            </div>
            <div class="stage-arrow" :class="{ active: (petInfo?.stage || 1) >= 2 }">→</div>
            <div class="stage-item" :class="{ active: (petInfo?.stage || 1) >= 2, current: petInfo?.stage === 2 }">
              <div class="stage-icon">🐥</div>
              <div class="stage-info">
                <span class="stage-name">成长期</span>
                <span class="stage-exp">100 exp</span>
              </div>
            </div>
            <div class="stage-arrow" :class="{ active: (petInfo?.stage || 1) >= 3 }">→</div>
            <div class="stage-item" :class="{ active: (petInfo?.stage || 1) >= 3, current: petInfo?.stage === 3 }">
              <div class="stage-icon">🐦</div>
              <div class="stage-info">
                <span class="stage-name">完全体</span>
                <span class="stage-exp">500 exp</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore } from '../../store'
import { pet as petApi } from '../../api'

const petStore = usePetStore()
const petInfo = computed(() => petStore.petInfo)

const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const expPercent = computed(() => {
  const exp = petInfo.value?.exp || 0
  const max = petInfo.value?.nextEvolutionExp || 100
  return Math.min(100, (exp / max) * 100)
})

onMounted(async () => {
  try {
    const data = await petApi.getInfo()
    petStore.$patch({ petInfo: data })
  } catch (err) {
    console.error(err)
  }
})

const updatePet = async (action) => {
  try {
    const data = await action()
    petStore.$patch({ petInfo: data })
    ElMessage.success('操作成功')
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const feed = () => updatePet(petApi.feed)
const bath = () => updatePet(petApi.bath)
const play = () => updatePet(petApi.play)
const rest = () => ElMessage.info('休息中...')
</script>

<style scoped>
.growth-page { padding: 0; }
.mt-6 { margin-top: 24px; }

.pet-display-card {
  text-align: center;
  padding: 40px;
}

.pet-stage {
  margin-bottom: 20px;
}

.stage-badge {
  background: linear-gradient(135deg, #D5AAFF 0%, #FFB3C6 100%);
  color: white;
  padding: 8px 24px;
  border-radius: 20px;
  font-size: 14px;
}

.pet-avatar {
  margin: 30px auto;
  width: 180px;
  height: 180px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-emoji {
  font-size: 100px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.pet-name {
  font-size: 32px;
  margin-bottom: 8px;
}

.pet-level {
  color: var(--text-secondary);
  margin-bottom: 30px;
}

.evolution-progress {
  text-align: left;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.care-actions-card h3 { margin-bottom: 20px; }

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.action-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.action-content {
  margin-bottom: 12px;
}

.action-name {
  font-weight: 600;
  display: block;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.evolution-card h3 { margin-bottom: 24px; }

.evolution-stages {
  display: flex;
  align-items: center;
  justify-content: center;
}

.stage-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 40px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  opacity: 0.5;
  transition: all 0.3s;
}

.stage-item.active {
  opacity: 1;
}

.stage-item.current {
  background: linear-gradient(135deg, rgba(213, 170, 255, 0.2) 0%, rgba(255, 179, 198, 0.2) 100%);
  box-shadow: var(--shadow-sm);
}

.stage-icon {
  font-size: 60px;
  margin-bottom: 12px;
}

.stage-item.current .stage-icon {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.stage-name {
  font-weight: 600;
  display: block;
  margin-bottom: 4px;
}

.stage-exp {
  font-size: 12px;
  color: var(--text-secondary);
}

.stage-arrow {
  font-size: 24px;
  color: #E0E0E0;
  margin: 0 16px;
}

.stage-arrow.active {
  color: var(--accent-purple);
}
</style>
