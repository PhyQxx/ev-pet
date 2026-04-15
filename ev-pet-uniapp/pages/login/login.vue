<template>
  <view class="login-container">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>
    
    <!-- Logo区域 -->
    <view class="logo-section">
      <view class="pet-avatar">
        <text class="pet-emoji">🐾</text>
      </view>
      <text class="app-title">EV Pet</text>
      <text class="app-subtitle">AI虚拟宠物养成</text>
    </view>
    
    <!-- 登录按钮区域 -->
    <view class="login-section">
      <text class="welcome-text">欢迎回家，小主人~</text>
      
      <!-- 微信登录 -->
      <button class="login-btn wechat-btn" @click="wechatLogin">
        <text class="btn-icon">📱</text>
        <text class="btn-text">微信登录</text>
      </button>
      
      <!-- 手机号登录 -->
      <button class="login-btn phone-btn" @click="showPhoneLogin = true">
        <text class="btn-icon">📞</text>
        <text class="btn-text">手机号登录</text>
      </button>
      
      <!-- 游客模式 -->
      <button class="guest-btn" @click="guestLogin">
        <text class="guest-text">游客模式先看看</text>
      </button>
    </view>
    
    <!-- 协议 -->
    <view class="agreement">
      <text class="agreement-text">登录即表示同意</text>
      <text class="link">《用户协议》</text>
      <text class="agreement-text">和</text>
      <text class="link">《隐私政策》</text>
    </view>
    
    <!-- 手机号登录弹窗 -->
    <view class="phone-modal" v-if="showPhoneLogin" @click="showPhoneLogin = false">
      <view class="phone-panel" @click.stop>
        <text class="panel-title">手机号登录</text>
        
        <view class="input-group">
          <input class="phone-input" v-model="phone" placeholder="请输入手机号" type="number" maxlength="11" />
        </view>
        
        <view class="input-group verify-group">
          <input class="verify-input" v-model="verifyCode" placeholder="验证码" type="number" maxlength="6" />
          <button class="send-code-btn" @click="sendCode" :disabled="countdown > 0">
            {{ countdown > 0 ? countdown + 's' : '发送验证码' }}
          </button>
        </view>
        
        <button class="confirm-btn" @click="phoneLogin">确认登录</button>
        <button class="cancel-btn" @click="showPhoneLogin = false">取消</button>
      </view>
    </view>
  </view>
</template>

<script>
import { auth, setToken, setUserInfo } from '../../utils/api.js'

export default {
  data() {
    return {
      showPhoneLogin: false,
      phone: '',
      verifyCode: '',
      countdown: 0
    }
  },
  methods: {
    // 微信登录（简化版，实际需要微信SDK）
    wechatLogin() {
      uni.showLoading({ title: '登录中...' })
      auth.login({ loginType: 1, code: 'wechat_' + Date.now() })
        .then(data => {
          setToken(data.token)
          setUserInfo(data.user)
          uni.setStorageSync('petInfo', data.pet)
          uni.hideLoading()
          uni.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            uni.switchTab({ url: '/pages/index/index' })
          }, 1000)
        })
        .catch(err => {
          uni.hideLoading()
          console.error('Login failed:', err)
        })
    },
    
    // 游客登录
    guestLogin() {
      uni.showLoading({ title: '进入中...' })
      auth.login({ loginType: 2, phone: 'guest_' + Date.now() })
        .then(data => {
          setToken(data.token)
          setUserInfo(data.user)
          uni.setStorageSync('petInfo', data.pet)
          uni.hideLoading()
          uni.switchTab({ url: '/pages/index/index' })
        })
        .catch(err => {
          uni.hideLoading()
        })
    },
    
    // 发送验证码（简化版）
    sendCode() {
      if (!this.phone || this.phone.length !== 11) {
        uni.showToast({ title: '请输入正确手机号', icon: 'none' })
        return
      }
      this.countdown = 60
      let timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
      uni.showToast({ title: '验证码已发送', icon: 'success' })
    },
    
    // 手机号登录
    phoneLogin() {
      if (!this.phone || !this.verifyCode) {
        uni.showToast({ title: '请填写完整', icon: 'none' })
        return
      }
      uni.showLoading({ title: '登录中...' })
      auth.login({ loginType: 2, phone: this.phone, verifyCode: this.verifyCode })
        .then(data => {
          setToken(data.token)
          setUserInfo(data.user)
          uni.setStorageSync('petInfo', data.pet)
          uni.hideLoading()
          uni.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            uni.switchTab({ url: '/pages/index/index' })
          }, 1000)
        })
        .catch(err => {
          uni.hideLoading()
        })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFD5E5 0%, #D5AAFF 50%, #A8D8EA 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
}

.circle-1 {
  width: 300rpx;
  height: 300rpx;
  background: #FFB3C6;
  top: -100rpx;
  right: -100rpx;
}

.circle-2 {
  width: 200rpx;
  height: 200rpx;
  background: #D5AAFF;
  bottom: 200rpx;
  left: -80rpx;
}

.circle-3 {
  width: 150rpx;
  height: 150rpx;
  background: #A8D8EA;
  bottom: 100rpx;
  right: 50rpx;
}

.logo-section {
  margin-top: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pet-avatar {
  width: 180rpx;
  height: 180rpx;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20rpx 40rpx rgba(213, 170, 255, 0.4);
}

.pet-emoji {
  font-size: 100rpx;
}

.app-title {
  font-size: 56rpx;
  font-weight: 700;
  color: #FFFFFF;
  margin-top: 30rpx;
  text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
}

.app-subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 10rpx;
}

.login-section {
  margin-top: 100rpx;
  width: 600rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.welcome-text {
  font-size: 32rpx;
  color: #FFFFFF;
  margin-bottom: 60rpx;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  border: none;
  font-size: 32rpx;
}

.wechat-btn {
  background: #07C160;
  color: #FFFFFF;
}

.phone-btn {
  background: #FFFFFF;
  color: #4A3F55;
}

.btn-icon {
  margin-right: 16rpx;
  font-size: 36rpx;
}

.guest-btn {
  background: transparent;
  border: none;
  margin-top: 20rpx;
}

.guest-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 26rpx;
}

.agreement {
  position: absolute;
  bottom: 60rpx;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
}

.agreement-text {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.link {
  font-size: 22rpx;
  color: #FFFFFF;
  text-decoration: underline;
}

/* 手机号登录弹窗 */
.phone-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 999;
}

.phone-panel {
  width: 100%;
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  padding: 60rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.panel-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #4A3F55;
  margin-bottom: 50rpx;
}

.input-group {
  width: 100%;
  margin-bottom: 30rpx;
}

.phone-input, .verify-input {
  width: 100%;
  height: 96rpx;
  background: #F8F8F8;
  border-radius: 20rpx;
  padding: 0 30rpx;
  font-size: 30rpx;
}

.verify-group {
  display: flex;
  gap: 20rpx;
}

.verify-input {
  flex: 1;
}

.send-code-btn {
  width: 240rpx;
  height: 96rpx;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 20rpx;
  color: #FFFFFF;
  font-size: 26rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.confirm-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 48rpx;
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 500;
  margin-top: 20rpx;
  border: none;
}

.cancel-btn {
  background: transparent;
  border: none;
  color: #A898B8;
  font-size: 28rpx;
  margin-top: 20rpx;
}
</style>
