<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">🐾</span>
        <span class="logo-text">EV Pet</span>
      </div>
      <el-menu :default-active="route.path" router class="sidebar-menu">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/content">
          <el-icon><Document /></el-icon>
          <span>内容审核</span>
        </el-menu-item>
        <el-menu-item index="/items">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/activities">
          <el-icon><MagicStick /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
        <el-menu-item index="/permissions">
          <el-icon><Lock /></el-icon>
          <span>账号权限</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <h2 class="page-title">{{ pageTitle }}</h2>
        <div class="header-right">
          <el-button type="primary" plain size="small">预览前台</el-button>
          <el-dropdown>
            <span class="admin-user">
              <el-avatar :size="32">管</el-avatar>
              <span>管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageTitle = computed(() => {
  const titles = {
    '/dashboard': '数据看板',
    '/users': '用户管理',
    '/content': '内容审核',
    '/items': '商品管理',
    '/announcements': '公告管理',
    '/activities': '活动管理',
    '/permissions': '账号权限',
    '/settings': '系统配置'
  }
  return titles[route.path] || '管理后台'
})
</script>

<style scoped>
.layout { height: 100vh; }
.sidebar { background: linear-gradient(180deg, #2C3E50 0%, #34495E 100%); }
.logo { padding: 20px; display: flex; align-items: center; gap: 10px; color: white; }
.logo-icon { font-size: 28px; }
.logo-text { font-size: 20px; font-weight: 700; }
.sidebar-menu { border-right: none; }
.header { background: white; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; border-bottom: 1px solid #eee; }
.page-title { font-size: 18px; font-weight: 600; }
.header-right { display: flex; align-items: center; gap: 16px; }
.admin-user { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.main { padding: 24px; background: #F5F7FA; }
</style>
