<template>
  <div class="work-page">
    <!-- Topbar -->
    <div class="topbar">
      <div class="topbar-logo">
        <div class="topbar-logo-icon">🐾</div>
        <span class="topbar-logo-text">EV Pet · PC 打工中心</span>
      </div>
      <div class="topbar-right">
        <span>👤 {{ userStore.userInfo?.nickname || '裴浩宇' }}</span>
        <div class="topbar-avatar">{{ petStore.petInfo?.emoji || '🐱' }}</div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabbar">
      <div class="tab" :class="{ active: activeTab === 'jobs' }" @click="activeTab = 'jobs'">📋 打工</div>
      <div class="tab" :class="{ active: activeTab === 'earnings' }" @click="activeTab = 'earnings'">💰 收益</div>
      <div class="tab" :class="{ active: activeTab === 'history' }" @click="activeTab = 'history'">📜 履历</div>
    </div>

    <!-- Main Content -->
    <div class="main-area">

      <!-- ===== 打工页 ===== -->
      <div v-show="activeTab === 'jobs'" class="page active">

        <!-- Working Banner -->
        <div v-if="currentWork" class="working-banner">
          <div class="working-content">
            <div class="working-emoji">{{ currentWork.icon }}</div>
            <div class="working-info">
              <div class="working-title">{{ petStore.petInfo?.name || '小甜饼' }}正在努力打工中...</div>
              <div class="working-job">{{ currentWork.name }} · 收益 +{{ currentWork.earnings }} 🪙 · 消耗 ⚡{{ currentWork.staminaCost }}</div>
              <div class="working-timer">{{ formatTime(remainingTime) }}</div>
              <div class="working-progress">
                <div class="working-progress-fill" :style="{ width: workProgress + '%' }"></div>
              </div>
              <div class="working-time-left">剩余 {{ formatTime(remainingTime) }} 完成</div>
            </div>
            <div class="working-actions">
              <button class="cancel-btn" @click="cancelWork">取消打工（无奖励）</button>
            </div>
          </div>
        </div>

        <!-- Idle Pet Card -->
        <div v-if="!currentWork" class="idle-card">
          <div class="idle-content">
            <div class="idle-avatar">{{ petStore.petInfo?.emoji || '🐱' }}</div>
            <div class="idle-info">
              <div class="idle-name">{{ petStore.petInfo?.name || '小甜饼' }}</div>
              <div class="idle-type">{{ petStore.petInfo?.type || '布偶猫' }} · LV.{{ petStore.petInfo?.level || 12 }} {{ petStore.petInfo?.stage || '幼年期' }}</div>
              <div class="idle-attrs">
                <div class="idle-attr">
                  <span class="idle-attr-label">⚡ 体力</span>
                  <div class="idle-attr-bar">
                    <div class="idle-attr-fill" :style="{ width: (petStore.petInfo?.stamina || 70) + '%', background: '#FFE5A0' }"></div>
                  </div>
                </div>
                <div class="idle-attr">
                  <span class="idle-attr-label">😊 心情</span>
                  <div class="idle-attr-bar">
                    <div class="idle-attr-fill" :style="{ width: (petStore.petInfo?.mood || 92) + '%', background: '#B8F1CC' }"></div>
                  </div>
                </div>
                <div class="idle-attr">
                  <span class="idle-attr-label">🍖 饱食</span>
                  <div class="idle-attr-bar">
                    <div class="idle-attr-fill" :style="{ width: (petStore.petInfo?.fullness || 85) + '%', background: '#D5AAFF' }"></div>
                  </div>
                </div>
              </div>
            </div>
            <div class="idle-actions">
              <button class="work-btn" @click="showQuickWork">🚀 开始打工</button>
              <div class="work-hint">每次消耗 ⚡30 体力</div>
            </div>
          </div>
        </div>

        <!-- Stats Row -->
        <div class="stats-row">
          <div class="stat-card">
            <div class="stat-icon" style="background:#FFF0F5;">📅</div>
            <div class="stat-val">{{ workStats.totalCount }}</div>
            <div class="stat-label">累计打工次数</div>
            <div class="stat-change up">↑ 本周 +{{ workStats.weeklyCount }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background:#FFFBEF;">💰</div>
            <div class="stat-val">{{ workStats.totalEarnings.toLocaleString() }}</div>
            <div class="stat-label">累计收益 (🪙)</div>
            <div class="stat-change up">↑ 今日 +{{ workStats.todayEarnings }}</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background:#F0FFF5;">⭐</div>
            <div class="stat-val">{{ workStats.unlockedJobs }}</div>
            <div class="stat-label">已解锁工作</div>
            <div class="stat-change">共 {{ jobs.length }} 种</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background:#F0F8FF;">⏱️</div>
            <div class="stat-val">{{ workStats.weeklyHours }}</div>
            <div class="stat-label">本周打工时长 (h)</div>
            <div class="stat-change up">↑ 昨日 +{{ workStats.yesterdayHours }}h</div>
          </div>
        </div>

        <!-- Quick Jobs -->
        <div class="card" style="margin-bottom:24px;">
          <div class="card-title">⚡ 快速打工 <span class="more" @click="activeTab = 'jobs'">全部 →</span></div>
          <div class="quick-grid">
            <div v-for="job in quickJobs" :key="job.id" class="quick-card" :class="{ locked: !isJobUnlocked(job) }" @click="openJobModal(job)">
              <div class="quick-icon">{{ job.icon }}</div>
              <div class="quick-name">{{ job.name }}</div>
              <div class="quick-reward">+{{ job.earnings }} 🪙</div>
              <div class="quick-time">⏱ {{ job.duration }}分钟 · ⚡{{ job.staminaCost }}</div>
              <button class="quick-btn" :disabled="!isJobUnlocked(job)">{{ isJobUnlocked(job) ? '开始' : '🔒 解锁' }}</button>
            </div>
          </div>
        </div>

        <!-- All Jobs -->
        <div class="card">
          <div class="card-title">📋 全部工作</div>
          <div class="job-grid">
            <div v-for="job in jobs" :key="job.id" class="job-card" :class="{ locked: !isJobUnlocked(job) }" @click="openJobModal(job)">
              <div class="job-icon" :style="getJobIconStyle(job.id)">{{ job.icon }}</div>
              <div class="job-info">
                <div class="job-name">{{ job.name }}</div>
                <div class="job-desc">{{ job.description }}</div>
                <div class="job-tags">
                  <span class="job-tag">⏱ {{ job.duration }}分钟</span>
                  <span class="job-tag hard">⚡ 消耗{{ job.staminaCost }}</span>
                </div>
              </div>
              <div class="job-reward">
                <div class="job-gold">🪙 +{{ job.earnings }}</div>
                <div class="job-time">{{ isJobUnlocked(job) ? 'LV.' + job.minLevel + ' 可接' : '🔒 LV.' + job.minLevel + ' 解锁' }}</div>
                <button class="job-start-btn" :disabled="!isJobUnlocked(job)">{{ isJobUnlocked(job) ? '开始' : '🔒 解锁' }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== 收益页 ===== -->
      <div v-show="activeTab === 'earnings'" class="page">
        <div class="earnings-layout">
          <!-- Main -->
          <div class="earnings-main">
            <!-- Stats -->
            <div class="stats-row" style="grid-template-columns:repeat(3,1fr);">
              <div class="stat-card">
                <div class="stat-icon" style="background:#FFFBEF;">💰</div>
                <div class="stat-val">{{ workStats.todayEarnings }}</div>
                <div class="stat-label">今日收益</div>
                <div class="stat-change up">↑ 较昨日 +{{ workStats.todayEarnings - 220 }}</div>
              </div>
              <div class="stat-card">
                <div class="stat-icon" style="background:#F0FFF5;">📈</div>
                <div class="stat-val">{{ workStats.weeklyEarnings.toLocaleString() }}</div>
                <div class="stat-label">本周收益</div>
                <div class="stat-change up">↑ 持续增长</div>
              </div>
              <div class="stat-card">
                <div class="stat-icon" style="background:#FFF0F5;">🏆</div>
                <div class="stat-val">TOP 35%</div>
                <div class="stat-label">收益排名</div>
                <div class="stat-change up">↑ 升 2 位</div>
              </div>
            </div>

            <!-- Earnings Table -->
            <div class="card">
              <div class="card-title">💰 收益明细</div>
              <table class="rank-table">
                <thead>
                  <tr>
                    <th>工作</th>
                    <th>日期</th>
                    <th>时长</th>
                    <th>收益</th>
                    <th>状态</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="record in earningsRecords" :key="record.id">
                    <td>
                      <div class="rank-user">
                        <span style="font-size:18px;">{{ record.icon }}</span>
                        <div><div class="rank-name">{{ record.name }}</div></div>
                      </div>
                    </td>
                    <td>{{ record.date }}</td>
                    <td>{{ record.duration }}分钟</td>
                    <td class="rank-gold-val">+{{ record.earnings }} 🪙</td>
                    <td><span style="color:#3BAF5D;font-weight:700;">✓ 已完成</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Right: Today Total -->
          <div class="earnings-right">
            <div class="earnings-card">
              <div class="earnings-total">
                <div class="earnings-total-label">今日累计收入</div>
                <div class="earnings-total-value">🪙 {{ workStats.todayEarnings }}</div>
              </div>
              <div style="font-size:13px;font-weight:700;margin-bottom:12px;">📋 今日工作</div>
              <div class="earnings-list">
                <div v-for="item in todayWorkList" :key="item.id" class="earnings-item">
                  <div class="earnings-icon">{{ item.icon }}</div>
                  <div class="earnings-info">
                    <div class="earnings-job">{{ item.name }}</div>
                    <div class="earnings-date">{{ item.time }} · {{ item.duration }}分钟</div>
                  </div>
                  <div class="earnings-amount">+{{ item.earnings }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ===== 履历页 ===== -->
      <div v-show="activeTab === 'history'" class="page">
        <div class="history-layout">
          <div class="card" style="flex:1;">
            <div class="card-title">📜 打工履历</div>
            <div class="history-list">
              <div v-for="item in workHistory" :key="item.id" class="history-item">
                <div class="history-icon">{{ item.icon }}</div>
                <div class="history-content">
                  <div class="history-text">
                    完成了 <strong>{{ item.name }}</strong>
                    <span v-if="item.status === 'done'" style="color:#3BAF5D;"> · 获得 +{{ item.earnings }} 🪙</span>
                    <span v-else style="color:#FF6B6B;"> · 已取消</span>
                  </div>
                  <div class="history-time">{{ item.time }}</div>
                </div>
                <div class="history-badge">{{ item.status === 'done' ? '✓' : '✗' }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Job Modal -->
    <div class="modal-overlay" :class="{ show: showModal }" @click.self="showModal = false">
      <div class="modal" v-if="selectedJob">
        <div class="modal-handle"></div>
        <div class="modal-emoji">{{ selectedJob.icon }}</div>
        <div class="modal-title">{{ selectedJob.name }}</div>
        <div class="modal-sub">{{ selectedJob.description }}</div>
        <div class="modal-stats">
          <div class="modal-stat">
            <div class="modal-stat-icon">💰</div>
            <div class="modal-stat-val">+{{ selectedJob.earnings }}</div>
            <div class="modal-stat-label">金币收益</div>
          </div>
          <div class="modal-stat">
            <div class="modal-stat-icon">⭐</div>
            <div class="modal-stat-val">+{{ selectedJob.exp }}</div>
            <div class="modal-stat-label">经验值</div>
          </div>
          <div class="modal-stat">
            <div class="modal-stat-icon">⏱</div>
            <div class="modal-stat-val">{{ selectedJob.duration }}</div>
            <div class="modal-stat-label">分钟</div>
          </div>
          <div class="modal-stat">
            <div class="modal-stat-icon">⚡</div>
            <div class="modal-stat-val">{{ selectedJob.staminaCost }}</div>
            <div class="modal-stat-label">体力消耗</div>
          </div>
        </div>
        <div class="modal-desc">
          <template v-if="isJobUnlocked(selectedJob)">
            {{ petStore.petInfo?.name || '你的宠物' }}将进行 {{ selectedJob.duration }} 分钟的 {{ selectedJob.name }} 工作
          </template>
          <template v-else>
            需要达到 LV.{{ selectedJob.minLevel }} 才能接取此工作
          </template>
        </div>
        <button v-if="isJobUnlocked(selectedJob)" class="modal-start-btn" @click="startJob(selectedJob)">开始工作</button>
        <div class="modal-cancel" @click="showModal = false">取消</div>
      </div>
    </div>

    <!-- Reward Animation -->
    <Transition name="reward">
      <div v-if="showReward" class="reward-overlay">
        <div class="reward-content">
          <div class="reward-emoji">{{ rewardJob?.icon || '🎉' }}</div>
          <div class="reward-title">工作完成！</div>
          <div class="reward-amount">+{{ rewardJob?.earnings || 0 }} 🪙</div>
          <div class="reward-exp">+{{ rewardJob?.exp || 0 }} 经验</div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore, usePetStore } from '../../store'
import { work as workApi } from '../../api/index.js'

const userStore = useUserStore()
const petStore = usePetStore()

const activeTab = ref('jobs')
const showModal = ref(false)
const selectedJob = ref(null)
const showReward = ref(false)
const rewardJob = ref(null)

const currentWork = ref(null)
const remainingTime = ref(0)
const workProgress = ref(0)
let workTimer = null

const jobs = ref([
  { id: 1, name: '做作业', icon: '📚', description: '认真完成每日的作业任务', duration: 5, earnings: 20, exp: 5, staminaCost: 10, minLevel: 1 },
  { id: 2, name: '打扫房间', icon: '🧹', description: '帮家里打扫卫生，整理房间', duration: 10, earnings: 35, exp: 8, staminaCost: 15, minLevel: 1 },
  { id: 3, name: '便利店兼职', icon: '🏪', description: '在便利店帮忙整理货架', duration: 20, earnings: 60, exp: 12, staminaCost: 20, minLevel: 3 },
  { id: 4, name: '外卖派送员', icon: '🛵', description: '骑电动车穿梭城市，送外卖赚小费', duration: 30, earnings: 80, exp: 18, staminaCost: 25, minLevel: 5 },
  { id: 5, name: '咖啡馆服务员', icon: '☕', description: '在咖啡馆为客人服务', duration: 40, earnings: 120, exp: 25, staminaCost: 30, minLevel: 15 },
  { id: 6, name: '家教老师', icon: '👩‍🏫', description: '帮助其他小朋友学习，耐心讲解', duration: 45, earnings: 150, exp: 30, staminaCost: 35, minLevel: 10 },
  { id: 7, name: '插画师', icon: '🎨', description: '为客人绘制可爱宠物插画', duration: 60, earnings: 250, exp: 50, staminaCost: 40, minLevel: 20 },
  { id: 8, name: '主播助理', icon: '🎙️', description: '协助主播进行直播准备工作', duration: 90, earnings: 400, exp: 80, staminaCost: 50, minLevel: 30 }
])

const quickJobs = computed(() => jobs.value.slice(0, 4))

const workStats = ref({
  totalCount: 0,
  weeklyCount: 0,
  totalEarnings: 0,
  todayEarnings: 0,
  weeklyEarnings: 0,
  unlockedJobs: 0,
  weeklyHours: 0,
  yesterdayHours: 0
})

const earningsRecords = ref([])

const todayWorkList = computed(() => earningsRecords.value.filter(r => r.date.startsWith('今天')))

const workHistory = ref([])

const userLevel = computed(() => petStore.petInfo?.level || 1)

const isJobUnlocked = (job) => userLevel.value >= job.minLevel

const getJobIconStyle = (id) => {
  const styles = {
    1: { background: '#FFF0F5' },
    2: { background: '#F0FFF5' },
    3: { background: '#FFFBEF' },
    4: { background: '#FFF0F5' },
    5: { background: '#F5F0FF' },
    6: { background: '#F0FFF5' },
    7: { background: '#F5F0FF' },
    8: { background: '#FFFBEF' }
  }
  return styles[id] || { background: '#F5F0FF' }
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const openJobModal = (job) => {
  if (!isJobUnlocked(job)) return
  selectedJob.value = job
  showModal.value = true
}

const showQuickWork = () => {
  ElMessage.info('选择一项快速工作开始打工~')
}

const loadWorkInfo = async () => {
  try {
    const res = await workApi.getInfo()
    if (res.code === 200 && res.data) {
      const d = res.data
      workStats.value = {
        totalCount: d.totalCount || 0,
        weeklyCount: d.weeklyCount || 0,
        totalEarnings: d.totalEarnings || 0,
        todayEarnings: d.todayEarnings || 0,
        weeklyEarnings: d.weeklyEarnings || 0,
        unlockedJobs: d.unlockedJobs || 0,
        weeklyHours: d.weeklyHours || 0,
        yesterdayHours: d.yesterdayHours || 0
      }
      // 如果有进行中的工作，恢复计时器
      if (d.currentWork) {
        currentWork.value = jobs.value.find(j => j.id === d.currentWork.workId) || null
        remainingTime.value = d.currentWork.remainingSeconds || 0
        const totalTime = currentWork.value ? currentWork.value.duration * 60 : 0
        workProgress.value = totalTime > 0 ? ((totalTime - remainingTime.value) / totalTime) * 100 : 0
        if (remainingTime.value > 0) startWorkTimer()
      }
      // 打工记录
      if (d.records && d.records.length > 0) {
        earningsRecords.value = d.records.map(r => {
          const job = jobs.value.find(j => j.id === r.workId) || {}
          return {
            id: r.id,
            name: job.name || r.workName || '打工',
            icon: job.icon || '📋',
            date: r.createTime ? new Date(r.createTime).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }).replace(/\//g, '-') : '',
            duration: job.duration || r.duration || 0,
            earnings: r.earnings || 0
          }
        })
      }
      if (d.history && d.history.length > 0) {
        workHistory.value = d.history.map(r => {
          const job = jobs.value.find(j => j.id === r.workId) || {}
          return {
            id: r.id,
            name: job.name || r.workName || '打工',
            icon: job.icon || '📋',
            time: r.createTime ? new Date(r.createTime).toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }).replace(/\//g, '-') : '',
            earnings: r.earnings || 0,
            status: r.status === 'completed' ? 'done' : (r.status === 'cancelled' ? 'cancelled' : 'done')
          }
        })
      }
    }
  } catch (e) {
    console.error('加载打工信息失败', e)
  }
}

const startWorkTimer = () => {
  if (workTimer) clearInterval(workTimer)
  const totalTime = currentWork.value ? currentWork.value.duration * 60 : 0
  workTimer = setInterval(() => {
    remainingTime.value--
    workProgress.value = totalTime > 0 ? ((totalTime - remainingTime.value) / totalTime) * 100 : 0
    if (remainingTime.value <= 0) {
      clearInterval(workTimer)
      completeWork(currentWork.value)
    }
  }, 1000)
}

const startJob = async (job) => {
  if (currentWork.value) {
    ElMessage.warning('当前有工作正在进行中')
    return
  }
  showModal.value = false
  try {
    await workApi.start(job.id)
  } catch (e) {
    console.error('开始打工失败', e)
  }
  currentWork.value = job
  remainingTime.value = job.duration * 60
  workProgress.value = 0
  startWorkTimer()
  ElMessage.success(`开始打工：${job.name}`)
}

const completeWork = async (job) => {
  currentWork.value = null
  workProgress.value = 100
  rewardJob.value = job
  showReward.value = true
  try {
    // 调用后端领奖接口
    // await workApi.claim(recordId)
  } catch (e) {
    console.error('领取奖励失败', e)
  }
  workStats.value.todayEarnings += job.earnings
  workStats.value.totalEarnings += job.earnings
  workStats.value.totalCount++

  workHistory.value.unshift({
    id: Date.now(),
    name: job.name,
    icon: job.icon,
    time: '刚刚',
    earnings: job.earnings,
    status: 'done'
  })

  earningsRecords.value.unshift({
    id: Date.now(),
    name: job.name,
    icon: job.icon,
    date: '刚刚',
    duration: job.duration,
    earnings: job.earnings
  })

  setTimeout(() => {
    showReward.value = false
  }, 2500)
}

const cancelWork = async () => {
  if (workTimer) clearInterval(workTimer)
  workHistory.value.unshift({
    id: Date.now(),
    name: currentWork.value.name,
    icon: currentWork.value.icon,
    time: '刚刚',
    earnings: 0,
    status: 'cancelled'
  })
  currentWork.value = null
  ElMessage.info('已取消打工，无奖励')
}

onMounted(() => {
  if (petStore.petInfo) {
    userLevel.value = petStore.petInfo.level || 1
  }
  loadWorkInfo()
})

onUnmounted(() => {
  if (workTimer) clearInterval(workTimer)
})
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.work-page {
  min-height: 100vh;
  background: $ev-bg-page;
}

.topbar {
  background: linear-gradient(135deg, $ev-text, #6B5B8A);
  padding: 14px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
}

.topbar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.topbar-logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
}

.topbar-logo-text {
  font-size: 14px;
  font-weight: 700;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #fff;
  font-size: 13px;
}

.topbar-avatar {
  width: 30px;
  height: 30px;
  background: $ev-purple;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}

.tabbar {
  background: #fff;
  padding: 0 28px;
  display: flex;
  gap: 4px;
  border-bottom: 1px solid $ev-purple-light;
}

.tab {
  padding: 10px 18px;
  border-radius: 10px 10px 0 0;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid transparent;
  border-bottom: none;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
  margin-bottom: -1px;
}

.tab:hover {
  color: $ev-purple;
}

.tab.active {
  background: $ev-purple;
  color: #fff;
  border-color: $ev-purple;
}

.main-area {
  padding: 24px 28px;
}

.card {
  background: #fff;
  border-radius: $ev-radius;
  padding: 22px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title::before {
  content: '';
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, $ev-primary, $ev-purple);
  border-radius: 2px;
}

.card-title .more {
  margin-left: auto;
  font-size: 12px;
  color: $ev-purple;
  cursor: pointer;
  font-weight: 500;
}

.working-banner {
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.working-banner::before {
  content: '';
  position: absolute;
  right: -20px;
  top: -20px;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.working-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.working-emoji {
  font-size: 64px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

.working-info {
  flex: 1;
}

.working-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
}

.working-job {
  font-size: 13px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.working-timer {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
}

.working-progress {
  height: 10px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 6px;
}

.working-progress-fill {
  height: 100%;
  background: #fff;
  border-radius: 10px;
  transition: width 1s linear;
}

.working-time-left {
  font-size: 12px;
  opacity: 0.85;
}

.working-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #fff;
  padding: 10px 20px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.4);
}

.idle-card {
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.idle-card::before {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.idle-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.idle-avatar {
  width: 90px;
  height: 90px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.idle-info {
  flex: 1;
}

.idle-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 2px;
}

.idle-type {
  font-size: 12px;
  opacity: 0.85;
  margin-bottom: 12px;
}

.idle-attrs {
  display: flex;
  gap: 16px;
}

.idle-attr {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.idle-attr-label {
  font-size: 11px;
  opacity: 0.75;
}

.idle-attr-bar {
  width: 80px;
  height: 7px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 7px;
  overflow: hidden;
}

.idle-attr-fill {
  height: 100%;
  border-radius: 7px;
}

.idle-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  z-index: 1;
}

.work-btn {
  background: #fff;
  color: $ev-purple;
  padding: 10px 22px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}

.work-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.15);
}

.work-hint {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: $ev-radius-sm + 6px;
  padding: 18px;
  box-shadow: 0 2px 8px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.stat-icon {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
  margin-bottom: 10px;
}

.stat-val {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 11px;
  color: #7A6B8A;
}

.stat-change {
  font-size: 11px;
  margin-top: 4px;
}

.stat-change.up {
  color: #3BAF5D;
}

.stat-change.down {
  color: #FF6B6B;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-card {
  background: #fff;
  border-radius: $ev-radius-sm + 6px;
  padding: 18px;
  text-align: center;
  border: 1px solid $ev-bg;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(180, 150, 200, 0.12);
  border-color: $ev-purple;
}

.quick-card.locked {
  opacity: 0.5;
  cursor: not-allowed;
}

.quick-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.quick-name {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 4px;
}

.quick-reward {
  font-size: 13px;
  color: #D4A000;
  font-weight: 600;
  margin-bottom: 4px;
}

.quick-time {
  font-size: 11px;
  color: #7A6B8A;
  margin-bottom: 10px;
}

.quick-btn {
  width: 100%;
  padding: 8px;
  border-radius: 10px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.quick-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.job-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.job-card {
  background: #fff;
  border-radius: $ev-radius;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s;
  cursor: pointer;
}

.job-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(180, 150, 200, 0.13);
  border-color: $ev-purple;
}

.job-card.locked {
  opacity: 0.55;
  cursor: not-allowed;
}

.job-icon {
  width: 56px;
  height: 56px;
  border-radius: $ev-radius-sm;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.job-info {
  flex: 1;
}

.job-name {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
}

.job-desc {
  font-size: 12px;
  color: #7A6B8A;
  margin-bottom: 8px;
  line-height: 1.4;
}

.job-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.job-tag {
  padding: 3px 10px;
  background: #F9F5FF;
  border-radius: 8px;
  font-size: 11px;
  color: $ev-purple;
  border: 1px solid $ev-purple-light;
}

.job-tag.hard {
  background: #FFFBEF;
  color: #8B6914;
  border-color: #FFD166;
}

.job-reward {
  text-align: right;
  flex-shrink: 0;
}

.job-gold {
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: flex-end;
}

.job-time {
  font-size: 11px;
  color: #7A6B8A;
  margin-top: 4px;
}

.job-start-btn {
  margin-top: 10px;
  padding: 8px 18px;
  border-radius: 10px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.job-card.locked .job-start-btn {
  background: #E0E0E0;
  color: #A898B8;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 63, 85, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
}

.modal-overlay.show {
  opacity: 1;
  pointer-events: all;
}

.modal {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  width: 460px;
  transform: scale(0.95);
  transition: transform 0.3s;
}

.modal-overlay.show .modal {
  transform: scale(1);
}

.modal-handle {
  width: 40px;
  height: 4px;
  background: #E0E0E0;
  border-radius: 2px;
  margin: 0 auto 20px;
}

.modal-emoji {
  font-size: 60px;
  text-align: center;
  margin-bottom: 12px;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 4px;
}

.modal-sub {
  font-size: 13px;
  color: #7A6B8A;
  text-align: center;
  margin-bottom: 20px;
}

.modal-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.modal-stat {
  background: #F9F5FF;
  border-radius: 14px;
  padding: 14px;
  text-align: center;
}

.modal-stat-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.modal-stat-val {
  font-size: 18px;
  font-weight: 700;
}

.modal-stat-label {
  font-size: 11px;
  color: #7A6B8A;
}

.modal-desc {
  font-size: 13px;
  color: #7A6B8A;
  line-height: 1.6;
  margin-bottom: 20px;
}

.modal-start-btn {
  width: 100%;
  padding: 13px;
  border-radius: 16px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.modal-cancel {
  text-align: center;
  margin-top: 10px;
  font-size: 12px;
  color: #A898B8;
  cursor: pointer;
}

.rank-table {
  width: 100%;
  border-collapse: collapse;
}

.rank-table th {
  text-align: left;
  padding: 10px 12px;
  font-size: 11px;
  font-weight: 700;
  color: #A898B8;
  letter-spacing: 0.5px;
  border-bottom: 2px solid $ev-bg;
}

.rank-table td {
  padding: 12px 12px;
  border-bottom: 1px solid $ev-bg;
  font-size: 13px;
}

.rank-table tr:hover {
  background: #FAFAFA;
}

.rank-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rank-name {
  font-weight: 600;
}

.rank-gold-val {
  font-weight: 700;
  color: #D4A000;
}

.earnings-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.earnings-card {
  background: #fff;
  border-radius: $ev-radius;
  padding: 22px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.earnings-total {
  text-align: center;
  padding: 20px 0;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #FFFBEF, #FFF9FB);
  border-radius: $ev-radius-sm;
  border: 1px solid #FFF4C2;
}

.earnings-total-label {
  font-size: 12px;
  color: #7A6B8A;
  margin-bottom: 6px;
}

.earnings-total-value {
  font-size: 32px;
  font-weight: 700;
  color: #D4A000;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.earnings-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.earnings-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid $ev-bg;
}

.earnings-item:last-child {
  border-bottom: none;
}

.earnings-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.earnings-info {
  flex: 1;
}

.earnings-job {
  font-size: 13px;
  font-weight: 600;
}

.earnings-date {
  font-size: 11px;
  color: #A898B8;
}

.earnings-amount {
  font-size: 14px;
  font-weight: 700;
  color: #D4A000;
}

.history-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid $ev-bg;
}

.history-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.history-content {
  flex: 1;
}

.history-text {
  font-size: 12px;
  line-height: 1.5;
}

.history-time {
  font-size: 11px;
  color: #A898B8;
  margin-top: 2px;
}

.history-badge {
  font-size: 20px;
}

.reward-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 63, 85, 0.6);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 300;
}

.reward-content {
  text-align: center;
  color: #fff;
  animation: rewardPop 0.5s ease-out;
}

@keyframes rewardPop {
  0% { transform: scale(0.5); opacity: 0; }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); opacity: 1; }
}

.reward-emoji {
  font-size: 80px;
  margin-bottom: 16px;
}

.reward-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.reward-amount {
  font-size: 42px;
  font-weight: 700;
  color: #FFE5A0;
  margin-bottom: 4px;
}

.reward-exp {
  font-size: 18px;
  opacity: 0.9;
}

.reward-enter-active,
.reward-leave-active {
  transition: opacity 0.5s ease;
}

.reward-enter-from,
.reward-leave-to {
  opacity: 0;
}
</style>
