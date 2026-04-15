<template>
  <div class="content-page">
    <div class="toolbar">
      <el-radio-group v-model="type" @change="loadContent">
        <el-radio-button label="chat">聊天记录</el-radio-button>
        <el-radio-button label="post">动态</el-radio-button>
        <el-radio-button label="comment">评论</el-radio-button>
      </el-radio-group>
      <el-select v-model="status" placeholder="审核状态" @change="loadContent">
        <el-option label="全部" value="" />
        <el-option label="待审核" value="pending" />
        <el-option label="通过" value="approved" />
        <el-option label="违规" value="rejected" />
      </el-select>
    </div>
    
    <el-table :data="contentList" stripe class="content-table">
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          <el-tag size="small" :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内容" min-width="300">
        <template #default="{ row }">
          <div class="content-cell">
            <p class="content-text">{{ row.content }}</p>
            <span class="content-meta">{{ row.userName }} · {{ row.time }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="宠物状态" width="100">
        <template #default="{ row }">
          <span class="pet-status" :class="row.petStatus">{{ row.petStatus }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reportCount" label="举报次数" width="100" />
      <el-table-column prop="createTime" label="发布时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'pending'" size="small" type="success" @click="approve(row)">通过</el-button>
          <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="reject(row)">违规</el-button>
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      class="pagination"
      background
      layout="prev, pager, next, total"
      :total="total"
      :page-size="pageSize"
      v-model:current-page="currentPage"
      @current-change="loadContent"
    />
    
    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetail" title="内容详情" width="600px">
      <div class="detail-content" v-if="selected">
        <div class="detail-header">
          <el-tag>{{ getTypeName(selected.type) }}</el-tag>
          <el-tag :type="getStatusTag(selected.status)">{{ getStatusName(selected.status) }}</el-tag>
        </div>
        <div class="detail-body">
          <p class="detail-text">{{ selected.content }}</p>
          <div class="detail-meta">
            <span>发布用户：{{ selected.userName }}</span>
            <span>发布时间：{{ selected.createTime }}</span>
          </div>
        </div>
        <div class="detail-reason" v-if="selected.rejectReason">
          <h4>违规原因</h4>
          <p>{{ selected.rejectReason }}</p>
        </div>
      </div>
      <template #footer v-if="selected && selected.status === 'pending'">
        <el-button @click="showDetail = false">关闭</el-button>
        <el-button type="success" @click="approve(selected); showDetail = false">通过</el-button>
        <el-button type="danger" @click="showRejectDialog">标记违规</el-button>
      </template>
    </el-dialog>
    
    <!-- 违规原因弹窗 -->
    <el-dialog v-model="showReject" title="标记违规" width="400px">
      <el-form>
        <el-form-item label="违规原因">
          <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入违规原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReject = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const type = ref('chat')
const status = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(100)
const showDetail = ref(false)
const showReject = ref(false)
const selected = ref(null)
const rejectReason = ref('')

const contentList = ref([
  { id: 1, type: 'chat', content: '你好呀，今天天气真不错~', userName: '小明', petStatus: 'happy', status: 'approved', reportCount: 0, time: '10分钟前', createTime: '2026-04-15 10:30', rejectReason: '' },
  { id: 2, type: 'chat', content: '这个游戏太好玩了！', userName: '小红', petStatus: 'happy', status: 'pending', reportCount: 2, time: '30分钟前', createTime: '2026-04-15 10:00', rejectReason: '' },
  { id: 3, type: 'post', content: '我的宠物终于进化啦！开心~', userName: '小华', petStatus: 'excited', status: 'pending', reportCount: 1, time: '1小时前', createTime: '2026-04-15 09:30', rejectReason: '' },
  { id: 4, type: 'comment', content: '好可爱呀！', userName: '阿月', petStatus: 'happy', status: 'rejected', reportCount: 5, time: '2小时前', createTime: '2026-04-15 08:00', rejectReason: '包含广告信息' },
  { id: 5, type: 'chat', content: '有人想一起玩吗？', userName: '阿华', petStatus: 'normal', status: 'approved', reportCount: 0, time: '3小时前', createTime: '2026-04-15 07:00', rejectReason: '' }
])

const getTypeTag = (type) => ({ chat: '', post: 'warning', comment: 'info' }[type] || '')
const getTypeName = (type) => ({ chat: '聊天', post: '动态', comment: '评论' }[type] || type)
const getStatusTag = (status) => ({ pending: 'warning', approved: 'success', rejected: 'danger' }[status] || '')
const getStatusName = (status) => ({ pending: '待审核', approved: '通过', rejected: '违规' }[status] || status)

const loadContent = () => {
  console.log('Load content:', type.value, status.value, currentPage.value)
}

const viewDetail = (row) => {
  selected.value = row
  showDetail.value = true
}

const approve = (row) => {
  row.status = 'approved'
  ElMessage.success('已通过')
}

const showRejectDialog = () => {
  showReject.value = true
}

const confirmReject = () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入违规原因')
    return
  }
  selected.value.status = 'rejected'
  selected.value.rejectReason = rejectReason.value
  showReject.value = false
  showDetail.value = false
  ElMessage.success('已标记违规')
  rejectReason.value = ''
}

onMounted(() => {
  loadContent()
})
</script>

<style scoped>
.content-page { padding: 0; }

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.content-table { background: white; border-radius: 8px; }

.content-cell { text-align: left; }

.content-text {
  margin: 0 0 4px 0;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.content-meta { font-size: 12px; color: #7A6B8A; }

.pet-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  background: #E8FFF0;
  color: #4A3F55;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-header { display: flex; gap: 8px; margin-bottom: 16px; }

.detail-body {
  padding: 16px;
  background: #F8F8F8;
  border-radius: 8px;
  margin-bottom: 16px;
}

.detail-text { font-size: 14px; line-height: 1.6; margin: 0 0 12px 0; }

.detail-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7A6B8A;
}

.detail-reason {
  padding: 12px;
  background: #FFF0F0;
  border-radius: 8px;
}

.detail-reason h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #FF6B6B;
}

.detail-reason p { margin: 0; font-size: 14px; }
</style>
