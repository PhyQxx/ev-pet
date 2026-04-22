<template>
  <div class="admin-page">
    <div class="page-title">👥 用户管理</div>
    <div class="page-sub">查看、搜索、管理所有注册用户 · 共 {{ (total > 0 ? total : users.length).toLocaleString() }} 人</div>

    <!-- Search / Filter Bar -->
    <div class="card" style="margin-bottom:20px;">
      <div style="display:flex;gap:10px;flex-wrap:wrap;align-items:center;">
        <el-input
          v-model="keyword"
          placeholder="搜索用户ID / 昵称 / 宠物名..."
          style="flex:1;max-width:300px;"
          clearable
          @change="searchUsers"
        >
          <template #prefix>🔍</template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="全部状态" style="width:140px;" clearable @change="searchUsers">
          <el-option label="全部状态" value="" />
          <el-option label="正常" value="active" />
          <el-option label="禁用" value="banned" />
          <el-option label="待验证" value="pending" />
        </el-select>
        <el-select v-model="typeFilter" placeholder="全部类型" style="width:140px;" clearable @change="searchUsers">
          <el-option label="全部类型" value="" />
          <el-option label="微信用户" value="wechat" />
          <el-option label="手机号用户" value="phone" />
          <el-option label="游客" value="guest" />
        </el-select>
        <button class="btn btn-primary" @click="searchUsers">🔍 搜索</button>
        <button class="btn">📤 导出</button>
      </div>
    </div>

    <!-- User Table Card -->
    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>用户</th>
              <th>宠物</th>
              <th>注册方式</th>
              <th>最后活跃</th>
              <th>付费金额</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>
                <div class="td-user">
                  <div class="td-avatar" :style="{ background: user.avatarBg }">{{ user.avatarEmoji }}</div>
                  <div>
                    <div class="td-name">{{ user.nickname }}</div>
                    <div class="td-id">ID: {{ user.id }}</div>
                  </div>
                </div>
              </td>
              <td>{{ user.petName }} {{ user.petEmoji }} LV.{{ user.petLevel }}</td>
              <td>
                <span :class="['badge', user.regType === 'wechat' ? 'badge-blue' : user.regType === 'phone' ? 'badge-purple' : 'badge-yellow']">
                  {{ user.regType === 'wechat' ? '微信' : user.regType === 'phone' ? '手机' : '游客' }}
                </span>
              </td>
              <td>{{ user.lastActive }}</td>
              <td :style="user.paidAmount > 0 ? 'color:#D4A000;font-weight:600;' : 'color:#A898B8;'">
                {{ user.paidAmount > 0 ? '¥' + user.paidAmount : '—' }}
              </td>
              <td>
                <span :class="['badge', user.status === '正常' ? 'badge-green' : user.status === '禁用' ? 'badge-red' : 'badge-yellow']">
                  {{ user.status }}
                </span>
              </td>
              <td>
                <span class="action-link" @click="openEditModal(user)">编辑</span>
                <span class="action-link" :style="{ color: user.status === '正常' ? '#FF6B6B' : '#3BAF5D' }" @click="toggleBan(user)">
                  {{ user.status === '正常' ? '禁用' : '启用' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <!-- Pagination -->
      <div style="display:flex;justify-content:space-between;align-items:center;margin-top:16px;flex-wrap:wrap;gap:10px;">
        <div style="font-size:12px;color:#7A6B8A;">显示 1-{{ users.length }} 条，共 {{ total > 0 ? total : users.length }} 条</div>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="loadUsers"
        />
      </div>
    </div>

    <!-- Edit User Modal -->
    <el-dialog v-model="showEditModal" title="编辑用户" width="500px">
      <div v-if="editUser">
        <div class="form-item">
          <div class="form-label">用户昵称</div>
          <el-input v-model="editUser.nickname" placeholder="请输入昵称" />
        </div>
        <div class="form-row">
          <div class="form-item">
            <div class="form-label">宠物名</div>
            <el-input v-model="editUser.petName" placeholder="宠物名" />
          </div>
          <div class="form-item">
            <div class="form-label">宠物等级</div>
            <el-input-number v-model="editUser.petLevel" :min="1" :max="100" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <div class="form-label">金币</div>
            <el-input-number v-model="editUser.gold" :min="0" />
          </div>
          <div class="form-item">
            <div class="form-label">用户等级</div>
            <el-input-number v-model="editUser.level" :min="1" :max="100" />
          </div>
        </div>
        <div class="form-item">
          <div class="form-label">状态</div>
          <el-select v-model="editUser.statusValue" style="width:100%;">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="banned" />
            <el-option label="待验证" value="pending" />
          </el-select>
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="showEditModal = false">取消</button>
        <button class="btn btn-primary" @click="saveUser">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAdmin } from '@/api/index.js'

const keyword = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const showEditModal = ref(false)
const editUser = ref(null)
const loading = ref(false)

const users = ref([])

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (keyword.value) params.keyword = keyword.value
    if (statusFilter.value) params.status = statusFilter.value
    const res = await userAdmin.list(params)
    if (res.code === 200 && res.data) {
      users.value = (res.data.list || []).map(u => ({
        id: u.id,
        nickname: u.nickname || '未知',
        avatarBg: '#FFD5E5',
        avatarEmoji: '🐱',
        petName: '小布',
        petEmoji: '🐱',
        petLevel: u.level || 1,
        regType: u.openId ? 'wechat' : u.phone ? 'phone' : 'guest',
        lastActive: u.updateTime ? new Date(u.updateTime).toLocaleDateString('zh-CN') : '未知',
        paidAmount: 0,
        status: u.status === 0 ? '禁用' : '正常',
        statusValue: u.status === 0 ? 'banned' : 'active',
        gold: u.gold || 0,
        level: u.level || 1
      }))
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载用户失败', e)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const searchUsers = () => {
  currentPage.value = 1
  loadUsers()
}

const openEditModal = (user) => {
  editUser.value = { ...user }
  showEditModal.value = true
}

const saveUser = async () => {
  try {
    const res = await userAdmin.update(editUser.value.id, {
      nickname: editUser.value.nickname,
      gold: editUser.value.gold,
      level: editUser.value.level
    })
    if (res.code === 200) {
      ElMessage.success('用户信息已保存')
      showEditModal.value = false
      loadUsers()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const toggleBan = async (user) => {
  const action = user.status === '正常' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${user.nickname} 吗？`, '提示')
    const res = user.status === '正常'
      ? await userAdmin.ban(user.id)
      : await userAdmin.unban(user.id)
    if (res.code === 200) {
      ElMessage.success(`用户已${action}`)
      loadUsers()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-page { display: block; }
</style>
