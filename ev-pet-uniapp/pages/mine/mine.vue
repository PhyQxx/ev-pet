<template>
  <view class="mine-container">
    <!-- Profile Header -->
    <view class="profile-header">
      <view class="profile-bg"></view>
      <view class="profile-content">
        <view class="avatar-wrap">
          <view class="avatar">
            <text class="avatar-emoji">{{ avatarEmoji }}</text>
          </view>
          <view class="avatar-ring"></view>
        </view>
        <view class="profile-info">
          <view class="nickname-row">
            <text class="nickname">{{ userInfo?.nickname || '加载中...' }}</text>
            <view class="level-badge">
              <text class="level-icon">⭐</text>
              <text class="level-text">Lv.{{ userInfo?.level || 1 }}</text>
            </view>
          </view>
          <text class="pet-stage">🌟 {{ petStageName }} · {{ petInfo?.name || '我的宠物' }}</text>
          <view class="exp-bar-wrap">
            <view class="exp-bar">
              <view class="exp-fill" :style="{ width: expPercent + '%' }"></view>
            </view>
            <text class="exp-label">{{ userInfo?.exp || 0 }} / {{ expNext }}</text>
          </view>
        </view>
        <view class="edit-btn" @click="editProfile">编辑</view>
      </view>
    </view>

    <!-- Stats Row -->
    <view class="stats-row">
      <view class="stat-item" @click="showToast('金币: ' + (userInfo?.gold || 0))">
        <text class="stat-val">{{ formatNum(userInfo?.gold || 0) }}</text>
        <text class="stat-label">金币</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @click="goTo('/pages/achievement/achievement')">
        <text class="stat-val">{{ achievementsCount }}</text>
        <text class="stat-label">成就</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-val">{{ consecutiveDays }}</text>
        <text class="stat-label">连续天数</text>
      </view>
    </view>

    <!-- Pet Info Card -->
    <view class="pet-card" @click="toPetDetail">
      <view class="pet-card-left">
        <text class="pet-emoji">{{ petEmoji }}</text>
        <view class="pet-info">
          <text class="pet-name">{{ petInfo?.name || '小可爱' }}</text>
          <text class="pet-level-text">LV.{{ petInfo?.level || 1 }} · {{ petStageName }}</text>
          <view class="pet-status-row">
            <view class="status-dot" :class="petStatusColor"></view>
            <text class="pet-status-text">{{ statusText }}</text>
          </view>
        </view>
      </view>
      <view class="pet-card-right">
        <view class="attr-item">
          <text class="attr-icon">⚡</text>
          <text class="attr-label">体力</text>
          <text class="attr-val">{{ petInfo?.health || 80 }}</text>
        </view>
        <view class="attr-item">
          <text class="attr-icon">🍎</text>
          <text class="attr-label">饱食</text>
          <text class="attr-val">{{ petInfo?.fullness || 65 }}</text>
        </view>
        <view class="attr-item">
          <text class="attr-icon">❤️</text>
          <text class="attr-label">心情</text>
          <text class="attr-val">{{ petInfo?.mood || 90 }}</text>
        </view>
      </view>
    </view>

    <!-- Menu Section -->
    <view class="menu-section">
      <view class="menu-item" @click="goTo('/pages/achievement/achievement')">
        <text class="menu-icon">🏆</text>
        <text class="menu-text">成就中心</text>
        <text class="menu-tag">{{ achievementsCount }}个</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goTo('/pages/backpack/backpack')">
        <text class="menu-icon">🎒</text>
        <text class="menu-text">我的背包</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goTo('/pages/shop/shop')">
        <text class="menu-icon">🛒</text>
        <text class="menu-text">商店订单</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @click="showToast('账号设置')">
        <text class="menu-icon">👤</text>
        <text class="menu-text">账号</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="showToast('通知设置')">
        <text class="menu-icon">🔔</text>
        <text class="menu-text">通知</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="showToast('隐私设置')">
        <text class="menu-icon">🔐</text>
        <text class="menu-text">隐私</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="showToast('关于我们')">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- Logout -->
    <view class="logout-btn" @click="logout">退出登录</view>

    <!-- Version -->
    <text class="version">EV Pet v1.0.0</text>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { pet as petApi, getUserInfo, getToken, removeToken } from '../../utils/api.js'

const userInfo = ref(null)
const petInfo = ref(null)
const achievementsCount = ref(8)
const consecutiveDays = ref(7)

const avatarEmoji = computed(() => {
  const names = userInfo.value?.nickname || ''
  return names.charAt(0) || '我'
})

const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const petStageName = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '幼年期' : stage === 2 ? '成长期' : '完全体'
})

const statusText = computed(() => {
  const status = petInfo.value?.status
  if (status === 'happy') return '心情很好~'
  if (status === 'hungry') return '肚子饿了'
  if (status === 'tired') return '有点累'
  if (status === 'angry') return '不开心'
  return '开心'
})

const petStatusColor = computed(() => {
  const status = petInfo.value?.status
  if (status === 'happy') return 'dot-happy'
  if (status === 'hungry') return 'dot-hungry'
  if (status === 'tired') return 'dot-tired'
  return 'dot-default'
})

const expPercent = computed(() => {
  const exp = userInfo.value?.exp || 0
  const level = userInfo.value?.level || 1
  const currentLevelExp = (level - 1) * 100
  const nextLevelExp = level * 100
  const progress = ((exp - currentLevelExp) / (nextLevelExp - currentLevelExp)) * 100
  return Math.min(100, Math.max(0, progress))
})

const expNext = computed(() => {
  return (userInfo.value?.level || 1) * 100
})

onMounted(() => {
  userInfo.value = getUserInfo()
  loadPetInfo()
})

function loadPetInfo() {
  petApi.getInfo()
    .then(data => {
      petInfo.value = data
    })
    .catch(() => {})
}

function formatNum(n) {
  if (!n && n !== 0) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return String(n)
}

function goTo(url) {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function editProfile() {
  uni.showToast({ title: '编辑功能开发中', icon: 'none' })
}

function toPetDetail() {
  uni.showToast({ title: '宠物详情开发中', icon: 'none' })
}

function showToast(title) {
  uni.showToast({ title, icon: 'none' })
}

function logout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        removeToken()
        uni.clearStorageSync()
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  })
}
</script>

<style scoped>
.mine-container {
  min-height: 100vh;
  background: #FFF8FA;
  max-width: 430px;
  margin: 0 auto;
  padding-bottom: 40px;
}

/* Profile Header */
.profile-header {
  position: relative;
  padding: 16px 16px 0;
  overflow: hidden;
}
.profile-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 180px;
  background: linear-gradient(180deg, #FFE5F0 0%, #FFF8FA 100%);
  border-radius: 0 0 40px 40px;
  z-index: 0;
}
.profile-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding-top: 12px;
}
.avatar-wrap {
  position: relative;
  flex-shrink: 0;
}
.avatar {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(255,179,198,0.35);
}
.avatar-emoji {
  font-size: 36px;
  color: #fff;
}
.profile-info {
  flex: 1;
  padding-top: 4px;
}
.nickname-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.nickname {
  font-size: 20px;
  font-weight: 700;
  color: #4A3F55;
}
.level-badge {
  display: flex;
  align-items: center;
  gap: 3px;
  background: linear-gradient(135deg, #FFD166, #FFB3C6);
  padding: 3px 10px;
  border-radius: 20px;
}
.level-icon { font-size: 11px; }
.level-text { font-size: 11px; font-weight: 700; color: #4A3F55; }
.pet-stage {
  font-size: 12px;
  color: #7A6B8A;
  display: block;
  margin-bottom: 6px;
}
.exp-bar-wrap { display: flex; align-items: center; gap: 8px; }
.exp-bar {
  flex: 1;
  height: 6px;
  background: #EDE4FF;
  border-radius: 3px;
  overflow: hidden;
}
.exp-fill {
  height: 100%;
  background: linear-gradient(90deg, #D5AAFF, #FFB3C6);
  border-radius: 3px;
  transition: width 0.6s ease;
}
.exp-label { font-size: 10px; color: #A898B8; white-space: nowrap; }
.edit-btn {
  background: rgba(255,255,255,0.9);
  color: #7A6B8A;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
  cursor: pointer;
}

/* Stats Row */
.stats-row {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 18px;
  margin: 12px 16px;
  padding: 14px 20px;
  box-shadow: 0 2px 12px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
}
.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.stat-val {
  font-size: 20px;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 2px;
}
.stat-label {
  font-size: 11px;
  color: #7A6B8A;
}
.stat-divider {
  width: 1px;
  height: 30px;
  background: #F0EDF5;
}

/* Pet Card */
.pet-card {
  background: #fff;
  border-radius: 20px;
  margin: 0 16px 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 12px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
  cursor: pointer;
}
.pet-card-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}
.pet-emoji { font-size: 48px; }
.pet-info { display: flex; flex-direction: column; }
.pet-name { font-size: 15px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; }
.pet-level-text { font-size: 11px; color: #7A6B8A; margin-bottom: 4px; }
.pet-status-row { display: flex; align-items: center; gap: 5px; }
.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #B8F1CC;
}
.status-dot.dot-happy { background: #B8F1CC; }
.status-dot.dot-hungry { background: #FFACA8; }
.status-dot.dot-tired { background: #A8D8EA; }
.pet-status-text { font-size: 11px; color: #7A6B8A; }
.pet-card-right {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.attr-item {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #FAFAFA;
  padding: 5px 10px;
  border-radius: 10px;
}
.attr-icon { font-size: 12px; }
.attr-label { font-size: 10px; color: #A898B8; }
.attr-val { font-size: 12px; font-weight: 700; color: #4A3F55; }

/* Menu Section */
.menu-section {
  background: #fff;
  border-radius: 18px;
  margin: 0 16px 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(180,150,200,0.05);
  border: 1px solid #F5EEF8;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #F5EEF8;
  cursor: pointer;
  transition: background 0.15s;
}
.menu-item:last-child { border-bottom: none; }
.menu-item:active { background: #F9F5FF; }
.menu-icon { font-size: 18px; width: 28px; text-align: center; }
.menu-text {
  flex: 1;
  font-size: 14px;
  color: #4A3F55;
  margin-left: 10px;
}
.menu-tag {
  font-size: 11px;
  color: #D5AAFF;
  background: #F5EEF8;
  padding: 2px 8px;
  border-radius: 10px;
  margin-right: 8px;
}
.menu-arrow {
  color: #A898B8;
  font-size: 18px;
  font-weight: 600;
}

/* Logout */
.logout-btn {
  margin: 8px 16px 0;
  text-align: center;
  background: #FFF0F0;
  color: #FF6B6B;
  padding: 14px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  border: 1px solid #FFE5E5;
}

/* Version */
.version {
  display: block;
  text-align: center;
  font-size: 11px;
  color: #A898B8;
  margin-top: 16px;
}
</style>
