<template>
  <div class="announcements-page">
    <div class="toolbar">
      <el-button type="primary" @click="showAddDialog">发布公告</el-button>
      <el-select v-model="typeFilter" placeholder="公告类型" @change="loadData">
        <el-option label="全部" value="" />
        <el-option label="系统公告" value="system" />
        <el-option label="活动公告" value="activity" />
        <el-option label="维护公告" value="maintenance" />
      </el-select>
    </div>
    
    <el-table :data="announcements" stripe class="table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="公告信息" min-width="300">
        <template #default="{ row }">
          <div class="announce-cell">
            <h4 class="announce-title">{{ row.title }}</h4>
            <p class="announce-content">{{ row.content }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="120">
        <template #default="{ row }">
          <el-tag size="small" :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="范围" width="100">
        <template #default="{ row }">
          {{ row.scope === 'all' ? '全量' : '指定用户' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="row.status === 'published' ? 'success' : 'info'">
            {{ row.status === 'published' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="views" label="浏览" width="80" />
      <el-table-column prop="createTime" label="发布时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="editAnnounce(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteAnnounce(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="公告标题" required>
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="公告内容" required>
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入公告内容" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" required>
          <el-radio-group v-model="form.type">
            <el-radio label="system">系统公告</el-radio>
            <el-radio label="activity">活动公告</el-radio>
            <el-radio label="maintenance">维护公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发布范围">
          <el-radio-group v-model="form.scope">
            <el-radio label="all">全量用户</el-radio>
            <el-radio label="specific">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="推送时间">
          <el-date-picker
            v-model="form.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            :disabled-date="disabledDate"
          />
          <span class="tip">留空则立即发布</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" @click="publish">立即发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const typeFilter = ref('')
const showDialog = ref(false)
const isEdit = ref(false)
const selected = ref(null)

const form = ref({
  title: '',
  content: '',
  type: 'system',
  scope: 'all',
  publishTime: null
})

const announcements = ref([
  { id: 1, title: '【活动】周末登录送好礼', content: '周末登录游戏即可获得神秘礼包一份！', type: 'activity', scope: 'all', status: 'published', views: 1256, createTime: '2026-04-14 10:00' },
  { id: 2, title: '系统升级通知', content: '系统将于今晚23:00-24:00进行升级维护。', type: 'maintenance', scope: 'all', status: 'published', views: 2345, createTime: '2026-04-13 15:30' },
  { id: 3, title: '新功能上线公告', content: 'AI对话功能正式上线，快来和你的宠物聊天吧！', type: 'system', scope: 'all', status: 'published', views: 3456, createTime: '2026-04-12 09:00' },
  { id: 4, title: '【活动】节日限定皮肤', content: '节日限定皮肤限时上架！', type: 'activity', scope: 'all', status: 'draft', views: 0, createTime: '2026-04-15 11:00' }
])

const getTypeTag = (type) => ({ system: '', activity: 'warning', maintenance: 'danger' }[type] || '')
const getTypeName = (type) => ({ system: '系统公告', activity: '活动公告', maintenance: '维护公告' }[type] || type)

const loadData = () => {
  console.log('Load:', typeFilter.value)
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { title: '', content: '', type: 'system', scope: 'all', publishTime: null }
  showDialog.value = true
}

const editAnnounce = (row) => {
  isEdit.value = true
  selected.value = row
  form.value = { ...row }
  showDialog.value = true
}

const saveDraft = () => {
  if (!validateForm()) return
  ElMessage.success('草稿已保存')
  showDialog.value = false
}

const publish = () => {
  if (!validateForm()) return
  ElMessage.success('公告已发布')
  showDialog.value = false
  loadData()
}

const deleteAnnounce = (row) => {
  ElMessageBox.confirm(`确定要删除公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('公告已删除')
    loadData()
  })
}

const validateForm = () => {
  if (!form.value.title) {
    ElMessage.warning('请输入公告标题')
    return false
  }
  if (!form.value.content) {
    ElMessage.warning('请输入公告内容')
    return false
  }
  return true
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.announcements-page { padding: 0; }

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.table { background: white; border-radius: 8px; }

.announce-cell { text-align: left; }

.announce-title {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
}

.announce-content {
  margin: 0;
  font-size: 12px;
  color: #7A6B8A;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tip {
  margin-left: 12px;
  font-size: 12px;
  color: #A898B8;
}
</style>
