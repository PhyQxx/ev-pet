<template>
  <div class="login-page">
    <div class="login-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="pet-avatar">
            <span>🐾</span>
          </div>
          <h1 class="app-title">EV Pet</h1>
          <p class="app-subtitle">AI虚拟宠物养成平台</p>
        </div>
        
        <div class="login-body">
          <p class="welcome-text">欢迎回家，小主人~</p>
          
          <button class="login-btn wechat" @click="wechatLogin">
            <span class="btn-icon">📱</span>
            <span>微信登录</span>
          </button>
          
          <button class="login-btn phone" @click="showPhoneDialog = true">
            <span class="btn-icon">📞</span>
            <span>手机号登录</span>
          </button>
          
          <button class="login-btn guest" @click="guestLogin">
            <span class="btn-text">游客模式先看看</span>
          </button>
        </div>
        
        <div class="login-footer">
          <span>登录即表示同意</span>
          <a href="#">《用户协议》</a>
          <span>和</span>
          <a href="#">《隐私政策》</a>
        </div>
      </div>
    </div>
    
    <!-- 手机号登录弹窗 -->
    <el-dialog v-model="showPhoneDialog" title="手机号登录" width="400px" class="phone-dialog">
      <el-form @submit.prevent="phoneLogin">
        <el-form-item>
          <el-input v-model="phone" placeholder="请输入手机号" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="verifyCode" placeholder="验证码" size="large">
            <template #append>
              <el-button @click="sendCode" :disabled="countdown > 0">
                {{ countdown > 0 ? `${countdown}s` : '发送' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" class="submit-btn" @click="phoneLogin">
          确认登录
        </el-button>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '../../api'
import { useUserStore } from '../../store'

const router = useRouter()
const userStore = useUserStore()

const showPhoneDialog = ref(false)
const phone = ref('')
const verifyCode = ref('')
const countdown = ref(0)

const wechatLogin = async () => {
  try {
    const data = await auth.login({ loginType: 1, code: 'wechat_' + Date.now() })
    userStore.setUser(data.user, data.pet, data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (err) {
    ElMessage.error('登录失败')
  }
}

const guestLogin = async () => {
  try {
    const data = await auth.login({ loginType: 2, phone: 'guest_' + Date.now() })
    userStore.setUser(data.user, data.pet, data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (err) {
    ElMessage.error('登录失败')
  }
}

const sendCode = () => {
  if (!phone.value || phone.value.length !== 11) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  ElMessage.success('验证码已发送')
}

const phoneLogin = async () => {
  if (!phone.value || !verifyCode.value) {
    ElMessage.warning('请填写完整')
    return
  }
  try {
    const data = await auth.login({ loginType: 2, phone: phone.value, verifyCode: verifyCode.value })
    userStore.setUser(data.user, data.pet, data.token)
    ElMessage.success('登录成功')
    showPhoneDialog.value = false
    router.push('/')
  } catch (err) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFD5E5 0%, #D5AAFF 50%, #A8D8EA 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-decoration {
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
  width: 400px;
  height: 400px;
  background: #FFB3C6;
  top: -150px;
  right: -100px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: #D5AAFF;
  bottom: -100px;
  left: -100px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: #A8D8EA;
  bottom: 200px;
  right: 50px;
}

.login-container {
  position: relative;
  z-index: 1;
}

.login-card {
  background: white;
  border-radius: 40px;
  padding: 60px 50px;
  width: 440px;
  box-shadow: 0 40px 80px rgba(213, 170, 255, 0.4);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.pet-avatar {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 60px;
  box-shadow: 0 20px 40px rgba(213, 170, 255, 0.4);
}

.app-title {
  font-size: 40px;
  font-weight: 700;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 8px;
}

.app-subtitle {
  color: var(--text-secondary);
  font-size: 16px;
}

.login-body {
  text-align: center;
}

.welcome-text {
  color: var(--text-secondary);
  margin-bottom: 30px;
}

.login-btn {
  width: 100%;
  height: 56px;
  border-radius: 28px;
  border: none;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 16px;
}

.login-btn.wechat {
  background: #07C160;
  color: white;
}

.login-btn.wechat:hover {
  background: #06AD56;
}

.login-btn.phone {
  background: white;
  color: var(--text-primary);
  border: 2px solid var(--primary);
}

.login-btn.phone:hover {
  background: var(--primary-light);
}

.login-btn.guest {
  background: transparent;
  border: none;
  color: var(--text-placeholder);
  height: 40px;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  font-size: 12px;
  color: var(--text-placeholder);
}

.login-footer a {
  color: var(--primary);
  text-decoration: none;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border: none;
  font-size: 16px;
}

.phone-dialog :deep(.el-dialog) {
  border-radius: 20px;
}
</style>
