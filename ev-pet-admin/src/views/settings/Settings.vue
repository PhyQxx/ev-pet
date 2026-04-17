<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基础配置" name="basic">
        <div class="config-section">
          <h3 class="section-title">基础设置</h3>
          <el-form label-width="160px">
            <el-form-item label="应用名称">
              <el-input v-model="config.appName" placeholder="请输入应用名称" />
            </el-form-item>
            <el-form-item label="应用版本">
              <el-input v-model="config.version" disabled />
            </el-form-item>
            <el-form-item label="默认宠物名">
              <el-input v-model="config.defaultPetName" placeholder="新用户默认宠物名" />
            </el-form-item>
            <el-form-item label="新用户初始金币">
              <el-input-number v-model="config.initialGold" :min="0" />
            </el-form-item>
            <el-form-item label="宠物属性衰减(小时)">
              <el-input-number v-model="config.attributeDecayHour" :min="1" />
              <span class="tip">宠物属性每隔多少小时衰减一次</span>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="saveConfig('basic')">保存配置</el-button>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="AI对话" name="ai">
        <div class="config-section">
          <h3 class="section-title">AI对话配置</h3>
          <el-form label-width="160px">
            <el-form-item label="AI服务商">
              <el-select v-model="config.aiProvider">
                <el-option label="MiniMax" value="minimax" />
                <el-option label="百度ERNIE" value="ernie" />
                <el-option label="OpenAI" value="openai" />
              </el-select>
            </el-form-item>
            <el-form-item label="API Key">
              <el-input v-model="config.aiApiKey" type="password" show-password placeholder="请输入API Key" />
            </el-form-item>
            <el-form-item label="Group ID">
              <el-input v-model="config.aiGroupId" placeholder="请输入Group ID" />
            </el-form-item>
            <el-form-item label="模型名称">
              <el-input v-model="config.aiModel" placeholder="如: MiniMax-Text-01" />
            </el-form-item>
            <el-form-item label="单次最大Token">
              <el-input-number v-model="config.maxTokens" :min="100" :max="4000" />
            </el-form-item>
            <el-form-item label="温度参数">
              <el-slider v-model="config.temperature" :min="0" :max="1" :step="0.1" show-input />
            </el-form-item>
            <el-form-item label="内容安全过滤">
              <el-switch v-model="config.contentFilter" />
              <span class="tip">启用后将过滤敏感词和违规内容</span>
            </el-form-item>
          </el-form>
          <el-button type="primary" :loading="saving" @click="saveConfig('ai')">保存配置</el-button>
          <el-button :loading="testing" @click="testAI">测试连接</el-button>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="游戏规则" name="game">
        <div class="config-section">
          <h3 class="section-title">养成系统配置</h3>
          <el-form label-width="160px">
            <el-form-item label="喂食饱食度恢复">
              <el-input-number v-model="config.feedFullness" :min="1" />
              <span class="unit">点/次</span>
            </el-form-item>
            <el-form-item label="洗澡体力恢复">
              <el-input-number v-model="config.bathHealth" :min="1" />
              <span class="unit">点/次</span>
            </el-form-item>
            <el-form-item label="陪玩心情恢复">
              <el-input-number v-model="config.playMood" :min="1" />
              <span class="unit">点/次</span>
            </el-form-item>
            <el-form-item label="陪玩体力消耗">
              <el-input-number v-model="config.playHealthCost" :min="0" />
              <span class="unit">点/次</span>
            </el-form-item>
          </el-form>
          
          <h3 class="section-title mt-6">进化规则配置</h3>
          <el-form label-width="160px">
            <el-form-item label="第一阶段所需经验">
              <el-input-number v-model="config.expToStage2" :min="1" />
            </el-form-item>
            <el-form-item label="第二阶段所需经验">
              <el-input-number v-model="config.expToStage3" :min="1" />
            </el-form-item>
          </el-form>
          
          <h3 class="section-title mt-6">属性衰减配置</h3>
          <el-form label-width="160px">
            <el-form-item label="体力每小时衰减">
              <el-input-number v-model="config.healthDecayPerHour" :min="0" />
            </el-form-item>
            <el-form-item label="饱食度每小时衰减">
              <el-input-number v-model="config.fullnessDecayPerHour" :min="0" />
            </el-form-item>
            <el-form-item label="心情每小时衰减">
              <el-input-number v-model="config.moodDecayPerHour" :min="0" />
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="saveConfig('game')">保存配置</el-button>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="版本管理" name="version">
        <div class="config-section">
          <h3 class="section-title">版本发布</h3>
          <el-button type="primary" @click="showVersionDialog">发布新版本</el-button>
          
          <el-table :data="versions" stripe class="mt-4">
            <el-table-column prop="version" label="版本号" width="120" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="getVersionTag(row.type)">{{ getVersionName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="更新内容" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 'current' ? 'success' : 'info'">
                  {{ row.status === 'current' ? '当前版本' : '历史版本' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="160" />
          </el-table>
        </div>
        
        <!-- 发布版本弹窗 -->
        <el-dialog v-model="showVersionDialogFlag" title="发布新版本" width="500px">
          <el-form label-width="100px">
            <el-form-item label="版本号" required>
              <el-input v-model="versionForm.version" placeholder="如: 1.0.1" />
            </el-form-item>
            <el-form-item label="版本类型" required>
              <el-radio-group v-model="versionForm.type">
                <el-radio label="major">大版本</el-radio>
                <el-radio label="minor">小版本</el-radio>
                <el-radio label="patch">热修复</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="更新内容" required>
              <el-input v-model="versionForm.content" type="textarea" :rows="4" placeholder="请输入更新内容" />
            </el-form-item>
            <el-form-item label="强制更新">
              <el-switch v-model="versionForm.forceUpdate" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showVersionDialogFlag = false">取消</el-button>
            <el-button type="primary" @click="publishVersion">发布</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
      
      <el-tab-pane label="弹窗管理" name="popup">
        <div class="config-section">
          <h3 class="section-title">弹窗配置</h3>
          <el-button type="primary" @click="showPopupDialog">添加弹窗</el-button>
          
          <el-table :data="popups" stripe class="mt-4">
            <el-table-column prop="title" label="弹窗标题" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ getPopupTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="显示时机" width="120">
              <template #default="{ row }">
                {{ row.showTime }}s
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="editPopup(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deletePopup(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <el-dialog v-model="showPopupDialogFlag" :title="isEditPopup ? '编辑弹窗' : '添加弹窗'" width="600px">
          <el-form label-width="100px">
            <el-form-item label="弹窗标题" required>
              <el-input v-model="popupForm.title" />
            </el-form-item>
            <el-form-item label="弹窗内容">
              <el-input v-model="popupForm.content" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="popupForm.type">
                <el-option label="公告" value="announcement" />
                <el-option label="活动" value="activity" />
                <el-option label="更新日志" value="changelog" />
              </el-select>
            </el-form-item>
            <el-form-item label="显示时机">
              <el-input-number v-model="popupForm.showTime" :min="0" :max="60" />
              <span class="tip">用户登录后多少秒显示，0表示立即显示</span>
            </el-form-item>
            <el-form-item label="显示位置">
              <el-radio-group v-model="popupForm.position">
                <el-radio label="center">居中</el-radio>
                <el-radio label="bottom">底部</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="按钮配置">
              <el-input v-model="popupForm.buttonText" placeholder="按钮文字" />
              <el-switch v-model="popupForm.showClose" active-text="显示关闭按钮" style="margin-left: 12px" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showPopupDialogFlag = false">取消</el-button>
            <el-button type="primary" @click="savePopup">保存</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const activeTab = ref('basic')
const showVersionDialogFlag = ref(false)
const showPopupDialogFlag = ref(false)
const isEditPopup = ref(false)
const saving = ref(false)
const testing = ref(false)

const config = ref({
  appName: 'EV Pet',
  version: '1.0.0',
  defaultPetName: '小可爱',
  initialGold: 1000,
  attributeDecayHour: 1,
  aiProvider: 'minimax',
  aiApiKey: '',
  aiGroupId: '',
  aiModel: 'MiniMax-Text-01',
  maxTokens: 2000,
  temperature: 0.7,
  contentFilter: true,
  feedFullness: 20,
  bathHealth: 20,
  playMood: 20,
  playHealthCost: 5,
  expToStage2: 100,
  expToStage3: 500,
  healthDecayPerHour: 3,
  fullnessDecayPerHour: 5,
  moodDecayPerHour: 2
})

// AI配置 key 与前端字段的映射
const aiConfigMap = {
  ai_api_url: null,
  ai_api_key: 'aiApiKey',
  ai_group_id: 'aiGroupId',
  ai_model: 'aiModel',
  ai_max_tokens: 'maxTokens',
  ai_temperature: 'temperature',
  ai_content_filter: 'contentFilter'
}

const versionForm = ref({
  version: '',
  type: 'minor',
  content: '',
  forceUpdate: false
})

const popupForm = ref({
  title: '',
  content: '',
  type: 'announcement',
  showTime: 0,
  position: 'center',
  buttonText: '我知道了',
  showClose: true
})

const versions = ref([
  { version: '1.0.0', type: 'major', content: '正式版本发布', status: 'current', createTime: '2026-04-15 10:00' },
  { version: '0.9.0', type: 'minor', content: 'AI对话功能优化', status: 'old', createTime: '2026-04-10 15:00' },
  { version: '0.8.5', type: 'patch', content: '修复已知问题', status: 'old', createTime: '2026-04-05 12:00' }
])

const popups = ref([
  { id: 1, title: '新功能上线', content: 'AI对话功能已上线，快来体验吧！', type: 'changelog', showTime: 3, position: 'center', buttonText: '我知道了', showClose: true, status: 1 },
  { id: 2, title: '周末活动', content: '周末登录送好礼，不要错过哦~', type: 'activity', showTime: 5, position: 'bottom', buttonText: '立即参加', showClose: true, status: 1 }
])

// 从数据库加载AI配置
const loadAIConfig = async () => {
  try {
    const { data } = await axios.get('/api/admin/config')
    if (data.code === 200 && data.data) {
      const dbConfig = data.data
      if (dbConfig.ai_api_key) config.value.aiApiKey = dbConfig.ai_api_key
      if (dbConfig.ai_group_id) config.value.aiGroupId = dbConfig.ai_group_id
      if (dbConfig.ai_model) config.value.aiModel = dbConfig.ai_model
      if (dbConfig.ai_max_tokens) config.value.maxTokens = Number(dbConfig.ai_max_tokens)
      if (dbConfig.ai_temperature) config.value.temperature = Number(dbConfig.ai_temperature)
      if (dbConfig.ai_content_filter !== undefined) config.value.contentFilter = dbConfig.ai_content_filter === 'true'
    }
  } catch (e) {
    console.warn('加载配置失败，使用默认值', e)
  }
}

const saveConfig = async (tab) => {
  if (tab !== 'ai') {
    ElMessage.success(`${tab === 'basic' ? '基础配置' : '游戏规则'}已保存`)
    return
  }
  saving.value = true
  try {
    const payload = {
      ai_api_key: config.value.aiApiKey,
      ai_group_id: config.value.aiGroupId,
      ai_model: config.value.aiModel,
      ai_max_tokens: String(config.value.maxTokens),
      ai_temperature: String(config.value.temperature),
      ai_content_filter: String(config.value.contentFilter)
    }
    const { data } = await axios.post('/api/admin/config', payload)
    if (data.code === 200) {
      ElMessage.success('AI配置已保存')
    } else {
      ElMessage.error(data.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.response?.data?.message || e.message))
  } finally {
    saving.value = false
  }
}

const testAI = async () => {
  testing.value = true
  try {
    const { data } = await axios.post('/api/admin/config/test-ai')
    if (data.code === 200) {
      ElMessage.success('AI服务连接正常')
    } else {
      ElMessage.error(data.message || '连接失败')
    }
  } catch (e) {
    ElMessage.error('连接失败: ' + (e.response?.data?.message || e.message))
  } finally {
    testing.value = false
  }
}

onMounted(() => {
  loadAIConfig()
})

const showVersionDialog = () => {
  versionForm.value = { version: '', type: 'minor', content: '', forceUpdate: false }
  showVersionDialogFlag.value = true
}

const publishVersion = () => {
  if (!versionForm.value.version || !versionForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success('版本已发布')
  showVersionDialogFlag.value = false
}

const showPopupDialog = () => {
  isEditPopup.value = false
  popupForm.value = { title: '', content: '', type: 'announcement', showTime: 0, position: 'center', buttonText: '我知道了', showClose: true }
  showPopupDialogFlag.value = true
}

const editPopup = (popup) => {
  isEditPopup.value = true
  popupForm.value = { ...popup }
  showPopupDialogFlag.value = true
}

const deletePopup = (popup) => {
  ElMessage.success('弹窗已删除')
}

const savePopup = () => {
  if (!popupForm.value.title) {
    ElMessage.warning('请输入弹窗标题')
    return
  }
  ElMessage.success(isEditPopup.value ? '弹窗已更新' : '弹窗已添加')
  showPopupDialogFlag.value = false
}

const getVersionTag = (type) => ({ major: 'primary', minor: 'success', patch: 'warning' }[type] || '')
const getVersionName = (type) => ({ major: '大版本', minor: '小版本', patch: '热修复' }[type] || type)
const getPopupTypeName = (type) => ({ announcement: '公告', activity: '活动', changelog: '更新日志' }[type] || type)
</script>

<style scoped>
.settings-page { padding: 0; }

.config-section { background: white; border-radius: 8px; padding: 24px; }

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}

.mt-6 { margin-top: 32px; }

.tip {
  margin-left: 12px;
  font-size: 12px;
  color: #A898B8;
}

.unit {
  margin-left: 12px;
  font-size: 14px;
  color: #7A6B8A;
}

.mt-4 { margin-top: 16px; }
</style>
