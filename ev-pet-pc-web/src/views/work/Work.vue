<template>
  <div class="work-page">
    <el-row :gutter="24">
      <el-col :span="16">
        <div class="work-list-card card">
          <h3>打工任务</h3>
          <div class="work-tabs">
            <el-radio-group v-model="workFilter">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="available">可接取</el-radio-button>
              <el-radio-button label="ongoing">进行中</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="work-list">
            <div v-for="work in filteredWorks" :key="work.id" class="work-item">
              <div class="work-icon">{{ work.icon }}</div>
              <div class="work-info">
                <h4 class="work-name">{{ work.name }}</h4>
                <p class="work-desc">{{ work.description }}</p>
                <div class="work-meta">
                  <span class="meta-item">⏱️ {{ work.duration }}分钟</span>
                  <span class="meta-item">🪙 {{ work.earnings }}金币</span>
                  <span class="meta-item">📈 {{ work.exp }}经验</span>
                </div>
              </div>
              <div class="work-action">
                <el-button v-if="work.status === 'available'" type="primary" @click="startWork(work)">开始打工</el-button>
                <el-tag v-else-if="work.status === 'ongoing'" type="warning">进行中</el-tag>
                <el-tag v-else type="info">已完成</el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="work-stats-card card">
          <h3>打工收益统计</h3>
          <div class="stats-list">
            <div class="stat-item">
              <span class="stat-label">今日收益</span>
              <span class="stat-value">{{ todayEarnings }} 🪙</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">累计收益</span>
              <span class="stat-value">{{ totalEarnings }} 🪙</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">打工次数</span>
              <span class="stat-value">{{ workCount }} 次</span>
            </div>
          </div>
        </div>
        
        <div class="work-history-card card">
          <h3>打工记录</h3>
          <div class="history-list">
            <div v-for="record in workHistory" :key="record.id" class="history-item">
              <div class="history-icon">{{ record.icon }}</div>
              <div class="history-info">
                <span class="history-name">{{ record.name }}</span>
                <span class="history-time">{{ record.time }}</span>
              </div>
              <span class="history-earnings">+{{ record.earnings }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const workFilter = ref('available')

const works = ref([
  { id: 1, name: '派发传单', description: '在公园帮助商家派发传单', icon: '📄', duration: 30, earnings: 50, exp: 10, status: 'available' },
  { id: 2, name: '外卖配送', description: '帮助送外卖赚取报酬', icon: '🛵', duration: 45, earnings: 80, exp: 15, status: 'available' },
  { id: 3, name: '餐厅帮厨', description: '在餐厅帮忙准备食材', icon: '🍳', duration: 60, earnings: 120, exp: 20, status: 'ongoing' },
  { id: 4, name: '家教辅导', description: '帮助小朋友辅导作业', icon: '📚', duration: 90, earnings: 200, exp: 30, status: 'available' }
])

const filteredWorks = computed(() => {
  if (workFilter.value === 'all') return works.value
  return works.value.filter(w => w.status === workFilter.value)
})

const todayEarnings = ref(150)
const totalEarnings = ref(2580)
const workCount = ref(42)

const workHistory = ref([
  { id: 1, name: '派发传单', icon: '📄', time: '今天 14:30', earnings: 50 },
  { id: 2, name: '外卖配送', icon: '🛵', time: '今天 10:15', earnings: 80 },
  { id: 3, name: '餐厅帮厨', icon: '🍳', time: '昨天 18:00', earnings: 120 }
])

const startWork = (work) => {
  work.status = 'ongoing'
  ElMessage.success(`开始打工：${work.name}`)
  setTimeout(() => {
    work.status = 'done'
    todayEarnings.value += work.earnings
    totalEarnings.value += work.earnings
    workCount.value++
    ElMessage.success(`打工完成！获得 ${work.earnings} 金币`)
  }, work.duration * 1000)
}
</script>

<style scoped>
.work-page { padding: 0; }

.work-list-card h3, .work-stats-card h3, .work-history-card h3 { margin-bottom: 16px; }

.work-tabs { margin: 16px 0; }

.work-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.work-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.work-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #A8D8EA 0%, #7EC8D8 100%);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-right: 16px;
}

.work-info { flex: 1; }

.work-name {
  font-size: 16px;
  margin-bottom: 4px;
}

.work-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.work-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  font-size: 12px;
  color: var(--text-secondary);
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 16px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.stat-label { color: var(--text-secondary); }
.stat-value { font-weight: 600; }

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: var(--bg-page);
  border-radius: var(--radius-sm);
}

.history-icon {
  font-size: 24px;
  margin-right: 12px;
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.history-name { font-size: 14px; }
.history-time { font-size: 12px; color: var(--text-secondary); }
.history-earnings { font-weight: 600; color: var(--accent-yellow-dark); }
</style>
