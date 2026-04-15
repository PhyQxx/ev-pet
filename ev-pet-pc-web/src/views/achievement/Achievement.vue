<template>
  <div class="achievement-page">
    <el-row :gutter="24">
      <el-col :span="8">
        <div class="stats-card card">
          <h3>我的成就</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-num">{{ completedCount }}</span>
              <span class="stat-label">已获得</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ totalCount }}</span>
              <span class="stat-label">总成就</span>
            </div>
          </div>
          <el-progress :percentage="progressPercent" :stroke-width="10" />
          <p class="progress-tip">完成 {{ completedCount }}/{{ totalCount }} 成就</p>
        </div>
      </el-col>
      
      <el-col :span="16">
        <div class="achievements-card card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="进行中" name="progress" />
            <el-tab-pane label="已完成" name="completed" />
          </el-tabs>
          
          <div class="achievement-list">
            <div v-for="item in filteredAchievements" :key="item.id" class="achievement-item">
              <div class="achievement-icon" :class="{ locked: !item.completed }">
                {{ item.icon }}
              </div>
              <div class="achievement-info">
                <h4 class="achievement-name">{{ item.name }}</h4>
                <p class="achievement-desc">{{ item.description }}</p>
                <div class="achievement-progress">
                  <el-progress :percentage="item.progress" :stroke-width="4" :show-text="false" size="small" />
                  <span class="progress-text">{{ item.current }}/{{ item.target }}</span>
                </div>
              </div>
              <div class="achievement-reward">
                <span class="reward-icon">🪙</span>
                <span class="reward-num">{{ item.reward }}</span>
                <el-button v-if="item.completed && !item.claimed" type="primary" size="small">领取</el-button>
                <el-tag v-else-if="item.claimed" size="small" type="success">已领取</el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const activeTab = ref('all')

const achievements = ref([
  { id: 1, name: '初次见面', description: '创建你的第一只宠物', icon: '🥚', completed: true, claimed: true, current: 1, target: 1, reward: 100 },
  { id: 2, name: '喂养达人', description: '累计喂食50次', icon: '🍖', completed: false, claimed: false, current: 30, target: 50, reward: 200 },
  { id: 3, name: '清洁大师', description: '累计洗澡30次', icon: '🛁', completed: false, claimed: false, current: 20, target: 30, reward: 200 },
  { id: 4, name: '玩耍专家', description: '累计陪玩50次', icon: '🎮', completed: false, claimed: false, current: 15, target: 50, reward: 200 },
  { id: 5, name: 'Lv10', description: '宠物等级达到10级', icon: '⭐', completed: false, claimed: false, current: 5, target: 10, reward: 300 },
  { id: 6, name: 'Lv20', description: '宠物等级达到20级', icon: '🌟', completed: false, claimed: false, current: 5, target: 20, reward: 500 },
  { id: 7, name: '第一次进化', description: '宠物完成第一次进化', icon: '✨', completed: false, claimed: false, current: 0, target: 1, reward: 400 },
  { id: 8, name: '完全进化', description: '宠物完成第三次进化', icon: '🌈', completed: false, claimed: false, current: 0, target: 1, reward: 1000 }
])

const totalCount = computed(() => achievements.value.length)
const completedCount = computed(() => achievements.value.filter(a => a.completed).length)
const progressPercent = computed(() => Math.round((completedCount.value / totalCount.value) * 100))

const filteredAchievements = computed(() => {
  if (activeTab.value === 'progress') return achievements.value.filter(a => !a.completed)
  if (activeTab.value === 'completed') return achievements.value.filter(a => a.completed)
  return achievements.value
})
</script>

<style scoped>
.achievement-page { padding: 0; }

.stats-card {
  text-align: center;
  padding: 32px;
}

.stats-card h3 { margin-bottom: 24px; }

.stats-grid {
  display: flex;
  justify-content: space-around;
  margin-bottom: 24px;
}

.stat-num {
  font-size: 48px;
  font-weight: 700;
  display: block;
  color: var(--accent-purple);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.progress-tip {
  margin-top: 16px;
  font-size: 14px;
  color: var(--text-secondary);
}

.achievements-card h3 { margin-bottom: 16px; }

.achievement-list {
  margin-top: 20px;
}

.achievement-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
}

.achievement-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-right: 16px;
}

.achievement-icon.locked {
  background: #E0E0E0;
  filter: grayscale(1);
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  font-size: 16px;
  margin-bottom: 4px;
}

.achievement-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.achievement-progress {
  display: flex;
  align-items: center;
  gap: 12px;
}

.achievement-progress .el-progress {
  flex: 1;
}

.progress-text {
  font-size: 12px;
  color: var(--text-secondary);
  width: 50px;
}

.achievement-reward {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
}

.reward-icon { font-size: 20px; }
.reward-num { font-weight: 600; }
</style>
