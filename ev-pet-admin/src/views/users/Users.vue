<template>
  <div class="users-page">
    <div class="search-bar">
      <el-input placeholder="搜索用户ID/昵称/手机号" prefix-icon="Search" v-model="keyword" @change="searchUsers" />
      <el-select placeholder="用户状态" v-model="statusFilter" @change="searchUsers">
        <el-option label="全部" value="" />
        <el-option label="正常" value="active" />
        <el-option label="禁用" value="banned" />
      </el-select>
      <el-button type="primary">导出</el-button>
    </div>
    
    <el-table :data="users" stripe class="users-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="用户信息" min-width="200">
        <template #default="{ row }">
          <div class="user-info-cell">
            <span class="user-avatar">{{ row.nickname?.charAt(0) || '?' }}</span>
            <div class="user-detail">
              <span class="user-name">{{ row.nickname }}</span>
              <span class="user-phone">{{ row.phone || '-' }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="宠物信息" width="180">
        <template #default="{ row }">
          <div class="pet-info-cell">
            <span class="pet-emoji">{{ getPetEmoji(row.petStage) }}</span>
            <div class="pet-detail">
              <span>{{ row.petName || '-' }}</span>
              <span class="pet-level">Lv.{{ row.petLevel || 1 }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="gold" label="金币" width="120" />
      <el-table-column prop="level" label="等级" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
            {{ row.status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="viewDetail(row)">详情</el-button>
          <el-button size="small" type="danger" @click="toggleBan(row)">
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
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
      @current-change="loadUsers"
    />
    
    <!-- 用户详情弹窗 -->
    <el-dialog v-model="showDetail" title="用户详情" width="600px">
      <div class="user-detail-modal" v-if="selectedUser">
        <div class="detail-header">
          <span class="detail-avatar">{{ selectedUser.nickname?.charAt(0) || '?' }}</span>
          <div class="detail-info">
            <h3>{{ selectedUser.nickname }}</h3>
            <p>ID: {{ selectedUser.id }}</p>
          </div>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="手机号">{{ selectedUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="等级">Lv.{{ selectedUser.level }}</el-descriptions-item>
          <el-descriptions-item label="金币">{{ selectedUser.gold }}</el-descriptions-item>
          <el-descriptions-item label="经验">{{ selectedUser.exp }}</el-descriptions-item>
          <el-descriptions-item label="宠物">{{ selectedUser.petName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="宠物阶段">第{{ selectedUser.petStage || 1 }}阶段</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ selectedUser.createTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ selectedUser.lastLoginTime || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const keyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(100)
const showDetail = ref(false)
const selectedUser = ref(null)

const users = ref([
  { id: 1, nickname: '小明', phone: '138****1234', petName: '小白', petStage: 2, petLevel: 12, gold: 5600, level: 8, exp: 850, status: 'active', createTime: '2026-04-01 10:30' },
  { id: 2, nickname: '小红', phone: '139****5678', petName: '小粉', petStage: 1, petLevel: 5, gold: 2300, level: 4, exp: 320, status: 'active', createTime: '2026-04-03 15:20' },
  { id: 3, nickname: '小华', phone: '137****9012', petName: '大星', petStage: 3, petLevel: 20, gold: 12000, level: 15, exp: 1800, status: 'active', createTime: '2026-03-28 09:15' },
  { id: 4, nickname: '阿月', phone: '136****3456', petName: '萌萌', petStage: 1, petLevel: 3, gold: 800, level: 2, exp: 150, status: 'banned', createTime: '2026-04-10 18:45' },
  { id: 5, nickname: '阿华', phone: '135****7890', petName: '小乖', petStage: 2, petLevel: 8, gold: 3200, level: 6, exp: 580, status: 'active', createTime: '2026-04-05 12:00' }
])

const getPetEmoji = (stage) => {
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
}

const searchUsers = () => {
  console.log('Search:', keyword.value, statusFilter.value)
}

const viewDetail = (user) => {
  selectedUser.value = user
  showDetail.value = true
}

const toggleBan = (user) => {
  const action = user.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}用户 ${user.nickname} 吗？`, '提示').then(() => {
    user.status = user.status === 'active' ? 'banned' : 'active'
    ElMessage.success(`用户已${action}`)
  })
}

const loadUsers = () => {
  console.log('Load page:', currentPage.value)
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-page { padding: 0; }

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar .el-input {
  width: 300px;
}

.users-table { background: white; border-radius: 8px; }

.user-info-cell, .pet-info-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar, .detail-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name { font-weight: 600; }
.user-phone { font-size: 12px; color: #7A6B8A; }

.pet-emoji { font-size: 24px; }

.pet-detail {
  display: flex;
  flex-direction: column;
  font-size: 13px;
}

.pet-level { font-size: 11px; color: #7A6B8A; }

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.user-detail-modal { padding: 0 10px; }

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEEF5;
}

.detail-avatar {
  width: 64px;
  height: 64px;
  font-size: 28px;
}

.detail-info h3 { margin-bottom: 4px; }
.detail-info p { font-size: 13px; color: #7A6B8A; }
</style>
