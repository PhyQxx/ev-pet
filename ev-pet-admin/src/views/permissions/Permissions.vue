<template>
  <div class="admin-page">
    <div class="page-title">⚙️ 账号权限</div>
    <div class="page-sub">权限管理、角色配置、系统参数</div>

    <!-- Admin Accounts Card -->
    <div class="card" style="margin-bottom:20px;">
      <div class="card-title">👥 管理员账号</div>
      <div style="display:flex;flex-direction:column;gap:0;">
        <div v-for="admin in admins" :key="admin.id" class="admin-item">
          <div class="admin-avatar" :style="{ background: admin.avatarBg }">{{ admin.avatarEmoji }}</div>
          <div class="admin-info">
            <div class="admin-name">{{ admin.name }}</div>
            <div class="admin-meta">{{ admin.role }} · 上次登录：{{ admin.lastLogin }}</div>
          </div>
          <span :class="['badge', admin.roleBadge]">{{ admin.roleName }}</span>
          <div style="display:flex;gap:6px;margin-left:12px;">
            <button class="btn btn-sm" @click="openEditAdmin(admin)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="deleteAdmin(admin)" v-if="!admin.isSuper">删除</button>
          </div>
        </div>
      </div>
      <button class="btn btn-primary" style="margin-top:16px;" @click="openAddAdmin">+ 添加管理员</button>
    </div>

    <!-- Role Permissions Card -->
    <div class="card" style="margin-bottom:20px;">
      <div class="card-title">🔐 角色权限</div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>角色名称</th>
              <th>描述</th>
              <th>成员数</th>
              <th>权限</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="role in roles" :key="role.name">
              <td><span class="badge badge-purple">{{ role.name }}</span></td>
              <td>{{ role.desc }}</td>
              <td>{{ role.userCount }}</td>
              <td>
                <span v-for="perm in role.permissions.slice(0, 3)" :key="perm" class="badge badge-blue" style="margin-right:4px;">{{ perm }}</span>
                <span v-if="role.permissions.length > 3" class="badge badge-yellow">+{{ role.permissions.length - 3 }}</span>
              </td>
              <td>
                <span class="action-link" @click="openEditRole(role)">编辑</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- AI Model Config Card -->
    <div class="card" style="margin-bottom:20px;">
      <div class="card-title">🤖 AI 模型配置</div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">API 类型</div>
          <el-select v-model="aiConfig.ai_api_type" style="margin-top:6px;width:100%;">
            <el-option label="OpenAI 兼容（MiMo / DeepSeek / 通义等）" value="openai" />
            <el-option label="MiniMax" value="minimax" />
          </el-select>
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">模型名称</div>
          <el-input v-model="aiConfig.ai_model" placeholder="如 mimo-v2.5-pro / MiniMax-Text-01" style="margin-top:6px;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;grid-column:1/-1;">
          <div class="form-label">API 地址</div>
          <el-input v-model="aiConfig.ai_api_url" placeholder="https://api.xiaomimimo.com/v1/chat/completions" style="margin-top:6px;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;grid-column:1/-1;">
          <div class="form-label">API Key</div>
          <el-input v-model="aiConfig.ai_api_key" type="password" placeholder="请输入 API Key" show-password style="margin-top:6px;" />
        </div>
      </div>
      <div style="display:flex;gap:10px;margin-top:14px;">
        <button class="btn btn-primary" @click="saveAIConfig">💾 保存 AI 配置</button>
        <button class="btn" @click="testAIConfig">🔍 测试连接</button>
      </div>
    </div>

    <!-- System Parameters Card -->
    <div class="card">
      <div class="card-title">🔧 系统参数配置</div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">单用户最大宠物数</div>
          <el-input-number v-model="params.maxPets" :min="1" style="margin-top:6px;width:100%;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">体力上限</div>
          <el-input-number v-model="params.maxHealth" :min="1" style="margin-top:6px;width:100%;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">AI 对话频率限制（条/分钟）</div>
          <el-input-number v-model="params.aiLimit" :min="1" style="margin-top:6px;width:100%;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">免费用户每日最大对话数</div>
          <el-input-number v-model="params.dailyChatLimit" :min="1" style="margin-top:6px;width:100%;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">打工每次消耗体力</div>
          <el-input-number v-model="params.workHealthCost" :min="0" style="margin-top:6px;width:100%;" />
        </div>
        <div class="form-item" style="padding:12px;background:#FAFAFA;border-radius:12px;border:none;">
          <div class="form-label">连续登录奖励天数上限</div>
          <el-input-number v-model="params.maxLoginDays" :min="1" style="margin-top:6px;width:100%;" />
        </div>
      </div>
      <button class="btn btn-primary" style="margin-top:14px;" @click="saveParams">💾 保存参数</button>
    </div>

    <!-- Add/Edit Admin Modal -->
    <el-dialog v-model="showAdminModal" :title="isEditAdmin ? '编辑管理员' : '+ 添加管理员'" width="420px">
      <div class="form-item">
        <div class="form-label">用户名</div>
        <el-input v-model="adminForm.username" placeholder="请输入用户名" :disabled="isEditAdmin" />
      </div>
      <div class="form-item" v-if="!isEditAdmin">
        <div class="form-label">密码</div>
        <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" show-password />
      </div>
      <div class="form-item">
        <div class="form-label">角色</div>
        <el-select v-model="adminForm.role" style="width:100%;">
          <el-option label="超级管理员" value="SUPER_ADMIN" />
          <el-option label="运营管理员" value="OP_ADMIN" />
          <el-option label="内容编辑" value="EDITOR" />
          <el-option label="运维人员" value="OPS" />
          <el-option label="客服" value="SUPPORT" />
        </el-select>
      </div>
      <template #footer>
        <button class="btn" @click="showAdminModal = false">取消</button>
        <button class="btn btn-primary" @click="saveAdmin">保存</button>
      </template>
    </el-dialog>

    <!-- Edit Role Modal -->
    <el-dialog v-model="showRoleModal" title="编辑角色权限" width="520px">
      <div class="form-item">
        <div class="form-label">角色名称</div>
        <el-input v-model="roleForm.name" disabled />
      </div>
      <div class="form-item">
        <div class="form-label">角色描述</div>
        <el-input v-model="roleForm.desc" />
      </div>
      <div class="form-item">
        <div class="form-label">权限配置</div>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;">
          <el-checkbox v-for="perm in allPermissions" :key="perm.value" v-model="roleForm.permissions" :label="perm.value">{{ perm.label }}</el-checkbox>
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="showRoleModal = false">取消</button>
        <button class="btn btn-primary" @click="saveRole">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminAccount, roleAdmin, systemConfig } from '@/api/index.js'

const showAdminModal = ref(false)
const showRoleModal = ref(false)
const isEditAdmin = ref(false)
const currentAdmin = ref(null)

const adminForm = ref({
  username: '',
  password: '',
  role: 'SUPPORT'
})

const roleForm = ref({
  name: '',
  desc: '',
  permissions: []
})

const params = ref({
  maxPets: 3,
  maxHealth: 100,
  aiLimit: 10,
  dailyChatLimit: 50,
  workHealthCost: 30,
  maxLoginDays: 7
})

const aiConfig = ref({
  ai_api_type: 'openai',
  ai_api_url: '',
  ai_api_key: '',
  ai_model: ''
})

const admins = ref([])
const roles = ref([])

const allPermissions = [
  { value: 'user.view', label: '查看用户' },
  { value: 'user.edit', label: '编辑用户' },
  { value: 'user.delete', label: '删除用户' },
  { value: 'item.view', label: '查看商品' },
  { value: 'item.edit', label: '编辑商品' },
  { value: 'item.delete', label: '删除商品' },
  { value: 'content.audit', label: '内容审核' },
  { value: 'announcement.publish', label: '发布公告' },
  { value: 'activity.manage', label: '活动管理' },
  { value: 'system.config', label: '系统配置' }
]

const getRoleBadge = (role) => {
  return { SUPER_ADMIN: 'badge-green', OP_ADMIN: 'badge-purple', EDITOR: 'badge-purple', OPS: 'badge-blue', SUPPORT: 'badge-blue' }[role] || 'badge-blue'
}

const loadAdmins = async () => {
  try {
    const res = await adminAccount.list()
    if (res.code === 200 && res.data) {
      admins.value = res.data.map(a => ({
        id: a.id,
        name: a.name || '',
        avatarBg: a.avatarBg || '#D5AAFF',
        avatarEmoji: a.avatarEmoji || '👨‍💻',
        role: a.role || 'SUPPORT',
        roleName: a.roleName || a.role,
        roleBadge: getRoleBadge(a.role),
        lastLogin: a.lastLogin || '',
        isSuper: a.isSuper || false
      }))
    }
  } catch (e) {
    console.error('加载管理员列表失败', e)
  }
}

const loadRoles = async () => {
  try {
    const res = await roleAdmin.list()
    if (res.code === 200 && res.data) {
      roles.value = res.data.map(r => ({
        id: r.id,
        name: r.name || '',
        desc: r.desc || '',
        userCount: r.userCount || 0,
        permissions: r.permissions || []
      }))
    }
  } catch (e) {
    console.error('加载角色列表失败', e)
  }
}

const loadParams = async () => {
  try {
    const res = await systemConfig.getAll()
    if (res.code === 200 && res.data) {
      const p = res.data
      if (p.maxPets) params.value.maxPets = parseInt(p.maxPets)
      if (p.maxHealth) params.value.maxHealth = parseInt(p.maxHealth)
      if (p.aiLimit) params.value.aiLimit = parseInt(p.aiLimit)
      if (p.dailyChatLimit) params.value.dailyChatLimit = parseInt(p.dailyChatLimit)
      if (p.workHealthCost) params.value.workHealthCost = parseInt(p.workHealthCost)
      if (p.maxLoginDays) params.value.maxLoginDays = parseInt(p.maxLoginDays)
      // AI config
      if (p.ai_api_type) aiConfig.value.ai_api_type = p.ai_api_type
      if (p.ai_api_url) aiConfig.value.ai_api_url = p.ai_api_url
      if (p.ai_api_key) aiConfig.value.ai_api_key = p.ai_api_key
      if (p.ai_model) aiConfig.value.ai_model = p.ai_model
    }
  } catch (e) {
    console.error('加载系统参数失败', e)
  }
}

const openAddAdmin = () => {
  isEditAdmin.value = false
  adminForm.value = { username: '', password: '', role: 'SUPPORT' }
  showAdminModal.value = true
}

const openEditAdmin = (admin) => {
  isEditAdmin.value = true
  currentAdmin.value = admin
  adminForm.value = { username: admin.name, password: '', role: admin.role }
  showAdminModal.value = true
}

const saveAdmin = async () => {
  if (!isEditAdmin.value && !adminForm.value.password) { ElMessage.warning('请输入密码'); return }
  try {
    let res
    if (isEditAdmin.value) {
      res = await adminAccount.update(currentAdmin.value.id, { role: adminForm.value.role })
    } else {
      res = await adminAccount.create(adminForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(isEditAdmin.value ? '管理员已更新' : '管理员已添加')
      showAdminModal.value = false
      loadAdmins()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteAdmin = async (admin) => {
  try {
    await ElMessageBox.confirm(`确定要删除管理员「${admin.name}」吗？`, '提示')
    const res = await adminAccount.delete(admin.id)
    if (res.code === 200) {
      ElMessage.success('管理员已删除')
      loadAdmins()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const openEditRole = (role) => {
  roleForm.value = { name: role.name, desc: role.desc, permissions: [...role.permissions] }
  showRoleModal.value = true
}

const saveRole = async () => {
  try {
    const role = roles.value.find(r => r.name === roleForm.value.name)
    if (!role) return
    const res = await roleAdmin.update(role.id, { desc: roleForm.value.desc, permissions: roleForm.value.permissions })
    if (res.code === 200) {
      ElMessage.success('角色权限已保存')
      showRoleModal.value = false
      loadRoles()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const saveAIConfig = async () => {
  try {
    const configMap = {
      ai_api_type: aiConfig.value.ai_api_type,
      ai_api_url: aiConfig.value.ai_api_url,
      ai_api_key: aiConfig.value.ai_api_key,
      ai_model: aiConfig.value.ai_model
    }
    const res = await systemConfig.save(configMap)
    if (res.code === 200) {
      ElMessage.success('AI 配置已保存')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const testAIConfig = async () => {
  try {
    const res = await systemConfig.testAI()
    if (res.code === 200) {
      ElMessage.success(res.data || 'AI 服务连接正常')
    } else {
      ElMessage.error(res.message || '连接测试失败')
    }
  } catch (e) {
    ElMessage.error('连接测试失败')
  }
}

const saveParams = async () => {
  try {
    const configMap = {
      maxPets: String(params.value.maxPets),
      maxHealth: String(params.value.maxHealth),
      aiLimit: String(params.value.aiLimit),
      dailyChatLimit: String(params.value.dailyChatLimit),
      workHealthCost: String(params.value.workHealthCost),
      maxLoginDays: String(params.value.maxLoginDays)
    }
    const res = await systemConfig.save(configMap)
    if (res.code === 200) {
      ElMessage.success('系统参数已保存')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadAdmins()
  loadRoles()
  loadParams()
})
</script>

<style scoped>
.admin-page { display: block; }

.admin-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #F5EEF8;
}
.admin-item:last-child { border-bottom: none; }

.admin-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.admin-info { flex: 1; }
.admin-name { font-weight: 600; }
.admin-meta { font-size: 12px; color: #7A6B8A; }
</style>
