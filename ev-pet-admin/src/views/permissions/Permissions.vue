<template>
  <div class="permissions-page">
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3>管理员账号</h3>
            <el-button type="primary" size="small" @click="showAddDialog">添加管理员</el-button>
          </div>
          
          <el-table :data="admins" stripe>
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="role" label="角色" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastLogin" label="最后登录" width="160" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="editAdmin(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteAdmin(row)" :disabled="row.isSuper">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3>角色权限</h3>
            <el-button type="primary" size="small" @click="showRoleDialog">编辑角色</el-button>
          </div>
          
          <el-table :data="roles" stripe>
            <el-table-column prop="name" label="角色名称" width="120" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="userCount" label="成员数" width="80" />
            <el-table-column label="权限" width="200">
              <template #default="{ row }">
                <el-tag v-for="perm in row.permissions.slice(0, 3)" :key="perm" size="small" style="margin-right: 4px">{{ perm }}</el-tag>
                <el-tag v-if="row.permissions.length > 3" size="small">+{{ row.permissions.length - 3 }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="24" class="mt-6">
      <el-col :span="24">
        <div class="card">
          <div class="card-header">
            <h3>操作日志</h3>
            <el-select v-model="logFilter" placeholder="筛选类型" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="用户管理" value="user" />
              <el-option label="商品管理" value="item" />
              <el-option label="系统配置" value="system" />
            </el-select>
          </div>
          
          <el-table :data="logs" stripe>
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="operator" label="操作人" width="120" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ getLogTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="操作内容" />
            <el-table-column prop="ip" label="IP地址" width="140" />
          </el-table>
          
          <el-pagination
            class="pagination"
            background
            layout="prev, pager, next, total"
            :total="logTotal"
            :page-size="logPageSize"
            v-model:current-page="logPage"
          />
        </div>
      </el-col>
    </el-row>
    
    <!-- 添加/编辑管理员弹窗 -->
    <el-dialog v-model="showAdminDialog" :title="isEditAdmin ? '编辑管理员' : '添加管理员'" width="400px">
      <el-form :model="adminForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="adminForm.username" placeholder="请输入用户名" :disabled="isEditAdmin" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEditAdmin" required>
          <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="adminForm.role">
            <el-option label="超级管理员" value="SUPER_ADMIN" />
            <el-option label="运营管理员" value="OP_ADMIN" />
            <el-option label="客服" value="SUPPORT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdminDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAdmin">保存</el-button>
      </template>
    </el-dialog>
    
    <!-- 编辑角色权限弹窗 -->
    <el-dialog v-model="showRoleDialog" title="编辑角色权限" width="500px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="roleForm.description" />
        </el-form-item>
        <el-form-item label="权限配置">
          <el-checkbox-group v-model="roleForm.permissions">
            <el-checkbox label="user.view">查看用户</el-checkbox>
            <el-checkbox label="user.edit">编辑用户</el-checkbox>
            <el-checkbox label="user.delete">删除用户</el-checkbox>
            <el-checkbox label="item.view">查看商品</el-checkbox>
            <el-checkbox label="item.edit">编辑商品</el-checkbox>
            <el-checkbox label="content.audit">内容审核</el-checkbox>
            <el-checkbox label="announcement.publish">发布公告</el-checkbox>
            <el-checkbox label="activity.manage">活动管理</el-checkbox>
            <el-checkbox label="system.config">系统配置</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const logFilter = ref('')
const logPage = ref(1)
const logPageSize = ref(20)
const logTotal = ref(100)
const showAdminDialog = ref(false)
const showRoleDialog = ref(false)
const isEditAdmin = ref(false)

const adminForm = ref({
  username: '',
  password: '',
  role: 'SUPPORT'
})

const roleForm = ref({
  name: '',
  description: '',
  permissions: []
})

const admins = ref([
  { id: 1, username: 'admin', role: 'SUPER_ADMIN', lastLogin: '2026-04-15 10:30', status: 1, isSuper: true },
  { id: 2, username: 'operator1', role: 'OP_ADMIN', lastLogin: '2026-04-15 09:20', status: 1, isSuper: false },
  { id: 3, username: 'support1', role: 'SUPPORT', lastLogin: '2026-04-14 18:45', status: 1, isSuper: false },
  { id: 4, username: 'operator2', role: 'OP_ADMIN', lastLogin: '2026-04-10 12:00', status: 0, isSuper: false }
])

const roles = ref([
  { name: 'SUPER_ADMIN', description: '超级管理员，拥有所有权限', userCount: 1, permissions: ['user.view', 'user.edit', 'user.delete', 'item.view', 'item.edit', 'content.audit', 'announcement.publish', 'activity.manage', 'system.config'] },
  { name: 'OP_ADMIN', description: '运营管理员，日常运营操作', userCount: 2, permissions: ['user.view', 'item.view', 'item.edit', 'content.audit', 'announcement.publish', 'activity.manage'] },
  { name: 'SUPPORT', description: '客服，仅查看和基础操作', userCount: 1, permissions: ['user.view', 'content.audit'] }
])

const logs = ref([
  { time: '2026-04-15 10:30:15', operator: 'admin', type: 'user', content: '编辑用户[小明]信息', ip: '192.168.1.100' },
  { time: '2026-04-15 10:15:20', operator: 'operator1', type: 'item', content: '添加商品[生日蛋糕]', ip: '192.168.1.101' },
  { time: '2026-04-15 09:45:30', operator: 'admin', type: 'system', content: '修改系统配置项[max_upload_size]', ip: '192.168.1.100' },
  { time: '2026-04-15 09:30:00', operator: 'operator1', type: 'content', content: '审核通过动态#1234', ip: '192.168.1.101' },
  { time: '2026-04-15 09:15:45', operator: 'support1', type: 'user', content: '查看用户[小红]详情', ip: '192.168.1.102' }
])

const getLogTypeName = (type) => ({ user: '用户管理', item: '商品管理', content: '内容审核', system: '系统配置' }[type] || type)

const showAddDialog = () => {
  isEditAdmin.value = false
  adminForm.value = { username: '', password: '', role: 'SUPPORT' }
  showAdminDialog.value = true
}

const editAdmin = (admin) => {
  isEditAdmin.value = true
  adminForm.value = { ...admin }
  showAdminDialog.value = true
}

const deleteAdmin = (admin) => {
  ElMessageBox.confirm(`确定要删除管理员"${admin.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('管理员已删除')
  })
}

const toggleStatus = (admin) => {
  ElMessage.success(`管理员已${admin.status === 1 ? '启用' : '禁用'}`)
}

const saveAdmin = () => {
  if (!isEditAdmin.value && !adminForm.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  ElMessage.success(isEditAdmin.value ? '管理员已更新' : '管理员已添加')
  showAdminDialog.value = false
}

const saveRole = () => {
  ElMessage.success('角色权限已保存')
  showRoleDialog.value = false
}
</script>

<style scoped>
.permissions-page { padding: 0; }

.mt-6 { margin-top: 24px; }

.card { background: white; border-radius: 8px; padding: 20px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 { margin: 0; font-size: 16px; }

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
