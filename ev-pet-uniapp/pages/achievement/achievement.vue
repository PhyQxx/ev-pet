<template>
  <view class="achievement-page">
    <!-- Topbar -->
    <view class="topbar">
      <view class="topbar-title">🏆 成就系统</view>
      <view class="topbar-sub">解锁徽章，收集勋章，成为最优秀的宠物主人</view>
    </view>

    <!-- Summary Bar -->
    <view class="summary-bar">
      <view class="summary-chip">
        <view class="summary-val">{{ summary.unlocked }}</view>
        <view class="summary-label">已解锁</view>
      </view>
      <view class="summary-chip">
        <view class="summary-val">{{ summary.points }}</view>
        <view class="summary-label">成就积分</view>
      </view>
      <view class="summary-chip">
        <view class="summary-val">{{ summary.streak }}天</view>
        <view class="summary-label">连续完成</view>
      </view>
    </view>

    <!-- Rank Banner -->
    <view class="rank-banner" v-if="rankInfo">
      <view class="rank-left">
        <text class="rank-emoji">{{ rankInfo.currentIcon }}</text>
      </view>
      <view class="rank-content">
        <view class="rank-title">{{ rankInfo.currentTitle }}</view>
        <view class="rank-progress-text">{{ rankInfo.progressText }}</view>
        <view class="rank-track">
          <view class="rank-fill" :style="{ width: rankInfo.progress + '%' }"></view>
        </view>
      </view>
      <view class="rank-next">
        <text class="rank-next-emoji">{{ rankInfo.nextIcon }}</text>
        <view class="rank-next-label">{{ rankInfo.nextTitle }}</view>
      </view>
    </view>

    <!-- Tabs -->
    <view class="tabs">
      <view class="tab" :class="{ active: currentTab === 'all' }" @click="switchTab('all')">全部</view>
      <view class="tab" :class="{ active: currentTab === 'care' }" @click="switchTab('care')">🪄 养成</view>
      <view class="tab" :class="{ active: currentTab === 'social' }" @click="switchTab('social')">💬 互动</view>
      <view class="tab" :class="{ active: currentTab === 'special' }" @click="switchTab('special')">⭐ 特殊</view>
    </view>

    <view class="content">
      <!-- Medal Wall -->
      <view class="medal-section">
        <view class="section-title">🏅 勋章墙</view>
        <view class="medal-grid">
          <view v-for="medal in medalWall" :key="medal.id" class="medal-item" :class="{ locked: medal.status === 'locked' }">
            <text class="medal-emoji">{{ medal.icon }}</text>
            <view class="medal-name">{{ medal.status === 'locked' ? '???' : medal.name }}</view>
            <view class="medal-count">{{ medal.status === 'locked' ? '未解锁' : medal.count + '枚' }}</view>
          </view>
        </view>
      </view>

      <!-- Achievement Cards -->
      <view v-for="item in filteredAchievements" :key="item.id"
        class="ach-card"
        :class="getCardClass(item.status)"
        @click="onCardClick(item)"
      >
        <view class="ach-icon" :style="{ background: getIconBg(item.status) }">
          <text class="ach-icon-emoji">{{ item.icon }}</text>
        </view>
        <view class="ach-info">
          <view class="ach-name">{{ item.name }}</view>
          <view class="ach-desc">{{ item.description }}</view>
          <view class="ach-progress" v-if="item.status !== 'locked'">
            <view class="ach-progress-fill" :style="{
              width: item.status === 'completed' ? '100%' : (item.progress / item.target * 100) + '%',
              background: getProgressColor(item.status)
            }"></view>
          </view>
          <view class="ach-progress-text">
            <text v-if="item.status === 'completed'">已完成</text>
            <text v-else>{{ item.progress }} / {{ item.target }}</text>
          </view>
        </view>
        <view class="ach-right">
          <view class="ach-reward" v-if="item.reward">{{ item.reward }}</view>
          <view class="ach-status" :class="getStatusClass(item.status)">
            <text v-if="item.status === 'completed'">✓ 已获得</text>
            <text v-else-if="item.status === 'in_progress' && item.claimable">领取</text>
            <text v-else-if="item.status === 'in_progress'">进行中</text>
            <text v-else>🔒 锁定</text>
          </view>
        </view>
      </view>

      <view style="height: 40px;"></view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { achievement } from '@/utils/api.js'

const currentTab = ref('all')
const achievements = ref([])
const summary = ref({ unlocked: 0, points: 0, streak: 0 })
const rankInfo = ref(null)

const medalWall = computed(() =>
  achievements.value.slice(0, 8).map(a => ({
    id: a.id,
    icon: a.icon,
    name: a.name,
    count: a.claimedCount || 1,
    status: a.status
  }))
)

const filteredAchievements = computed(() => {
  if (currentTab.value === 'all') return achievements.value
  return achievements.value.filter(a => a.category === currentTab.value)
})

onMounted(() => {
  fetchAchievements()
})

async function fetchAchievements() {
  try {
    const data = await achievement.getList()
    achievements.value = data.list || data || []
    if (data.summary) summary.value = data.summary
    if (data.rankInfo) rankInfo.value = data.rankInfo
    if (!data.summary && achievements.value.length) {
      const unlocked = achievements.value.filter(a => a.status === 'completed').length
      summary.value = { unlocked, points: unlocked * 100, streak: data.streak || 0 }
      rankInfo.value = data.rankInfo || buildRankInfo(unlocked)
    }
  } catch (e) {
    mockData()
  }
}

function mockData() {
  achievements.value = [
    { id: 1, name: '初次相遇', icon: '🌟', description: '迎接你的第一只宠物', progress: 1, target: 1, status: 'completed', reward: '🪙 100', category: 'care', claimedCount: 1 },
    { id: 2, name: '喂食达人', icon: '🍖', description: '累计喂食 100 次', progress: 100, target: 100, status: 'completed', reward: '🪙 200', category: 'care', claimedCount: 3 },
    { id: 3, name: '成就猎人', icon: '🏆', description: '解锁 20 个成就', progress: 13, target: 20, status: 'in_progress', reward: '🪙 500', category: 'all', claimable: false },
    { id: 4, name: '话痨主人', icon: '💬', description: '与宠物对话 50 次', progress: 40, target: 50, status: 'in_progress', reward: '💎 50', category: 'social', claimable: false },
    { id: 5, name: '至尊主人', icon: '👑', description: '解锁全部成就（48个）', progress: 0, target: 48, status: 'locked', reward: '🌟', category: 'all' },
    { id: 6, name: '爱干净', icon: '🛁', description: '累计洗澡 30 次', progress: 21, target: 30, status: 'in_progress', reward: '🪙 150', category: 'care', claimable: false },
    { id: 7, name: '进化大师', icon: '🐱', description: '宠物进化至完全体', progress: 30, target: 100, status: 'in_progress', reward: '🪙 300', category: 'care', claimable: false },
    { id: 8, name: '全满属性', icon: '⭐', description: '四维属性全部满值', progress: 0, target: 4, status: 'locked', reward: '💎 100', category: 'care' },
    { id: 9, name: '初次对话', icon: '💬', description: '与宠物完成第一次对话', progress: 1, target: 1, status: 'completed', reward: '🪙 50', category: 'social', claimedCount: 1 },
    { id: 10, name: '社交达人', icon: '🤝', description: '添加 10 个好友', progress: 8, target: 10, status: 'in_progress', reward: '🪙 200', category: 'social', claimable: false },
    { id: 11, name: '合照大师', icon: '📸', description: '与好友宠物合照 20 次', progress: 0, target: 20, status: 'locked', reward: '🪙 300', category: 'social' },
    { id: 12, name: '连续登录 7 天', icon: '🔥', description: '本周每日签到', progress: 7, target: 7, status: 'in_progress', reward: '💎 30', category: 'special', claimable: true },
    { id: 13, name: '早睡早起', icon: '🌙', description: '连续 14 天 22:00 前入睡', progress: 14, target: 14, status: 'completed', reward: '💎 20', category: 'special', claimedCount: 1 },
    { id: 14, name: '钻石会员', icon: '💎', description: '累计充值 500 元', progress: 0, target: 500, status: 'locked', reward: '🌟', category: 'special' },
    { id: 15, name: '首次登录', icon: '🏅', description: '完成首次登录', progress: 1, target: 1, status: 'completed', reward: '🪙 50', category: 'all', claimedCount: 1 },
    { id: 16, name: '心灵导师', icon: '🔮', description: '与宠物谈心 20 次', progress: 5, target: 20, status: 'in_progress', reward: '🪙 150', category: 'social', claimable: false },
  ]
  summary.value = { unlocked: 5, points: 2340, streak: 7 }
  rankInfo.value = {
    currentIcon: '🌟',
    currentTitle: '资深铲屎官',
    nextIcon: '🥇',
    nextTitle: '大师级',
    progress: 65,
    progressText: '再完成 12 个成就升至「大师级」'
  }
}

function buildRankInfo(unlocked) {
  const ranks = [
    { min: 0, icon: '🌱', title: '新手主人' },
    { min: 5, icon: '🌟', title: '资深铲屎官' },
    { min: 15, icon: '🥇', title: '大师级' },
    { min: 30, icon: '👑', title: '至尊级' },
  ]
  let current = ranks[0]
  let nextRank = ranks[1]
  for (let i = 0; i < ranks.length; i++) {
    if (unlocked >= ranks[i].min) {
      current = ranks[i]
      nextRank = ranks[i + 1] || ranks[i]
    }
  }
  const currentIdx = ranks.indexOf(current)
  const next = ranks[currentIdx + 1]
  const prevMin = current.min
  const range = next ? next.min - prevMin : prevMin
  const progress = next ? Math.min(100, Math.round((unlocked - prevMin) / range * 100)) : 100
  return {
    currentIcon: current.icon,
    currentTitle: current.title,
    nextIcon: next ? next.icon : current.icon,
    nextTitle: next ? next.title : current.title,
    progress,
    progressText: next ? `再完成 ${next.min - unlocked} 个成就升至「${next.title}」` : '已达成最高称号'
  }
}

function switchTab(tab) {
  currentTab.value = tab
}

function getCardClass(status) {
  if (status === 'completed') return 'completed'
  if (status === 'in_progress') return 'in-progress'
  return 'locked'
}

function getIconBg(status) {
  if (status === 'completed') return '#FFF0F5'
  if (status === 'in_progress') return '#F0F8FF'
  return '#F0EDF5'
}

function getProgressColor(status) {
  if (status === 'completed') return '#B8F1CC'
  if (status === 'in_progress') return '#A8D8EA'
  return '#E0D8E8'
}

function getStatusClass(status) {
  if (status === 'completed') return 'done'
  if (status === 'in_progress') return 'active'
  return 'lock'
}

function onCardClick(item) {
  if (item.status === 'in_progress' && item.claimable) {
    uni.showToast({ title: '奖励已领取！', icon: 'success' })
    return
  }
  if (item.status === 'locked') {
    uni.showToast({ title: '未解锁', icon: 'none' })
    return
  }
  uni.showToast({ title: item.name, icon: 'none' })
}
</script>

<style scoped>
.achievement-page {
  min-height: 100vh;
  background: #FFF9FB;
  max-width: 430px;
  margin: 0 auto;
  padding-bottom: 72px;
}

/* Topbar */
.topbar {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  padding: 16px 16px 14px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.topbar-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 2px;
}
.topbar-sub {
  font-size: 11px;
  color: rgba(255,255,255,0.85);
}

/* Summary Bar */
.summary-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #F5EEF8;
}
.summary-chip {
  flex: 1;
  background: #FAFAFA;
  border-radius: 14px;
  padding: 10px 8px;
  text-align: center;
  border: 1px solid #F5EEF8;
}
.summary-val {
  font-size: 22px;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 2px;
}
.summary-label {
  font-size: 10px;
  color: #7A6B8A;
}

/* Rank Banner */
.rank-banner {
  margin: 12px 16px;
  background: linear-gradient(135deg, #FFD166, #FFB3C6);
  border-radius: 18px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  color: #fff;
  position: relative;
  overflow: hidden;
}
.rank-banner::before {
  content: '';
  position: absolute;
  right: -10px;
  top: -10px;
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.15);
  border-radius: 50%;
}
.rank-left { position: relative; z-index: 1; }
.rank-emoji { font-size: 44px; }
.rank-content { flex: 1; position: relative; z-index: 1; }
.rank-title { font-size: 15px; font-weight: 700; margin-bottom: 2px; }
.rank-progress-text { font-size: 11px; opacity: 0.9; margin-bottom: 6px; }
.rank-track { height: 7px; background: rgba(255,255,255,0.3); border-radius: 7px; overflow: hidden; }
.rank-fill { height: 100%; background: #fff; border-radius: 7px; transition: width 0.8s; }
.rank-next { text-align: center; position: relative; z-index: 1; }
.rank-next-emoji { font-size: 28px; display: block; margin-bottom: 2px; }
.rank-next-label { font-size: 10px; opacity: 0.85; }

/* Tabs */
.tabs {
  display: flex;
  gap: 6px;
  padding: 12px 16px 0;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid #EDE4FF;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
}
.tab.active {
  background: #D5AAFF;
  color: #fff;
  border-color: #D5AAFF;
}

/* Content */
.content {
  padding: 14px 16px;
}

/* Medal Wall */
.medal-section { margin-bottom: 18px; }
.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #4A3F55;
  margin-bottom: 10px;
}
.medal-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.medal-item {
  background: #fff;
  border-radius: 16px;
  padding: 12px 8px;
  text-align: center;
  border: 1px solid #F5EEF8;
}
.medal-item.locked { opacity: 0.45; }
.medal-emoji { font-size: 30px; margin-bottom: 5px; display: block; }
.medal-name { font-size: 10px; font-weight: 600; color: #4A3F55; margin-bottom: 1px; }
.medal-count { font-size: 9px; color: #7A6B8A; }

/* Achievement Card */
.ach-card {
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(180,150,200,0.06);
  border: 1px solid #F5EEF8;
  transition: all 0.2s;
  cursor: pointer;
}
.ach-card:active { transform: scale(0.98); }
.ach-card.completed { border-color: #FFD166; background: linear-gradient(180deg, #FFFBEF 0%, #fff 70%); }
.ach-card.in-progress { border-color: #A8D8EA; }
.ach-card.locked { opacity: 0.6; }
.ach-icon {
  width: 48px;
  height: 48px;
  border-radius: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ach-icon-emoji { font-size: 26px; }
.ach-info { flex: 1; }
.ach-name { font-size: 13px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; }
.ach-desc { font-size: 10px; color: #7A6B8A; margin-bottom: 5px; }
.ach-progress { height: 5px; background: #F0EDF5; border-radius: 5px; overflow: hidden; margin-bottom: 3px; }
.ach-progress-fill { height: 100%; border-radius: 5px; transition: width 0.8s; }
.ach-progress-text { font-size: 10px; color: #7A6B8A; }
.ach-right { text-align: center; flex-shrink: 0; }
.ach-reward { font-size: 13px; font-weight: 700; color: #4A3F55; margin-bottom: 4px; }
.ach-status {
  font-size: 10px;
  padding: 3px 9px;
  border-radius: 10px;
  font-weight: 600;
  display: inline-block;
}
.ach-status.done { background: #F0FFF5; color: #3BAF5D; }
.ach-status.active { background: #F0F8FF; color: #4A90D9; }
.ach-status.lock { background: #F0EDF5; color: #A898B8; }
</style>
