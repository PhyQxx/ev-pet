<template>
  <div class="admin-page">
    <div class="page-title">📝 内容审核</div>
    <div class="page-sub">监控用户生成内容（UGC）· 宠物的名字、动态、聊天内容</div>

    <!-- Tab Filters -->
    <div class="card" style="margin-bottom:20px;">
      <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:center;">
        <div
          v-for="tab in tabs"
          :key="tab.value"
          :class="['tab-item', { active: activeTab === tab.value }]"
          @click="activeTab = tab.value"
        >
          {{ tab.icon }} {{ tab.label }}
        </div>
        <button class="btn" style="margin-left:auto;">📤 导出记录</button>
      </div>
    </div>

    <!-- Content Review Cards -->
    <div class="card">
      <div style="display:flex;flex-direction:column;gap:12px;">
        <!-- Pending Items -->
        <template v-if="activeTab === 'pending'">
          <div v-for="item in pendingList" :key="item.id" class="review-card">
            <div class="review-icon">{{ item.icon }}</div>
            <div class="review-body">
              <div class="review-title">{{ item.title }}</div>
              <div class="review-content">用户「<b>{{ item.userName }}</b>」的{{ item.target }}为「<b>{{ item.violation }}</b>」，{{ item.reason }}</div>
              <div class="review-meta">ID: {{ item.userId }} · {{ item.time }}</div>
            </div>
            <div class="review-actions">
              <button class="btn btn-sm" style="background:#F0FFF5;color:#3BAF5D;border-color:#B8F1CC;" @click="approve(item)">✅ 通过</button>
              <button class="btn btn-sm btn-danger" @click="reject(item)">❌ 驳回</button>
            </div>
          </div>
          <div v-if="pendingList.length === 0" style="text-align:center;padding:40px;color:#A898B8;">暂无待审核内容</div>
        </template>

        <!-- Approved Items -->
        <template v-if="activeTab === 'approved'">
          <div v-for="item in approvedList" :key="item.id" class="review-card approved">
            <div class="review-icon">✅</div>
            <div class="review-body">
              <div class="review-title">{{ item.title }}</div>
              <div class="review-content">用户「<b>{{ item.userName }}</b>」{{ item.content }}</div>
              <div class="review-meta">ID: {{ item.userId }} · {{ item.time }}</div>
            </div>
            <div class="review-meta" style="margin-left:auto;">{{ item.reviewTime }}</div>
          </div>
          <div v-if="approvedList.length === 0" style="text-align:center;padding:40px;color:#A898B8;">暂无已通过内容</div>
        </template>

        <!-- Rejected Items -->
        <template v-if="activeTab === 'rejected'">
          <div v-for="item in rejectedList" :key="item.id" class="review-card rejected">
            <div class="review-icon">❌</div>
            <div class="review-body">
              <div class="review-title">{{ item.title }}</div>
              <div class="review-content">用户「<b>{{ item.userName }}</b>」{{ item.content }}</div>
              <div class="review-meta">ID: {{ item.userId }} · {{ item.time }}</div>
            </div>
            <div class="review-body" style="min-width:200px;">
              <div class="review-reason">违规原因：{{ item.reason }}</div>
            </div>
          </div>
          <div v-if="rejectedList.length === 0" style="text-align:center;padding:40px;color:#A898B8;">暂无已驳回内容</div>
        </template>
      </div>
    </div>

    <!-- Reject Dialog -->
    <el-dialog v-model="showRejectDialog" title="驳回内容" width="420px">
      <div class="form-item">
        <div class="form-label">违规原因</div>
        <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入违规原因" />
      </div>
      <template #footer>
        <button class="btn" @click="showRejectDialog = false">取消</button>
        <button class="btn btn-primary" @click="confirmReject">确认驳回</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { contentReview } from '@/api/index.js'

const activeTab = ref('pending')
const showRejectDialog = ref(false)
const rejectReason = ref('')
const selectedItem = ref(null)

const tabs = [
  { label: '待处理', value: 'pending', icon: '🚨' },
  { label: '已通过', value: 'approved', icon: '✅' },
  { label: '已驳回', value: 'rejected', icon: '❌' }
]

const pendingList = ref([])
const approvedList = ref([])
const rejectedList = ref([])

const loadReviews = async () => {
  try {
    const res = await contentReview.list({ status: activeTab.value })
    if (res.code === 200 && res.data) {
      const list = res.data.list || []
      if (activeTab.value === 'pending') {
        pendingList.value = list.map(item => ({
          id: item.id,
          icon: '🚨',
          title: item.type === 'nickname' ? '违规昵称' : '违规内容',
          userName: item.userName || '未知',
          userId: item.userId,
          target: item.type === 'nickname' ? '宠物昵称' : '动态内容',
          violation: item.violation || '',
          reason: item.reason || '',
          time: item.time || ''
        }))
      } else if (activeTab.value === 'approved') {
        approvedList.value = list.map(item => ({
          id: item.id,
          title: item.title || '',
          userName: item.userName || '',
          userId: item.userId,
          content: item.content || '',
          time: item.time || '',
          reviewTime: item.reviewTime || ''
        }))
      } else {
        rejectedList.value = list.map(item => ({
          id: item.id,
          title: item.title || '',
          userName: item.userName || '',
          userId: item.userId,
          content: item.content || '',
          time: item.time || '',
          reason: item.reason || ''
        }))
      }
    }
  } catch (e) {
    console.error('加载审核列表失败', e)
  }
}

const approve = async (item) => {
  try {
    const res = await contentReview.approve(item.id)
    if (res.code === 200) {
      ElMessage.success('已通过')
      loadReviews()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const reject = (item) => {
  selectedItem.value = item
  rejectReason.value = ''
  showRejectDialog.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入违规原因')
    return
  }
  try {
    const res = await contentReview.reject(selectedItem.value.id, { reason: rejectReason.value })
    if (res.code === 200) {
      ElMessage.success('已驳回')
      showRejectDialog.value = false
      loadReviews()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadReviews()
})

watch(activeTab, () => {
  loadReviews()
})
</script>

<style scoped>
.admin-page { display: block; }

.tab-item {
  font-size: 12px;
  padding: 7px 14px;
  border-radius: 10px;
  cursor: pointer;
  border: 1px solid #EDE4FF;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
}
.tab-item.active {
  background: #FF6B6B;
  color: #fff;
  border-color: #FF6B6B;
}
.tab-item:hover:not(.active) {
  border-color: #D5AAFF;
  color: #D5AAFF;
}

.review-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #FFF5F5;
  border-radius: 14px;
  border: 1px solid #FFE5E5;
}
.review-card.approved {
  background: #F0FFF5;
  border-color: #B8F1CC;
}
.review-card.rejected {
  background: #FFF0F0;
  border-color: #FFE5E5;
}

.review-icon { font-size: 28px; flex-shrink: 0; }

.review-body { flex: 1; }
.review-title { font-weight: 700; font-size: 14px; margin-bottom: 4px; }
.review-content { font-size: 13px; color: #7A6B8A; }
.review-meta { font-size: 11px; color: #A898B8; margin-top: 4px; }
.review-reason { font-size: 12px; color: #FF6B6B; margin-top: 4px; }

.review-actions { display: flex; flex-direction: column; gap: 6px; }
</style>
