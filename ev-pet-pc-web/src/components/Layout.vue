<template>
  <div class="ev-layout">
    <!-- ── 左侧边栏 ── -->
    <aside class="ev-sidebar">
      <!-- Logo -->
      <div class="ev-logo">
        <div class="ev-logo-icon">🦊</div>
        <span class="ev-logo-text">EV Pet</span>
      </div>

      <!-- 导航 -->
      <nav class="ev-nav">
        <div class="ev-nav-section">
          <span class="ev-nav-label">主菜单</span>
          <button
            v-for="item in mainNav"
            :key="item.path"
            class="ev-nav-item"
            :class="{ active: currentRoute === item.path }"
            @click="navigate(item.path)"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span class="ev-nav-text">{{ item.label }}</span>
          </button>
        </div>

        <div class="ev-nav-section">
          <span class="ev-nav-label">我的</span>
          <button
            v-for="item in myNav"
            :key="item.path"
            class="ev-nav-item"
            :class="{ active: currentRoute === item.path }"
            @click="navigate(item.path)"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span class="ev-nav-text">{{ item.label }}</span>
          </button>
        </div>
      </nav>

      <!-- 侧边栏底部用户信息 -->
      <div class="ev-sidebar-footer">
        <div class="ev-user-card">
          <div class="ev-user-avatar">{{ userAvatar }}</div>
          <div class="ev-user-info">
            <span class="ev-user-name">{{ userStore.userInfo?.nickname || '玩家' }}</span>
            <span class="ev-user-role">{{ userStore.userInfo?.title || '冒险者' }} Lv.{{ userStore.userInfo?.level || 1 }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- ── 主内容区 ── -->
    <div class="ev-main">
      <!-- 顶部栏 -->
      <header class="ev-header">
        <div class="ev-header-left">
          <span class="ev-page-title">{{ pageTitle }}</span>
          <span class="ev-page-date">{{ today }}</span>
        </div>
        <div class="ev-header-right">
          <button class="ev-header-btn">
            <span>🔔</span>
            <span class="ev-header-btn-text">提醒</span>
          </button>
          <button class="ev-header-btn ev-header-btn-primary" @click="newChat">
            <span>✨</span>
            <span class="ev-header-btn-text">新建对话</span>
          </button>
          <div class="ev-header-gold">
            <span>💰</span>
            <span class="ev-gold-num">{{ userStore.userInfo?.gold || 0 }}</span>
            <span class="ev-gold-label">金币余额</span>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="ev-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mainNav = [
  { icon: '📊', label: '仪表盘', path: '/' },
  { icon: '💬', label: 'AI对话', path: '/chat' },
  { icon: '🌱', label: '养成中心', path: '/growth' },
  { icon: '🎨', label: '装扮工作室', path: '/studio' },
  { icon: '🛒', label: '商店', path: '/shop' },
]

const myNav = [
  { icon: '🏆', label: '成就', path: '/achievement' },
  { icon: '⚙️', label: '设置', path: '/settings' },
  { icon: '👥', label: '运营后台', path: '/admin', adminOnly: true },
]

const currentRoute = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/': '仪表盘',
    '/chat': 'AI对话',
    '/growth': '养成中心',
    '/studio': '装扮工作室',
    '/shop': '商店',
    '/work': '打工系统',
    '/social': '社交中心',
    '/achievement': '成就系统',
    '/settings': '设置',
  }
  return titles[route.path] || 'EV Pet'
})

const today = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

const userAvatar = computed(() => {
  return userStore.userInfo?.nickname?.charAt(0) || '我'
})

const navigate = (path) => {
  if (path === '/admin') {
    window.open('http://localhost:5177', '_blank')
    return
  }
  router.push(path)
}

const newChat = () => {
  router.push('/chat')
}
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.ev-layout {
  display: flex;
  min-height: 100vh;
  background: $ev-bg-page;
}

// ── 侧边栏 ──────────────────────────────────────────────────
.ev-sidebar {
  width: $ev-sidebar-width;
  min-width: $ev-sidebar-width;
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-right: 1px solid $ev-border;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  z-index: 100;
}

// Logo
.ev-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px 16px;
}

.ev-logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: $ev-gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba($ev-primary, 0.4);
}

.ev-logo-text {
  font-size: 16px;
  font-weight: 700;
  color: $ev-text;
}

// 导航
.ev-nav {
  flex: 1;
  padding: 8px 12px;
}

.ev-nav-section {
  margin-bottom: 20px;
}

.ev-nav-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  color: $ev-text-muted;
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 8px;
  margin-bottom: 4px;
}

.ev-nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 9px 12px;
  border-radius: $ev-radius-sm;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;
  font-size: $ev-font-size-base;
  color: $ev-text;
  text-align: left;
  margin-bottom: 2px;

  &:hover {
    background: $ev-primary-light;
    color: $ev-primary-dark;
  }

  &.active {
    background: $ev-primary-light;
    color: $ev-primary-dark;
    font-weight: 600;
  }
}

.ev-nav-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.ev-nav-text {
  font-size: $ev-font-size-base;
}

// 侧边栏底部
.ev-sidebar-footer {
  padding: 16px;
  border-top: 1px solid $ev-border;
}

.ev-user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: $ev-bg;
  border-radius: $ev-radius-sm;
}

.ev-user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: $ev-gradient-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}

.ev-user-info {
  display: flex;
  flex-direction: column;
  gap: 1px;
  overflow: hidden;
}

.ev-user-name {
  font-size: 14px;
  font-weight: 700;
  color: $ev-text;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ev-user-role {
  font-size: 11px;
  color: $ev-text-muted;
}

// ── 主内容区 ───────────────────────────────────────────────
.ev-main {
  margin-left: $ev-sidebar-width;
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

// 顶部栏
.ev-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid $ev-border;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 50;
}

.ev-header-left {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.ev-page-title {
  font-size: 18px;
  font-weight: 700;
  color: $ev-text;
}

.ev-page-date {
  font-size: 11px;
  color: $ev-text-muted;
}

.ev-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ev-header-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: $ev-radius-sm;
  border: 1px solid $ev-border;
  background: white;
  color: $ev-text-sec;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;
  box-shadow: $ev-shadow;

  &:hover {
    background: $ev-primary-light;
    color: $ev-primary-dark;
    border-color: $ev-primary-light;
  }

  &.ev-header-btn-primary {
    background: $ev-gradient-primary;
    color: white;
    border: none;
    box-shadow: 0 4px 12px rgba($ev-primary, 0.35);

    &:hover {
      opacity: 0.9;
      transform: translateY(-1px);
    }
  }
}

.ev-header-gold {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: $ev-bg;
  border-radius: $ev-radius-full;
  border: 1px solid $ev-border;
  font-size: 14px;
}

.ev-gold-num {
  font-weight: 700;
  color: $ev-text;
}

.ev-gold-label {
  font-size: 11px;
  color: $ev-text-muted;
}

// 页面内容
.ev-content {
  flex: 1;
  overflow: hidden;
  padding: 28px 32px;
}
</style>
