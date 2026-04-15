<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">🐾</span>
        <span class="logo-text">EV Pet</span>
      </div>
      
      <el-menu
        :default-active="currentRoute"
        class="sidebar-menu"
        router
        :collapse="isCollapse"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI对话</template>
        </el-menu-item>
        <el-menu-item index="/growth">
          <el-icon><TrendCharts /></el-icon>
          <template #title>养成中心</template>
        </el-menu-item>
        <el-menu-item index="/studio">
          <el-icon><Brush /></el-icon>
          <template #title>装扮工作室</template>
        </el-menu-item>
        <el-menu-item index="/shop">
          <el-icon><Shop /></el-icon>
          <template #title>商店</template>
        </el-menu-item>
        <el-menu-item index="/work">
          <el-icon><Briefcase /></el-icon>
          <template #title>打工系统</template>
        </el-menu-item>
        <el-menu-item index="/social">
          <el-icon><User /></el-icon>
          <template #title>社交中心</template>
        </el-menu-item>
        <el-menu-item index="/achievement">
          <el-icon><Trophy /></el-icon>
          <template #title>成就系统</template>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <template #title>设置</template>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-footer">
        <div class="pet-mini" @click="goHome">
          <span class="pet-mini-emoji">{{ petEmoji }}</span>
          <div class="pet-mini-info">
            <span class="pet-mini-name">{{ petStore.petInfo?.name || '小可爱' }}</span>
            <el-progress :percentage="petStore.petInfo?.health || 0" :stroke-width="4" :show-text="false" />
          </div>
        </div>
      </div>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2 class="page-title">{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <div class="user-info">
            <span class="gold">
              <span class="gold-icon">🪙</span>
              <span class="gold-num">{{ userStore.userInfo?.gold || 0 }}</span>
            </span>
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="36" class="user-avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || '我' }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="settings">设置</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, usePetStore } from '../store'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const petStore = usePetStore()

const isCollapse = false

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
    '/settings': '设置'
  }
  return titles[route.path] || 'EV Pet'
})

const petEmoji = computed(() => {
  const stage = petStore.petInfo?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const goHome = () => router.push('/')

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  } else if (command === 'profile') {
    router.push('/settings')
  } else if (command === 'settings') {
    router.push('/settings')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #FFFFFF 0%, #FFF4F8 100%);
  border-right: 1px solid rgba(213, 170, 255, 0.2);
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 36px;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

:deep(.el-menu-item) {
  border-radius: 12px;
  margin: 4px 12px;
  height: 48px;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(255, 179, 198, 0.3) 0%, rgba(213, 170, 255, 0.3) 100%);
  color: var(--primary-dark);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(213, 170, 255, 0.2);
}

.pet-mini {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.pet-mini:hover {
  background: rgba(255, 179, 198, 0.2);
}

.pet-mini-emoji {
  font-size: 40px;
}

.pet-mini-info {
  flex: 1;
}

.pet-mini-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.header {
  background: var(--bg-card);
  border-bottom: 1px solid rgba(213, 170, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.gold {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #FFF4C2 0%, #FFE5A0 100%);
  padding: 8px 16px;
  border-radius: 20px;
}

.gold-icon {
  font-size: 20px;
}

.gold-num {
  font-weight: 600;
  color: var(--text-primary);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
}

.user-name {
  font-weight: 500;
}

.main-content {
  background: var(--bg-page);
  padding: 24px;
  overflow-y: auto;
}
</style>
