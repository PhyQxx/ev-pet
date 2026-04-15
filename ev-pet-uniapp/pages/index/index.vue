<template>
  <view class="page">
    <!-- 顶部状态栏 -->
    <view class="status-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-left">
        <view class="pet-avatar">🦊</view>
        <view class="pet-info">
          <text class="pet-name">{{ petInfo?.name || '小可爱' }} 🏅</text>
          <text class="pet-level">Lv.{{ petInfo?.level || 1 }} · {{ stageName }}</text>
        </view>
      </view>
      <view class="header-right">
        <view class="icon-btn" @click="toAchievement">
          <text style="font-size:18px;">🔔</text>
        </view>
        <view class="gold-chip">
          <text style="font-size:14px;">💰</text>
          <text class="gold-num">{{ petInfo?.gold || userInfo?.gold || 0 }}</text>
        </view>
      </view>
    </view>

    <!-- 宠物展示区域 -->
    <view class="pet-stage">
      <view class="pet-bg"></view>
      <view class="pet-container">
        <view class="pet-sprite" :class="petStatusClass">{{ petEmoji }}</view>
        <view class="pet-mood-text">主人~ 我现在 <text class="mood-highlight">{{ moodText }}</text> 哦~ ✨</view>
      </view>
    </view>

    <!-- 属性条 -->
    <view class="status-section">
      <view class="status-card">
        <view class="status-row">
          <view class="status-item">
            <view class="status-label">
              <text class="status-icon">⚡</text>
              <text>体力</text>
            </view>
            <view class="status-bar-wrap">
              <view class="status-bar">
                <view class="status-bar-fill health" :class="{ low: health < 20 }" :style="{ width: health + '%' }"></view>
              </view>
              <text class="status-value">{{ health }}</text>
            </view>
          </view>
          <view class="status-item">
            <view class="status-label">
              <text class="status-icon">🍎</text>
              <text>饱食</text>
            </view>
            <view class="status-bar-wrap">
              <view class="status-bar">
                <view class="status-bar-fill fullness" :class="{ low: fullness < 20 }" :style="{ width: fullness + '%' }"></view>
              </view>
              <text class="status-value">{{ fullness }}</text>
            </view>
          </view>
          <view class="status-item">
            <view class="status-label">
              <text class="status-icon">❤️</text>
              <text>心情</text>
            </view>
            <view class="status-bar-wrap">
              <view class="status-bar">
                <view class="status-bar-fill mood" :class="{ low: petMood < 20 }" :style="{ width: petMood + '%' }"></view>
              </view>
              <text class="status-value">{{ petMood }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷操作 -->
    <view class="action-section">
      <view class="section-title">日常照料</view>
      <view class="action-grid">
        <view class="action-btn" @click="doAction('feed')">
          <view class="action-icon" style="background:rgba(255,224,160,0.3);">🍖</view>
          <text class="action-label">喂食</text>
          <text class="action-count">饱食+25</text>
        </view>
        <view class="action-btn" @click="doAction('bath')">
          <view class="action-icon" style="background:rgba(168,216,234,0.3);">🚿</view>
          <text class="action-label">清洁</text>
          <text class="action-count">体力-5</text>
        </view>
        <view class="action-btn" @click="doAction('play')">
          <view class="action-icon" style="background:rgba(213,170,255,0.3);">🎾</view>
          <text class="action-label">陪玩</text>
          <text class="action-count">心情+30</text>
        </view>
        <view class="action-btn" @click="toWork">
          <view class="action-icon" style="background:rgba(184,241,204,0.3);">💼</view>
          <text class="action-label">打工</text>
          <text class="action-count">金币+50</text>
        </view>
      </view>
    </view>

    <!-- 宠物好友 -->
    <view class="friends-section">
      <view class="section-title">宠物好友</view>
      <scroll-view class="friends-scroll" scroll-x>
        <view class="friend-chip" v-for="f in mockFriends" :key="f.name">
          <view class="friend-avatar">{{ f.emoji }}</view>
          <text class="friend-name">{{ f.name }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 今日任务 -->
    <view class="quest-section">
      <view class="section-title">今日任务</view>
      <view class="quest-card">
        <view class="quest-header">
          <view class="quest-title">🌟 每日活跃</view>
          <view class="quest-progress">3/5 完成</view>
        </view>
        <view class="quest-list">
          <view class="quest-item" v-for="q in quests" :key="q.text">
            <text class="quest-icon">{{ q.icon }}</text>
            <text class="quest-text">{{ q.text }}</text>
            <text class="quest-reward">💰+{{ q.reward }}</text>
            <view class="quest-check" :style="q.done ? 'background:#B8F1CC;' : 'background:#F0F0F0;'">
              <text v-if="q.done" style="color:white;font-size:10px;">✓</text>
              <text v-else style="font-size:10px;color:#888;">0</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 养成操作遮罩 -->
    <view class="action-overlay" :class="{ show: actionVisible }">
      <view class="action-modal">
        <view class="action-pet-big">{{ petEmoji }}</view>
        <view class="action-title">{{ actionTitle }}</view>
        <view class="action-desc">{{ actionDesc }}</view>
        <view class="action-progress">
          <view class="action-progress-fill" :style="{ width: actionProgress + '%' }"></view>
        </view>
        <view class="action-complete" :class="{ show: actionDone }">{{ actionComplete }}</view>
      </view>
    </view>
  </view>
</template>

<script>
import { pet as petApi, getUserInfo } from '../../utils/api.js'

export default {
  data() {
    return {
      statusBarHeight: 0,
      userInfo: null,
      petInfo: null,
      timer: null,
      actionVisible: false,
      actionTitle: '',
      actionDesc: '',
      actionComplete: '',
      actionProgress: 0,
      actionDone: false,
      stageName: '幼年期',
      mockFriends: [
        { emoji: '🐰', name: '兔小白' },
        { emoji: '🐱', name: '喵呜' },
        { emoji: '🐻', name: '小熊' },
        { emoji: '🦄', name: '独角兽' },
        { emoji: '🐨', name: '考拉' }
      ],
      quests: [
        { icon: '🍖', text: '喂食宠物 3 次', reward: 20, done: true },
        { icon: '💬', text: '和宠物聊天 5 次', reward: 30, done: true },
        { icon: '🎾', text: '陪玩 1 次', reward: 50, done: false },
        { icon: '⭐', text: '分享宠物到社交平台', reward: 100, done: false }
      ]
    }
  },
  computed: {
    petEmoji() {
      const stage = this.petInfo?.stage || 1
      const status = this.petInfo?.status || 'happy'
      if (status === 'hungry') return '🥺'
      if (status === 'tired') return '😴'
      if (status === 'angry') return '😠'
      if (status === 'sleeping') return '💤'
      if (stage === 1) return '🐣'
      if (stage === 2) return '🐥'
      if (stage === 3) return '🦊'
      return '🦊'
    },
    petStatusClass() {
      const status = this.petInfo?.status || 'happy'
      return 'status-' + status
    },
    moodText() {
      const m = this.petMood
      if (m >= 80) return '很开心'
      if (m >= 50) return '还不错'
      if (m >= 20) return '有点低落'
      return '不太开心'
    },
    health() {
      return this.petInfo?.health || 0
    },
    fullness() {
      return this.petInfo?.fullness || 0
    },
    petMood() {
      return this.petInfo?.mood || 0
    }
  },
  onLoad() {
    const info = uni.getSystemInfoSync()
    this.statusBarHeight = info.statusBarHeight || 0
    this.userInfo = getUserInfo()
  },
  onShow() {
    this.loadPetInfo()
    this.timer = setInterval(() => this.loadPetInfo(), 30000)
  },
  onHide() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    loadPetInfo() {
      petApi.getInfo().then(data => {
        this.petInfo = data
        const stage = data.stage || 1
        const names = { 1: '幼年期', 2: '成长期', 3: '完全体' }
        this.stageName = names[stage] || '幼年期'
      }).catch(() => {})
    },
    doAction(type) {
      const configs = {
        feed: { title: '喂食中...', desc: '小福正在享用美食~ 🍖', complete: '✨ 完成！饱食+25' },
        bath: { title: '洗澡中...', desc: '小福正在洗香香~ 🚿', complete: '✨ 完成！心情+10' },
        play: { title: '玩耍中...', desc: '小福玩得好开心~ 🎾', complete: '✨ 完成！心情+30' }
      }
      const cfg = configs[type]
      if (!cfg) return

      this.actionTitle = cfg.title
      this.actionDesc = cfg.desc
      this.actionComplete = cfg.complete
      this.actionProgress = 0
      this.actionDone = false
      this.actionVisible = true

      const apiMap = { feed: 'feed', bath: 'bath', play: 'play' }
      petApi[apiMap[type]]().then(data => {
        this.petInfo = data
      }).catch(() => {})

      let progress = 0
      const interval = setInterval(() => {
        progress += 5
        this.actionProgress = Math.min(progress, 100)
        if (progress >= 100) {
          clearInterval(interval)
          this.actionDone = true
          this.actionTitle = this.actionComplete
          this.actionDesc = '🎉'
          setTimeout(() => {
            this.actionVisible = false
          }, 1200)
        }
      }, 100)
    },
    toWork() {
      uni.navigateTo({ url: '/pages/work/work' })
    },
    toAchievement() {
      uni.navigateTo({ url: '/pages/achievement/achievement' })
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
.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 32rpx 12rpx;
  position: sticky;
  top: 0;
  z-index: 10;
  background: linear-gradient(to bottom, #F5EEF8, transparent);
}
.header-left { display: flex; align-items: center; gap: 16rpx; }
.pet-avatar {
  width: 72rpx; height: 72rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border: 3rpx solid #FFFFFF;
  display: flex; align-items: center; justify-content: center;
  font-size: 36rpx;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.pet-info { display: flex; flex-direction: column; }
.pet-name { font-size: 30rpx; font-weight: 700; color: #4A3F55; }
.pet-level { font-size: 22rpx; color: #D5AAFF; font-weight: 600; margin-top: 2rpx; }
.header-right { display: flex; gap: 16rpx; align-items: center; }
.icon-btn {
  width: 72rpx; height: 72rpx;
  border-radius: 24rpx;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(16rpx);
  border: none;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.gold-chip {
  display: flex; align-items: center; gap: 6rpx;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16rpx);
  padding: 14rpx 20rpx;
  border-radius: 999rpx;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.gold-num { font-size: 26rpx; font-weight: 700; color: #4A3F55; }

/* Pet Stage */
.pet-stage {
  position: relative;
  padding: 24rpx 32rpx 20rpx;
  display: flex;
  justify-content: center;
}
.pet-bg {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 360rpx;
  background: linear-gradient(180deg, #FFE5F0 0%, #F5EEF8 100%);
  border-radius: 0 0 60rpx 60rpx;
  z-index: 0;
}
.pet-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.pet-sprite {
  font-size: 180rpx;
  line-height: 1;
  animation: petBreathe 2s ease-in-out infinite;
  filter: drop-shadow(0 12rpx 24rpx rgba(255,179,198,0.35));
  user-select: none;
  margin-bottom: 12rpx;
}
@keyframes petBreathe {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10rpx) scale(1.02); }
}
.pet-mood-text {
  font-size: 26rpx;
  color: #7A6B8A;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16rpx);
  padding: 8rpx 28rpx;
  border-radius: 999rpx;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.mood-highlight { font-weight: 600; color: #FF8FAB; }

/* Status Bars */
.status-section { padding: 0 32rpx; }
.status-card {
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(80,60,100,0.08);
}
.status-row { display: flex; flex-direction: column; gap: 16rpx; }
.status-item { display: flex; align-items: center; gap: 16rpx; }
.status-label { display: flex; align-items: center; gap: 6rpx; width: 96rpx; }
.status-icon { font-size: 26rpx; }
.status-label text { font-size: 22rpx; color: #A898B8; font-weight: 600; }
.status-bar-wrap { display: flex; align-items: center; flex: 1; gap: 12rpx; }
.status-bar {
  flex: 1; height: 16rpx;
  border-radius: 8rpx;
  background: rgba(0,0,0,0.07);
  overflow: hidden;
}
.status-bar-fill {
  height: 100%;
  border-radius: 8rpx;
  transition: width 600ms ease;
}
.status-bar-fill.health { background: linear-gradient(90deg, #A8D8EA, #7EC8D8); }
.status-bar-fill.fullness { background: linear-gradient(90deg, #FFE5A0, #FFD166); }
.status-bar-fill.mood { background: linear-gradient(90deg, #FFB3C6, #FF8FAB); }
.status-bar-fill.low { background: linear-gradient(90deg, #FFACA8, #FF6B6B) !important; animation: pulseBar 1s ease-in-out infinite; }
@keyframes pulseBar { 0%,100%{opacity:1;} 50%{opacity:0.65;} }
.status-value { font-size: 24rpx; font-weight: 700; color: #4A3F55; min-width: 48rpx; text-align: right; }

/* Quick Actions */
.action-section { padding: 32rpx 32rpx 16rpx; }
.section-title { font-size: 30rpx; font-weight: 700; color: #4A3F55; margin-bottom: 20rpx; }
.action-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16rpx; }
.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 4rpx;
  border-radius: 24rpx;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(16rpx);
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
  border: none;
}
.action-icon {
  width: 88rpx; height: 88rpx;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 44rpx;
  margin-bottom: 4rpx;
}
.action-label { font-size: 24rpx; font-weight: 600; color: #4A3F55; }
.action-count { font-size: 20rpx; color: #A898B8; background: #F5EEF8; padding: 4rpx 10rpx; border-radius: 999rpx; }

/* Friends */
.friends-section { padding: 24rpx 32rpx 16rpx; }
.friends-scroll { display: flex; gap: 24rpx; overflow-x: auto; white-space: nowrap; }
.friends-scroll::-webkit-scrollbar { display: none; }
.friend-chip { display: flex; flex-direction: column; align-items: center; gap: 8rpx; flex-shrink: 0; }
.friend-avatar {
  width: 96rpx; height: 96rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #EDE4FF, #FFD5E5);
  border: 3rpx solid #FFFFFF;
  display: flex; align-items: center; justify-content: center;
  font-size: 52rpx;
  box-shadow: 0 4rpx 16rpx rgba(80,60,100,0.08);
}
.friend-name { font-size: 20rpx; color: #7A6B8A; }

/* Quests */
.quest-section { padding: 24rpx 32rpx 16rpx; }
.quest-card {
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 28rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 24rpx rgba(80,60,100,0.08);
}
.quest-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.quest-title { font-size: 28rpx; font-weight: 700; color: #4A3F55; }
.quest-progress { font-size: 22rpx; color: #FF8FAB; font-weight: 600; }
.quest-list { display: flex; flex-direction: column; gap: 12rpx; }
.quest-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(16rpx);
  border-radius: 20rpx;
  padding: 16rpx 20rpx;
}
.quest-icon { font-size: 32rpx; }
.quest-text { flex: 1; font-size: 24rpx; color: #7A6B8A; }
.quest-reward { font-size: 22rpx; color: #C49A1A; font-weight: 700; }
.quest-check {
  width: 36rpx; height: 36rpx;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

/* Action Overlay */
.action-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 999; opacity: 0; pointer-events: none; transition: opacity 250ms;
}
.action-overlay.show { opacity: 1; pointer-events: all; }
.action-modal {
  background: #FFFFFF;
  border-radius: 32rpx;
  padding: 48rpx 40rpx;
  width: 480rpx;
  text-align: center;
  box-shadow: 0 8rpx 40rpx rgba(80,60,100,0.2);
}
.action-pet-big { font-size: 160rpx; margin-bottom: 16rpx; }
.action-title { font-size: 32rpx; font-weight: 700; color: #4A3F55; margin-bottom: 8rpx; }
.action-desc { font-size: 26rpx; color: #7A6B8A; margin-bottom: 24rpx; }
.action-progress { height: 12rpx; border-radius: 6rpx; background: rgba(0,0,0,0.07); margin-bottom: 16rpx; overflow: hidden; }
.action-progress-fill { height: 100%; border-radius: 6rpx; background: linear-gradient(90deg, #FFB3C6, #D5AAFF); transition: width 200ms ease; }
.action-complete { font-size: 26rpx; font-weight: 700; color: #5DA06B; display: none; }
.action-complete.show { display: block; }
</style>
