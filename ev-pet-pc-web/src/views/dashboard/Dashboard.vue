<template>
  <div class="dashboard">
    <el-row :gutter="24">
      <!-- 左侧：宠物展示 -->
      <el-col :span="8">
        <div class="pet-card card">
          <div class="pet-header">
            <div class="pet-avatar">
              <span class="pet-emoji" :class="petStatus">{{ petEmoji }}</span>
            </div>
            <div class="pet-info">
              <h3 class="pet-name">{{ petInfo?.name || '小可爱' }}</h3>
              <div class="pet-tags">
                <el-tag size="small" type="warning">Lv.{{ petInfo?.level || 1 }}</el-tag>
                <el-tag size="small" type="success">第{{ petInfo?.stage || 1 }}阶段</el-tag>
              </div>
            </div>
          </div>
          
          <div class="pet-status-badge" :class="petInfo?.status">
            <span>{{ statusText }}</span>
          </div>
          
          <div class="pet-attributes">
            <div class="attr-item">
              <div class="attr-header">
                <span class="attr-icon">❤️</span>
                <span class="attr-name">体力</span>
                <span class="attr-value">{{ petInfo?.health || 0 }}%</span>
              </div>
              <el-progress :percentage="petInfo?.health || 0" :stroke-width="8" :show-text="false" color="#FF6B6B" />
            </div>
            <div class="attr-item">
              <div class="attr-header">
                <span class="attr-icon">🍖</span>
                <span class="attr-name">饱食</span>
                <span class="attr-value">{{ petInfo?.fullness || 0 }}%</span>
              </div>
              <el-progress :percentage="petInfo?.fullness || 0" :stroke-width="8" :show-text="false" color="#FFD166" />
            </div>
            <div class="attr-item">
              <div class="attr-header">
                <span class="attr-icon">😊</span>
                <span class="attr-name">心情</span>
                <span class="attr-value">{{ petInfo?.mood || 0 }}%</span>
              </div>
              <el-progress :percentage="petInfo?.mood || 0" :stroke-width="8" :show-text="false" color="#4ECDC4" />
            </div>
            <div class="attr-item">
              <div class="attr-header">
                <span class="attr-icon">⭐</span>
                <span class="attr-name">经验</span>
                <span class="attr-value">{{ petInfo?.exp || 0 }}/{{ petInfo?.nextEvolutionExp || 100 }}</span>
              </div>
              <el-progress :percentage="expPercent" :stroke-width="8" :show-text="false" color="#D5AAFF" />
            </div>
          </div>
          
          <div class="pet-actions">
            <el-button type="primary" @click="feed" :loading="loading">
              <span>🍖</span> 喂食
            </el-button>
            <el-button type="success" @click="bath" :loading="loading">
              <span>🛁</span> 洗澡
            </el-button>
            <el-button type="warning" @click="play" :loading="loading">
              <span>🎮</span> 陪玩
            </el-button>
          </div>
        </div>
      </el-col>
      
      <!-- 中间：快捷操作 + 今日任务 -->
      <el-col :span="10">
        <div class="quick-actions card">
          <h3 class="section-title">快捷操作</h3>
          <div class="action-grid">
            <div class="action-item" @click="$router.push('/chat')">
              <div class="action-icon chat">💬</div>
              <span class="action-text">AI对话</span>
            </div>
            <div class="action-item" @click="$router.push('/growth')">
              <div class="action-icon growth">📈</div>
              <span class="action-text">养成</span>
            </div>
            <div class="action-item" @click="$router.push('/studio')">
              <div class="action-icon studio">👗</div>
              <span class="action-text">装扮</span>
            </div>
            <div class="action-item" @click="$router.push('/shop')">
              <div class="action-icon shop">🛒</div>
              <span class="action-text">商店</span>
            </div>
          </div>
        </div>
        
        <div class="tasks-card card">
          <h3 class="section-title">今日任务</h3>
          <div class="task-list">
            <div class="task-item" v-for="task in dailyTasks" :key="task.id">
              <div class="task-icon">{{ task.icon }}</div>
              <div class="task-info">
                <span class="task-name">{{ task.name }}</span>
                <span class="task-progress">{{ task.current }}/{{ task.target }}</span>
              </div>
              <el-button size="small" type="primary" plain>{{ task.btnText }}</el-button>
            </div>
          </div>
        </div>
      </el-col>
      
      <!-- 右侧：统计 -->
      <el-col :span="6">
        <div class="stats-card card">
          <h3 class="section-title">我的数据</h3>
          <div class="stat-list">
            <div class="stat-item">
              <div class="stat-icon">🪙</div>
              <div class="stat-info">
                <span class="stat-value">{{ userInfo?.gold || 0 }}</span>
                <span class="stat-label">金币</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">🏆</div>
              <div class="stat-info">
                <span class="stat-value">{{ userInfo?.level || 1 }}</span>
                <span class="stat-label">等级</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">⭐</div>
              <div class="stat-info">
                <span class="stat-value">{{ userInfo?.exp || 0 }}</span>
                <span class="stat-label">经验值</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="evolution-card card">
          <h3 class="section-title">进化进度</h3>
          <div class="evolution-stages">
            <div class="stage" :class="{ active: (petInfo?.stage || 1) >= 1, current: petInfo?.stage === 1 }">
              <span class="stage-emoji">🐣</span>
              <span class="stage-name">幼年期</span>
            </div>
            <div class="stage-line" :class="{ active: (petInfo?.stage || 1) >= 2 }"></div>
            <div class="stage" :class="{ active: (petInfo?.stage || 1) >= 2, current: petInfo?.stage === 2 }">
              <span class="stage-emoji">🐥</span>
              <span class="stage-name">成长期</span>
            </div>
            <div class="stage-line" :class="{ active: (petInfo?.stage || 1) >= 3 }"></div>
            <div class="stage" :class="{ active: (petInfo?.stage || 1) >= 3, current: petInfo?.stage === 3 }">
              <span class="stage-emoji">🐦</span>
              <span class="stage-name">完全体</span>
            </div>
          </div>
          <p class="evolution-tip">再获得 {{ remainingExp }} 经验即可进化</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore, usePetStore } from '../../store'
import { pet as petApi } from '../../api'

const userStore = useUserStore()
const petStore = usePetStore()

const loading = ref(false)
const petInfo = computed(() => petStore.petInfo)
const userInfo = computed(() => userStore.userInfo)

const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const petStatus = computed(() => petInfo.value?.status || 'happy')

const statusText = computed(() => {
  const status = petInfo.value?.status
  if (status === 'happy') return '✨ 心情很好~'
  if (status === 'hungry') return '🍖 肚子饿了'
  if (status === 'tired') return '😴 有点累'
  if (status === 'angry') return '😠 不开心'
  return '😊 开心'
})

const expPercent = computed(() => {
  const exp = petInfo.value?.exp || 0
  const max = petInfo.value?.nextEvolutionExp || 100
  return Math.min(100, (exp / max) * 100)
})

const remainingExp = computed(() => {
  return Math.max(0, (petInfo.value?.nextEvolutionExp || 100) - (petInfo.value?.exp || 0))
})

const dailyTasks = ref([
  { id: 1, icon: '🍖', name: '喂食宠物', current: 0, target: 3, btnText: '去完成' },
  { id: 2, icon: '💬', name: '和宠物对话', current: 0, target: 5, btnText: '去完成' },
  { id: 3, icon: '🎮', name: '陪宠物玩耍', current: 0, target: 2, btnText: '去完成' }
])

onMounted(async () => {
  try {
    const data = await petApi.getInfo()
    petStore.$patch({ petInfo: data })
  } catch (err) {
    console.error('Failed to load pet info:', err)
  }
})

const feed = async () => {
  loading.value = true
  try {
    const data = await petApi.feed()
    petStore.$patch({ petInfo: data })
    ElMessage.success('喂食成功~')
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

const bath = async () => {
  loading.value = true
  try {
    const data = await petApi.bath()
    petStore.$patch({ petInfo: data })
    ElMessage.success('洗香香~')
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

const play = async () => {
  loading.value = true
  try {
    const data = await petApi.play()
    petStore.$patch({ petInfo: data })
    ElMessage.success('好开心~')
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.card {
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary);
}

/* 宠物卡片 */
.pet-card {
  padding: 24px;
}

.pet-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.pet-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-emoji {
  font-size: 60px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.pet-info h3 {
  font-size: 24px;
  margin-bottom: 8px;
}

.pet-tags {
  display: flex;
  gap: 8px;
}

.pet-status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 20px;
}

.pet-status-badge.happy { background: #E8FFF0; color: #4A3F55; }
.pet-status-badge.hungry { background: #FFF0EF; color: #FF6B6B; }
.pet-status-badge.tired { background: #EEF6FF; color: #4A3F55; }
.pet-status-badge.angry { background: #FFF0F0; color: #FF6B6B; }

.pet-attributes {
  margin-bottom: 20px;
}

.attr-item {
  margin-bottom: 16px;
}

.attr-header {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.attr-icon {
  font-size: 18px;
  width: 30px;
}

.attr-name {
  font-size: 14px;
  color: var(--text-secondary);
  width: 50px;
}

.attr-value {
  font-size: 14px;
  font-weight: 600;
  margin-left: auto;
}

.pet-actions {
  display: flex;
  gap: 12px;
}

.pet-actions .el-button span {
  margin-right: 4px;
}

/* 快捷操作 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-sm);
}

.action-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin-bottom: 10px;
}

.action-icon.chat { background: linear-gradient(135deg, #D5AAFF 0%, #C48FFF 100%); }
.action-icon.growth { background: linear-gradient(135deg, #FFB3C6 0%, #FF8FAB 100%); }
.action-icon.studio { background: linear-gradient(135deg, #A8D8EA 0%, #7EC8D8 100%); }
.action-icon.shop { background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%); }

.action-text {
  font-size: 14px;
  color: var(--text-primary);
}

/* 任务列表 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.task-icon {
  font-size: 24px;
  width: 40px;
}

.task-info {
  flex: 1;
  margin-left: 12px;
}

.task-name {
  font-size: 14px;
  color: var(--text-primary);
  display: block;
}

.task-progress {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 统计数据 */
.stat-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.stat-icon {
  font-size: 32px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  display: block;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 进化进度 */
.evolution-stages {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  opacity: 0.4;
}

.stage.active {
  opacity: 1;
}

.stage.current .stage-emoji {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

.stage-emoji {
  font-size: 40px;
}

.stage-name {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.stage-line {
  width: 60px;
  height: 6px;
  background: #E0E0E0;
  border-radius: 3px;
  margin: 0 8px;
}

.stage-line.active {
  background: linear-gradient(90deg, #D5AAFF 0%, #C48FFF 100%);
}

.evolution-tip {
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
