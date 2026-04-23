<template>
  <div class="admin-page">
    <div class="page-title">🏆 成就管理</div>
    <div class="page-sub">查看和管理游戏成就配置</div>

    <!-- Filter -->
    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">分类</span>
        <select class="filter-select" v-model="filterCategory">
          <option value="">全部分类</option>
          <option value="login">登录类</option>
          <option value="consume">消费类</option>
          <option value="social">社交类</option>
          <option value="pet">宠物类</option>
        </select>
      </div>
      <div class="filter-item">
        <span class="filter-label">状态</span>
        <select class="filter-select" v-model="filterStatus">
          <option value="">全部</option>
          <option value="active">进行中</option>
          <option value="completed">已完成</option>
        </select>
      </div>
      <button class="btn btn-primary" @click="loadAchievements">刷新</button>
    </div>

    <!-- Stats Cards -->
    <div class="kpi-row">
      <div class="kpi-card">
        <div class="kpi-icon" style="background:#FFF0F5;">🏆</div>
        <div class="kpi-val">{{ stats.total }}</div>
        <div class="kpi-label">成就总数</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon" style="background:#F0FFF5;">✅</div>
        <div class="kpi-val">{{ stats.completed }}</div>
        <div class="kpi-label">已完成</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon" style="background:#FFE5A0;">⏳</div>
        <div class="kpi-val">{{ stats.inProgress }}</div>
        <div class="kpi-label">进行中</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-icon" style="background:#F5F0FF;">🔒</div>
        <div class="kpi-val">{{ stats.locked }}</div>
        <div class="kpi-label">未解锁</div>
      </div>
    </div>

    <!-- Achievement List -->
    <div class="card" style="margin-top:20px;">
      <div class="card-title">成就列表</div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>图标</th>
              <th>名称</th>
              <th>描述</th>
              <th>分类</th>
              <th>进度</th>
              <th>奖励</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredList" :key="item.id">
              <td>{{ item.id }}</td>
              <td style="font-size:20px;">{{ item.icon }}</td>
              <td><b>{{ item.name }}</b></td>
              <td style="color:#7A6B8A;font-size:12px;">{{ item.description }}</td>
              <td><el-tag size="small">{{ item.category }}</el-tag></td>
              <td>
                <el-progress :percentage="item.progress" :stroke-width="6" :show-text="false" style="width:80px;display:inline-block;" />
                <span style="font-size:11px;color:#A898B8;margin-left:6px;">{{ item.current }}/{{ item.target }}</span>
              </td>
              <td>💰 {{ item.reward }}</td>
              <td>
                <el-tag v-if="item.completed" size="small" type="success">已完成</el-tag>
                <el-tag v-else-if="item.current > 0" size="small" type="warning">进行中</el-tag>
                <el-tag v-else size="small">未解锁</el-tag>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const filterCategory = ref('')
const filterStatus = ref('')

const achievements = ref([])

const stats = computed(() => ({
  total: achievements.value.length,
  completed: achievements.value.filter(a => a.completed).length,
  inProgress: achievements.value.filter(a => !a.completed && a.current > 0).length,
  locked: achievements.value.filter(a => !a.completed && a.current === 0).length
}))

const filteredList = computed(() => {
  return achievements.value.filter(a => {
    if (filterCategory.value && a.category !== filterCategory.value) return false
    if (filterStatus.value === 'active' && (!a.completed && a.current > 0)) return false
    if (filterStatus.value === 'completed' && !a.completed) return false
    return true
  })
})

const MOCK_DATA = [
  { id: 1, name: '初次见面', description: '创建角色并领取第一只宠物', icon: '🌟', category: 'pet', completed: true, current: 1, target: 1, reward: 50 },
  { id: 2, name: '7天连续登录', description: '连续登录7天', icon: '📅', category: 'login', completed: false, current: 4, target: 7, reward: 100 },
  { id: 3, name: '收集10种服装', description: '解锁10种不同服装/配饰', icon: '👗', category: 'consume', completed: false, current: 3, target: 10, reward: 200 },
  { id: 4, name: '充值任意金额', description: '完成首次充值', icon: '💎', category: 'consume', completed: false, current: 0, target: 1, reward: 0 },
  { id: 5, name: '打工达人', description: '打工累计获得1000金币', icon: '💼', category: 'pet', completed: false, current: 580, target: 1000, reward: 150 },
  { id: 6, name: '宠物进化', description: '将宠物进化到成长期', icon: '⬆️', category: 'pet', completed: false, current: 0, target: 1, reward: 80 },
]

const loadAchievements = async () => {
  // Try API first, fallback to mock data
  try {
    const res = await fetch('/api/admin/achievements', {
      headers: { 'Authorization': localStorage.getItem('token') || '' }
    })
    const data = await res.json()
    if (data.code === 200 && data.data) {
      achievements.value = data.data.map(a => ({
        ...a,
        progress: a.target > 0 ? Math.round((a.current / a.target) * 100) : 0,
        category: a.category || 'other'
      }))
      return
    }
  } catch (e) {}
  achievements.value = MOCK_DATA.map(a => ({
    ...a,
    progress: a.target > 0 ? Math.round((a.current / a.target) * 100) : 0,
    category: a.category || 'other'
  }))
}

onMounted(() => { loadAchievements() })
</script>

<style scoped>
.filter-bar { display: flex; gap: 12px; align-items: center; margin-bottom: 20px; }
.filter-item { display: flex; align-items: center; gap: 6px; }
.filter-label { font-size: 13px; color: var(--ev-text-secondary); }
</style>
