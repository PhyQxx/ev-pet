<template>
  <div class="achievement-page">
    <el-row :gutter="20">
      <!-- 左侧统计卡片 -->
      <el-col :span="6">
        <div class="stats-card card">
          <div class="stats-header">
            <span class="stats-emoji">🏆</span>
            <h3>我的成就</h3>
          </div>
          <div class="stats-body">
            <div class="big-stat">
              <span class="big-num">{{ completedCount }}</span>
              <span class="big-label">已获得</span>
            </div>
            <el-progress
              :percentage="progressPercent"
              :stroke-width="12"
              :show-text="false"
              color="#D5AAFF"
            />
            <div class="progress-info">
              <span>完成进度</span>
              <span>{{ completedCount }}/{{ totalCount }}</span>
            </div>
            <div class="reward-summary">
              <span class="reward-label">可领取奖励</span>
              <span class="reward-value">💰 {{ claimableReward }}</span>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧成就列表 -->
      <el-col :span="18">
        <div class="achievements-card card">
          <div class="card-header">
            <h3>成就中心</h3>
            <el-tabs v-model="activeTab" class="category-tabs">
              <el-tab-pane label="全部" name="all" />
              <el-tab-pane label="进行中" name="progress" />
              <el-tab-pane label="已完成" name="completed" />
            </el-tabs>
          </div>

          <div class="achievement-grid">
            <div
              v-for="item in filteredAchievements"
              :key="item.id"
              class="achievement-card"
              :class="{ completed: item.completed, locked: !item.completed && item.progress === 0 }"
            >
              <div class="achievement-icon-wrap">
                <div class="achievement-icon" :class="{ locked: !item.completed && item.progress === 0 }">
                  {{ item.icon }}
                </div>
                <div v-if="item.completed" class="completed-badge">✓</div>
              </div>
              <div class="achievement-content">
                <h4 class="achievement-name">{{ item.name }}</h4>
                <p class="achievement-desc">{{ item.description }}</p>
                <div class="achievement-progress-wrap">
                  <el-progress
                    :percentage="item.progress"
                    :stroke-width="6"
                    :show-text="false"
                    :color="item.completed ? '#B8F1CC' : '#D5AAFF'"
                  />
                  <span class="progress-text">{{ item.current }}/{{ item.target }}</span>
                </div>
              </div>
              <div class="achievement-actions">
                <div class="reward-info">
                  <span class="coin-icon">💰</span>
                  <span class="coin-num">{{ item.reward }}</span>
                </div>
                <el-button
                  v-if="item.completed && !item.claimed"
                  class="claim-btn"
                  @click="claimReward(item)"
                >
                  领取
                </el-button>
                <el-tag v-else-if="item.claimed" size="small" type="success" class="claimed-tag">
                  已领取
                </el-tag>
                <span v-else-if="!item.completed" class="ongoing-hint">进行中</span>
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
import { achievement as achievementApi } from '../../api/index.js'

const activeTab = ref('all')

const achievements = ref([])

const totalCount = computed(() => achievements.value.length)
const completedCount = computed(() => achievements.value.filter(a => a.completed).length)
const progressPercent = computed(() => totalCount.value > 0 ? Math.round((completedCount.value / totalCount.value) * 100) : 0)
const claimableReward = computed(() => {
  try {
    return achievements.value
      .filter(a => a.completed && !a.claimed)
      .reduce((sum, a) => sum + (a.reward || 0), 0)
  } catch { return 0 }
})

const filteredAchievements = computed(() => {
  if (activeTab.value === 'progress') return achievements.value.filter(a => !a.completed)
  if (activeTab.value === 'completed') return achievements.value.filter(a => a.completed)
  return achievements.value
})

const loadAchievements = async () => {
  try {
    const data = await achievementApi.getList()
    const list = Array.isArray(data) ? data : (data?.achievements || [])
    achievements.value = list.map(a => ({
      id: a.id,
      name: a.name || '',
      description: a.description || '',
      icon: a.icon || '🏆',
      completed: a.completed || false,
      claimed: a.claimed || false,
      current: a.current || 0,
      target: a.target || 1,
      reward: a.reward || 0
    }))
  } catch (e) {
    console.error('Failed to load achievements:', e)
    achievements.value = []
  }
}

const claimReward = async (item) => {
  try {
    await achievementApi.claim(item.id)
    item.claimed = true
    ElMessage.success(`领取成功！获得 ${item.reward} 金币`)
  } catch (e) {
    console.error('领取奖励失败', e)
    ElMessage.error('领取失败，请重试')
  }
}

onMounted(() => {
  loadAchievements()
})
</script>

<style scoped lang="scss">
@use '@/styles/ev-pet.scss' as *;
.achievement-page {
  padding: 0;
}

.stats-card {
  padding: 24px;
  background: linear-gradient(180deg, #FFFFFF 0%, #FAF8FF 100%);
}

.stats-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}

.stats-emoji {
  font-size: 28px;
}

.stats-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: $ev-text;
  margin: 0;
}

.stats-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.big-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.big-num {
  font-size: 56px;
  font-weight: 700;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.big-label {
  font-size: 14px;
  color: $ev-text-sec;
  margin-top: 4px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: $ev-text-sec;
}

.reward-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(135deg, rgba(255, 179, 198, 0.15), rgba(213, 170, 255, 0.15));
  border-radius: $ev-radius-sm;
  margin-top: 8px;
}

.reward-label {
  font-size: 13px;
  color: $ev-text-sec;
}

.reward-value {
  font-weight: 600;
  color: $ev-text;
}

.achievements-card {
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.category-tabs {
  margin: 0;
}

.category-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.achievement-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #FAF8FF;
  border-radius: $ev-radius;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.achievement-card:hover {
  border-color: $ev-primary;
  box-shadow: $ev-shadow;
}

.achievement-card.completed {
  background: linear-gradient(135deg, rgba(184, 241, 204, 0.2), rgba(213, 170, 255, 0.15));
  border-color: rgba(184, 241, 204, 0.5);
}

.achievement-card.locked {
  opacity: 0.6;
}

.achievement-icon-wrap {
  position: relative;
}

.achievement-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #FFE5A0, #FFD166);
  border-radius: $ev-radius;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
}

.achievement-icon.locked {
  background: #E8E0F0;
  filter: grayscale(0.5);
}

.completed-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  background: #B8F1CC;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
  font-weight: bold;
}

.achievement-content {
  flex: 1;
  min-width: 0;
}

.achievement-name {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: $ev-text;
}

.achievement-desc {
  font-size: 12px;
  color: $ev-text-sec;
  margin: 0 0 10px 0;
}

.achievement-progress-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.achievement-progress-wrap .el-progress {
  flex: 1;
}

.progress-text {
  font-size: 11px;
  color: $ev-text-muted;
  white-space: nowrap;
}

.achievement-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.reward-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.coin-icon {
  font-size: 16px;
}

.coin-num {
  font-weight: 600;
  font-size: 14px;
  color: $ev-text;
}

.claim-btn {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF) !important;
  border: none !important;
  color: white !important;
  font-weight: 600;
  padding: 6px 16px;
  border-radius: $ev-radius-sm;
}

.claim-btn:hover {
  filter: brightness(1.05);
}

.claimed-tag {
  background: #B8F1CC !important;
  border-color: #B8F1CC !important;
  color: #2e7d32 !important;
}

.ongoing-hint {
  font-size: 12px;
  color: $ev-text-muted;
}
</style>
