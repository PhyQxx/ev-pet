<template>
  <view class="page">
    <!-- 顶部栏 -->
    <view class="top-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="top-bar-row">
        <view class="top-bar-left">
          <view class="top-bar-title">🐾 打工系统</view>
          <view class="top-bar-sub">宠物外出工作，赚取金币奖励</view>
        </view>
        <view class="energy-wrap">
          <text class="energy-icon">⚡</text>
          <view class="energy-track">
            <view class="energy-fill" :style="{ width: energy + '%' }"></view>
          </view>
          <text class="energy-text">{{ energy }}</text>
        </view>
      </view>
    </view>

    <!-- Tab切换 -->
    <view class="tab-bar">
      <view
        class="tab-btn"
        :class="{ active: currentTab === 'work' }"
        @click="switchTab('work')"
      >📋 打工</view>
      <view
        class="tab-btn"
        :class="{ active: currentTab === 'earnings' }"
        @click="switchTab('earnings')"
      >💰 收益</view>
      <view
        class="tab-btn"
        :class="{ active: currentTab === 'history' }"
        @click="switchTab('history')"
      >📜 记录</view>
    </view>

    <!-- 内容区 -->
    <view class="content">

      <!-- ===== 打工页 ===== -->
      <view v-if="currentTab === 'work'">

        <!-- 打工进行中横幅 -->
        <view class="working-banner" v-if="workStatus === 'working'">
          <text class="working-pet-emoji">{{ petInfo?.emoji || '🐱' }}</text>
          <view class="working-title">{{ petInfo?.name || '宠物' }}正在努力打工中...</view>
          <view class="working-job">{{ currentWorkJob?.name }} · 收益 +{{ currentWorkJob?.reward }} 🪙</view>
          <view class="working-timer">{{ timerDisplay }}</view>
          <view class="progress-track">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <view class="time-left">剩余 {{ timeLeftText }} 完成</view>
          <button class="cancel-btn" @click="cancelWork">取消打工（不获得奖励）</button>
        </view>

        <!-- 打工完成横幅 -->
        <view class="complete-banner" v-if="workStatus === 'completed'">
          <text class="complete-emoji">🎉</text>
          <view class="complete-title">打工完成！</view>
          <view class="complete-job">{{ petInfo?.name || '宠物' }}完成了「{{ completedJob?.name }}」</view>
          <view class="complete-reward">+ {{ completedJob?.reward }} 🪙</view>
          <view class="complete-exp">+ {{ completedJob?.exp }} 经验</view>
          <button class="confirm-btn" @click="confirmComplete">确定</button>
        </view>

        <!-- 宠物状态卡片 -->
        <view class="status-card" v-if="workStatus === 'idle'">
          <view class="status-inner">
            <view class="status-avatar">{{ petInfo?.emoji || '🐱' }}</view>
            <view class="status-info">
              <view class="status-name">{{ petInfo?.name || '加载中...' }}</view>
              <view class="status-type">{{ petInfo?.type || '' }} · LV.{{ petInfo?.level || 1 }} {{ petInfo?.stageName || '幼年期' }}</view>
              <view class="status-stats">
                <text>⚡ 体力 {{ energy }}</text>
                <text>😊 心情 {{ mood }}</text>
              </view>
            </view>
            <view class="status-right">
              <button class="work-main-btn" @click="openQuickWork">🚀 开始打工</button>
              <text class="work-hint">消耗 ⚡{{ currentWorkJob?.energyCost || 0 }} 体力</text>
            </view>
          </view>
        </view>

        <!-- 快速打工 -->
        <view class="section" v-if="workStatus === 'idle'">
          <view class="section-header">
            <view class="section-title">⚡ 快速打工</view>
            <view class="section-more" @click="switchTab('work')">全部 →</view>
          </view>
          <view class="quick-grid">
            <view
              class="quick-card"
              v-for="job in quickJobs"
              :key="job.id"
              :class="{ locked: !job.unlocked }"
              @click="openJobModal(job)"
            >
              <text class="quick-icon">{{ job.emoji }}</text>
              <view class="quick-name">{{ job.name }}</view>
              <view class="quick-reward">+{{ job.reward }} 🪙</view>
              <view class="quick-time">⏱ {{ job.duration }}分钟</view>
              <button
                class="quick-btn"
                :disabled="!job.unlocked || energy < job.energyCost"
                @click.stop="openJobModal(job)"
              >{{ job.unlocked ? '开始' : '🔒 解锁' }}</button>
            </view>
          </view>
        </view>

        <!-- 全部工作列表 -->
        <view class="section" v-if="workStatus === 'idle'">
          <view class="section-header">
            <view class="section-title">📋 全部工作</view>
            <view class="section-more">收益排行 →</view>
          </view>
          <view
            class="job-card"
            v-for="job in allJobs"
            :key="job.id"
            :class="{ locked: !job.unlocked }"
            @click="openJobModal(job)"
          >
            <view class="job-icon" :style="{ background: job.bgColor }">
              <text style="font-size:26rpx;">{{ job.emoji }}</text>
            </view>
            <view class="job-info">
              <view class="job-name">{{ job.name }}</view>
              <view class="job-desc">{{ job.description }}</view>
              <view class="job-tags">
                <text class="job-tag">⏱ {{ job.duration }}分钟</text>
                <text class="job-tag hard" v-if="job.energyCost >= 25">⚡ 消耗{{ job.energyCost }}</text>
              </view>
            </view>
            <view class="job-reward-side" v-if="job.unlocked">
              <view class="job-gold">🪙 +{{ job.reward }}</view>
              <view class="job-time">LV.{{ job.minLevel }} 可接</view>
            </view>
            <view class="lock-side" v-else>
              <text style="font-size:40rpx;">🔒</text>
              <view class="job-gold">🪙 +{{ job.reward }}</view>
              <view class="job-time">LV.{{ job.minLevel }} 解锁</view>
            </view>
          </view>
        </view>

      </view>

      <!-- ===== 收益页 ===== -->
      <view v-if="currentTab === 'earnings'">
        <view class="earnings-card">
          <view class="earnings-header">
            <view class="earnings-title">💰 今日收益</view>
            <view class="earnings-more">{{ todayStr }}</view>
          </view>
          <view class="earnings-total">
            <view class="earnings-total-label">今日累计收入</view>
            <view class="earnings-total-value">🪙 {{ todayTotal }}</view>
          </view>
          <view class="earnings-list">
            <view class="earnings-item" v-for="item in todayRecords" :key="item.id">
              <text class="earnings-icon">{{ item.emoji }}</text>
              <view class="earnings-info">
                <view class="earnings-job">{{ item.workName }}</view>
                <view class="earnings-date">{{ item.dateStr }} · 用时{{ item.duration }}分钟</view>
              </view>
              <view class="earnings-amount">+{{ item.reward }} 🪙</view>
            </view>
            <view v-if="todayRecords.length === 0" class="empty-tip">今日暂无收益记录</view>
          </view>
        </view>
      </view>

      <!-- ===== 记录页 ===== -->
      <view v-if="currentTab === 'history'">
        <view class="section">
          <view class="section-header">
            <view class="section-title">📜 打工履历</view>
          </view>
          <view
            class="history-card"
            v-for="record in allRecords"
            :key="record.id"
          >
            <view class="history-icon" :style="{ background: record.bgColor }">
              <text style="font-size:26rpx;">{{ record.emoji }}</text>
            </view>
            <view class="history-info">
              <view class="history-name">{{ record.workName }} ✓</view>
              <view class="history-desc">{{ record.dateStr }} · 获得 +{{ record.reward }} 🪙 · +{{ record.exp }}经验</view>
            </view>
            <text style="font-size:44rpx;">🎉</text>
          </view>
          <view v-if="allRecords.length === 0" class="empty-tip">暂无打工记录，快去打工吧！</view>
        </view>
      </view>

    </view>

    <!-- 底部留白 -->
    <view style="height:72px;"></view>

    <!-- 工作详情弹窗 -->
    <view class="modal-overlay" :class="{ show: modalVisible }" @click="closeModal">
      <view class="modal" @click.stop>
        <view class="modal-handle"></view>
        <text class="modal-emoji">{{ selectedJob?.emoji }}</text>
        <view class="modal-title">{{ selectedJob?.name }}</view>
        <view class="modal-sub">{{ selectedJob?.sub }}</view>
        <view class="modal-stats">
          <view class="modal-stat">
            <text class="modal-stat-icon">🪙</text>
            <view class="modal-stat-val">+{{ selectedJob?.reward }}</view>
            <view class="modal-stat-label">收益</view>
          </view>
          <view class="modal-stat">
            <text class="modal-stat-icon">⏱</text>
            <view class="modal-stat-val">{{ selectedJob?.duration }}分钟</view>
            <view class="modal-stat-label">时长</view>
          </view>
          <view class="modal-stat">
            <text class="modal-stat-icon">⚡</text>
            <view class="modal-stat-val">-{{ selectedJob?.energyCost }}</view>
            <view class="modal-stat-label">消耗体力</view>
          </view>
          <view class="modal-stat">
            <text class="modal-stat-icon">⭐</text>
            <view class="modal-stat-val">+{{ selectedJob?.exp }}</view>
            <view class="modal-stat-label">经验</view>
          </view>
        </view>
        <view class="modal-desc">{{ selectedJob?.desc }}</view>
        <button
          class="modal-start-btn"
          :disabled="!selectedJob?.unlocked || energy < selectedJob?.energyCost"
          @click="confirmStartWork"
        >🐾 开始打工</button>
        <view class="modal-cancel" @click="closeModal">取消</view>
      </view>
    </view>

  </view>
</template>

<script>
import { work as workApi, pet as petApi } from '../../utils/api.js'

export default {
  data() {
    return {
      statusBarHeight: 0,
      currentTab: 'work',
      energy: 70,
      mood: 92,
      petInfo: null,

      // 工作状态: idle | working | completed
      workStatus: 'idle',
      workTimer: null,
      workSeconds: 0,
      timerDisplay: '00:00',
      timeLeftText: '0分0秒',
      progressPercent: 0,
      currentWorkJob: null,
      completedJob: null,

      // 打工数据
      quickJobs: [],
      allJobs: [],
      todayRecords: [],
      allRecords: [],

      // 弹窗
      modalVisible: false,
      selectedJob: null,

      // 今日统计
      todayTotal: 0,
      todayStr: '',
    }
  },

  onLoad() {
    const info = uni.getSystemInfoSync()
    this.statusBarHeight = info.statusBarHeight || 0
    const now = new Date()
    this.todayStr = `${now.getMonth() + 1}月${now.getDate()}日`
  },

  onShow() {
    this.loadData()
  },

  onUnload() {
    if (this.workTimer) clearInterval(this.workTimer)
  },

  methods: {
    switchTab(tab) {
      this.currentTab = tab
    },

    loadData() {
      workApi.getInfo().then(data => {
        // 宠物信息
        if (data.pet) {
          this.petInfo = data.pet
          this.energy = data.pet.energy || data.pet.health || 70
          this.mood = data.pet.mood || 90
        }
        // 工作列表
        if (data.jobs) {
          this.buildJobs(data.jobs)
        }
        // 进行中的打工
        if (data.working) {
          this.startWorking(data.working.job, data.working.endTime, false)
        }
        // 今日收益
        if (data.todayRecords) {
          this.todayRecords = data.todayRecords.map(r => this.formatRecord(r, true))
          this.todayTotal = data.todayTotal || data.todayRecords.reduce((s, r) => s + (r.reward || 0), 0)
        }
        // 所有记录
        if (data.records) {
          this.allRecords = data.records.map(r => this.formatRecord(r, false))
        }
      }).catch(() => {
        // 降级为模拟数据
        this.loadMockData()
      })
    },

    buildJobs(jobs) {
      const jobBgColors = {
        '做作业': '#FFF0F5',
        '打扫房间': '#F0FFF5',
        '便利店兼职': '#FFF4C2',
        '外卖派送员': '#FFF0F5',
        '家教老师': '#F0FFF5',
        '咖啡馆服务员': '#F5F0FF',
        '插画师': '#F5F0FF',
        '主播助理': '#FFFBEF',
      }
      this.quickJobs = jobs.slice(0, 4).map(j => ({
        ...j,
        bgColor: jobBgColors[j.name] || '#F9F5FF',
        sub: j.sub || '',
        desc: j.desc || j.description || '',
        energyCost: j.energyCost || j.energy_cost || 15,
        exp: j.exp || 10,
      }))
      this.allJobs = jobs.map(j => ({
        ...j,
        bgColor: jobBgColors[j.name] || '#F9F5FF',
        sub: j.sub || '',
        desc: j.desc || j.description || '',
        energyCost: j.energyCost || j.energy_cost || 15,
        exp: j.exp || 10,
      }))
      if (this.quickJobs.length > 0 && !this.currentWorkJob) {
        this.currentWorkJob = this.quickJobs[0]
      }
    },

    loadMockData() {
      const mockJobs = [
        { id: 1, emoji: '📚', name: '做作业', sub: '温故知新，奖励丰厚', reward: 20, duration: 5, energyCost: 10, exp: 5, desc: '小甜饼认真完成作业，字迹工整，老师夸奖！', minLevel: 1, unlocked: true, bgColor: '#FFF0F5' },
        { id: 2, emoji: '🧹', name: '打扫房间', sub: '简单家务，稳赚不亏', reward: 35, duration: 10, energyCost: 15, exp: 10, desc: '小甜饼拿起小扫帚，认真地把房间打扫得干干净净！', minLevel: 1, unlocked: true, bgColor: '#F0FFF5' },
        { id: 3, emoji: '🏪', name: '便利店兼职', sub: '城市打工，报酬不错', reward: 60, duration: 20, energyCost: 20, exp: 20, desc: '小甜饼在便利店帮忙理货，遇到可爱的客人还聊了几句～', minLevel: 5, unlocked: true, bgColor: '#FFF4C2' },
        { id: 4, emoji: '🛵', name: '外卖派送员', sub: '风里来雨里去', reward: 80, duration: 30, energyCost: 25, exp: 30, desc: '小甜饼骑上小电动车，穿梭在城市街头送外卖，超级努力！', minLevel: 5, unlocked: true, bgColor: '#FFF0F5' },
        { id: 5, emoji: '👩‍🏫', name: '家教老师', sub: '知识就是金币', reward: 150, duration: 45, energyCost: 35, exp: 50, desc: '小甜饼耐心地帮助其他小朋友学习，大家都好喜欢它！', minLevel: 10, unlocked: true, bgColor: '#F0FFF5' },
        { id: 6, emoji: '🎨', name: '插画师', sub: '艺术创作，灵感无限', reward: 250, duration: 60, energyCost: 40, exp: 80, desc: '小甜饼为客人绘制可爱宠物插画，每一笔都是心意～', minLevel: 20, unlocked: false, bgColor: '#F5F0FF' },
        { id: 7, emoji: '🎙️', name: '主播助理', sub: '直播行业，前景广阔', reward: 400, duration: 90, energyCost: 50, exp: 120, desc: '小甜饼协助主播进行直播准备工作，超级认真！', minLevel: 30, unlocked: false, bgColor: '#FFFBEF' },
      ]
      this.quickJobs = mockJobs.slice(0, 4)
      this.allJobs = mockJobs
      this.currentWorkJob = mockJobs[0]
      this.todayRecords = [
        { id: 1, workName: '打扫房间', emoji: '🧹', reward: 35, duration: 10, dateStr: '今天 14:20', bgColor: '#F0FFF5', exp: 10 },
        { id: 2, workName: '便利店兼职', emoji: '🏪', reward: 60, duration: 20, dateStr: '今天 11:30', bgColor: '#FFF4C2', exp: 20 },
        { id: 3, workName: '做作业', emoji: '📚', reward: 20, duration: 5, dateStr: '今天 09:15', bgColor: '#FFF0F5', exp: 5 },
      ]
      this.todayTotal = 115
      this.allRecords = [
        { id: 1, workName: '打扫房间', emoji: '🧹', reward: 35, duration: 10, dateStr: '2026-04-15 14:20', bgColor: '#F0FFF5', exp: 10 },
        { id: 2, workName: '便利店兼职', emoji: '🏪', reward: 60, duration: 20, dateStr: '2026-04-15 11:30', bgColor: '#FFF4C2', exp: 20 },
        { id: 3, workName: '做作业', emoji: '📚', reward: 20, duration: 5, dateStr: '2026-04-15 09:15', bgColor: '#FFF0F5', exp: 5 },
        { id: 4, workName: '外卖派送员', emoji: '🛵', reward: 80, duration: 30, dateStr: '2026-04-14 16:40', bgColor: '#FFF0F5', exp: 30 },
        { id: 5, workName: '家教老师', emoji: '👩‍🏫', reward: 150, duration: 45, dateStr: '2026-04-14 14:00', bgColor: '#F0FFF5', exp: 50 },
      ]
    },

    formatRecord(r, isToday) {
      const d = new Date(r.createdAt || r.endTime || Date.now())
      const dateStr = isToday
        ? `${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`
        : `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`
      const bgColorMap = {
        '做作业': '#FFF0F5', '打扫房间': '#F0FFF5', '便利店兼职': '#FFF4C2',
        '外卖派送员': '#FFF0F5', '家教老师': '#F0FFF5', '咖啡馆服务员': '#F5F0FF',
      }
      return {
        ...r,
        dateStr: isToday ? `今天 ${dateStr}` : dateStr,
        bgColor: bgColorMap[r.workName] || '#F9F5FF',
      }
    },

    openJobModal(job) {
      if (!job.unlocked) {
        uni.showToast({ title: `需要达到 LV.${job.minLevel} 才能接此工作`, icon: 'none' })
        return
      }
      this.selectedJob = job
      this.modalVisible = true
    },

    closeModal() {
      this.modalVisible = false
    },

    openQuickWork() {
      const firstAvailable = this.quickJobs.find(j => j.unlocked && this.energy >= j.energyCost)
      if (firstAvailable) {
        this.openJobModal(firstAvailable)
      } else {
        uni.showToast({ title: '体力不足，无法打工', icon: 'none' })
      }
    },

    confirmStartWork() {
      if (!this.selectedJob) return
      if (this.energy < this.selectedJob.energyCost) {
        uni.showToast({ title: '体力不足！', icon: 'none' })
        return
      }
      this.closeModal()

      workApi.start(this.selectedJob.id).then(data => {
        this.startWorking(this.selectedJob, data?.endTime || (Date.now() + this.selectedJob.duration * 60000), true)
      }).catch(() => {
        // 模拟开始
        const endTime = Date.now() + this.selectedJob.duration * 60000
        this.startWorking(this.selectedJob, endTime, true)
      })
    },

    startWorking(job, endTime, fromApi) {
      this.currentWorkJob = job
      this.workStatus = 'working'
      this.workSeconds = Math.max(0, Math.floor((endTime - Date.now()) / 1000))
      const totalSeconds = job.duration * 60

      this.updateTimer()
      this.workTimer = setInterval(() => {
        this.workSeconds--
        if (this.workSeconds <= 0) {
          clearInterval(this.workTimer)
          this.workTimer = null
          this.workStatus = 'completed'
          this.completedJob = job
        } else {
          this.updateTimer()
        }
      }, 1000)
    },

    updateTimer() {
      const m = Math.floor(this.workSeconds / 60)
      const s = this.workSeconds % 60
      this.timerDisplay = `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
      this.timeLeftText = `${m}分${s}秒`
      const total = this.currentWorkJob.duration * 60
      this.progressPercent = Math.round(((total - this.workSeconds) / total) * 100)
    },

    cancelWork() {
      if (this.workTimer) {
        clearInterval(this.workTimer)
        this.workTimer = null
      }
      this.workStatus = 'idle'
      this.workSeconds = 0
      uni.showToast({ title: '已取消打工', icon: 'none' })
    },

    confirmComplete() {
      // 领取奖励
      workApi.claim(this.completedJob?.id).then(() => {
        uni.showToast({ title: '奖励已发放！', icon: 'success' })
      }).catch(() => {})

      this.workStatus = 'idle'
      this.completedJob = null
      // 刷新体力
      petApi.getInfo().then(data => {
        this.energy = data.energy || data.health || 70
        this.mood = data.mood || 90
      }).catch(() => {})
    },
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #FFF9FB;
  padding-bottom: 72px;
}

/* Top Bar */
.top-bar {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  padding: 16rpx 32rpx 28rpx;
  position: sticky;
  top: 0;
  z-index: 50;
}
.top-bar-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
.top-bar-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6rpx;
}
.top-bar-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}
.energy-wrap {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  padding: 8rpx 16rpx;
}
.energy-icon {
  font-size: 28rpx;
}
.energy-track {
  width: 100rpx;
  height: 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  overflow: hidden;
}
.energy-fill {
  height: 100%;
  background: #FFE5A0;
  border-radius: 8px;
  transition: width 0.5s;
}
.energy-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 600;
}

/* Tab Bar */
.tab-bar {
  display: flex;
  gap: 16rpx;
  padding: 32rpx 32rpx 0;
  position: sticky;
  top: 100rpx;
  z-index: 40;
  background: #FFF9FB;
}
.tab-btn {
  flex: 1;
  padding: 18rpx;
  text-align: center;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 600;
  border: 2rpx solid #EDE4FF;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
}
.tab-btn.active {
  background: #D5AAFF;
  color: #fff;
  border-color: #D5AAFF;
}

/* Content */
.content {
  padding: 24rpx 32rpx;
}

/* Working Banner */
.working-banner {
  background: #FFF9FB;
  border: 2rpx solid #FFD5E5;
  border-radius: 40rpx;
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
  text-align: center;
}
.working-pet-emoji {
  font-size: 112rpx;
  display: block;
  margin-bottom: 16rpx;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
.working-title {
  font-size: 30rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  color: #4A3F55;
}
.working-job {
  font-size: 26rpx;
  color: #7A6B8A;
  margin-bottom: 20rpx;
}
.working-timer {
  font-size: 56rpx;
  font-weight: 700;
  color: #D5AAFF;
  margin-bottom: 20rpx;
}
.progress-track {
  height: 20rpx;
  background: #F0EDF5;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FFB3C6, #D5AAFF);
  border-radius: 20rpx;
  transition: width 1s linear;
}
.time-left {
  font-size: 24rpx;
  color: #7A6B8A;
  margin-bottom: 24rpx;
}
.cancel-btn {
  background: #FFF0F0;
  color: #FF6B6B;
  border: 2rpx solid #FFE5E5;
  padding: 16rpx 40rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 600;
}

/* Complete Banner */
.complete-banner {
  background: #FFF9FB;
  border: 2rpx solid #FFD5E5;
  border-radius: 40rpx;
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
  text-align: center;
}
.complete-emoji {
  font-size: 112rpx;
  display: block;
  margin-bottom: 16rpx;
}
.complete-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 16rpx;
}
.complete-job {
  font-size: 26rpx;
  color: #7A6B8A;
  margin-bottom: 16rpx;
}
.complete-reward {
  font-size: 48rpx;
  font-weight: 700;
  color: #D4A000;
  margin-bottom: 8rpx;
}
.complete-exp {
  font-size: 26rpx;
  color: #7A6B8A;
  margin-bottom: 32rpx;
}
.confirm-btn {
  background: #F0FFF5;
  color: #3BAF5D;
  border: 2rpx solid #B8F1CC;
  padding: 16rpx 40rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 600;
}

/* Status Card */
.status-card {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  border-radius: 40rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
  position: relative;
  overflow: hidden;
}
.status-card::before {
  content: '';
  position: absolute;
  right: -40rpx;
  top: -40rpx;
  width: 200rpx;
  height: 200rpx;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}
.status-inner {
  display: flex;
  align-items: center;
  gap: 28rpx;
  position: relative;
  z-index: 1;
}
.status-avatar {
  width: 128rpx;
  height: 128rpx;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 68rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
  flex-shrink: 0;
}
.status-info {
  flex: 1;
}
.status-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4rpx;
}
.status-type {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 12rpx;
}
.status-stats {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  display: flex;
  gap: 24rpx;
}
.status-right {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.work-main-btn {
  background: #fff;
  color: #D5AAFF;
  padding: 20rpx 36rpx;
  border-radius: 28rpx;
  font-size: 26rpx;
  font-weight: 700;
  border: none;
  white-space: nowrap;
}
.work-hint {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

/* Section */
.section {
  margin-bottom: 40rpx;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: #4A3F55;
}
.section-title::before {
  content: '';
  width: 6rpx;
  height: 28rpx;
  background: linear-gradient(180deg, #FFB3C6, #D5AAFF);
  border-radius: 4rpx;
}
.section-more {
  font-size: 24rpx;
  color: #D5AAFF;
}

/* Quick Grid */
.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}
.quick-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx 24rpx;
  text-align: center;
  border: 2rpx solid #F5EEF8;
  transition: all 0.2s;
}
.quick-card:active {
  transform: scale(0.97);
}
.quick-card.locked {
  opacity: 0.55;
}
.quick-icon {
  font-size: 72rpx;
  display: block;
  margin-bottom: 16rpx;
}
.quick-name {
  font-size: 26rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  color: #4A3F55;
}
.quick-reward {
  font-size: 24rpx;
  color: #D4A000;
  font-weight: 600;
  margin-bottom: 12rpx;
}
.quick-time {
  font-size: 22rpx;
  color: #7A6B8A;
  margin-bottom: 16rpx;
}
.quick-btn {
  width: 100%;
  padding: 14rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  font-size: 24rpx;
  font-weight: 700;
  border: none;
}
.quick-btn:disabled {
  opacity: 0.5;
}

/* Job Card */
.job-card {
  background: #fff;
  border-radius: 36rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(180, 150, 200, 0.07);
  border: 2rpx solid #F5EEF8;
  display: flex;
  align-items: center;
  gap: 28rpx;
  transition: all 0.2s;
}
.job-card:active {
  transform: scale(0.98);
}
.job-card.locked {
  opacity: 0.6;
}
.job-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.job-info {
  flex: 1;
}
.job-name {
  font-size: 28rpx;
  font-weight: 700;
  margin-bottom: 4rpx;
  color: #4A3F55;
}
.job-desc {
  font-size: 22rpx;
  color: #7A6B8A;
  margin-bottom: 12rpx;
}
.job-tags {
  display: flex;
  gap: 8rpx;
  flex-wrap: wrap;
}
.job-tag {
  padding: 4rpx 16rpx;
  background: #F9F5FF;
  border-radius: 16rpx;
  font-size: 20rpx;
  color: #D5AAFF;
  border: 2rpx solid #EDE4FF;
}
.job-tag.hard {
  background: #FFF4C2;
  color: #8B6914;
  border-color: #FFD166;
}
.job-reward-side {
  text-align: right;
  flex-shrink: 0;
}
.job-gold {
  font-size: 30rpx;
  font-weight: 700;
  color: #D4A000;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4rpx;
}
.job-time {
  font-size: 22rpx;
  color: #7A6B8A;
  margin-top: 4rpx;
}
.lock-side {
  text-align: center;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

/* Earnings Card */
.earnings-card {
  background: #fff;
  border-radius: 36rpx;
  padding: 36rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(180, 150, 200, 0.07);
  border: 2rpx solid #F5EEF8;
}
.earnings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.earnings-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #4A3F55;
}
.earnings-more {
  font-size: 24rpx;
  color: #D5AAFF;
}
.earnings-total {
  text-align: center;
  padding: 32rpx 0;
  border-top: 2rpx solid #F5EEF8;
  border-bottom: 2rpx solid #F5EEF8;
  margin-bottom: 20rpx;
}
.earnings-total-label {
  font-size: 24rpx;
  color: #7A6B8A;
  margin-bottom: 8rpx;
}
.earnings-total-value {
  font-size: 64rpx;
  font-weight: 700;
  color: #D4A000;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}
.earnings-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.earnings-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #F5EEF8;
}
.earnings-item:last-child {
  border-bottom: none;
}
.earnings-icon {
  font-size: 40rpx;
  flex-shrink: 0;
}
.earnings-info {
  flex: 1;
}
.earnings-job {
  font-size: 26rpx;
  font-weight: 600;
  color: #4A3F55;
}
.earnings-date {
  font-size: 22rpx;
  color: #A898B8;
}
.earnings-amount {
  font-size: 28rpx;
  font-weight: 700;
  color: #D4A000;
  flex-shrink: 0;
}

/* History Card */
.history-card {
  background: #fff;
  border-radius: 36rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(180, 150, 200, 0.07);
  border: 2rpx solid #F5EEF8;
  display: flex;
  align-items: center;
  gap: 28rpx;
}
.history-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.history-info {
  flex: 1;
}
.history-name {
  font-size: 28rpx;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 6rpx;
}
.history-desc {
  font-size: 22rpx;
  color: #7A6B8A;
}

/* Empty */
.empty-tip {
  text-align: center;
  font-size: 26rpx;
  color: #A898B8;
  padding: 60rpx 0;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 63, 85, 0.4);
  backdrop-filter: blur(8rpx);
  display: flex;
  align-items: flex-end;
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
  border-radius: 48rpx 48rpx 0 0;
  width: 100%;
  max-height: 85vh;
  overflow-y: auto;
  padding: 56rpx 48rpx 80rpx;
  transform: translateY(80rpx);
  transition: transform 0.3s;
}
.modal-overlay.show .modal {
  transform: translateY(0);
}
.modal-handle {
  width: 80rpx;
  height: 8rpx;
  background: #E0E0E0;
  border-radius: 4rpx;
  margin: 0 auto 40rpx;
}
.modal-emoji {
  font-size: 128rpx;
  display: block;
  text-align: center;
  margin-bottom: 24rpx;
}
.modal-title {
  font-size: 40rpx;
  font-weight: 700;
  text-align: center;
  margin-bottom: 12rpx;
  color: #4A3F55;
}
.modal-sub {
  font-size: 26rpx;
  color: #7A6B8A;
  text-align: center;
  margin-bottom: 40rpx;
}
.modal-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24rpx;
  margin-bottom: 40rpx;
}
.modal-stat {
  background: #F9F5FF;
  border-radius: 28rpx;
  padding: 28rpx;
  text-align: center;
}
.modal-stat-icon {
  font-size: 44rpx;
  display: block;
  margin-bottom: 8rpx;
}
.modal-stat-val {
  font-size: 36rpx;
  font-weight: 700;
  color: #4A3F55;
}
.modal-stat-label {
  font-size: 22rpx;
  color: #7A6B8A;
  margin-top: 4rpx;
}
.modal-desc {
  font-size: 26rpx;
  color: #7A6B8A;
  line-height: 1.8;
  margin-bottom: 40rpx;
  text-align: center;
}
.modal-start-btn {
  width: 100%;
  padding: 28rpx;
  border-radius: 32rpx;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  font-size: 30rpx;
  font-weight: 700;
  border: none;
}
.modal-start-btn:disabled {
  opacity: 0.5;
}
.modal-cancel {
  text-align: center;
  font-size: 24rpx;
  color: #A898B8;
  margin-top: 20rpx;
}
</style>
