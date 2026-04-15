<template>
  <view class="page">
    <!-- Header -->
    <view class="header">
      <view class="header-title">养成中心</view>
      <view class="gold-chip">
        <text style="font-size:28rpx;">💰</text>
        <text class="gold-value">{{ petInfo?.gold || userInfo?.gold || 0 }}</text>
      </view>
    </view>

    <!-- Pet Display -->
    <view class="pet-display">
      <view class="pet-stage">
        <view class="pet-sprite">{{ petEmoji }}</view>
        <view class="pet-badge">⭐ Lv.{{ petInfo?.level || 1 }}</view>
      </view>
    </view>

    <!-- Status Bars -->
    <view class="status-section">
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
        </view>
      </view>
    </view>

    <!-- Care Actions -->
    <view class="care-section">
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
        <view class="care-card" @click="toWork">
          <view class="care-icon" style="background:rgba(184,241,204,0.3);">💼</view>
          <view class="care-title">打工</view>
          <view class="care-desc">宠物外出赚钱</view>
          <view class="care-reward work">💰 金币+50~200</view>
          <button class="care-btn work">安排打工</button>
        </view>
      </view>
    </view>

    <!-- Evolution Preview -->
    <view class="evo-section">
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
    </view>

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
      actionDone: false
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
        play: { title: '玩耍中...', desc: '小福玩得好开心~ 🎾', complete: '✨ 完成！心情+30', api: 'play' }
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
    toWork() {
      uni.navigateTo({ url: '/pages/work/work' })
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #F5EEF8;
  padding-bottom: 140rpx;
}

/* Header */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx 16rpx;
}
.header-title { font-size: 32rpx; font-weight: 700; color: #4A3F55; }
.gold-chip {
  display: flex; align-items: center; gap: 8rpx;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16rpx);
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.gold-value { font-size: 28rpx; font-weight: 700; color: #4A3F55; }

/* Pet Display */
.pet-display { display: flex; justify-content: center; padding: 0 32rpx 24rpx; }
.pet-stage {
  width: 280rpx; height: 280rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  display: flex; align-items: center; justify-content: center;
  position: relative;
  box-shadow: 0 8rpx 48rpx rgba(255,179,198,0.35);
  animation: petGlow 3s ease-in-out infinite;
}
@keyframes petGlow {
  0%, 100% { box-shadow: 0 8rpx 48rpx rgba(255,179,198,0.35); }
  50% { box-shadow: 0 8rpx 56rpx rgba(213,170,255,0.45); }
}
.pet-sprite { font-size: 160rpx; animation: petBreathe 2s ease-in-out infinite; }
@keyframes petBreathe { 0%,100%{transform:translateY(0) scale(1);} 50%{transform:translateY(-8rpx) scale(1.02);} }
.pet-badge {
  position: absolute; top: 16rpx; right: 16rpx;
  background: #FFE5A0; color: #4A3F55;
  font-size: 20rpx; font-weight: 700;
  padding: 6rpx 16rpx; border-radius: 999rpx;
  box-shadow: 0 2rpx 8rpx rgba(255,209,102,0.4);
}

/* Status Bars */
.status-section { padding: 0 32rpx; }
.status-card {
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 24rpx rgba(80,60,100,0.08);
}
.status-row { display: flex; flex-direction: column; gap: 20rpx; }
.status-item { display: flex; align-items: center; gap: 16rpx; }
.status-icon-wrap {
  width: 64rpx; height: 64rpx; border-radius: 16rpx;
  display: flex; align-items: center; justify-content: center; font-size: 32rpx; flex-shrink: 0;
}
.status-info { flex: 1; min-width: 0; }
.status-label-row { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.status-name { font-size: 24rpx; font-weight: 600; color: #7A6B8A; }
.status-num { font-size: 24rpx; font-weight: 700; color: #4A3F55; }
.status-bar { height: 16rpx; border-radius: 8rpx; background: rgba(0,0,0,0.07); overflow: hidden; }
.status-fill { height: 100%; border-radius: 8rpx; transition: width 600ms ease; }
.status-fill.health { background: linear-gradient(90deg, #A8D8EA, #7EC8D8); }
.status-fill.fullness { background: linear-gradient(90deg, #FFE5A0, #FFD166); }
.status-fill.mood { background: linear-gradient(90deg, #FFB3C6, #FF8FAB); }
.status-fill.low { background: linear-gradient(90deg, #FFACA8, #FF6B6B) !important; animation: pulseFill 1s ease-in-out infinite; }
@keyframes pulseFill { 0%,100%{opacity:1;} 50%{opacity:0.7;} }

/* Care Grid */
.care-section { padding: 32rpx 32rpx 16rpx; }
.section-title { font-size: 30rpx; font-weight: 700; color: #4A3F55; margin-bottom: 20rpx; }
.care-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20rpx; }
.care-card {
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(24rpx);
  border-radius: 28rpx;
  padding: 28rpx 20rpx;
  display: flex; flex-direction: column; align-items: center; gap: 8rpx;
  box-shadow: 0 4rpx 24rpx rgba(80,60,100,0.08);
}
.care-icon {
  width: 96rpx; height: 96rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; font-size: 52rpx; margin-bottom: 4rpx;
}
.care-title { font-size: 30rpx; font-weight: 700; color: #4A3F55; }
.care-desc { font-size: 22rpx; color: #A898B8; text-align: center; }
.care-reward {
  display: flex; align-items: center; gap: 4rpx;
  padding: 6rpx 16rpx; border-radius: 999rpx;
  font-size: 22rpx; font-weight: 600;
}
.care-reward.feed { background: rgba(255,224,160,0.3); color: #C49A1A; }
.care-reward.clean { background: rgba(168,216,234,0.3); color: #4A8FA0; }
.care-reward.play { background: rgba(213,170,255,0.3); color: #9060C0; }
.care-reward.work { background: rgba(184,241,204,0.3); color: #5DA06B; }
.care-btn {
  width: 100%; height: 64rpx;
  border-radius: 999rpx;
  border: none;
  font-size: 26rpx; font-weight: 700;
  font-family: inherit;
  margin-top: 8rpx;
}
.care-btn.feed { background: linear-gradient(135deg, #FFE5A0, #FFD166); color: #6B4A00; }
.care-btn.clean { background: linear-gradient(135deg, #A8D8EA, #7EC8D8); color: #1A5F72; }
.care-btn.play { background: linear-gradient(135deg, #D5AAFF, #B080E0); color: white; }
.care-btn.work { background: linear-gradient(135deg, #B8F1CC, #8FD8A0); color: #2D6B3F; }

/* Evolution */
.evo-section { padding: 24rpx 32rpx 16rpx; }
.evo-card {
  background: linear-gradient(135deg, rgba(255,224,160,0.3), rgba(213,170,255,0.3));
  border-radius: 28rpx;
  padding: 24rpx;
  display: flex; align-items: center; gap: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(80,60,100,0.08);
}
.evo-stage {
  width: 100rpx; height: 100rpx; border-radius: 50%;
  background: rgba(255,255,255,0.8);
  display: flex; align-items: center; justify-content: center; font-size: 56rpx; flex-shrink: 0;
  border: 3rpx solid rgba(255,255,255,0.9);
}
.evo-info { flex: 1; }
.evo-title { font-size: 28rpx; font-weight: 700; color: #4A3F55; margin-bottom: 6rpx; }
.evo-desc { font-size: 24rpx; color: #7A6B8A; margin-bottom: 12rpx; }
.evo-progress-bar { height: 12rpx; border-radius: 6rpx; background: rgba(0,0,0,0.1); overflow: hidden; }
.evo-progress-fill { height: 100%; border-radius: 6rpx; background: linear-gradient(90deg, #FFE5A0, #D5AAFF); transition: width 600ms ease; }
.evo-progress-label { font-size: 20rpx; color: #A898B8; margin-top: 4rpx; }

/* Action Overlay */
.action-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 999; opacity: 0; pointer-events: none; transition: opacity 200ms;
}
.action-overlay.show { opacity: 1; pointer-events: all; }
.action-modal {
  background: #FFFFFF; border-radius: 32rpx;
  padding: 48rpx 40rpx; width: 480rpx;
  text-align: center;
  animation: modalIn 300ms cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes modalIn { from { opacity: 0; transform: scale(0.8) translateY(30rpx); } to { opacity: 1; transform: scale(1) translateY(0); } }
.action-pet-big { font-size: 160rpx; margin-bottom: 16rpx; }
.action-title { font-size: 32rpx; font-weight: 700; color: #4A3F55; margin-bottom: 8rpx; }
.action-desc { font-size: 26rpx; color: #7A6B8A; margin-bottom: 24rpx; }
.action-progress { height: 12rpx; border-radius: 6rpx; background: rgba(0,0,0,0.07); margin-bottom: 16rpx; overflow: hidden; }
.action-progress-fill { height: 100%; border-radius: 6rpx; background: linear-gradient(90deg, #FFB3C6, #D5AAFF); transition: width 200ms ease; }
.action-complete { font-size: 26rpx; font-weight: 700; color: #5DA06B; display: none; }
.action-complete.show { display: block; }
</style>
