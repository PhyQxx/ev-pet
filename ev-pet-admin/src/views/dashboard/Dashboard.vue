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
/* Dashboard-specific overrides (not in global.css) */

/* Stat card hover */
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(180, 150, 200, 0.12);
}

/* Bar tooltip */
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

/* Badges — keep for Dashboard (custom colors differ from global.css) */
.badge {
  padding: 2px 9px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  display: inline-block;
}

.badge-green { background: #E8F9EE; color: #2DA44E; }
.badge-yellow { background: #FFF8E6; color: #D4A000; }
.badge-red { background: #FFF0F0; color: #FF6B6B; }
.badge-purple { background: #F5EEFF; color: #8B5CF6; }
.badge-blue { background: #E8F4FF; color: #2F80ED; }

/* Dashboard table overrides — transparent background, lighter borders */
table thead th {
  background: transparent;
  border-bottom: 2px solid #EDE4FF;
}

table tbody tr {
  border-bottom: 1px solid rgba(237, 228, 255, 0.3);
}

table tbody tr:hover {
  background: #FAF8FF;
}

table tbody td {
  background: transparent;
  border-bottom: none;
  padding: 11px 12px;
}

/* Dashboard-specific user list */
.user-list {
  gap: 10px;
}

.user-item {
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
}

.user-item:hover {
  background: #FAF8FF;
}

.user-item-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 20px;
}

/* Activity feed — transparent items on dashboard */
.activity-feed {
  gap: 0;
}

.activity-item {
  padding: 10px 0;
  border-bottom: 1px solid rgba(237, 228, 255, 0.25);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-feed .activity-dot {
  margin-top: 5px;
}

.activity-feed .activity-icon {
  font-size: 14px;
}

.activity-feed .activity-text {
  color: #7A6B8A;
}

.activity-feed .activity-time {
  font-size: 11px;
  color: #A898B8;
}
</style>
