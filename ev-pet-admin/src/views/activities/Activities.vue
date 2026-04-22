<template>
  <div class="admin-page">
    <div class="page-title">🎉 活动管理</div>
    <div class="page-sub">配置节日活动、限时任务、运营活动</div>

    <!-- Toolbar -->
    <div class="card" style="margin-bottom:20px;">
      <div style="display:flex;gap:10px;">
        <button class="btn btn-primary" @click="openAddModal">+ 创建活动</button>
      </div>
    </div>

    <!-- Activity Grid -->
    <div class="activity-grid">
      <div
        v-for="activity in activities"
        :key="activity.id"
        class="activity-card"
        :class="{ 'card-active': activity.statusText === '进行中', 'card-ended': activity.statusText === '已结束' }"
        :style="activity.statusText === '进行中' ? 'border:2px solid #FFD166;' : ''"
      >
        <div class="activity-card-header">
          <div style="font-size:14px;font-weight:700;">{{ activity.icon }} {{ activity.name }}</div>
          <span :class="['badge', activity.statusBadge]">{{ activity.statusText }}</span>
        </div>
        <div class="activity-card-time">{{ activity.startTime }} ~ {{ activity.endTime }}</div>
        <div class="activity-card-desc">{{ activity.desc }}</div>
        <div class="activity-card-participants">参与人数：{{ activity.participants.toLocaleString() }}</div>
        <div class="activity-card-actions">
          <button class="btn btn-sm" @click="openEditModal(activity)">编辑</button>
          <button class="btn btn-sm btn-danger" v-if="activity.statusText === '进行中'" @click="endActivity(activity)">结束活动</button>
          <button class="btn btn-sm" v-if="activity.statusText === '已结束'" @click="viewData(activity)">查看数据</button>
        </div>
      </div>
      <div v-if="activities.length === 0" style="grid-column:1/-1;text-align:center;padding:60px;color:#A898B8;">暂无活动</div>
    </div>

    <!-- Create/Edit Activity Modal -->
    <el-dialog v-model="showModal" :title="isEdit ? '编辑活动' : '+ 创建活动'" width="600px">
      <div class="form-item">
        <div class="form-label">活动名称</div>
        <el-input v-model="form.name" placeholder="请输入活动名称" />
      </div>
      <div class="form-item">
        <div class="form-label">活动描述</div>
        <el-input v-model="form.desc" type="textarea" :rows="2" placeholder="请输入活动描述" />
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">活动类型</div>
          <el-select v-model="form.type" style="width:100%;">
            <el-option label="登录活动" value="login" />
            <el-option label="充值活动" value="recharge" />
            <el-option label="任务活动" value="task" />
            <el-option label="抽奖活动" value="lottery" />
            <el-option label="排行榜活动" value="rank" />
          </el-select>
        </div>
        <div class="form-item">
          <div class="form-label">状态</div>
          <el-select v-model="form.status" style="width:100%;">
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">开始时间</div>
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width:100%;" />
        </div>
        <div class="form-item">
          <div class="form-label">结束时间</div>
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width:100%;" />
        </div>
      </div>
      <div class="form-item">
        <div class="form-label">活动奖励</div>
        <el-input v-model="form.rewards" placeholder="如: 金币x100, 限定服装x1" />
      </div>
      <template #footer>
        <button class="btn" @click="showModal = false">取消</button>
        <button class="btn btn-primary" @click="saveActivity">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { activityAdmin } from '@/api/index.js'

const showModal = ref(false)
const isEdit = ref(false)
const currentActivity = ref(null)

const form = ref({
  name: '',
  desc: '',
  type: 'login',
  status: 'pending',
  startTime: '',
  endTime: '',
  rewards: ''
})

const activities = ref([])

const getStatusBadge = (status) => {
  return { ongoing: 'badge-green', pending: 'badge-yellow', ended: 'badge-red' }[status] || 'badge-yellow'
}

const getStatusText = (status) => {
  return { ongoing: '进行中', pending: '未开始', ended: '已结束' }[status] || '未知'
}

const loadActivities = async () => {
  try {
    const res = await activityAdmin.list()
    if (res.code === 200 && res.data) {
      activities.value = res.data.map(a => ({
        id: a.id,
        name: a.name || '',
        icon: a.icon || '🎉',
        desc: a.desc || '',
        type: a.type || 'login',
        status: a.status || 'pending',
        statusText: getStatusText(a.status),
        statusBadge: getStatusBadge(a.status),
        startTime: a.startTime || '',
        endTime: a.endTime || '',
        participants: a.participants || 0
      }))
    }
  } catch (e) {
    console.error('加载活动失败', e)
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.value = { name: '', desc: '', type: 'login', status: 'pending', startTime: '', endTime: '', rewards: '' }
  showModal.value = true
}

const openEditModal = (activity) => {
  isEdit.value = true
  currentActivity.value = activity
  form.value = { name: activity.name, desc: activity.desc, type: activity.type, status: activity.status, startTime: activity.startTime, endTime: activity.endTime, rewards: '' }
  showModal.value = true
}

const saveActivity = async () => {
  if (!form.value.name) { ElMessage.warning('请输入活动名称'); return }
  if (!form.value.startTime || !form.value.endTime) { ElMessage.warning('请选择活动时间'); return }
  try {
    const data = {
      name: form.value.name,
      desc: form.value.desc,
      type: form.value.type,
      status: form.value.status,
      startTime: form.value.startTime,
      endTime: form.value.endTime,
      rewards: form.value.rewards
    }
    let res
    if (isEdit.value) {
      res = await activityAdmin.update(currentActivity.value.id, data)
    } else {
      res = await activityAdmin.create(data)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '活动已更新' : '活动已创建')
      showModal.value = false
      loadActivities()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const endActivity = async (activity) => {
  try {
    await ElMessageBox.confirm(`确定要结束活动「${activity.name}」吗？`, '提示')
    const res = await activityAdmin.end(activity.id)
    if (res.code === 200) {
      ElMessage.success('活动已结束')
      loadActivities()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const viewData = (activity) => {
  ElMessage.info('数据详情开发中...')
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.admin-page { display: block; }

.activity-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.activity-card {
  background: #fff;
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid #F5EEF8;
}

.card-active {
  border: 2px solid #FFD166;
}

.card-ended {
  opacity: 0.75;
}

.activity-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.activity-card-time {
  font-size: 12px;
  color: #7A6B8A;
  margin-bottom: 8px;
}

.activity-card-desc {
  font-size: 13px;
  margin-bottom: 10px;
}

.activity-card-participants {
  font-size: 12px;
  color: #7A6B8A;
  margin-bottom: 12px;
}

.activity-card-actions {
  display: flex;
  gap: 8px;
}
</style>
