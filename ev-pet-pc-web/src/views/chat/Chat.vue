<template>
  <div class="chat-page">
    <el-row :gutter="24">
      <el-col :span="16">
        <div class="chat-container card">
          <div class="chat-header">
            <div class="pet-info">
              <span class="pet-emoji">{{ petEmoji }}</span>
              <div class="pet-detail">
                <span class="pet-name">{{ petInfo?.name || '小可爱' }}</span>
                <span class="pet-status">{{ statusText }}</span>
              </div>
            </div>
          </div>
          
          <div class="chat-messages" ref="chatList">
            <div v-for="(msg, i) in messages" :key="i" class="message-item" :class="msg.role">
              <div class="message-avatar">
                {{ msg.role === 'assistant' ? petEmoji : '我' }}
              </div>
              <div class="message-bubble">
                <p class="message-text">{{ msg.content }}</p>
              </div>
            </div>
          </div>
          
          <div class="chat-input-area">
            <div class="quick-replies">
              <span v-for="reply in quickReplies" :key="reply" class="quick-btn" @click="sendQuickReply(reply)">
                {{ reply }}
              </span>
            </div>
            <div class="input-row">
              <el-input v-model="inputText" placeholder="和宠物说点什么..." size="large" @keyup.enter="sendMessage" />
              <el-button type="primary" size="large" @click="sendMessage" :loading="loading">发送</el-button>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="pet-status-card card">
          <h3>宠物状态</h3>
          <div class="status-bars">
            <div class="status-bar">
              <div class="bar-label">❤️ 体力</div>
              <el-progress :percentage="petInfo?.health || 0" :stroke-width="10" />
            </div>
            <div class="status-bar">
              <div class="bar-label">🍖 饱食</div>
              <el-progress :percentage="petInfo?.fullness || 0" :stroke-width="10" color="#FFD166" />
            </div>
            <div class="status-bar">
              <div class="bar-label">😊 心情</div>
              <el-progress :percentage="petInfo?.mood || 0" :stroke-width="10" color="#4ECDC4" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore } from '../../store'
import { chat as chatApi, pet as petApi } from '../../api'

const petStore = usePetStore()
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const chatList = ref(null)

const petInfo = computed(() => petStore.petInfo)
const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})
const statusText = computed(() => {
  const status = petInfo.value?.status
  if (status === 'happy') return '心情很好~'
  if (status === 'hungry') return '肚子饿了'
  if (status === 'tired') return '有点累'
  if (status === 'angry') return '不开心'
  return '开心'
})

const quickReplies = ['你好~', '我饿了', '陪我玩', '今天开心', '唱首歌']

onMounted(async () => {
  try {
    const [petData, history] = await Promise.all([
      petApi.getInfo(),
      chatApi.getHistory()
    ])
    petStore.$patch({ petInfo: petData })
    messages.value = history || []
    scrollToBottom()
  } catch (err) {
    console.error('Failed to load:', err)
  }
})

const sendMessage = async () => {
  const content = inputText.value.trim()
  if (!content || loading.value) return
  
  inputText.value = ''
  loading.value = true
  messages.value.push({ role: 'user', content })
  scrollToBottom()
  
  try {
    const response = await chatApi.send(content)
    messages.value.push({ role: 'assistant', content: response })
    const petData = await petApi.getInfo()
    petStore.$patch({ petInfo: petData })
    scrollToBottom()
  } catch (err) {
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
  }
}

const sendQuickReply = (reply) => {
  inputText.value = reply
  sendMessage()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatList.value) {
      chatList.value.scrollTop = chatList.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #F0F0F0;
}

.pet-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pet-emoji {
  font-size: 48px;
}

.pet-name {
  font-size: 18px;
  font-weight: 600;
  display: block;
}

.pet-status {
  font-size: 14px;
  color: var(--text-secondary);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  background: linear-gradient(135deg, #A8D8EA 0%, #7EC8D8 100%);
}

.message-bubble {
  max-width: 60%;
  margin: 0 12px;
  padding: 16px 20px;
  border-radius: 16px;
  background: #F8F8F8;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #D5AAFF 0%, #C48FFF 100%);
  color: white;
}

.message-text {
  line-height: 1.6;
  margin: 0;
}

.chat-input-area {
  border-top: 1px solid #F0F0F0;
  padding-top: 16px;
}

.quick-replies {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.quick-btn {
  padding: 6px 16px;
  background: var(--accent-purple-light);
  color: var(--text-secondary);
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
}

.quick-btn:hover {
  background: var(--accent-purple);
  color: white;
}

.input-row {
  display: flex;
  gap: 12px;
}

.input-row .el-input {
  flex: 1;
}

.pet-status-card h3 {
  margin-bottom: 20px;
}

.status-bars {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.bar-label {
  margin-bottom: 8px;
  font-size: 14px;
}
</style>
