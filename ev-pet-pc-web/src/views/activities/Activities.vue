<template>
  <div class="activities-page">
    <div class="page-header">
      <h2>🎉 活动中心</h2>
      <p class="page-sub">参与活动获取丰厚奖励</p>
    </div>

    <!-- Active Banner -->
    <div class="banner-card">
      <div class="banner-content">
        <div class="banner-tag">🔥 进行中</div>
        <h3>五一狂欢节</h3>
        <p>限时活动：4月30日 - 5月5日，登录即送限定宠物！</p>
        <div class="banner-progress">
          <div class="progress-bar"><div class="progress-fill" style="width:65%"></div></div>
          <span>已有 12,580 人参与</span>
        </div>
      </div>
      <div class="banner-icon">🎊</div>
    </div>

    <!-- Activity List -->
    <div class="activity-grid">
      <div v-for="act in activities" :key="act.id" class="activity-card" :class="act.status">
        <div class="activity-header">
          <span class="activity-icon">{{ act.icon }}</span>
          <el-tag v-if="act.status === 'active'" size="small" type="danger">进行中</el-tag>
          <el-tag v-else-if="act.status === 'upcoming'" size="small" type="warning">即将开始</el-tag>
          <el-tag v-else size="small">已结束</el-tag>
        </div>
        <h4>{{ act.name }}</h4>
        <p class="activity-desc">{{ act.description }}</p>
        <div class="activity-meta">
          <span>📅 {{ act.date }}</span>
          <span>👥 {{ act.participants.toLocaleString() }}人参与</span>
        </div>
        <div class="activity-rewards">
          <span v-for="(r, i) in act.rewards" :key="i" class="reward-tag">{{ r }}</span>
        </div>
        <el-button v-if="act.status === 'active'" class="join-btn" size="small">立即参与</el-button>
        <el-button v-else-if="act.status === 'upcoming'" class="join-btn upcoming" size="small">提醒我</el-button>
        <el-button v-else disabled class="join-btn" size="small">已结束</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activities = ref([
  { id: 1, name: '五一狂欢节', icon: '🎊', description: '五一假期期间登录游戏领取限定五一宠物，充值双倍金币', date: '4月30日-5月5日', participants: 12580, status: 'active', rewards: ['🐾 五一宠物', '💰 2倍金币', '🎁 宝箱'] },
  { id: 2, name: '每日签到', icon: '📅', description: '每日签到可获得金币和随机道具，连续7天额外奖励', date: '长期活动', participants: 45820, status: 'active', rewards: ['💰 金币', '🎁 道具'] },
  { id: 3, name: '宠物大赛', icon: '🏆', description: '提交您的宠物参加比赛，获胜者获得限定称号和稀有道具', date: '5月10日开启', participants: 0, status: 'upcoming', rewards: ['🏅 限定称号', '⭐ 稀有道具'] },
  { id: 4, name: '春节活动', icon: '🧧', description: '春节期间特别活动，已圆满结束', date: '1月25日-2月10日', participants: 38450, status: 'ended', rewards: ['🧧 红包', '🐉 年兽宠物'] },
])
</script>

<style scoped>
.activities-page { padding: 0; }
.page-header { margin-bottom: 24px; }
.page-header h2 { font-size: 20px; font-weight: 600; margin: 0 0 4px 0; }
.page-sub { font-size: 13px; color: var(--text-secondary, #7A6B8A); margin: 0; }
.banner-card { background: linear-gradient(135deg, #FFB3C6, #D5AAFF); border-radius: 20px; padding: 24px 28px; display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; color: #fff; }
.banner-tag { background: rgba(255,255,255,0.25); padding: 4px 12px; border-radius: 20px; font-size: 12px; display: inline-block; margin-bottom: 8px; }
.banner-card h3 { font-size: 20px; margin: 0 0 6px 0; }
.banner-card p { font-size: 13px; opacity: 0.9; margin: 0 0 12px 0; }
.banner-progress { display: flex; align-items: center; gap: 10px; }
.progress-bar { width: 160px; height: 6px; background: rgba(255,255,255,0.3); border-radius: 3px; }
.progress-fill { height: 100%; background: #fff; border-radius: 3px; }
.banner-progress span { font-size: 12px; opacity: 0.85; }
.banner-icon { font-size: 64px; }
.activity-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.activity-card { background: #fff; border-radius: 16px; padding: 20px; border: 1px solid #F0E8F8; }
.activity-card.active { border-color: #FFB3C6; }
.activity-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.activity-icon { font-size: 28px; }
.activity-card h4 { font-size: 16px; font-weight: 600; margin: 0 0 6px 0; }
.activity-desc { font-size: 12px; color: var(--text-secondary, #7A6B8A); margin: 0 0 12px 0; }
.activity-meta { display: flex; gap: 12px; font-size: 12px; color: #A898B8; margin-bottom: 12px; }
.activity-rewards { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 14px; }
.reward-tag { background: #F5F0FF; color: #7A6B8A; font-size: 11px; padding: 2px 8px; border-radius: 10px; }
.join-btn { width: 100%; background: linear-gradient(135deg, #FFB3C6, #D5AAFF) !important; border: none !important; color: #fff !important; font-weight: 600; }
.join-btn.upcoming { background: linear-gradient(135deg, #FFE5A0, #FFD166) !important; }
</style>
