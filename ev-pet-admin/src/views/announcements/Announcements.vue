<template>
  <div class="admin-page">
    <div class="page-title">📢 公告管理</div>
    <div class="page-sub">创建、编辑全端推送公告与弹窗</div>

    <!-- Toolbar -->
    <div class="card" style="margin-bottom:20px;">
      <div style="display:flex;gap:10px;flex-wrap:wrap;align-items:center;">
        <button class="btn btn-primary" @click="openAddModal">+ 创建公告</button>
        <el-select v-model="typeFilter" placeholder="全部类型" style="width:130px;" clearable @change="loadData">
          <el-option label="全部类型" value="" />
          <el-option label="系统公告" value="system" />
          <el-option label="活动公告" value="activity" />
          <el-option label="维护通知" value="maintenance" />
        </el-select>
      </div>
    </div>

    <!-- Announcement List -->
    <div class="card">
      <div style="display:flex;flex-direction:column;gap:12px;">
        <div v-for="item in filteredList" :key="item.id" class="announce-item">
          <div class="announce-icon" :style="{ background: item.iconBg }">{{ item.icon }}</div>
          <div class="announce-body">
            <div class="announce-title">{{ item.title }}</div>
            <div class="announce-meta">{{ item.time }} · {{ item.scope }} · <span :class="['badge', item.statusBadge]">{{ item.statusText }}</span></div>
          </div>
          <div style="display:flex;gap:8px;">
            <button class="btn btn-sm" @click="openEditModal(item)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="withdraw(item)" v-if="item.statusText === '已发布'">撤回</button>
          </div>
        </div>
        <div v-if="filteredList.length === 0" style="text-align:center;padding:40px;color:#A898B8;">暂无公告</div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <el-dialog v-model="showModal" :title="isEdit ? '编辑公告' : '+ 创建公告'" width="520px">
      <div class="form-item">
        <div class="form-label">公告标题</div>
        <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="50" show-word-limit />
      </div>
      <div class="form-item">
        <div class="form-label">公告内容</div>
        <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入公告内容" maxlength="500" show-word-limit />
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">公告类型</div>
          <el-select v-model="form.type" style="width:100%;">
            <el-option label="系统公告" value="system" />
            <el-option label="活动公告" value="activity" />
            <el-option label="维护通知" value="maintenance" />
          </el-select>
        </div>
        <div class="form-item">
          <div class="form-label">发布范围</div>
          <el-select v-model="form.scope" style="width:100%;">
            <el-option label="全量推送" value="全量推送" />
            <el-option label="指定用户" value="指定用户" />
          </el-select>
        </div>
      </div>
      <div class="form-item">
        <div class="form-label">推送时间</div>
        <el-date-picker v-model="form.publishTime" type="datetime" placeholder="选择发布时间" style="width:100%;" clearable />
        <div style="font-size:11px;color:#A898B8;margin-top:4px;">留空则立即发布</div>
      </div>
      <template #footer>
        <button class="btn" @click="showModal = false">取消</button>
        <button class="btn" @click="saveDraft">保存草稿</button>
        <button class="btn btn-primary" @click="publish">立即发布</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { announcementAdmin } from '@/api/index.js'

const typeFilter = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const currentItem = ref(null)

const form = ref({
  title: '',
  content: '',
  type: 'system',
  scope: '全量推送',
  publishTime: null
})

const allAnnouncements = ref([])

const filteredList = computed(() => {
  if (!typeFilter.value) return allAnnouncements.value
  return allAnnouncements.value.filter(a => a.type === typeFilter.value)
})

const getIconForType = (type) => {
  return { system: '📋', activity: '🎁', maintenance: '⚙️' }[type] || '📋'
}

const getIconBgForType = (type) => {
  return { system: '#D5AAFF', activity: '#FFD166', maintenance: '#A8D8EA' }[type] || '#D5AAFF'
}

const loadData = async () => {
  try {
    const params = {}
    if (typeFilter.value) params.type = typeFilter.value
    const res = await announcementAdmin.list(params)
    if (res.code === 200 && res.data) {
      allAnnouncements.value = res.data.map(a => ({
        id: a.id,
        title: a.title || '',
        content: a.content || '',
        type: a.type || 'system',
        icon: getIconForType(a.type),
        iconBg: getIconBgForType(a.type),
        scope: a.scope || '全量推送',
        statusText: a.status === 'published' ? '已发布' : '草稿',
        statusBadge: a.status === 'published' ? 'badge-green' : 'badge-yellow',
        time: a.createTime || ''
      }))
    }
  } catch (e) {
    console.error('加载公告失败', e)
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.value = { title: '', content: '', type: 'system', scope: '全量推送', publishTime: null }
  showModal.value = true
}

const openEditModal = (item) => {
  isEdit.value = true
  currentItem.value = item
  form.value = { title: item.title, content: item.content, type: item.type, scope: item.scope, publishTime: null }
  showModal.value = true
}

const saveDraft = async () => {
  if (!form.value.title) { ElMessage.warning('请输入公告标题'); return }
  if (!form.value.content) { ElMessage.warning('请输入公告内容'); return }
  try {
    const data = { title: form.value.title, content: form.value.content, type: form.value.type, scope: form.value.scope }
    let res
    if (isEdit.value) {
      res = await announcementAdmin.update(currentItem.value.id, data)
    } else {
      res = await announcementAdmin.create(data)
    }
    if (res.code === 200) {
      ElMessage.success('草稿已保存')
      showModal.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const publish = async () => {
  if (!form.value.title) { ElMessage.warning('请输入公告标题'); return }
  if (!form.value.content) { ElMessage.warning('请输入公告内容'); return }
  try {
    let res
    if (isEdit.value) {
      res = await announcementAdmin.update(currentItem.value.id, { ...form.value, status: 'published' })
      res = await announcementAdmin.publish(currentItem.value.id)
    } else {
      res = await announcementAdmin.create({ ...form.value, status: 'published' })
    }
    if (res.code === 200) {
      ElMessage.success('公告已发布')
      showModal.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

const withdraw = async (item) => {
  try {
    await ElMessageBox.confirm(`确定要撤回公告「${item.title}」吗？`, '提示')
    const res = await announcementAdmin.withdraw(item.id)
    if (res.code === 200) {
      ElMessage.success('公告已撤回')
      loadData()
    } else {
      ElMessage.error(res.message || '撤回失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('撤回失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-page { display: block; }

.announce-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: #FAFAFA;
  border-radius: 14px;
}

.announce-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.announce-body { flex: 1; }
.announce-title { font-weight: 700; font-size: 14px; margin-bottom: 4px; }
.announce-meta { font-size: 12px; color: #7A6B8A; display: flex; align-items: center; gap: 6px; }
</style>
