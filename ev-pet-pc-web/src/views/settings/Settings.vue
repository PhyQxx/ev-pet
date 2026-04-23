<template>
  <div class="settings-page">
    <div class="settings-layout">
      <!-- Left Nav -->
      <div class="settings-nav">
        <div
          v-for="nav in settingsNav"
          :key="nav.id"
          class="settings-nav-item"
          :class="{ active: activeNav === nav.id }"
          @click="activeNav = nav.id"
        >
          <span class="nav-icon">{{ nav.icon }}</span>
          {{ nav.label }}
          <span v-if="nav.count" class="nav-count">{{ nav.count }}</span>
        </div>
      </div>

      <!-- Right Content -->
      <div class="settings-content">

        <!-- Profile Section -->
        <div v-show="activeNav === 'profile'" class="settings-section">
          <div class="settings-section-title">👤 个人信息</div>
          <div class="profile-header">
            <div class="profile-avatar-wrap">
              <div class="profile-avatar">{{ petStore.petInfo?.emoji || '🐱' }}</div>
              <div class="profile-avatar-edit">✎</div>
            </div>
            <div class="profile-info">
              <div class="profile-name">{{ userStore.userInfo?.nickname || '裴浩宇' }}</div>
              <div class="profile-id">ID: {{ userInfo.id }}</div>
              <div class="profile-tags">
                <span v-for="tag in userInfo.tags" :key="tag" class="profile-tag">{{ tag }}</span>
              </div>
            </div>
            <div>
              <button class="btn">更换头像</button>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">昵称</div>
            <div class="form-control">
              <input v-model="userInfo.nickname" type="text" class="input-field">
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">个性签名</div>
            <div class="form-control">
              <textarea v-model="userInfo.bio" class="input-field" placeholder="介绍一下你的宠物或你自己..."></textarea>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">性别</div>
            <div class="form-control">
              <select v-model="userInfo.gender" class="select-field">
                <option value="secret">保密</option>
                <option value="male">男</option>
                <option value="female">女</option>
              </select>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">生日</div>
            <div class="form-control">
              <input v-model="userInfo.birthday" type="date" class="input-field">
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">时区</div>
            <div class="form-control">
              <select v-model="userInfo.timezone" class="select-field">
                <option value="Asia/Shanghai">Asia/Shanghai (GMT+8)</option>
                <option value="America/New_York">America/New_York (GMT-5)</option>
                <option value="Europe/London">Europe/London (GMT+0)</option>
              </select>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">宠物昵称</div>
            <div class="form-control">
              <input v-model="petName" type="text" class="input-field">
              <div class="form-hint">宠物名字将显示在主页和对话中</div>
            </div>
          </div>
          <div style="margin-top:16px;">
            <button class="btn-primary" @click="saveProfile">保存修改</button>
          </div>
        </div>

        <!-- Notification Section -->
        <div v-show="activeNav === 'notification'" class="settings-section">
          <div class="settings-section-title">🔔 通知设置</div>
          <div v-for="(toggle, key) in notificationSettings" :key="key" class="toggle-row">
            <div class="toggle-info">
              <div class="toggle-title">{{ toggle.title }}</div>
              <div class="toggle-desc">{{ toggle.desc }}</div>
            </div>
            <div class="toggle" :class="{ on: toggle.value }" @click="toggle.value = !toggle.value"></div>
          </div>
          <hr class="divider">
          <div class="form-item" style="padding-top:12px;">
            <div class="form-label">提醒时间</div>
            <div class="form-control">
              <select v-model="notifyTime" class="select-field">
                <option value="08:00">08:00</option>
                <option value="09:00">09:00</option>
                <option value="12:00">12:00</option>
                <option value="18:00">18:00</option>
                <option value="20:00">20:00</option>
                <option value="22:00">22:00</option>
              </select>
              <div class="form-hint">仅在开启通知时生效</div>
            </div>
          </div>
        </div>

        <!-- Privacy Section -->
        <div v-show="activeNav === 'privacy'" class="settings-section">
          <div class="settings-section-title">🔒 隐私与安全</div>
          <div class="form-item">
            <div class="form-label">当前密码</div>
            <div class="form-control">
              <input v-model="passwordForm.current" type="password" class="input-field" placeholder="请输入当前密码">
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">新密码</div>
            <div class="form-control">
              <input v-model="passwordForm.new" type="password" class="input-field" placeholder="请输入新密码（至少8位）">
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">确认密码</div>
            <div class="form-control">
              <input v-model="passwordForm.confirm" type="password" class="input-field" placeholder="请再次输入新密码">
            </div>
          </div>
          <hr class="divider">
          <div class="toggle-row">
            <div class="toggle-info">
              <div class="toggle-title">两步验证</div>
              <div class="toggle-desc">登录时需要手机验证码</div>
            </div>
            <div class="toggle" :class="{ on: twoFactor }" @click="twoFactor = !twoFactor"></div>
          </div>
          <hr class="divider">
          <div class="form-item">
            <div class="form-label">绑定手机</div>
            <div class="form-control">
              <div style="display:flex;gap:10px;align-items:center;">
                <input type="text" class="input-field" :value="userInfo.phone" style="flex:1;" readonly>
                <button class="btn" style="white-space:nowrap;">更换手机</button>
              </div>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">绑定邮箱</div>
            <div class="form-control">
              <div style="display:flex;gap:10px;align-items:center;">
                <input type="email" class="input-field" :value="userInfo.email" style="flex:1;" readonly>
                <button class="btn" style="white-space:nowrap;">修改邮箱</button>
              </div>
            </div>
          </div>
          <div style="margin-top:16px;">
            <button class="btn-primary" @click="updatePassword">更新密码</button>
          </div>
        </div>

        <!-- Appearance Section -->
        <div v-show="activeNav === 'appearance'" class="settings-section">
          <div class="settings-section-title">🎨 外观设置</div>
          <div class="form-item">
            <div class="form-label">主题模式</div>
            <div class="form-control">
              <div class="btn-group">
                <button
                  v-for="theme in themes"
                  :key="theme.id"
                  class="btn"
                  :class="{ 'btn-primary': activeTheme === theme.id }"
                  :style="activeTheme === theme.id ? '' : 'padding:8px 18px;'"
                  @click="activeTheme = theme.id"
                >
                  {{ theme.emoji }} {{ theme.name }}
                </button>
              </div>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">主题色</div>
            <div class="form-control">
              <div class="color-options">
                <div
                  v-for="color in themeColors"
                  :key="color"
                  class="color-option"
                  :class="{ selected: activeColor === color }"
                  :style="{ background: color }"
                  @click="activeColor = color"
                ></div>
              </div>
            </div>
          </div>
          <div class="toggle-row">
            <div class="toggle-info">
              <div class="toggle-title">宠物动画效果</div>
              <div class="toggle-desc">显示宠物的心情动画和特效</div>
            </div>
            <div class="toggle" :class="{ on: petAnimation }" @click="petAnimation = !petAnimation"></div>
          </div>
          <hr class="divider">
          <div class="toggle-row">
            <div class="toggle-info">
              <div class="toggle-title">音效反馈</div>
              <div class="toggle-desc">按钮点击和交互的音效</div>
            </div>
            <div class="toggle" :class="{ on: soundEffect }" @click="soundEffect = !soundEffect"></div>
          </div>
          <hr class="divider">
          <div class="toggle-row">
            <div class="toggle-info">
              <div class="toggle-title">背景音乐</div>
              <div class="toggle-desc">主页循环播放轻音乐</div>
            </div>
            <div class="toggle" :class="{ on: bgMusic }" @click="bgMusic = !bgMusic"></div>
          </div>
          <hr class="divider">
          <div class="form-item" style="padding-top:12px;">
            <div class="form-label">音量</div>
            <div class="form-control">
              <input type="range" min="0" max="100" v-model="volume" style="width:100%;">
            </div>
          </div>
        </div>

        <!-- Account Section -->
        <div v-show="activeNav === 'account'" class="settings-section">
          <div class="settings-section-title">📋 账号管理</div>
          <div class="form-item">
            <div class="form-label">账号创建时间</div>
            <div class="form-control" style="padding-top:8px;font-size:14px;color:#7A6B8A;">
              {{ userInfo.createdAt }}
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">会员状态</div>
            <div class="form-control" style="padding-top:8px;">
              <span class="vip-badge">{{ userInfo.vipStatus }}</span>
              <span style="font-size:12px;color:#7A6B8A;margin-left:10px;">{{ userInfo.vipExpire }}</span>
            </div>
          </div>
          <div class="form-item">
            <div class="form-label">数据导出</div>
            <div class="form-control">
              <button class="btn">📥 导出我的数据</button>
            </div>
          </div>
          <hr class="divider">
          <div style="display:flex;gap:12px;margin-top:12px;flex-wrap:wrap;">
            <button class="btn btn-danger" @click="confirmDelete">🗑️ 注销账号</button>
            <button class="btn">📱 绑定设备</button>
            <button class="btn" @click="switchAccount">🔄 切换账号</button>
          </div>
        </div>

        <!-- Account Deletion Warning -->
        <div v-show="activeNav === 'delete'" class="settings-section danger-zone">
          <div class="settings-section-title danger">🗑️ 注销账号</div>
          <p class="danger-warning">
            注销账号将删除所有个人数据，包括宠物、金币、装扮、成就等。此操作不可恢复！
          </p>
          <button class="btn btn-danger" @click="confirmDelete">确认注销</button>
        </div>

        <!-- About Section -->
        <div v-show="activeNav === 'about'" class="settings-section">
          <div class="settings-section-title">ℹ️ 关于我们</div>
          <div class="about-content">
            <div class="about-logo">🐾</div>
            <div class="about-name">EV Pet</div>
            <div class="about-version">Version 1.0.0</div>
            <div class="about-desc">一个温馨的AI虚拟宠物养成平台</div>
          </div>
          <hr class="divider" style="margin:20px 0;">
          <div class="about-links">
            <a href="#" class="about-link">📜 用户协议</a>
            <a href="#" class="about-link">🔒 隐私政策</a>
            <a href="#" class="about-link">📧 联系我们</a>
          </div>
        </div>

        <div class="version-info">
          EV Pet v1.0.0 · Made with 🐾 by Des · <a href="#">用户协议</a> · <a href="#">隐私政策</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore, usePetStore } from '../../store'

const router = useRouter()
const userStore = useUserStore()
const petStore = usePetStore()

const activeNav = ref('profile')

const settingsNav = [
  { id: 'profile', label: '个人信息', icon: '👤' },
  { id: 'notification', label: '通知设置', icon: '🔔' },
  { id: 'privacy', label: '隐私安全', icon: '🔒' },
  { id: 'appearance', label: '外观设置', icon: '🎨' },
  { id: 'account', label: '账号管理', icon: '📋' },
  { id: 'delete', label: '注销账号', icon: '🗑️' },
  { id: 'about', label: '关于我们', icon: 'ℹ️' }
]

const petName = computed({
  get: () => petStore.petInfo?.name || '小甜饼',
  set: (val) => {
    if (petStore.petInfo) {
      petStore.petInfo.name = val
    }
  }
})

const userInfo = ref({
  id: 'EV20240415001',
  nickname: userStore.userInfo?.nickname || '裴浩宇',
  phone: '138****1234',
  email: 'pei***@gmail.com',
  bio: '和小甜饼一起生活的第42天！🐱✨',
  gender: 'male',
  birthday: '2000-01-01',
  timezone: 'Asia/Shanghai',
  tags: ['🌟 资深铲屎官', '🎨 装扮达人'],
  createdAt: '2026-04-15',
  vipStatus: '🌟 月卡会员',
  vipExpire: '2026-05-15 到期'
})

const notificationSettings = ref({
  pet: { title: '宠物状态提醒', desc: '饱食度、心情、体力较低时发送通知', value: true },
  daily: { title: '每日签到提醒', desc: '固定时间提醒你领取每日奖励', value: true },
  achievement: { title: '成就解锁通知', desc: '解锁成就时显示动画和奖励', value: true },
  shop: { title: '商店折扣推送', desc: '接收限时折扣和特惠活动通知', value: false },
  summary: { title: 'AI 对话摘要', desc: '每日自动生成宠物状态摘要推送', value: true }
})

const notifyTime = ref('09:00')
const twoFactor = ref(false)
const passwordForm = ref({ current: '', new: '', confirm: '' })

const themes = [
  { id: 'pink', name: '粉糖少女', emoji: '🌸' },
  { id: 'mint', name: '薄荷清新', emoji: '🌿' },
  { id: 'night', name: '暗夜星空', emoji: '🌙' }
]
const activeTheme = ref('pink')
const themeColors = ['#FFB3C6', '#A8D8EA', '#FFE5A0', '#D5AAFF', '#B8F1CC', '#FFD5E5', '#C9E8F7', '#FFF0BA', '#E8D5F5', '#4A3F55']
const activeColor = ref('#FFB3C6')
const petAnimation = ref(true)
const soundEffect = ref(true)
const bgMusic = ref(false)
const volume = ref(70)

const saveProfile = () => {
  userStore.userInfo = { ...userStore.userInfo, nickname: userInfo.value.nickname }
  ElMessage.success('个人信息已保存')
}

const updatePassword = () => {
  if (!passwordForm.value.current) {
    ElMessage.warning('请输入当前密码')
    return
  }
  if (!passwordForm.value.new || passwordForm.value.new.length < 8) {
    ElMessage.warning('新密码至少8位')
    return
  }
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  ElMessage.success('密码已更新')
  passwordForm.value = { current: '', new: '', confirm: '' }
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}

const switchAccount = () => {
  ElMessage.info('正在跳转登录页...')
  userStore.logout()
  router.push('/login')
}

const confirmDelete = () => {
  ElMessageBox.confirm('注销账号将删除所有数据，此操作不可恢复！确定要注销吗？', '危险操作', {
    confirmButtonText: '确定注销',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    ElMessage.info('账号注销功能开发中...')
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.settings-page {
  padding: 0;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
  color: $ev-text;
}

.page-subtitle {
  font-size: 14px;
  color: $ev-text-sec;
  margin-bottom: 28px;
}

.settings-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 28px;
}

.settings-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.settings-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: $ev-radius-sm;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: $ev-text-sec;
  border: 1px solid transparent;
}

.settings-nav-item:hover {
  background: #fff;
  color: $ev-text;
  border-color: $ev-bg;
}

.settings-nav-item.active {
  background: #fff;
  color: $ev-purple;
  font-weight: 700;
  border-color: $ev-purple-light;
  box-shadow: 0 2px 8px rgba(180, 150, 200, 0.08);
}

.settings-nav-item .nav-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.settings-nav-item .nav-count {
  margin-left: auto;
  font-size: 11px;
  background: $ev-primary;
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
}

.settings-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-section {
  background: #fff;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(180, 150, 200, 0.08);
  border: 1px solid $ev-bg;
}

.settings-section-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.settings-section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, $ev-primary, $ev-purple);
  border-radius: 2px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid $ev-bg;
  margin-bottom: 20px;
}

.profile-avatar-wrap {
  position: relative;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba($ev-purple, 0.3);
}

.profile-avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 26px;
  height: 26px;
  background: $ev-purple;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  border: 2px solid #fff;
  cursor: pointer;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
}

.profile-id {
  font-size: 12px;
  color: #A898B8;
  margin-bottom: 8px;
}

.profile-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.profile-tag {
  padding: 4px 12px;
  background: #F9F5FF;
  border-radius: 20px;
  font-size: 11px;
  color: $ev-purple;
  border: 1px solid $ev-purple-light;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid $ev-bg;
}

.form-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.form-label {
  width: 140px;
  font-size: 14px;
  font-weight: 500;
  flex-shrink: 0;
}

.form-control {
  flex: 1;
}

.form-hint {
  font-size: 12px;
  color: #A898B8;
  margin-top: 4px;
}

.input-field {
  width: 100%;
  padding: 10px 14px;
  border-radius: $ev-radius-sm;
  border: 1px solid $ev-purple-light;
  font-size: 14px;
  font-family: inherit;
  color: $ev-text;
  background: #FAFAFA;
  transition: all 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: $ev-purple;
  background: #fff;
  box-shadow: 0 0 0 3px rgba($ev-purple, 0.1);
}

.input-field::placeholder {
  color: #A898B8;
}

textarea.input-field {
  resize: vertical;
  min-height: 80px;
}

.select-field {
  width: 100%;
  padding: 10px 14px;
  border-radius: $ev-radius-sm;
  border: 1px solid $ev-purple-light;
  font-size: 14px;
  font-family: inherit;
  color: $ev-text;
  background: #FAFAFA;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8'%3E%3Cpath fill='%237A6B8A' d='M6 8L0 0h12z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
}

.select-field:focus {
  outline: none;
  border-color: $ev-purple;
  background-color: #fff;
}

.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.toggle-info {}

.toggle-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.toggle-desc {
  font-size: 12px;
  color: #7A6B8A;
}

.toggle {
  width: 48px;
  height: 26px;
  background: #E0E0E0;
  border-radius: 13px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;
}

.toggle.on {
  background: #B8F1CC;
}

.toggle::after {
  content: '';
  position: absolute;
  top: 3px;
  left: 3px;
  width: 20px;
  height: 20px;
  background: #fff;
  border-radius: 50%;
  transition: all 0.3s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.toggle.on::after {
  left: 25px;
}

.color-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.color-option {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.2s;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.selected {
  border-color: $ev-text;
  box-shadow: 0 0 0 3px rgba($ev-text, 0.15);
}

.btn {
  padding: 10px 20px;
  border-radius: $ev-radius-sm;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: #7A6B8A;
}

.btn:hover {
  border-color: $ev-purple;
  color: $ev-purple;
}

.btn-primary {
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  border: none;
  padding: 12px 28px;
  border-radius: $ev-radius-sm + 2px;
  font-size: 14px;
  font-weight: 700;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba($ev-purple, 0.35);
}

.btn-danger {
  color: #FF6B6B;
  border-color: #FFE5E5;
}

.btn-danger:hover {
  background: #FFF0F0;
  border-color: #FF6B6B;
}

.btn-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.divider {
  border: none;
  border-top: 1px solid $ev-bg;
  margin: 4px 0;
}

.vip-badge {
  background: linear-gradient(135deg, $ev-yellow, $ev-primary);
  color: #fff;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.danger-zone {
  border: 1px solid #FFE5E5;
}

.settings-section-title.danger::before {
  background: #FF6B6B;
}

.danger-warning {
  font-size: 14px;
  color: #FF6B6B;
  line-height: 1.6;
  margin-bottom: 16px;
}

.about-content {
  text-align: center;
  padding: 20px 0;
}

.about-logo {
  font-size: 64px;
  margin-bottom: 12px;
}

.about-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}

.about-version {
  font-size: 13px;
  color: #A898B8;
  margin-bottom: 8px;
}

.about-desc {
  font-size: 14px;
  color: $ev-text-sec;
}

.about-links {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.about-link {
  color: $ev-purple;
  text-decoration: none;
  font-size: 13px;
}

.about-link:hover {
  text-decoration: underline;
}

.version-info {
  text-align: center;
  padding: 20px;
  font-size: 12px;
  color: #A898B8;
}

.version-info a {
  color: $ev-purple;
  text-decoration: none;
}

.version-info a:hover {
  text-decoration: underline;
}
</style>
