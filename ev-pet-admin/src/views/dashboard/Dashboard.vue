<template>
  <div class="dashboard">
    <!-- Page Header -->
    <div class="page-title">📊 数据看板</div>
    <div class="page-sub">实时数据一览 · {{ new Date().toLocaleDateString('zh-CN') }} 更新</div>

    <!-- Stat Cards Row -->
    <div class="stat-row">
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFF0F5;">👥</div>
        <div class="stat-val">{{ statsData.dau > 0 ? statsData.dau.toLocaleString() : '—' }}</div>
        <div class="stat-label">今日活跃用户 (DAU)</div>
        <div class="stat-change up">↑ 8.3% 较昨日</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#F0FFF5;">📅</div>
        <div class="stat-val">{{ statsData.mau > 0 ? statsData.mau.toLocaleString() : '—' }}</div>
        <div class="stat-label">本月活跃用户 (MAU)</div>
        <div class="stat-change up">↑ 12.6% 较上月</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFE5A0;">💰</div>
        <div class="stat-val" style="color:#D4A000;">¥{{ statsData.revenue > 0 ? statsData.revenue.toLocaleString() : '—' }}</div>
        <div class="stat-label">今日收入</div>
        <div class="stat-change up">↑ 5.1% 较昨日</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFF4C2;">💳</div>
        <div class="stat-val">{{ statsData.payingUsers > 0 ? statsData.payingUsers.toLocaleString() : '—' }}</div>
        <div class="stat-label">今日付费人数</div>
        <div class="stat-change up">↑ 3.2% 较昨日</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#F5F0FF;">📊</div>
        <div class="stat-val">{{ statsData.arpu > 0 ? '¥' + statsData.arpu.toFixed(2) : '—' }}</div>
        <div class="stat-label">ARPU</div>
        <div class="stat-change up">↑ 2.1%</div>
      </div>
    </div>

    <!-- Dashboard Grid: Chart + Activity -->
    <div class="dashboard-grid">
      <!-- Left Column -->
      <div class="dashboard-left">
        <!-- DAU Chart Card -->
        <div class="card">
          <div class="card-title">
            📈 近7日 DAU 趋势
            <span class="more">查看全部 →</span>
          </div>
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div class="chart-bar" style="height:55%;background:#FFB3C6;" data-label="周一"><div class="bar-tooltip">9,240</div></div>
              <div class="chart-bar" style="height:60%;background:#FFB3C6;" data-label="周二"><div class="bar-tooltip">10,120</div></div>
              <div class="chart-bar" style="height:70%;background:#D5AAFF;" data-label="周三"><div class="bar-tooltip">11,580</div></div>
              <div class="chart-bar" style="height:65%;background:#D5AAFF;" data-label="周四"><div class="bar-tooltip">10,890</div></div>
              <div class="chart-bar" style="height:80%;background:#A8D8EA;" data-label="周五"><div class="bar-tooltip">13,200</div></div>
              <div class="chart-bar" style="height:90%;background:#A8D8EA;" data-label="周六"><div class="bar-tooltip">14,560</div></div>
              <div class="chart-bar" style="height:100%;background:#B8F1CC;" data-label="周日"><div class="bar-tooltip">12,840</div></div>
            </div>
          </div>
          <div class="chart-summary">
            <span>↑ 周峰值 14,560（周六）</span>
            <span>平均 11,762</span>
            <span class="up">留存率 62.3%</span>
          </div>
        </div>

        <!-- Recent Users Table Card -->
        <div class="card">
          <div class="card-title">
            👥 最新注册用户
            <span class="more">查看全部 →</span>
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>用户</th>
                  <th>宠物</th>
                  <th>注册时间</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><div class="td-user"><div class="td-avatar" style="background:#FFD5E5;">🐱</div><div><div class="td-name">小华</div><div class="td-id">ID: 100234</div></div></div></td>
                  <td>小布 🐱 LV.3</td>
                  <td>2026-04-15 18:30</td>
                  <td><span class="badge badge-green">正常</span></td>
                  <td><span class="action-link">详情</span><span class="action-link" style="color:#FF6B6B;">禁用</span></td>
                </tr>
                <tr>
                  <td><div class="td-user"><div class="td-avatar" style="background:#D5AAFF;">🐰</div><div><div class="td-name">阿雪</div><div class="td-id">ID: 100233</div></div></div></td>
                  <td>小火狐 🦊 LV.18</td>
                  <td>2026-04-15 16:20</td>
                  <td><span class="badge badge-green">正常</span></td>
                  <td><span class="action-link">详情</span><span class="action-link" style="color:#FF6B6B;">禁用</span></td>
                </tr>
                <tr>
                  <td><div class="td-user"><div class="td-avatar" style="background:#FFE5A0;">🐶</div><div><div class="td-name">小林</div><div class="td-id">ID: 100232</div></div></div></td>
                  <td>旺财 🐶 LV.7</td>
                  <td>2026-04-15 14:05</td>
                  <td><span class="badge badge-yellow">待验证</span></td>
                  <td><span class="action-link">详情</span><span class="action-link" style="color:#FF6B6B;">禁用</span></td>
                </tr>
                <tr>
                  <td><div class="td-user"><div class="td-avatar" style="background:#A8D8EA;">🐹</div><div><div class="td-name">小团子</div><div class="td-id">ID: 100231</div></div></div></td>
                  <td>小绒 🐹 LV.2</td>
                  <td>2026-04-15 11:30</td>
                  <td><span class="badge badge-green">正常</span></td>
                  <td><span class="action-link">详情</span><span class="action-link" style="color:#FF6B6B;">禁用</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="dashboard-right">
        <!-- User List Card -->
        <div class="card">
          <div class="card-title">
            👑 活跃用户
            <span class="more">查看全部 →</span>
          </div>
          <div class="user-list">
            <div class="user-item">
              <div class="user-item-avatar" style="background:#FFD5E5;">🐱</div>
              <div class="user-item-info">
                <div class="user-item-name">裴浩宇</div>
                <div class="user-item-meta">今天 18:00 · LV.12</div>
              </div>
              <div class="user-item-badge badge-green">¥68</div>
            </div>
            <div class="user-item">
              <div class="user-item-avatar" style="background:#D5AAFF;">🐰</div>
              <div class="user-item-info">
                <div class="user-item-name">阿雪</div>
                <div class="user-item-meta">今天 16:20 · LV.18</div>
              </div>
              <div class="user-item-badge badge-green">¥128</div>
            </div>
            <div class="user-item">
              <div class="user-item-avatar" style="background:#FFE5A0;">🐶</div>
              <div class="user-item-info">
                <div class="user-item-name">小林</div>
                <div class="user-item-meta">昨天 22:30 · LV.7</div>
              </div>
              <div class="user-item-badge badge-purple">手机</div>
            </div>
            <div class="user-item">
              <div class="user-item-avatar" style="background:#A8D8EA;">🐹</div>
              <div class="user-item-info">
                <div class="user-item-name">小华</div>
                <div class="user-item-meta">昨天 20:15 · LV.3</div>
              </div>
              <div class="user-item-badge badge-blue">微信</div>
            </div>
            <div class="user-item">
              <div class="user-item-avatar" style="background:#FFACA8;">🐾</div>
              <div class="user-item-info">
                <div class="user-item-name">测试用户</div>
                <div class="user-item-meta">3天前 · LV.1</div>
              </div>
              <div class="user-item-badge badge-red">禁用</div>
            </div>
          </div>
        </div>

        <!-- Activity Feed Card -->
        <div class="card">
          <div class="card-title">🔔 实时动态</div>
          <div class="activity-feed">
            <div class="activity-item">
              <div class="activity-dot" style="background:#B8F1CC;"></div>
              <div class="activity-icon">👤</div>
              <div class="activity-text">用户 <b>阿雪</b> 完成了宠物进化，获得「小火狐」</div>
              <div class="activity-time">1分钟前</div>
            </div>
            <div class="activity-item">
              <div class="activity-dot" style="background:#FFE5A0;"></div>
              <div class="activity-icon">🛒</div>
              <div class="activity-text">用户 <b>小玲</b> 购买了「月卡·急速成长」</div>
              <div class="activity-time">3分钟前</div>
            </div>
            <div class="activity-item">
              <div class="activity-dot" style="background:#FFD5E5;"></div>
              <div class="activity-icon">🐾</div>
              <div class="activity-text">新用户 <b>小华</b> 刚刚领取了第一只宠物「小布」</div>
              <div class="activity-time">5分钟前</div>
            </div>
            <div class="activity-item">
              <div class="activity-dot" style="background:#A8D8EA;"></div>
              <div class="activity-icon">🏆</div>
              <div class="activity-text">用户 <b>小明</b> 达成了「连续登录7天」成就</div>
              <div class="activity-time">8分钟前</div>
            </div>
            <div class="activity-item">
              <div class="activity-dot" style="background:#FFACA8;"></div>
              <div class="activity-icon">🚨</div>
              <div class="activity-text">内容审核：发现 1 条违规宠物昵称，已标记</div>
              <div class="activity-time">12分钟前</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { stats, userAdmin } from '@/api/index.js'

// Dashboard stats data
const statsData = ref({
  dau: 0,
  mau: 0,
  revenue: 0,
  payingUsers: 0,
  arpu: 0,
  todayChange: 0
})

// Recent users table data
const recentUsers = ref([])

// Active users list
const activeUsers = ref([])

// Activity feed data
const activities = ref([])

// Chart data - DAU for last 7 days
const chartData = ref([])

const loadOverview = async () => {
  try {
    const res = await stats.getOverview()
    if (res.code === 200 && res.data) {
      const d = res.data
      statsData.value = {
        dau: d.todayActive || 0,
        mau: d.monthActive || 0,
        revenue: d.todayRevenue || 0,
        payingUsers: d.todayPayingUsers || 0,
        arpu: d.arpu || 0,
        todayChange: 8.3
      }
    }
  } catch (e) {
    console.error('加载统计概览失败', e)
  }
}

const loadDauTrend = async () => {
  try {
    const res = await stats.getDauTrend({ days: 7 })
    if (res.code === 200 && res.data) {
      const maxVal = Math.max(...res.data.map(d => d.value))
      chartData.value = res.data.map(d => ({
        label: d.label,
        value: d.value,
        height: Math.round((d.value / maxVal) * 100),
        color: '#FFB3C6'
      }))
    }
  } catch (e) {
    console.error('加载DAU趋势失败', e)
  }
}

const loadRecentUsers = async () => {
  try {
    const res = await userAdmin.list({ page: 1, pageSize: 5 })
    if (res.code === 200 && res.data?.list) {
      recentUsers.value = res.data.list.map(u => ({
        id: u.id,
        nickname: u.nickname || '未知',
        avatar: '🐱',
        bg: '#FFD5E5',
        pet: '小布',
        petEmoji: '🐱',
        level: u.level || 1,
        time: u.createTime ? new Date(u.createTime).toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit', month: '2-digit', day: '2-digit' }).replace(/\//g, '-') : '',
        status: u.status === 0 ? '禁用' : '正常'
      }))
    }
  } catch (e) {
    console.error('加载用户列表失败', e)
  }
}

const loadActiveUsers = async () => {
  try {
    const res = await userAdmin.list({ page: 1, pageSize: 5 })
    if (res.code === 200 && res.data?.list) {
      activeUsers.value = res.data.list.slice(0, 5).map(u => ({
        name: u.nickname || '未知',
        avatar: '🐱',
        bg: '#FFD5E5',
        meta: `LV.${u.level || 1}`,
        badge: '正常',
        badgeClass: 'badge-green'
      }))
    }
  } catch (e) {
    console.error('加载活跃用户失败', e)
  }
}

onMounted(() => {
  loadOverview()
  loadDauTrend()
  loadRecentUsers()
  loadActiveUsers()
  // 活动动态使用模拟数据
  activities.value = [
    { dot: '#B8F1CC', icon: '👤', text: '用户 <b>阿雪</b> 完成了宠物进化，获得「小火狐」', time: '1分钟前' },
    { dot: '#FFE5A0', icon: '🛒', text: '用户 <b>小玲</b> 购买了「月卡·急速成长」', time: '3分钟前' },
    { dot: '#FFD5E5', icon: '🐾', text: '新用户 <b>小华</b> 刚刚领取了第一只宠物「小布」', time: '5分钟前' },
    { dot: '#A8D8EA', icon: '🏆', text: '用户 <b>小明</b> 达成了「连续登录7天」成就', time: '8分钟前' },
    { dot: '#FFACA8', icon: '🚨', text: '内容审核：发现 1 条违规宠物昵称，已标记', time: '12分钟前' }
  ]
})
</script>

<style scoped>
/* Page Header */
.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}
.page-sub {
  font-size: 13px;
  color: #7A6B8A;
  margin-bottom: 24px;
}

/* Stat Cards Row */
.stat-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid #F5EEF8;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.stat-val {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 12px;
  color: #7A6B8A;
}

.stat-change {
  font-size: 11px;
  margin-top: 6px;
}

.stat-change.up {
  color: #3BAF5D;
}

.stat-change.down {
  color: #FF6B6B;
}

/* Dashboard Grid */
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 20px;
  margin-bottom: 24px;
}

.dashboard-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dashboard-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Card */
.card {
  background: #fff;
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid #F5EEF8;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title::before {
  content: '';
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, #FFB3C6, #D5AAFF);
  border-radius: 2px;
}

.card-title .more {
  margin-left: auto;
  font-size: 12px;
  color: #D5AAFF;
  cursor: pointer;
  font-weight: 500;
}

/* Chart Placeholder */
.chart-placeholder {
  height: 200px;
  background: linear-gradient(180deg, #F9F5FF 0%, #fff 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
  position: relative;
  overflow: hidden;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  height: 140px;
  padding: 0 20px;
  width: 100%;
}

.chart-bar {
  flex: 1;
  border-radius: 6px 6px 0 0;
  opacity: 0.7;
  transition: opacity 0.2s;
  cursor: pointer;
  position: relative;
}

.chart-bar:hover {
  opacity: 1;
}

.chart-bar::after {
  content: attr(data-label);
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #A898B8;
  white-space: nowrap;
}

.chart-bar .bar-tooltip {
  position: absolute;
  top: -28px;
  left: 50%;
  transform: translateX(-50%);
  background: #4A3F55;
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 6px;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
}

.chart-bar:hover .bar-tooltip {
  opacity: 1;
}

.chart-summary {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 12px;
  color: #7A6B8A;
}

.chart-summary .up {
  color: #3BAF5D;
}

/* Table */
.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

thead th {
  text-align: left;
  padding: 10px 12px;
  font-size: 11px;
  font-weight: 700;
  color: #A898B8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #F5EEF8;
}

tbody tr {
  transition: background 0.15s;
}

tbody tr:hover {
  background: #FAFAFA;
}

tbody td {
  padding: 11px 12px;
  border-bottom: 1px solid #F5EEF8;
}

.td-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.td-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.td-name {
  font-weight: 600;
}

.td-id {
  font-size: 11px;
  color: #A898B8;
}

/* Badges */
.badge {
  padding: 3px 9px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.badge-green {
  background: #F0FFF5;
  color: #3BAF5D;
}

.badge-yellow {
  background: #FFFBEF;
  color: #8B6914;
}

.badge-red {
  background: #FFF0F0;
  color: #FF6B6B;
}

.badge-purple {
  background: #F9F5FF;
  color: #9B6FD4;
}

.badge-blue {
  background: #F0F8FF;
  color: #4A90D9;
}

.action-link {
  color: #D5AAFF;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  margin-right: 10px;
}

.action-link:hover {
  text-decoration: underline;
}

/* User List */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 12px;
  transition: background 0.2s;
  cursor: pointer;
}

.user-item:hover {
  background: #FAFAFA;
}

.user-item-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.user-item-info {
  flex: 1;
}

.user-item-name {
  font-size: 13px;
  font-weight: 600;
}

.user-item-meta {
  font-size: 11px;
  color: #A898B8;
}

.user-item-badge {
  font-size: 10px;
  padding: 2px 7px;
  border-radius: 8px;
}

/* Activity Feed */
.activity-feed {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #F5EEF8;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
}

.activity-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.activity-text {
  font-size: 12px;
  line-height: 1.5;
  flex: 1;
}

.activity-time {
  font-size: 10px;
  color: #A898B8;
  white-space: nowrap;
}
</style>
