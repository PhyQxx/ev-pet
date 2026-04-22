<template>
  <view class="page">
    <!-- Top Bar -->
    <view class="top-bar">
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

    <!-- Tab Bar -->
    <view class="tab-bar">
      <view class="tab-btn" :class="{ active: currentTab === 'work' }" @click="switchTab('work')">📋 打工</view>
      <view class="tab-btn" :class="{ active: currentTab === 'earnings' }" @click="switchTab('earnings')">💰 收益</view>
      <view class="tab-btn" :class="{ active: currentTab === 'history' }" @click="switchTab('history')">📜 记录</view>
    </view>

    <!-- Content -->
    <view class="content">
      <!-- Work Tab -->
      <view v-if="currentTab === 'work'">
        <!-- Working Banner -->
        <view class="working-banner" v-if="workStatus === 'working'">
          <text class="working-pet-emoji">{{ petEmoji }}</text>
          <view class="working-title">{{ petInfo?.name || '宠物' }}正在努力打工中...</view>
          <view class="working-job">{{ currentWorkJob?.name }} · 收益 +{{ currentWorkJob?.reward }} 🪙</view>
          <view class="working-timer">{{ timerDisplay }}</view>
          <view class="progress-track">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <view class="time-left">剩余 {{ timeLeftText }} 完成</view>
          <button class="cancel-btn" @click="cancelWork">取消打工</button>
        </view>

        <!-- Complete Banner -->
        <view class="complete-banner" v-if="workStatus === 'completed'">
          <text class="complete-emoji">🎉</text>
          <view class="complete-title">打工完成！</view>
          <view class="complete-job">{{ petInfo?.name || '宠物' }}完成了「{{ completedJob?.name }}」</view>
          <view class="complete-reward">+ {{ completedJob?.reward }} 🪙</view>
          <view class="complete-exp">+ {{ completedJob?.exp }} 经验</view>
          <button class="confirm-btn" @click="confirmComplete">确定</button>
        </view>

        <!-- Idle Pet Card -->
        <view class="status-card" v-if="workStatus === 'idle'">
          <view class="status-inner">
            <view class="status-avatar">{{ petEmoji }}</view>
            <view class="status-info">
              <view class="status-name">{{ petInfo?.name || '加载中...' }}</view>
              <view class="status-type">LV.{{ petInfo?.level || 1 }} {{ stageName }}</view>
              <view class="status-stats">
                <text>⚡ {{ energy }}</text>
                <text>😊 {{ petMood }}</text>
              </view>
            </view>
            <view class="status-right">
              <button class="work-main-btn" @click="openQuickWork">🚀 开始打工</button>
              <text class="work-hint">消耗 ⚡{{ currentWorkJob?.energyCost || 0 }}</text>
            </view>
          </view>
        </view>

        <!-- Quick Jobs -->
        <view class="section" v-if="workStatus === 'idle'">
          <view class="section-header">
            <view class="section-title">⚡ 快速打工</view>
          </view>
          <view class="quick-grid">
            <view class="quick-card" v-for="job in quickJobs" :key="job.id" :class="{ locked: !job.unlocked }" @click="openJobModal(job)">
              <text class="quick-icon">{{ job.emoji }}</text>
              <view class="quick-name">{{ job.name }}</view>
              <view class="quick-reward">+{{ job.reward }} 🪙</view>
              <view class="quick-time">⏱ {{ job.duration }}分钟</view>
              <button class="quick-btn" :disabled="!job.unlocked || energy < job.energyCost">{{ job.unlocked ? '开始' : '🔒' }}</button>
            </view>
          </view>
        </view>

        <!-- All Jobs -->
        <view class="section" v-if="workStatus === 'idle'">
          <view class="section-header">
            <view class="section-title">📋 全部工作</view>
          </view>
          <view class="job-card" v-for="job in allJobs" :key="job.id" :class="{ locked: !job.unlocked }" @click="openJobModal(job)">
            <view class="job-icon" :style="{ background: job.bgColor }">{{ job.emoji }}</view>
            <view class="job-info">
              <view class="job-name">{{ job.name }}</view>
              <view class="job-desc">{{ job.sub }}</view>
              <view class="job-tags">
                <text class="job-tag">⏱ {{ job.duration }}分钟</text>
                <text class="job-tag hard" v-if="job.energyCost >= 25">⚡ {{ job.energyCost }}</text>
              </view>
            </view>
            <view class="job-reward-side" v-if="job.unlocked">
              <view class="job-gold">🪙 +{{ job.reward }}</view>
              <view class="job-time">LV.{{ job.minLevel }}</view>
            </view>
            <view class="lock-side" v-else>
              <text style="font-size:20px;">🔒</text>
              <view class="job-gold">🪙 +{{ job.reward }}</view>
              <view class="job-time">LV.{{ job.minLevel }}</view>
            </view>
          </view>
        </view>
      </view>

      <!-- Earnings Tab -->
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
                <view class="earnings-date">{{ item.dateStr }}</view>
              </view>
              <view class="earnings-amount">+{{ item.reward }}</view>
            </view>
            <view v-if="todayRecords.length === 0" class="empty-tip">今日暂无收益记录</view>
          </view>
        </view>
      </view>

      <!-- History Tab -->
      <view v-if="currentTab === 'history'">
        <view class="section">
          <view class="section-header">
            <view class="section-title">📜 打工履历</view>
          </view>
          <view class="history-card" v-for="record in allRecords" :key="record.id">
            <view class="history-icon" :style="{ background: record.bgColor }">{{ record.emoji }}</view>
            <view class="history-info">
              <view class="history-name">{{ record.workName }} ✓</view>
              <view class="history-desc">{{ record.dateStr }}</view>
            </view>
            <text style="font-size:22px;">🎉</text>
          </view>
          <view v-if="allRecords.length === 0" class="empty-tip">暂无打工记录</view>
        </view>
      </view>
    </view>

    <view style="height:36px;"></view>

    <!-- Job Modal -->
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
        <button class="modal-start-btn" :disabled="!selectedJob?.unlocked || energy < selectedJob?.energyCost" @click="confirmStartWork">🐾 开始打工</button>
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
      currentTab: 'work',
      energy: 70,
      petMood: 92,
      petInfo: null,
      workStatus: 'idle',
      workTimer: null,
      workSeconds: 0,
      timerDisplay: '00:00',
      timeLeftText: '0分0秒',
      progressPercent: 0,
      currentWorkJob: null,
      completedJob: null,
      quickJobs: [],
      allJobs: [],
      todayRecords: [],
      allRecords: [],
      modalVisible: false,
      selectedJob: null,
      todayTotal: 0,
      todayStr: '',
      stageName: '幼年期'
    }
  },
  computed: {
    petEmoji() {
      const stage = this.petInfo?.stage || 1
      if (stage === 1) return '🐣'
      if (stage === 2) return '🐥'
      return '🦊'
    }
  },
  onLoad() {
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
    switchTab(tab) { this.currentTab = tab },
    loadData() {
      petApi.getInfo().then(data => {
        this.petInfo = data
        this.energy = data.energy || data.health || 70
        this.petMood = data.mood || 90
        const names = { 1: '幼年期', 2: '成长期', 3: '完全体' }
        this.stageName = names[data.stage] || '幼年期'
      }).catch(() => {})

      workApi.getInfo().then(data => {
        if (data.jobs) this.buildJobs(data.jobs)
        if (data.working) this.startWorking(data.working.job, data.working.endTime, false)
        if (data.todayRecords) {
          this.todayRecords = data.todayRecords
          this.todayTotal = data.todayTotal || data.todayRecords.reduce((s, r) => s + (r.reward || 0), 0)
        }
        if (data.records) this.allRecords = data.records
      }).catch(() => {
        this.loadMockData()
      })
    },
    buildJobs(jobs) {
      const bgColors = { '做作业': '#FFF0F5', '打扫房间': '#F0FFF5', '便利店兼职': '#FFF4C2', '外卖派送员': '#FFF0F5', '家教老师': '#F0FFF5', '咖啡馆服务员': '#F5F0FF', '插画师': '#F5F0FF', '主播助理': '#FFFBEF' }
      this.quickJobs = jobs.slice(0, 4).map(j => ({ ...j, bgColor: bgColors[j.name] || '#F9F5FF', energyCost: j.energyCost || 15, exp: j.exp || 10 }))
      this.allJobs = jobs.map(j => ({ ...j, bgColor: bgColors[j.name] || '#F9F5FF', energyCost: j.energyCost || 15, exp: j.exp || 10 }))
      if (this.quickJobs.length > 0 && !this.currentWorkJob) this.currentWorkJob = this.quickJobs[0]
    },
    loadMockData() {
      const mockJobs = [
        { id: 1, emoji: '📚', name: '做作业', sub: '温故知新', reward: 20, duration: 5, energyCost: 10, exp: 5, desc: '认真完成作业~', minLevel: 1, unlocked: true, bgColor: '#FFF0F5' },
        { id: 2, emoji: '🧹', name: '打扫房间', sub: '简单家务', reward: 35, duration: 10, energyCost: 15, exp: 10, desc: '打扫得干干净净！', minLevel: 1, unlocked: true, bgColor: '#F0FFF5' },
        { id: 3, emoji: '🏪', name: '便利店兼职', sub: '城市打工', reward: 60, duration: 20, energyCost: 20, exp: 20, desc: '在便利店帮忙~', minLevel: 5, unlocked: true, bgColor: '#FFF4C2' },
        { id: 4, emoji: '🛵', name: '外卖派送员', sub: '风里来雨里去', reward: 80, duration: 30, energyCost: 25, exp: 30, desc: '穿梭城市送外卖~', minLevel: 5, unlocked: true, bgColor: '#FFF0F5' },
        { id: 5, emoji: '👩‍🏫', name: '家教老师', sub: '知识就是金币', reward: 150, duration: 45, energyCost: 35, exp: 50, desc: '耐心地帮助学习~', minLevel: 10, unlocked: true, bgColor: '#F0FFF5' },
        { id: 6, emoji: '🎨', name: '插画师', sub: '艺术创作', reward: 250, duration: 60, energyCost: 40, exp: 80, desc: '绘制可爱插画~', minLevel: 20, unlocked: false, bgColor: '#F5F0FF' },
        { id: 7, emoji: '🎙️', name: '主播助理', sub: '直播行业', reward: 400, duration: 90, energyCost: 50, exp: 120, desc: '协助直播准备~', minLevel: 30, unlocked: false, bgColor: '#FFFBEF' },
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
        { id: 1, workName: '打扫房间', emoji: '🧹', reward: 35, duration: 10, dateStr: '2026-04-15', bgColor: '#F0FFF5', exp: 10 },
        { id: 2, workName: '便利店兼职', emoji: '🏪', reward: 60, duration: 20, dateStr: '2026-04-15', bgColor: '#FFF4C2', exp: 20 },
        { id: 3, workName: '做作业', emoji: '📚', reward: 20, duration: 5, dateStr: '2026-04-15', bgColor: '#FFF0F5', exp: 5 },
        { id: 4, workName: '外卖派送员', emoji: '🛵', reward: 80, duration: 30, dateStr: '2026-04-14', bgColor: '#FFF0F5', exp: 30 },
      ]
    },
    openJobModal(job) {
      if (!job.unlocked) { uni.showToast({ title: `需要 LV.${job.minLevel}`, icon: 'none' }); return }
      this.selectedJob = job
      this.modalVisible = true
    },
    closeModal() { this.modalVisible = false },
    openQuickWork() {
      const available = this.quickJobs.find(j => j.unlocked && this.energy >= j.energyCost)
      if (available) this.openJobModal(available)
      else uni.showToast({ title: '体力不足', icon: 'none' })
    },
    confirmStartWork() {
      if (!this.selectedJob) return
      if (this.energy < this.selectedJob.energyCost) { uni.showToast({ title: '体力不足！', icon: 'none' }); return }
      this.closeModal()
      const endTime = Date.now() + this.selectedJob.duration * 60000
      this.startWorking(this.selectedJob, endTime, true)
    },
    startWorking(job, endTime, fromApi) {
      this.currentWorkJob = job
      this.workStatus = 'working'
      this.workSeconds = Math.max(0, Math.floor((endTime - Date.now()) / 1000))
      this.updateTimer()
      this.workTimer = setInterval(() => {
        this.workSeconds--
        if (this.workSeconds <= 0) { clearInterval(this.workTimer); this.workStatus = 'completed'; this.completedJob = job }
        else this.updateTimer()
      }, 1000)
    },
    updateTimer() {
      const m = Math.floor(this.workSeconds / 60)
      const s = this.workSeconds % 60
      this.timerDisplay = `${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
      this.timeLeftText = `${m}分${s}秒`
      const total = this.currentWorkJob.duration * 60
      this.progressPercent = Math.round(((total - this.workSeconds) / total) * 100)
    },
    cancelWork() {
      if (this.workTimer) { clearInterval(this.workTimer); this.workTimer = null }
      this.workStatus = 'idle'
      this.workSeconds = 0
      uni.showToast({ title: '已取消打工', icon: 'none' })
    },
    confirmComplete() {
      this.workStatus = 'idle'
      this.completedJob = null
      petApi.getInfo().then(data => { this.energy = data.energy || data.health || 70 }).catch(() => {})
    }
  }
}
</script>

<style scoped>
page {
  --primary: #FFB3C6; --primary-dark: #FF8FAB; --primary-light: #FFD5E5;
  --secondary: #A8D8EA; --yellow: #FFE5A0; --yellow-dark: #FFD166;
  --purple: #D5AAFF; --purple-light: #EDE4FF;
  --green: #B8F1CC; --coral: #FFACA8; --red: #FF6B6B;
  --bg: #FFF8FA; --bg-page: #EEE8F5;
  --text: #4A3F55; --text-secondary: #7A6B8A; --text-muted: #A898B8;
  --radius: 20px; --radius-sm: 12px; --radius-full: 999px;
  --shadow: 0 4px 16px rgba(80, 60, 100, 0.08); --shadow-lg: 0 8px 32px rgba(80, 60, 100, 0.14);
}
.page { min-height: 100vh; background: #FFF9FB; padding-bottom: 36px; }
.top-bar { background: linear-gradient(135deg, #FFB3C6, #D5AAFF); padding: 16px 16px 14px; position: sticky; top: 0; z-index: 50; }
.top-bar-row { display: flex; align-items: flex-start; justify-content: space-between; }
.top-bar-title { font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.top-bar-sub { font-size: 12px; color: rgba(255,255,255,0.8); }
.energy-wrap { display: flex; align-items: center; gap: 6px; background: rgba(255,255,255,0.2); border-radius: 20px; padding: 6px 12px; }
.energy-icon { font-size: 14px; }
.energy-track { width: 50px; height: 8px; background: rgba(255,255,255,0.3); border-radius: 8px; overflow: hidden; }
.energy-fill { height: 100%; background: #FFE5A0; border-radius: 8px; transition: width 0.5s; }
.energy-text { font-size: 12px; color: #fff; font-weight: 600; }
.tab-bar { display: flex; gap: 8px; padding: 12px 16px 0; background: #FFF9FB; position: sticky; top: 88px; z-index: 40; }
.tab-btn { flex: 1; padding: 9px; text-align: center; border-radius: 12px; font-size: 13px; font-weight: 600; border: 1px solid #EDE4FF; background: #fff; color: #7A6B8A; transition: all 0.2s; }
.tab-btn.active { background: #D5AAFF; color: #fff; border-color: #D5AAFF; }
.content { padding: 16px; }
.working-banner { background: #FFF9FB; border: 2px solid #FFD5E5; border-radius: 20px; padding: 20px; margin-bottom: 16px; text-align: center; }
.working-pet-emoji { font-size: 56px; display: block; margin-bottom: 8px; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{transform:scale(1)} 50%{transform:scale(1.08)} }
.working-title { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.working-job { font-size: 13px; color: #7A6B8A; margin-bottom: 10px; }
.working-timer { font-size: 28px; font-weight: 700; color: #D5AAFF; margin-bottom: 10px; }
.progress-track { height: 10px; background: #F0EDF5; border-radius: 10px; overflow: hidden; margin-bottom: 6px; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #FFB3C6, #D5AAFF); border-radius: 10px; transition: width 1s linear; }
.time-left { font-size: 12px; color: #7A6B8A; margin-bottom: 12px; }
.cancel-btn { margin-top: 12px; background: #FFF0F0; color: #FF6B6B; border: 1px solid #FFE5E5; padding: 8px 20px; border-radius: 12px; font-size: 13px; font-weight: 600; }
.complete-banner { background: #FFF9FB; border: 2px solid #FFD5E5; border-radius: 20px; padding: 20px; margin-bottom: 16px; text-align: center; }
.complete-emoji { font-size: 56px; display: block; margin-bottom: 8px; }
.complete-title { font-size: 15px; font-weight: 700; margin-bottom: 4px; }
.complete-job { font-size: 13px; color: #7A6B8A; margin-bottom: 10px; }
.complete-reward { font-size: 28px; font-weight: 700; color: #D4A000; margin-bottom: 8px; }
.complete-exp { font-size: 13px; color: #7A6B8A; margin-bottom: 16px; }
.confirm-btn { background: #F0FFF5; color: #3BAF5D; border: 1px solid #B8F1CC; padding: 8px 20px; border-radius: 12px; font-size: 13px; font-weight: 600; }
.status-card { background: linear-gradient(135deg, #FFB3C6, #D5AAFF); border-radius: 20px; padding: 20px; margin-bottom: 16px; position: relative; overflow: hidden; }
.status-card::before { content: ''; position: absolute; right: -20px; top: -20px; width: 100px; height: 100px; background: rgba(255,255,255,0.1); border-radius: 50%; }
.status-inner { display: flex; align-items: center; gap: 14px; position: relative; z-index: 1; }
.status-avatar { width: 64px; height: 64px; background: rgba(255,255,255,0.25); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 34px; border: 2px solid rgba(255,255,255,0.5); flex-shrink: 0; }
.status-info { flex: 1; color: #fff; }
.status-name { font-size: 17px; font-weight: 700; margin-bottom: 2px; }
.status-type { font-size: 12px; opacity: 0.85; }
.status-stats { font-size: 12px; opacity: 0.9; margin-top: 6px; display: flex; gap: 12px; }
.status-right { display: flex; flex-direction: column; gap: 8px; align-items: center; }
.work-main-btn { background: #fff; color: #D5AAFF; padding: 10px 18px; border-radius: 14px; font-size: 13px; font-weight: 700; border: none; white-space: nowrap; }
.work-hint { font-size: 11px; opacity: 0.8; color: #fff; }
.section { margin-bottom: 20px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.section-title { font-size: 15px; font-weight: 700; display: flex; align-items: center; gap: 6px; color: #4A3F55; }
.section-title::before { content: ''; width: 3px; height: 14px; background: linear-gradient(180deg, #FFB3C6, #D5AAFF); border-radius: 2px; }
.section-more { font-size: 12px; color: #D5AAFF; }
.quick-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.quick-card { background: #fff; border-radius: 16px; padding: 16px; text-align: center; border: 1px solid #F5EEF8; cursor: pointer; transition: all 0.2s; }
.quick-card:active { transform: scale(0.97); }
.quick-card.locked { opacity: 0.55; }
.quick-icon { font-size: 36px; display: block; margin-bottom: 8px; }
.quick-name { font-size: 13px; font-weight: 700; margin-bottom: 4px; }
.quick-reward { font-size: 12px; color: #D4A000; font-weight: 600; margin-bottom: 6px; }
.quick-time { font-size: 11px; color: #7A6B8A; margin-bottom: 8px; }
.quick-btn { width: 100%; padding: 7px; border-radius: 10px; background: linear-gradient(135deg, #FFB3C6, #D5AAFF); color: #fff; font-size: 12px; font-weight: 700; border: none; }
.quick-btn:disabled { opacity: 0.5; }
.job-card { background: #fff; border-radius: 18px; padding: 16px; margin-bottom: 12px; border: 1px solid #F5EEF8; display: flex; align-items: center; gap: 14px; transition: all 0.2s; cursor: pointer; }
.job-card:hover { transform: translateY(-2px); box-shadow: 0 6px 18px rgba(180,150,200,0.13); border-color: #D5AAFF; }
.job-card.locked { opacity: 0.55; cursor: not-allowed; }
.job-icon { width: 50px; height: 50px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 26px; flex-shrink: 0; }
.job-info { flex: 1; }
.job-name { font-size: 14px; font-weight: 700; margin-bottom: 2px; }
.job-desc { font-size: 11px; color: #7A6B8A; margin-bottom: 6px; }
.job-tags { display: flex; gap: 4px; flex-wrap: wrap; }
.job-tag { padding: 2px 8px; background: #F9F5FF; border-radius: 8px; font-size: 10px; color: #D5AAFF; border: 1px solid #EDE4FF; }
.job-tag.hard { background: #FFF4C2; color: #8B6914; border-color: #FFD166; }
.job-reward-side { text-align: right; flex-shrink: 0; }
.job-gold { font-size: 15px; font-weight: 700; display: flex; align-items: center; gap: 3px; justify-content: flex-end; }
.job-time { font-size: 11px; color: #7A6B8A; margin-top: 2px; }
.lock-side { text-align: center; flex-shrink: 0; }
.earnings-card { background: #fff; border-radius: 18px; padding: 18px; margin-bottom: 12px; border: 1px solid #F5EEF8; }
.earnings-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.earnings-title { font-size: 14px; font-weight: 700; }
.earnings-more { font-size: 12px; color: #D5AAFF; }
.earnings-total { text-align: center; padding: 16px 0; border-top: 1px solid #F5EEF8; border-bottom: 1px solid #F5EEF8; margin-bottom: 12px; }
.earnings-total-label { font-size: 12px; color: #7A6B8A; margin-bottom: 4px; }
.earnings-total-value { font-size: 32px; font-weight: 700; color: #D4A000; display: flex; align-items: center; justify-content: center; gap: 6px; }
.earnings-list { display: flex; flex-direction: column; gap: 10px; }
.earnings-item { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-top: 1px solid #F5EEF8; }
.earnings-icon { font-size: 20px; }
.earnings-info { flex: 1; }
.earnings-job { font-size: 13px; font-weight: 600; }
.earnings-date { font-size: 11px; color: #A898B8; }
.earnings-amount { font-size: 14px; font-weight: 700; color: #D4A000; }
.history-card { background: #fff; border-radius: 18px; padding: 16px; margin-bottom: 12px; border: 1px solid #F5EEF8; display: flex; align-items: center; gap: 14px; }
.history-icon { width: 50px; height: 50px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 26px; flex-shrink: 0; }
.history-info { flex: 1; }
.history-name { font-size: 14px; font-weight: 700; margin-bottom: 2px; }
.history-desc { font-size: 11px; color: #7A6B8A; }
.empty-tip { text-align: center; font-size: 13px; color: #A898B8; padding: 30px 0; }
.modal-overlay { position: fixed; inset: 0; background: rgba(74,63,85,0.4); backdrop-filter: blur(4px); display: flex; align-items: flex-end; justify-content: center; z-index: 200; opacity: 0; pointer-events: none; transition: opacity 0.3s; }
.modal-overlay.show { opacity: 1; pointer-events: all; }
.modal { background: #fff; border-radius: 24px 24px 0 0; width: 100%; max-height: 85vh; overflow-y: auto; padding: 28px 24px 40px; transform: translateY(40px); transition: transform 0.3s; }
.modal-overlay.show .modal { transform: translateY(0); }
.modal-handle { width: 40px; height: 4px; background: #E0E0E0; border-radius: 2px; margin: 0 auto 20px; }
.modal-emoji { font-size: 64px; text-align: center; margin-bottom: 12px; display: block; }
.modal-title { font-size: 20px; font-weight: 700; text-align: center; margin-bottom: 6px; }
.modal-sub { font-size: 13px; color: #7A6B8A; text-align: center; margin-bottom: 20px; }
.modal-stats { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 20px; }
.modal-stat { background: #F9F5FF; border-radius: 14px; padding: 14px; text-align: center; }
.modal-stat-icon { font-size: 22px; display: block; margin-bottom: 4px; }
.modal-stat-val { font-size: 18px; font-weight: 700; }
.modal-stat-label { font-size: 11px; color: #7A6B8A; }
.modal-desc { font-size: 13px; color: #7A6B8A; line-height: 1.6; margin-bottom: 20px; }
.modal-start-btn { width: 100%; padding: 14px; border-radius: 16px; background: linear-gradient(135deg, #FFB3C6, #D5AAFF); color: #fff; font-size: 15px; font-weight: 700; border: none; }
.modal-start-btn:disabled { opacity: 0.5; }
.modal-cancel { text-align: center; font-size: 12px; color: #A898B8; margin-top: 10px; }
</style>
