<template>
  <view class="mine-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-header">
        <view class="avatar">
          <text class="avatar-text">{{ userInfo?.nickname?.charAt(0) || '我' }}</text>
        </view>
        <view class="user-info">
          <text class="nickname">{{ userInfo?.nickname || '加载中' }}</text>
          <view class="level-row">
            <text class="level-badge">Lv.{{ userInfo?.level || 1 }}</text>
            <text class="exp-text">经验 {{ userInfo?.exp || 0 }}</text>
          </view>
        </view>
        <button class="edit-btn" @click="editProfile">编辑</button>
      </view>
      
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-num">{{ userInfo?.gold || 0 }}</text>
          <text class="stat-label">金币</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ petInfo?.level || 1 }}</text>
          <text class="stat-label">宠物等级</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-num">{{ achievementsCount }}</text>
          <text class="stat-label">成就</text>
        </view>
      </view>
    </view>
    
    <!-- 宠物信息 -->
    <view class="pet-card" @click="toPetDetail">
      <view class="pet-header">
        <text class="pet-title">我的宠物</text>
        <text class="more-arrow">></text>
      </view>
      <view class="pet-content">
        <text class="pet-emoji">{{ petEmoji }}</text>
        <view class="pet-detail">
          <text class="pet-name">{{ petInfo?.name || '小可爱' }}</text>
          <text class="pet-status">{{ statusText }}</text>
        </view>
      </view>
    </view>
    
    <!-- 功能列表 -->
    <view class="menu-section">
      <view class="menu-item" @click="toAchievement">
        <text class="menu-icon">🏆</text>
        <text class="menu-text">成就中心</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="toBackpack">
        <text class="menu-icon">🎒</text>
        <text class="menu-text">我的背包</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="toSettings">
        <text class="menu-icon">⚙️</text>
        <text class="menu-text">设置</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="logout">
        <text class="menu-icon">🚪</text>
        <text class="menu-text logout">退出登录</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
  </view>
</template>

<script>
import { pet as petApi, getUserInfo, getToken, removeToken } from '../../utils/api.js'

export default {
  data() {
    return {
      userInfo: null,
      petInfo: null,
      achievementsCount: 8
    }
  },
  computed: {
    petEmoji() {
      const stage = this.petInfo?.stage || 1
      return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
    },
    statusText() {
      const status = this.petInfo?.status
      if (status === 'happy') return '心情很好~'
      if (status === 'hungry') return '肚子饿了'
      if (status === 'tired') return '有点累'
      if (status === 'angry') return '不开心'
      return '开心'
    }
  },
  onShow() {
    this.userInfo = getUserInfo()
    this.loadPetInfo()
  },
  methods: {
    loadPetInfo() {
      petApi.getInfo()
        .then(data => {
          this.petInfo = data
        })
        .catch(err => {
          console.error('Failed to load pet info:', err)
        })
    },
    editProfile() {
      uni.showToast({ title: '编辑功能开发中', icon: 'none' })
    },
    toPetDetail() {
      uni.showToast({ title: '宠物详情开发中', icon: 'none' })
    },
    toAchievement() {
      uni.showToast({ title: '成就中心开发中', icon: 'none' })
    },
    toBackpack() {
      uni.showToast({ title: '背包开发中', icon: 'none' })
    },
    toSettings() {
      uni.showToast({ title: '设置开发中', icon: 'none' })
    },
    logout() {
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
  }
}
</script>

<style scoped>
.mine-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #D5E8F0 0%, #EDE4FF 100%);
  padding: 40rpx;
}

.user-card {
  background: #FFFFFF;
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(213, 170, 255, 0.2);
}

.user-header {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: 56rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.user-info {
  flex: 1;
  margin-left: 30rpx;
}

.nickname {
  font-size: 36rpx;
  font-weight: 700;
  color: #4A3F55;
}

.level-row {
  display: flex;
  align-items: center;
  margin-top: 10rpx;
}

.level-badge {
  background: linear-gradient(135deg, #D5AAFF 0%, #FFB3C6 100%);
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #FFFFFF;
}

.exp-text {
  font-size: 24rpx;
  color: #A898B8;
  margin-left: 16rpx;
}

.edit-btn {
  background: #EDE4FF;
  color: #7A6B8A;
  padding: 12rpx 30rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: none;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-top: 40rpx;
  padding-top: 30rpx;
  border-top: 1rpx solid #F0F0F0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #4A3F55;
}

.stat-label {
  font-size: 24rpx;
  color: #A898B8;
  margin-top: 8rpx;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #E0E0E0;
}

.pet-card {
  background: #FFFFFF;
  border-radius: 30rpx;
  padding: 30rpx;
  margin-top: 30rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.05);
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pet-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #4A3F55;
}

.more-arrow {
  color: #A898B8;
  font-size: 28rpx;
}

.pet-content {
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.pet-emoji {
  font-size: 80rpx;
}

.pet-detail {
  margin-left: 24rpx;
}

.pet-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #4A3F55;
}

.pet-status {
  font-size: 24rpx;
  color: #A898B8;
  margin-top: 6rpx;
}

.menu-section {
  background: #FFFFFF;
  border-radius: 30rpx;
  margin-top: 30rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 36rpx 30rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
  width: 60rpx;
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #4A3F55;
  margin-left: 20rpx;
}

.menu-text.logout {
  color: #FF6B6B;
}

.menu-arrow {
  color: #A898B8;
  font-size: 28rpx;
}
</style>
