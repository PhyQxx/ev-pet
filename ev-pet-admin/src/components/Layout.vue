<template>
  <el-container class="ev-admin-layout">
    <!-- Sidebar -->
    <el-aside width="220px" class="ev-sidebar">
      <!-- Logo -->
      <div class="ev-logo">
        <div class="ev-logo-icon">🐾</div>
        <span class="ev-logo-text">EV Pet</span>
      </div>

      <!-- Nav -->
      <nav class="ev-nav">
        <div class="ev-nav-section">
          <div class="ev-nav-label">数据概览</div>
          <router-link
            v-for="item in overviewNav"
            :key="item.path"
            :to="item.path"
            class="ev-nav-item"
            :class="{ active: isActive(item.path) }"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </router-link>
        </div>

        <div class="ev-nav-section">
          <div class="ev-nav-label">用户与内容</div>
          <router-link
            v-for="item in userNav"
            :key="item.path"
            :to="item.path"
            class="ev-nav-item"
            :class="{ active: isActive(item.path) }"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
            <span v-if="item.count" class="ev-nav-count">{{ item.count }}</span>
            <span v-if="item.dot" class="ev-nav-dot"></span>
          </router-link>
        </div>

        <div class="ev-nav-section">
          <div class="ev-nav-label">商城与运营</div>
          <router-link
            v-for="item in shopNav"
            :key="item.path"
            :to="item.path"
            class="ev-nav-item"
            :class="{ active: isActive(item.path) }"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </router-link>
        </div>

        <div class="ev-nav-section">
          <div class="ev-nav-label">系统</div>
          <router-link
            v-for="item in systemNav"
            :key="item.path"
            :to="item.path"
            class="ev-nav-item"
            :class="{ active: isActive(item.path) }"
          >
            <span class="ev-nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
            <span v-if="item.dot" class="ev-nav-dot"></span>
          </router-link>
        </div>
      </nav>
    </el-aside>

    <el-container class="ev-inner-container">
      <!-- Topbar -->
      <div class="ev-topbar">
        <div class="ev-topbar-brand">
          <span class="ev-topbar-brand-text">EV Pet · 运营后台</span>
        </div>
        <div class="ev-topbar-right">
          <div class="ev-admin-info">
            <span>👤 管理员：{{ adminName }}</span>
          </div>
          <div class="ev-admin-avatar">👨‍💻</div>
          <span class="ev-topbar-badge">Beta</span>
        </div>
      </div>

      <!-- Main Content -->
      <el-main class="ev-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const adminName = '小明'

const overviewNav = [
  { icon: '📊', label: '数据看板', path: '/dashboard' },
  { icon: '📈', label: '运营报表', path: '/report' },
]

const userNav = [
  { icon: '👥', label: '用户管理', path: '/users', count: 2 },
  { icon: '📝', label: '内容审核', path: '/content', dot: true },
  { icon: '🐾', label: '宠物管理', path: '/pets' },
]

const shopNav = [
  { icon: '🛒', label: '商品管理', path: '/items' },
  { icon: '📢', label: '公告管理', path: '/announcements' },
  { icon: '🎉', label: '活动管理', path: '/activities' },
]

const systemNav = [
  { icon: '⚙️', label: '账号权限', path: '/permissions' },
  { icon: '🔧', label: '系统配置', path: '/settings', dot: true },
]

const isActive = (path) => {
  if (path === '/dashboard') return route.path === '/' || route.path === '/dashboard'
  return route.path.startsWith(path)
}
</script>

<style scoped>
/* ── Layout ── */
.ev-admin-layout {
  display: flex;
  flex-direction: row;
  height: 100vh;
}

/* ── Sidebar ── */
.ev-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: linear-gradient(180deg, #4A3F55 0%, #2C3E50 100%);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
}

/* Logo */
.ev-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px 16px;
}

.ev-logo-icon {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.ev-logo-text {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}

/* Nav */
.ev-nav {
  padding: 4px 12px 20px;
  flex: 1;
}

.ev-nav-section {
  margin-bottom: 20px;
}

.ev-nav-label {
  font-size: 10px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.35);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 0 8px;
  margin-bottom: 6px;
}

.ev-nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.2s;
  border: 1px solid transparent;
  margin-bottom: 2px;
  text-decoration: none;
  position: relative;
}

.ev-nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
}

.ev-nav-item.active {
  background: rgba(213, 170, 255, 0.2);
  color: #D5AAFF;
  font-weight: 700;
  border-color: rgba(213, 170, 255, 0.3);
}

.ev-nav-icon {
  font-size: 14px;
  width: 18px;
  text-align: center;
  flex-shrink: 0;
}

.ev-nav-count {
  margin-left: auto;
  font-size: 10px;
  background: rgba(255, 107, 107, 0.2);
  color: #FF6B6B;
  padding: 2px 7px;
  border-radius: 8px;
}

.ev-nav-dot {
  margin-left: auto;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #B8F1CC;
  box-shadow: 0 0 6px rgba(184, 241, 204, 0.5);
}

/* ── Inner Container ── */
.ev-inner-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

/* ── Topbar ── */
.ev-topbar {
  background: linear-gradient(135deg, #4A3F55 0%, #6B5B8A 100%);
  padding: 16px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
  box-shadow: 0 2px 12px rgba(74, 63, 85, 0.25);
}

.ev-topbar-brand-text {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}

.ev-topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ev-admin-info {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  gap: 8px;
}

.ev-admin-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #D5AAFF, #FFB3C6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(213, 170, 255, 0.4);
}

.ev-topbar-badge {
  background: #FF6B6B;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px;
  font-weight: 700;
}

/* ── Main ── */
.ev-main {
  flex: 1;
  background: var(--ev-bg-page);
  padding: 24px 28px;
  overflow-y: auto;
}
</style>
