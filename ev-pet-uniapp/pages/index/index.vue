<template>
  <view class="page">
    <!-- Header -->
    <header class="header">
      <view class="header-left">
        <view class="pet-avatar">{{ petEmoji }}</view>
        <view class="pet-header-info">
          <text class="pet-name">{{ petInfo?.name || '小可爱' }} 🏅</text>
          <text class="pet-level">Lv.{{ petInfo?.level || 1 }} · {{ stageName }}</text>
        </view>
      </view>
      <view class="header-right">
        <button class="icon-btn" @click="toAchievement">
          <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
        </button>
        <view class="icon-btn coin-btn">
          <text style="font-size:14px;">💰</text>
          <text class="coin-num">{{ petInfo?.gold || userInfo?.gold || 0 }}</text>
        </view>
      </view>
    </header>

    <!-- Pet Stage -->
    <section class="pet-stage">
      <view class="pet-bg"></view>
      <view class="pet-container">
        <view class="pet-sprite" :class="petStatusClass">{{ petEmoji }}</view>
        <view class="pet-mood-text">主人~ 我现在 <text class="mood-highlight">{{ moodText }}</text> 哦~ ✨</view>
      </view>
    </section>

    <!-- Status Bars -->
    <section class="status-section">
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
    </section>

    <!-- Quick Actions -->
    <section class="action-section">
      <view class="section-title">日常照料</view>
      <view class="action-grid">
        <button class="action-btn" @click="doAction('feed')">
          <view class="action-icon" style="background:rgba(255,224,160,0.3);">🍖</view>
          <text class="action-label">喂食</text>
          <text class="action-count">饱食+25</text>
        </button>
        <button class="action-btn" @click="doAction('bath')">
          <view class="action-icon" style="background:rgba(168,216,234,0.3);">🚿</view>
          <text class="action-label">清洁</text>
          <text class="action-count">体力-5</text>
        </button>
        <button class="action-btn" @click="doAction('play')">
          <view class="action-icon" style="background:rgba(213,170,255,0.3);">🎾</view>
          <text class="action-label">陪玩</text>
          <text class="action-count">心情+30</text>
        </button>
        <button class="action-btn" @click="toWork">
          <view class="action-icon" style="background:rgba(184,241,204,0.3);">💼</view>
          <text class="action-label">打工</text>
          <text class="action-count">金币+50</text>
        </button>
      </view>
    </section>

    <!-- Friends -->
    <section class="friends-section">
      <view class="section-title">宠物好友</view>
      <scroll-view class="friends-scroll" scroll-x>
        <view class="friend-chip" v-for="f in mockFriends" :key="f.name">
          <view class="friend-avatar">{{ f.emoji }}</view>
          <text class="friend-name">{{ f.name }}</text>
        </view>
      </scroll-view>
    </section>

    <!-- Daily Quests -->
    <section class="quest-section">
      <view class="section-title">今日任务</view>
      <view class="quest-card">
        <view class="quest-header">
          <view class="quest-title">🌟 每日活跃</view>
          <view class="quest-progress">{{ completedCount }}/{{ quests.length }} 完成</view>
        </view>
        <view class="quest-list">
          <view class="quest-item" v-for="q in quests" :key="q.text">
            <text class="quest-icon">{{ q.icon }}</text>
            <text class="quest-text">{{ q.text }}</text>
            <text class="quest-reward">💰+{{ q.reward }}</text>
            <view class="quest-check" :style="q.done ? 'background:#B8F1CC;' : 'background:rgba(0,0,0,0.1);'">
              <text v-if="q.done" style="color:white;font-size:10px;">✓</text>
              <text v-else style="font-size:10px;color:#888;">0</text>
            </view>
          </view>
        </view>
      </view>
    </section>

    <!-- AI Chat CTA -->
    <section class="chat-cta-section">
      <view class="chat-cta-card" @click="toChat">
        <view class="chat-cta-left">
          <view class="chat-cta-emoji">💬</view>
          <view class="chat-cta-info">
            <text class="chat-cta-title">和 AI 小福聊天</text>
            <text class="chat-cta-desc">聊聊心事、问问建议~</text>
          </view>
        </view>
        <view class="chat-cta-arrow">→</view>
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
    completedCount() {
      return this.quests.filter(q => q.done).length
    },
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
        feed: { title: '喂食中...', desc: '小福正在享用美食~ 🍖', complete: '✨ 完成！饱食+25', api: 'feed' },
        bath: { title: '洗澡中...', desc: '小福正在洗香香~ 🚿', complete: '✨ 完成！心情+10', api: 'bath' },
        play: { title: '玩耍中...', desc: '小福玩得好开心~ 🎾', complete: '✨ 完成！心情+30', api: 'play' }
      }
      const cfg = configs[type]
      if (!cfg) return

      this.actionTitle = cfg.title
      this.actionDesc = cfg.desc
      this.actionComplete = cfg.complete
      this.actionProgress = 0
      this.actionDone = false
      this.actionVisible = true

      petApi[cfg.api]().then(data => {
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
    toChat() {
      uni.switchTab({ url: '/pages/chat/chat' })
    },
    toAchievement() {
      uni.navigateTo({ url: '/pages/achievement/achievement' })
    }
  }
}
</script>

<style scoped>
/* CSS Variables */
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
  padding: 10px 16px 6px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 10;
  background: linear-gradient(to bottom, #EEE8F5, transparent);
}
.header-left { display: flex; align-items: center; gap: 10px; }
.pet-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border: 2px solid white;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.pet-header-info { display: flex; flex-direction: column; }
.pet-name {
  font-size: 17px;
  font-weight: 700;
  color: #4A3F55;
  letter-spacing: 0.5px;
}
.pet-level {
  font-size: 11px;
  color: #D5AAFF;
  font-weight: 600;
}
.header-right { display: flex; gap: 10px; align-items: center; }
.icon-btn {
  width: 36px; height: 36px;
  border-radius: 12px;
  border: none;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(8px);
  color: #7A6B8A;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: all 150ms;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
  padding: 0;
}
.icon-btn:active { transform: scale(0.92); background: #FFD5E5; }
.icon-btn svg { width: 18px; height: 18px; }
.coin-btn {
  width: auto;
  padding: 0 12px;
  gap: 4px;
  font-size: 12px;
  flex-direction: row;
}
.coin-num { font-size: 12px; font-weight: 700; color: #4A3F55; margin-left: 2px; }

/* Pet Stage */
.pet-stage {
  position: relative;
  padding: 20px 16px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.pet-bg {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 240px;
  background: linear-gradient(180deg, #FFE5F0 0%, #EEE8F5 100%);
  border-radius: 0 0 40px 40px;
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
  font-size: 120px;
  line-height: 1;
  animation: petBreathe 2s ease-in-out infinite;
  filter: drop-shadow(0 8px 16px rgba(255,179,198,0.35));
  user-select: none;
  margin-bottom: 8px;
}
@keyframes petBreathe {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-6px) scale(1.02); }
}
.pet-mood-text {
  font-size: 13px;
  color: #7A6B8A;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(8px);
  padding: 4px 14px;
  border-radius: 999px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
  margin-bottom: 12px;
}
.mood-highlight { font-weight: 600; color: #FF8FAB; }

/* Status Bars */
.status-section {
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.status-row {
  display: flex;
  gap: 10px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  border-radius: 20px;
  padding: 12px 14px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.status-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.status-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #A898B8;
  font-weight: 600;
}
.status-icon { font-size: 13px; }
.status-bar-wrap { display: flex; align-items: center; gap: 6px; }
.status-bar {
  flex: 1;
  height: 8px;
  border-radius: 4px;
  background: rgba(0,0,0,0.07);
  overflow: hidden;
}
.status-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 600ms ease;
}
.status-bar-fill.health { background: linear-gradient(90deg, #A8D8EA, #7EC8D8); }
.status-bar-fill.fullness { background: linear-gradient(90deg, #FFE5A0, #FFD166); }
.status-bar-fill.mood { background: linear-gradient(90deg, #FFD5E5, #FFB3C6); }
.status-bar-fill.low { background: linear-gradient(90deg, #FFACA8, #FF6B6B); animation: pulseBar 1s ease-in-out infinite; }
@keyframes pulseBar {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.65; }
}
.status-value {
  font-size: 12px;
  font-weight: 700;
  color: #4A3F55;
  min-width: 28px;
  text-align: right;
}

/* Quick Actions */
.action-section {
  padding: 16px 16px 8px;
}
.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 12px;
}
.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 4px;
  border-radius: 20px;
  border: none;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
  cursor: pointer;
  transition: all 200ms;
  font-family: inherit;
}
.action-btn:active { transform: scale(0.93); box-shadow: 0 8px 32px rgba(80, 60, 100, 0.14); }
.action-icon {
  width: 48px; height: 48px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  margin-bottom: 2px;
}
.action-label { font-size: 12px; font-weight: 600; color: #7A6B8A; }
.action-count {
  font-size: 10px;
  color: #A898B8;
  background: #EEE8F5;
  padding: 2px 6px;
  border-radius: 999px;
}

/* Friends */
.friends-section {
  padding: 16px 16px 8px;
}
.friends-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.friends-scroll::-webkit-scrollbar { display: none; }
.friend-chip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}
.friend-avatar {
  width: 52px; height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #EDE4FF, #FFD5E5);
  border: 2px solid white;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.friend-name { font-size: 10px; color: #7A6B8A; }

/* Daily Quests */
.quest-section {
  padding: 16px 16px 8px;
}
.quest-card {
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 20px;
  padding: 14px 16px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
}
.quest-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.quest-title { font-size: 14px; font-weight: 700; color: #4A3F55; }
.quest-progress { font-size: 11px; color: #FF8FAB; font-weight: 600; }
.quest-list { display: flex; flex-direction: column; gap: 8px; }
.quest-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  padding: 10px 12px;
}
.quest-icon { font-size: 18px; }
.quest-text { flex: 1; font-size: 12px; color: #7A6B8A; }
.quest-reward { font-size: 11px; color: #C49A1A; font-weight: 700; display: flex; align-items: center; gap: 2px; }
.quest-check {
  width: 20px; height: 20px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

/* Chat CTA */
.chat-cta-section {
  padding: 16px 16px 8px;
}
.chat-cta-card {
  background: linear-gradient(135deg, #EDE4FF, #FFD5E5);
  border-radius: 20px;
  padding: 14px 16px;
  box-shadow: 0 4px 16px rgba(80, 60, 100, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.chat-cta-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.chat-cta-emoji {
  font-size: 32px;
}
.chat-cta-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.chat-cta-title { font-size: 14px; font-weight: 700; color: #4A3F55; }
.chat-cta-desc { font-size: 12px; color: #7A6B8A; }
.chat-cta-arrow { font-size: 20px; color: #D5AAFF; }

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
  border-radius: 20px;
  padding: 24px;
  width: 280px;
  text-align: center;
  box-shadow: 0 8px 40px rgba(80, 60, 100, 0.2);
  animation: modalIn 300ms cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes modalIn {
  from { opacity: 0; transform: scale(0.8) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.action-pet-big { font-size: 80px; margin-bottom: 12px; }
.action-title { font-size: 16px; font-weight: 700; color: #4A3F55; margin-bottom: 4px; }
.action-desc { font-size: 13px; color: #7A6B8A; margin-bottom: 16px; }
.action-progress {
  height: 8px; border-radius: 4px;
  background: rgba(0,0,0,0.07);
  margin-bottom: 12px; overflow: hidden;
}
.action-progress-fill {
  height: 100%; border-radius: 4px;
  background: linear-gradient(90deg, #FFB3C6, #D5AAFF);
  transition: width 200ms ease;
}
.action-complete {
  font-size: 13px; font-weight: 700; color: #5DA06B;
  display: none;
}
.action-complete.show { display: block; }
</style>
