<template>
  <view class="page">
    <!-- Header -->
    <header class="header">
      <button class="back-btn" @click="goBack">
        <svg fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><polyline points="15 18 9 12 15 6"/></svg>
      </button>
      <text class="header-title">养成中心</text>
      <view class="header-coins">
        <text class="coin-icon">💰</text>
        <text class="coin-value">{{ petInfo?.gold || userInfo?.gold || 0 }}</text>
      </view>
    </header>

    <!-- Pet Display -->
    <view class="pet-display">
      <view class="pet-stage">
        <view class="pet-sprite">{{ petEmoji }}</view>
        <view class="pet-badge">⭐ Lv.{{ petInfo?.level || 1 }}</view>
      </view>
    </view>

    <!-- Status Bars -->
    <section class="status-section">
      <view class="status-card">
        <view class="status-row">
          <view class="status-item">
            <view class="status-icon-wrap" style="background:rgba(168,216,234,0.3);">⚡</view>
            <view class="status-info">
              <view class="status-label-row">
                <text class="status-name">体力</text>
                <text class="status-num">{{ health }}/100</text>
              </view>
              <view class="status-bar">
                <view class="status-fill health" :class="{ low: health < 20 }" :style="{ width: health + '%' }"></view>
              </view>
            </view>
          </view>
          <view class="status-item">
            <view class="status-icon-wrap" style="background:rgba(255,224,160,0.3);">🍎</view>
            <view class="status-info">
              <view class="status-label-row">
                <text class="status-name">饱食</text>
                <text class="status-num">{{ fullness }}/100</text>
              </view>
              <view class="status-bar">
                <view class="status-fill fullness" :class="{ low: fullness < 20 }" :style="{ width: fullness + '%' }"></view>
              </view>
            </view>
          </view>
          <view class="status-item">
            <view class="status-icon-wrap" style="background:rgba(255,179,198,0.3);">❤️</view>
            <view class="status-info">
              <view class="status-label-row">
                <text class="status-name">心情</text>
                <text class="status-num">{{ petMood }}/100</text>
              </view>
              <view class="status-bar">
                <view class="status-fill mood" :class="{ low: petMood < 20 }" :style="{ width: petMood + '%' }"></view>
              </view>
            </view>
          </view>
          <view class="status-item">
            <view class="status-icon-wrap" style="background:rgba(213,170,255,0.3);">🤝</view>
            <view class="status-info">
              <view class="status-label-row">
                <text class="status-name">清洁度</text>
                <text class="status-num">{{ clean }}/100</text>
              </view>
              <view class="status-bar">
                <view class="status-fill clean" :class="{ low: clean < 20 }" :style="{ width: clean + '%' }"></view>
              </view>
            </view>
          </view>
          <view class="status-item">
            <view class="status-icon-wrap" style="background:rgba(184,241,204,0.3);">💕</view>
            <view class="status-info">
              <view class="status-label-row">
                <text class="status-name">亲密度</text>
                <text class="status-num">{{ intimacy }}/100</text>
              </view>
              <view class="status-bar">
                <view class="status-fill intimacy" :style="{ width: intimacy + '%' }"></view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </section>

    <!-- Care Actions -->
    <section class="care-section">
      <view class="section-title">日常照料</view>
      <view class="care-grid">
        <view class="care-card">
          <view class="care-icon" style="background:rgba(255,224,160,0.3);">🍖</view>
          <view class="care-title">喂食</view>
          <view class="care-desc">给宠物喂好吃的</view>
          <view class="care-reward feed">🍎 饱食+25</view>
          <button class="care-btn feed" @click="doCare('feed')">开始喂食</button>
        </view>
        <view class="care-card">
          <view class="care-icon" style="background:rgba(168,216,234,0.3);">🚿</view>
          <view class="care-title">清洁</view>
          <view class="care-desc">洗香香变干净</view>
          <view class="care-reward clean">⚡ 体力-5 · 心情+10</view>
          <button class="care-btn clean" @click="doCare('bath')">开始清洁</button>
        </view>
        <view class="care-card">
          <view class="care-icon" style="background:rgba(213,170,255,0.3);">🎾</view>
          <view class="care-title">陪玩</view>
          <view class="care-desc">和宠物一起玩</view>
          <view class="care-reward play">❤️ 心情+30</view>
          <button class="care-btn play" @click="doCare('play')">开始陪玩</button>
        </view>
        <view class="care-card">
          <view class="care-icon" style="background:rgba(184,241,204,0.3);">💤</view>
          <view class="care-title">休息</view>
          <view class="care-desc">恢复体力</view>
          <view class="care-reward rest">⚡ 体力+40</view>
          <button class="care-btn rest" @click="doCare('rest')">开始休息</button>
        </view>
      </view>
    </section>

    <!-- Evolution Preview -->
    <section class="evo-section">
      <view class="section-title">进化预览</view>
      <view class="evo-card">
        <view class="evo-stage">{{ nextStageEmoji }}</view>
        <view class="evo-info">
          <view class="evo-title">下一阶段：{{ nextStageName }}</view>
          <view class="evo-desc">再获得 {{ remainingExp }} 经验值即可进化！</view>
          <view class="evo-progress-bar">
            <view class="evo-progress-fill" :style="{ width: evoProgress + '%' }"></view>
          </view>
          <view class="evo-progress-label">{{ currentExp }} / {{ totalExp }} EXP</view>
        </view>
      </view>
    </section>

    <!-- Growth Timeline -->
    <section class="timeline-section">
      <view class="section-title">成长记录</view>
      <view class="timeline-list">
        <view class="timeline-item" v-for="record in growthRecords" :key="record.id">
          <view class="timeline-dot" :style="{ background: record.color }"></view>
          <view class="timeline-content">
            <text class="timeline-title">{{ record.title }}</text>
            <text class="timeline-date">{{ record.date }}</text>
          </view>
          <text class="timeline-exp">+{{ record.exp }} EXP</text>
        </view>
      </view>
    </section>

    <!-- Action Overlay -->
    <view class="action-overlay" :class="{ show: actionVisible }">
      <view class="action-modal">
        <view class="action-pet-big">{{ petEmoji }}</view>
        <view class="action-title">{{ actionTitle }}</view>
        <view class="action-desc">{{ actionDesc }}</view>
        <view class="action-progress">
          <view class="action-progress-fill" :style="{ width: actionProgress + '%' }"></view>
        </view>
        <view class="action-complete" :class="{ show: actionDone }">{{ actionCompleteText }}</view>
      </view>
    </view>
  </view>
</template>

<script>
import { pet as petApi, getUserInfo } from '../../utils/api.js'

export default {
  data() {
    return {
      petInfo: null,
      userInfo: null,
      actionVisible: false,
      actionTitle: '',
      actionDesc: '',
      actionCompleteText: '',
      actionProgress: 0,
      actionDone: false,
      growthRecords: [
        { id: 1, title: '完成首次喂食', date: '4月12日', exp: 10, color: '#FFE5A0' },
        { id: 2, title: '陪玩获得开心笑容', date: '4月13日', exp: 20, color: '#D5AAFF' },
        { id: 3, title: '洗澡后毛发变亮', date: '4月14日', exp: 15, color: '#A8D8EA' },
        { id: 4, title: '成功打工赚取金币', date: '4月15日', exp: 50, color: '#B8F1CC' },
        { id: 5, title: '解锁新技能', date: '4月16日', exp: 100, color: '#FFB3C6' }
      ]
    }
  },
  computed: {
    petEmoji() {
      const stage = this.petInfo?.stage || 1
      if (stage === 1) return '🐣'
      if (stage === 2) return '🐥'
      return '🦊'
    },
    health() { return this.petInfo?.health || 0 },
    fullness() { return this.petInfo?.fullness || 0 },
    petMood() { return this.petInfo?.mood || 0 },
    clean() { return this.petInfo?.clean || 80 },
    intimacy() { return this.petInfo?.intimacy || 50 },
    currentExp() { return this.petInfo?.exp || 0 },
    totalExp() { return this.petInfo?.nextEvolutionExp || 100 },
    remainingExp() { return Math.max(0, this.totalExp - this.currentExp) },
    evoProgress() { return Math.min(100, (this.currentExp / this.totalExp) * 100) },
    nextStageName() {
      const names = { 1: '成长期', 2: '完全体', 3: '究极体' }
      return names[this.petInfo?.stage || 1] || '成长期'
    },
    nextStageEmoji() {
      const emojis = { 1: '🐥', 2: '🦊', 3: '🔮' }
      return emojis[this.petInfo?.stage || 1] || '🐥'
    }
  },
  onShow() {
    this.userInfo = getUserInfo()
    this.loadPetInfo()
  },
  methods: {
    loadPetInfo() {
      petApi.getInfo().then(data => {
        this.petInfo = data
      }).catch(() => {})
    },
    doCare(type) {
      const configs = {
        feed: { title: '喂食中...', desc: '小福正在享用美食~ 🍖', complete: '✨ 完成！饱食+25', api: 'feed' },
        bath: { title: '洗澡中...', desc: '小福正在洗香香~ 🚿', complete: '✨ 完成！心情+10', api: 'bath' },
        play: { title: '玩耍中...', desc: '小福玩得好开心~ 🎾', complete: '✨ 完成！心情+30', api: 'play' },
        rest: { title: '休息中...', desc: '小福正在好好休息~ 💤', complete: '✨ 完成！体力+40', api: 'rest' }
      }
      const cfg = configs[type]
      if (!cfg) return

      this.actionTitle = cfg.title
      this.actionDesc = cfg.desc
      this.actionCompleteText = cfg.complete
      this.actionProgress = 0
      this.actionDone = false
      this.actionVisible = true

      petApi[cfg.api]().then(data => {
        this.petInfo = data
      }).catch(() => {})

      let progress = 0
      const interval = setInterval(() => {
        progress += 4
        this.actionProgress = Math.min(progress, 100)
        if (progress >= 100) {
          clearInterval(interval)
          this.actionDone = true
          this.actionTitle = this.actionCompleteText
          this.actionDesc = '🎉'
          setTimeout(() => { this.actionVisible = false }, 1200)
        }
      }, 80)
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
page {
  --primary: #FFB3C6;
  --primary-dark: #FF8FAB;
  --primary-light: #FFD5E5;
  --secondary: #A8D8EA;
  --secondary-dark: #7EC8D8;
  --secondary-light: #D5E8F0;
  --yellow: #FFE5A0;
  --yellow-dark: #FFD166;
  --yellow-light: #FFF4C2;
  --purple: #D5AAFF;
  --purple-light: #EDE4FF;
  --green: #B8F1CC;
  --coral: #FFACA8;
  --red: #FF6B6B;
  --bg: #FFF8FA;
  --bg-page: #EEE8F5;
  --text: #4A3F55;
  --text-secondary: #7A6B8A;
  --text-muted: #A898B8;
  --radius: 20px;
  --radius-sm: 12px;
  --radius-full: 999px;
  --shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
  --shadow-lg: 0 8px 32px rgba(80, 60, 100, 0.14);
}

.page {
  min-height: 100vh;
  background: #EEE8F5;
  padding-bottom: 140rpx;
}

/* Header */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px 8px;
}
.back-btn {
  width: 34px; height: 34px;
  border-radius: 12px;
  border: none; background: rgba(255,255,255,0.8); backdrop-filter: blur(8px);
  color: #7A6B8A; display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 150ms;
  padding: 0;
}
.back-btn:active { transform: scale(0.92); background: #FFD5E5; }
.back-btn svg { width: 18px; height: 18px; }
.header-title { font-size: 16px; font-weight: 700; color: #4A3F55; }
.header-coins {
  display: flex; align-items: center; gap: 6px;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(8px);
  padding: 6px 12px; border-radius: 999px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.coin-icon { font-size: 16px; }
.coin-value { font-size: 14px; font-weight: 700; color: #4A3F55; }

/* Pet Display */
.pet-display { display: flex; justify-content: center; padding: 0 16px 12px; }
.pet-stage {
  width: 180px; height: 180px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  display: flex; align-items: center; justify-content: center;
  position: relative;
  box-shadow: 0 8px 32px rgba(255,179,198,0.3);
  animation: petGlow 3s ease-in-out infinite;
}
@keyframes petGlow {
  0%, 100% { box-shadow: 0 8px 32px rgba(255,179,198,0.3); }
  50% { box-shadow: 0 8px 40px rgba(213,170,255,0.4); }
}
.pet-sprite { font-size: 100px; animation: petBreathe 2s ease-in-out infinite; }
@keyframes petBreathe { 0%,100%{transform:translateY(0) scale(1);} 50%{transform:translateY(-5px) scale(1.02);} }
.pet-badge {
  position: absolute; top: 8px; right: 8px;
  background: #FFE5A0; color: #4A3F55;
  font-size: 10px; font-weight: 700;
  padding: 3px 8px; border-radius: 999px;
  box-shadow: 0 2px 6px rgba(255,209,102,0.4);
}

/* Status Bars */
.status-section { padding: 0 16px 8px; }
.status-card {
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 14px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.status-row { display: flex; flex-direction: column; gap: 10px; }
.status-item { display: flex; align-items: center; gap: 10px; }
.status-icon-wrap {
  width: 32px; height: 32px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center; font-size: 16px; flex-shrink: 0;
}
.status-info { flex: 1; min-width: 0; }
.status-label-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
.status-name { font-size: 12px; font-weight: 600; color: #7A6B8A; }
.status-num { font-size: 12px; font-weight: 700; color: #4A3F55; }
.status-bar { height: 10px; border-radius: 5px; background: rgba(0,0,0,0.07); overflow: hidden; }
.status-fill { height: 100%; border-radius: 5px; transition: width 600ms ease; }
.status-fill.health { background: linear-gradient(90deg, #A8D8EA, #7EC8D8); }
.status-fill.fullness { background: linear-gradient(90deg, #FFE5A0, #FFD166); }
.status-fill.mood { background: linear-gradient(90deg, #FFB3C6, #FF8FAB); }
.status-fill.clean { background: linear-gradient(90deg, #D5AAFF, #B080E0); }
.status-fill.intimacy { background: linear-gradient(90deg, #FFACA8, #FF8FAB); }
.status-fill.low { background: linear-gradient(90deg, #FFACA8, #FF6B6B) !important; animation: pulseFill 1s ease-in-out infinite; }
@keyframes pulseFill { 0%,100%{opacity:1;} 50%{opacity:0.7;} }

/* Care Actions */
.care-section { padding: 16px 16px 8px; }
.section-title { font-size: 15px; font-weight: 700; color: #4A3F55; margin-bottom: 12px; }
.care-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.care-card {
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 18px 16px;
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.care-icon {
  width: 60px; height: 60px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; font-size: 30px; margin-bottom: 4px;
}
.care-title { font-size: 15px; font-weight: 700; color: #4A3F55; }
.care-desc { font-size: 11px; color: #A898B8; text-align: center; }
.care-reward {
  display: flex; align-items: center; gap: 3px;
  padding: 3px 10px; border-radius: 999px;
  font-size: 11px; font-weight: 600;
}
.care-reward.feed { background: rgba(255,224,160,0.3); color: #C49A1A; }
.care-reward.clean { background: rgba(168,216,234,0.3); color: #4A8FA0; }
.care-reward.play { background: rgba(213,170,255,0.3); color: #9060C0; }
.care-reward.rest { background: rgba(184,241,204,0.3); color: #5DA06B; }
.care-btn {
  width: 100%; height: 36px;
  border-radius: 999px;
  border: none;
  font-size: 13px; font-weight: 700;
  cursor: pointer; transition: all 150ms;
  font-family: inherit;
  margin-top: 8rpx;
}
.care-btn.feed { background: linear-gradient(135deg, #FFE5A0, #FFD166); color: #6B4A00; }
.care-btn.clean { background: linear-gradient(135deg, #A8D8EA, #7EC8D8); color: #1A5F72; }
.care-btn.play { background: linear-gradient(135deg, #D5AAFF, #B080E0); color: white; }
.care-btn.rest { background: linear-gradient(135deg, #B8F1CC, #8FD8A0); color: #2D6B3F; }
.care-btn:active { transform: scale(0.96); filter: brightness(0.92); }
.care-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* Evolution */
.evo-section { padding: 16px 16px 8px; }
.evo-card {
  background: linear-gradient(135deg, rgba(255,224,160,0.3), rgba(213,170,255,0.3));
  border-radius: 20px;
  padding: 16px;
  display: flex; align-items: center; gap: 16px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.evo-stage {
  width: 64px; height: 64px; border-radius: 50%;
  background: rgba(255,255,255,0.8);
  display: flex; align-items: center; justify-content: center; font-size: 36px; flex-shrink: 0;
  border: 2px solid rgba(255,255,255,0.9);
}
.evo-info { flex: 1; }
.evo-title { font-size: 14px; font-weight: 700; color: #4A3F55; margin-bottom: 4px; }
.evo-desc { font-size: 12px; color: #7A6B8A; margin-bottom: 8px; }
.evo-progress-bar { height: 6px; border-radius: 3px; background: rgba(0,0,0,0.1); overflow: hidden; }
.evo-progress-fill { height: 100%; border-radius: 3px; background: linear-gradient(90deg, #FFE5A0, #D5AAFF); transition: width 600ms ease; }
.evo-progress-label { font-size: 10px; color: #A898B8; margin-top: 3px; }

/* Growth Timeline */
.timeline-section { padding: 16px 16px 8px; }
.timeline-list { display: flex; flex-direction: column; gap: 8px; }
.timeline-item {
  display: flex; align-items: center; gap: 12px;
  background: rgba(255,255,255,0.9);
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 2px 8px rgba(80, 60, 100, 0.06);
}
.timeline-dot {
  width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0;
}
.timeline-content { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.timeline-title { font-size: 13px; font-weight: 600; color: #4A3F55; }
.timeline-date { font-size: 11px; color: #A898B8; }
.timeline-exp { font-size: 12px; font-weight: 700; color: #D4A000; }

/* Action Overlay */
.action-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 999; opacity: 0; pointer-events: none; transition: opacity 200ms;
}
.action-overlay.show { opacity: 1; pointer-events: all; }
.action-modal {
  background: #FFFFFF; border-radius: 20px;
  padding: 24px; width: 280px;
  text-align: center;
  animation: modalIn 300ms cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes modalIn { from { opacity: 0; transform: scale(0.8) translateY(30px); } to { opacity: 1; transform: scale(1) translateY(0); } }
.action-pet-big { font-size: 80px; margin-bottom: 12px; }
.action-title { font-size: 16px; font-weight: 700; color: #4A3F55; margin-bottom: 4px; }
.action-desc { font-size: 13px; color: #7A6B8A; margin-bottom: 16px; }
.action-progress { height: 8px; border-radius: 4px; background: rgba(0,0,0,0.07); margin-bottom: 12px; overflow: hidden; }
.action-progress-fill { height: 100%; border-radius: 4px; background: linear-gradient(90deg, #FFB3C6, #D5AAFF); transition: width 200ms ease; }
.action-complete { font-size: 13px; font-weight: 700; color: #5DA06B; display: none; }
.action-complete.show { display: block; }
</style>
