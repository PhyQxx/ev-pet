<template>
  <div class="activities-page">
    <div class="toolbar">
      <el-button type="primary" @click="showAddDialog">创建活动</el-button>
      <el-select v-model="statusFilter" placeholder="活动状态" @change="loadData">
        <el-option label="全部" value="" />
        <el-option label="进行中" value="ongoing" />
        <el-option label="未开始" value="pending" />
        <el-option label="已结束" value="ended" />
      </el-select>
    </div>
    
    <el-row :gutter="20" class="activity-grid">
      <el-col :span="8" v-for="activity in activities" :key="activity.id">
        <div class="activity-card" :class="activity.status">
          <div class="card-header">
            <el-tag :type="getStatusTag(activity.status)" size="small">
              {{ getStatusName(activity.status) }}
            </el-tag>
            <el-dropdown>
              <span class="el-dropdown-link">···</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="editActivity(activity)">编辑</el-dropdown-item>
                  <el-dropdown-item @click="toggleActivity(activity)">
                    {{ activity.status === 'ongoing' ? '暂停' : '开启' }}
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="deleteActivity(activity)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <div class="card-body">
            <h3 class="activity-name">{{ activity.name }}</h3>
            <p class="activity-desc">{{ activity.description }}</p>
            
            <div class="activity-stats">
              <div class="stat-item">
                <span class="stat-value">{{ activity.participants }}</span>
                <span class="stat-label">参与人数</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ activity.rewards }}</span>
                <span class="stat-label">奖励发放</span>
              </div>
            </div>
            
            <div class="activity-time">
              <span class="time-icon">📅</span>
              <span>{{ activity.startTime }} ~ {{ activity.endTime }}</span>
            </div>
          </div>
          
          <div class="card-footer">
            <el-button size="small" @click="viewDetail(activity)">数据详情</el-button>
            <el-button size="small" type="primary" @click="editActivity(activity)">编辑</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 创建/编辑活动弹窗 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑活动' : '创建活动'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称" required>
          <el-input v-model="form.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="活动类型" required>
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="登录活动" value="login" />
            <el-option label="充值活动" value="recharge" />
            <el-option label="任务活动" value="task" />
            <el-option label="抽奖活动" value="lottery" />
            <el-option label="排行榜活动" value="rank" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" required>
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" required>
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动规则">
          <el-input v-model="form.rules" type="textarea" :rows="3" placeholder="请输入活动规则" />
        </el-form-item>
        <el-form-item label="奖励设置">
          <div class="reward-list">
            <div v-for="(reward, i) in form.rewards" :key="i" class="reward-item">
              <el-input v-model="reward.name" placeholder="奖励名称" style="width: 150px" />
              <el-input-number v-model="reward.count" :min="0" placeholder="数量" />
              <el-button @click="removeReward(i)" :disabled="form.rewards.length <= 1">删除</el-button>
            </div>
            <el-button @click="addReward">添加奖励</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button @click="saveActivity">保存</el-button>
        <el-button type="primary" @click="publishActivity">发布活动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const statusFilter = ref('')
const showDialog = ref(false)
const isEdit = ref(false)
const selected = ref(null)

const form = ref({
  name: '',
  description: '',
  type: 'login',
  startTime: '',
  endTime: '',
  rules: '',
  rewards: [{ name: '', count: 0 }]
})

const activities = ref([
  {
    id: 1,
    name: '周末登录送好礼',
    description: '周末登录即可领取神秘礼包',
    type: 'login',
    status: 'ongoing',
    participants: 1256,
    rewards: 890,
    startTime: '2026-04-12 00:00',
    endTime: '2026-04-20 23:59'
  },
  {
    id: 2,
    name: '充值返利活动',
    description: '充值满100送50，多充多送',
    type: 'recharge',
    status: 'ended',
    participants: 456,
    rewards: 456,
    startTime: '2026-04-01 00:00',
    endTime: '2026-04-10 23:59'
  },
  {
    id: 3,
    name: '劳动节任务活动',
    description: '完成任务获得双倍经验',
    type: 'task',
    status: 'pending',
    participants: 0,
    rewards: 0,
    startTime: '2026-05-01 00:00',
    endTime: '2026-05-07 23:59'
  },
  {
    id: 4,
    name: '幸运大转盘',
    description: '每日抽奖，百分百中奖',
    type: 'lottery',
    status: 'ongoing',
    participants: 2345,
    rewards: 2345,
    startTime: '2026-04-08 00:00',
    endTime: '2026-04-30 23:59'
  },
  {
    id: 5,
    name: '等级排行榜',
    description: '等级排名前十获得限定称号',
    type: 'rank',
    status: 'ongoing',
    participants: 5678,
    rewards: 10,
    startTime: '2026-04-01 00:00',
    endTime: '2026-04-30 23:59'
  }
])

const getStatusTag = (status) => ({ ongoing: 'success', pending: 'warning', ended: 'info' }[status] || '')
const getStatusName = (status) => ({ ongoing: '进行中', pending: '未开始', ended: '已结束' }[status] || status)

const loadData = () => {
  console.log('Load:', statusFilter.value)
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    name: '',
    description: '',
    type: 'login',
    startTime: '',
    endTime: '',
    rules: '',
    rewards: [{ name: '', count: 0 }]
  }
  showDialog.value = true
}

const editActivity = (activity) => {
  isEdit.value = true
  selected.value = activity
  form.value = { ...activity, rewards: activity.rewards || [{ name: '', count: 0 }] }
  showDialog.value = true
}

const viewDetail = (activity) => {
  ElMessage.info('数据详情开发中...')
}

const toggleActivity = (activity) => {
  activity.status = activity.status === 'ongoing' ? 'pending' : 'ongoing'
  ElMessage.success(`活动已${activity.status === 'ongoing' ? '开启' : '暂停'}`)
}

const deleteActivity = (activity) => {
  ElMessageBox.confirm(`确定要删除活动"${activity.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('活动已删除')
    loadData()
  })
}

const addReward = () => {
  form.value.rewards.push({ name: '', count: 0 })
}

const removeReward = (i) => {
  form.value.rewards.splice(i, 1)
}

const saveActivity = () => {
  if (!validateForm()) return
  ElMessage.success('活动已保存')
  showDialog.value = false
}

const publishActivity = () => {
  if (!validateForm()) return
  ElMessage.success('活动已发布')
  showDialog.value = false
  loadData()
}

const validateForm = () => {
  if (!form.value.name) {
    ElMessage.warning('请输入活动名称')
    return false
  }
  if (!form.value.startTime || !form.value.endTime) {
    ElMessage.warning('请选择活动时间')
    return false
  }
  return true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.activities-page { padding: 0; }

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.activity-grid { margin-top: 0; }

.activity-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.activity-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.activity-card.ended {
  opacity: 0.7;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.activity-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.activity-desc {
  margin: 0 0 16px 0;
  font-size: 13px;
  color: #7A6B8A;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--accent-purple);
}

.stat-label {
  font-size: 12px;
  color: #7A6B8A;
}

.activity-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #7A6B8A;
  margin-bottom: 16px;
}

.card-footer {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #F0F0F0;
}

.reward-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reward-item {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
