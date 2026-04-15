<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon blue">👥</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.dau }}</span>
            <span class="stat-label">日活用户</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon purple">🪙</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.mau }}</span>
            <span class="stat-label">月活用户</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon pink">💰</div>
          <div class="stat-info">
            <span class="stat-value">¥{{ stats.revenue }}</span>
            <span class="stat-label">月收入</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon green">🐾</div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.pets }}</span>
            <span class="stat-label">宠物总数</span>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <div class="chart-card card">
          <h3>用户增长趋势</h3>
          <div ref="userChart" style="height: 300px"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card card">
          <h3>宠物阶段分布</h3>
          <div ref="petChart" style="height: 300px"></div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 最新用户列表 -->
    <div class="recent-users card">
      <h3>最新注册用户</h3>
      <el-table :data="recentUsers" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="level" label="等级" width="80" />
        <el-table-column prop="petStage" label="宠物阶段" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="success">第{{ row.petStage }}阶段</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const stats = ref({
  dau: 1234,
  mau: 5678,
  revenue: 12345,
  pets: 890
})

const recentUsers = ref([
  { id: 1, nickname: '小明', level: 5, petStage: 2, createTime: '2026-04-15 10:30' },
  { id: 2, nickname: '小红', level: 3, petStage: 1, createTime: '2026-04-15 09:20' },
  { id: 3, nickname: '小华', level: 8, petStage: 3, createTime: '2026-04-14 18:45' },
  { id: 4, nickname: '阿月', level: 2, petStage: 1, createTime: '2026-04-14 15:30' }
])

const userChart = ref(null)
const petChart = ref(null)

onMounted(() => {
  // 用户增长趋势图
  const userChartInstance = echarts.init(userChart.value)
  userChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['日活', '新增'] },
    xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
    yAxis: [{ type: 'value' }],
    series: [
      { name: '日活', type: 'line', data: [820, 932, 901, 1234, 1290, 1330, 1320], smooth: true },
      { name: '新增', type: 'line', data: [120, 132, 101, 134, 190, 230, 210], smooth: true }
    ]
  })
  
  // 宠物阶段分布图
  const petChartInstance = echarts.init(petChart.value)
  petChartInstance.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 500, name: '第一阶段' },
        { value: 300, name: '第二阶段' },
        { value: 100, name: '第三阶段' }
      ]
    }]
  })
})
</script>

<style scoped>
.stats-row { margin-bottom: 20px; }
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon { font-size: 40px; }
.stat-value { font-size: 28px; font-weight: 700; display: block; }
.stat-label { font-size: 14px; color: #7A6B8A; }
.charts-row { margin-bottom: 20px; }
.chart-card { padding: 20px; }
.chart-card h3 { margin-bottom: 16px; font-size: 16px; }
.recent-users { padding: 20px; }
.recent-users h3 { margin-bottom: 16px; }
</style>
