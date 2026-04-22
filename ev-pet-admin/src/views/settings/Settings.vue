<template>
  <div class="admin-page">
    <div class="page-title">🔧 系统配置</div>
    <div class="page-sub">版本管理、弹窗配置、系统参数调整</div>

    <!-- Version Management Card -->
    <div class="card" style="margin-bottom:20px;">
      <div class="card-title">📋 版本管理</div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>版本号</th>
              <th>平台</th>
              <th>强制更新</th>
              <th>发布时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ver in versions" :key="ver.id">
              <td><span :class="['badge', ver.status === '已发布' ? 'badge-green' : 'badge-purple']">{{ ver.version }}</span></td>
              <td>{{ ver.platform }}</td>
              <td>
                <span :class="['badge', ver.forceUpdate === '是' ? 'badge-red' : ver.forceUpdate === '建议' ? 'badge-yellow' : 'badge-blue']">
                  {{ ver.forceUpdate }}
                </span>
              </td>
              <td>{{ ver.time }}</td>
              <td><span :class="['badge', ver.statusBadge]">{{ ver.status }}</span></td>
              <td><span class="action-link" @click="openVersionModal(ver)">编辑</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      <button class="btn btn-primary" style="margin-top:14px;" @click="openAddVersion">+ 添加版本</button>
    </div>

    <!-- Popup Management Card -->
    <div class="card" style="margin-bottom:20px;">
      <div class="card-title">📢 弹窗管理</div>
      <div style="display:flex;flex-direction:column;gap:10px;">
        <div v-for="popup in popups" :key="popup.id" class="popup-item">
          <div class="popup-body">
            <div style="font-weight:600;font-size:13px;">{{ popup.icon }} {{ popup.title }}</div>
            <div style="font-size:12px;color:#7A6B8A;">{{ popup.time }}</div>
          </div>
          <span :class="['badge', popup.statusBadge]">{{ popup.statusText }}</span>
          <button class="btn btn-sm" @click="openEditPopup(popup)">编辑</button>
        </div>
      </div>
      <button class="btn btn-primary" style="margin-top:14px;" @click="openAddPopup">+ 添加弹窗</button>
    </div>

    <!-- System Parameters Card -->
    <div class="card">
      <div class="card-title">🔧 系统参数</div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-bottom:16px;">
        <div class="param-item">
          <div class="param-label">单用户最大宠物数</div>
          <div class="param-control">
            <el-input-number v-model="params.maxPets" :min="1" size="small" />
            <span class="param-unit">只</span>
          </div>
        </div>
        <div class="param-item">
          <div class="param-label">体力上限</div>
          <div class="param-control">
            <el-input-number v-model="params.maxHealth" :min="1" size="small" />
          </div>
        </div>
        <div class="param-item">
          <div class="param-label">AI 对话频率限制</div>
          <div class="param-control">
            <el-input-number v-model="params.aiLimit" :min="1" size="small" />
            <span class="param-unit">条/分钟</span>
          </div>
        </div>
        <div class="param-item">
          <div class="param-label">免费用户每日对话上限</div>
          <div class="param-control">
            <el-input-number v-model="params.dailyChatLimit" :min="1" size="small" />
            <span class="param-unit">条/天</span>
          </div>
        </div>
        <div class="param-item">
          <div class="param-label">打工每次消耗体力</div>
          <div class="param-control">
            <el-input-number v-model="params.workHealthCost" :min="0" size="small" />
          </div>
        </div>
        <div class="param-item">
          <div class="param-label">连续登录奖励天数上限</div>
          <div class="param-control">
            <el-input-number v-model="params.maxLoginDays" :min="1" size="small" />
          </div>
        </div>
      </div>
      <button class="btn btn-primary" @click="saveParams">💾 保存参数</button>
    </div>

    <!-- Version Modal -->
    <el-dialog v-model="showVersionModal" :title="isEditVersion ? '编辑版本' : '+ 添加版本'" width="480px">
      <div class="form-item">
        <div class="form-label">版本号</div>
        <el-input v-model="versionForm.version" placeholder="如: v1.2.3" />
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">平台</div>
          <el-select v-model="versionForm.platform" style="width:100%;">
            <el-option label="Android" value="Android" />
            <el-option label="iOS" value="iOS" />
            <el-option label="PC Web" value="PC Web" />
          </el-select>
        </div>
        <div class="form-item">
          <div class="form-label">强制更新</div>
          <el-select v-model="versionForm.forceUpdate" style="width:100%;">
            <el-option label="是" value="是" />
            <el-option label="建议" value="建议" />
            <el-option label="否" value="否" />
          </el-select>
        </div>
      </div>
      <div class="form-item">
        <div class="form-label">更新内容</div>
        <el-input v-model="versionForm.content" type="textarea" :rows="3" placeholder="请输入更新内容" />
      </div>
      <template #footer>
        <button class="btn" @click="showVersionModal = false">取消</button>
        <button class="btn btn-primary" @click="saveVersion">保存</button>
      </template>
    </el-dialog>

    <!-- Popup Modal -->
    <el-dialog v-model="showPopupModal" :title="isEditPopup ? '编辑弹窗' : '+ 添加弹窗'" width="520px">
      <div class="form-item">
        <div class="form-label">弹窗标题</div>
        <el-input v-model="popupForm.title" placeholder="请输入弹窗标题" />
      </div>
      <div class="form-item">
        <div class="form-label">弹窗内容</div>
        <el-input v-model="popupForm.content" type="textarea" :rows="3" placeholder="请输入弹窗内容" />
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">类型</div>
          <el-select v-model="popupForm.type" style="width:100%;">
            <el-option label="公告" value="announcement" />
            <el-option label="活动" value="activity" />
            <el-option label="更新日志" value="changelog" />
          </el-select>
        </div>
        <div class="form-item">
          <div class="form-label">预计展示</div>
          <el-input-number v-model="popupForm.showDays" :min="1" style="width:100%;" />
        </div>
      </div>
      <template #footer>
        <button class="btn" @click="showPopupModal = false">取消</button>
        <button class="btn btn-primary" @click="savePopup">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const showVersionModal = ref(false)
const showPopupModal = ref(false)
const isEditVersion = ref(false)
const isEditPopup = ref(false)
const currentVersion = ref(null)
const currentPopup = ref(null)

const versionForm = ref({
  version: '',
  platform: 'Android',
  forceUpdate: '否',
  content: ''
})

const popupForm = ref({
  title: '',
  content: '',
  type: 'announcement',
  showDays: 7
})

const params = ref({
  maxPets: 3,
  maxHealth: 100,
  aiLimit: 10,
  dailyChatLimit: 50,
  workHealthCost: 30,
  maxLoginDays: 7
})

const versions = ref([
  { id: 1, version: 'v1.2.3', platform: 'Android', forceUpdate: '是', time: '2026-04-15', status: '已发布', statusBadge: 'badge-green' },
  { id: 2, version: 'v1.2.3', platform: 'iOS', forceUpdate: '建议', time: '2026-04-15', status: '审核中', statusBadge: 'badge-yellow' },
  { id: 3, version: 'v1.1.8', platform: 'PC Web', forceUpdate: '是', time: '2026-04-10', status: '已发布', statusBadge: 'badge-green' }
])

const popups = ref([
  { id: 1, title: '五一活动弹窗', icon: '🎉', time: '2026-04-28 00:00 · 预计展示 7 天', statusText: '待发布', statusBadge: 'badge-yellow' },
  { id: 2, title: '新人引导弹窗', icon: '👋', time: '常驻 · 新用户首次打开 App', statusText: '展示中', statusBadge: 'badge-green' },
  { id: 3, title: '版本更新提示', icon: '🔔', time: '检测到旧版本时强制展示', statusText: '展示中', statusBadge: 'badge-green' }
])

const openAddVersion = () => {
  isEditVersion.value = false
  versionForm.value = { version: '', platform: 'Android', forceUpdate: '否', content: '' }
  showVersionModal.value = true
}

const openVersionModal = (ver) => {
  isEditVersion.value = true
  currentVersion.value = ver
  versionForm.value = { version: ver.version, platform: ver.platform, forceUpdate: ver.forceUpdate, content: ver.content || '' }
  showVersionModal.value = true
}

const saveVersion = () => {
  if (!versionForm.value.version) { ElMessage.warning('请输入版本号'); return }
  ElMessage.success(isEditVersion.value ? '版本已更新' : '版本已添加')
  showVersionModal.value = false
}

const openAddPopup = () => {
  isEditPopup.value = false
  popupForm.value = { title: '', content: '', type: 'announcement', showDays: 7 }
  showPopupModal.value = true
}

const openEditPopup = (popup) => {
  isEditPopup.value = true
  currentPopup.value = popup
  popupForm.value = { title: popup.title, content: popup.content || '', type: popup.type || 'announcement', showDays: popup.showDays || 7 }
  showPopupModal.value = true
}

const savePopup = () => {
  if (!popupForm.value.title) { ElMessage.warning('请输入弹窗标题'); return }
  ElMessage.success(isEditPopup.value ? '弹窗已更新' : '弹窗已添加')
  showPopupModal.value = false
}

const saveParams = () => {
  ElMessage.success('系统参数已保存')
}
</script>

<style scoped>
.admin-page { display: block; }

.popup-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #FAFAFA;
  border-radius: 12px;
}

.popup-body { flex: 1; }

.param-item {
  padding: 14px;
  background: #FAFAFA;
  border-radius: 12px;
}

.param-label {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}

.param-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.param-unit {
  font-size: 12px;
  color: #7A6B8A;
}
</style>
