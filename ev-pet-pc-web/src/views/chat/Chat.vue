<template>
  <div class="chat-page">
    <!-- Left Sidebar -->
    <aside class="left-sidebar">
      <div class="sidebar-logo">
        <div class="logo-icon">🐾</div>
        <div class="logo-text">EV Pet</div>
      </div>

      <div class="chat-list">
        <div
          v-for="chat in chatHistory"
          :key="chat.id"
          class="chat-list-item"
          :class="{ active: activeChatId === chat.id }"
          @click="selectChat(chat)"
        >
          <div class="chat-list-avatar">{{ chat.avatar }}</div>
          <div class="chat-list-info">
            <div class="chat-list-name">{{ chat.name }}</div>
            <div class="chat-list-preview">{{ chat.preview }}</div>
          </div>
        </div>
      </div>

      <button class="chat-new-btn" @click="createNewChat">
        <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建对话
      </button>
    </aside>

    <!-- Main Chat Area -->
    <main class="chat-main">
      <!-- Chat Header -->
      <div class="chat-header">
        <div class="chat-pet-avatar">{{ petEmoji }}</div>
        <div class="chat-info">
          <div class="chat-pet-name">{{ petInfo?.name || '小可爱' }}</div>
          <div class="chat-pet-status">
            <div class="status-dot"></div>
            在线 · AI 宠物
          </div>
        </div>
        <div class="header-actions">
          <button class="icon-btn" title="语音">
            <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.61 3.18 2 2 0 0 1 3.6 1h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.91 8.6a16 16 0 0 0 6.06 6.06l1.06-.96a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/>
            </svg>
          </button>
          <button class="icon-btn" title="详情">
            <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
          </button>
          <button class="icon-btn" title="收起">
            <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
              <polyline points="4 14 10 14 10 20"/>
              <polyline points="20 10 14 10 14 4"/>
              <line x1="14" y1="10" x2="21" y2="3"/>
              <line x1="3" y1="21" x2="10" y2="14"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Messages Area -->
      <div class="messages-area" ref="messagesArea">
        <!-- Pet Reaction -->
        <div v-if="currentReaction" class="pet-reaction">
          <span class="pet-reaction-emoji">{{ petEmoji }}</span>
          <span class="pet-reaction-text">{{ currentReaction }}</span>
        </div>

        <!-- Messages -->
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="msg-group"
          :class="msg.role === 'user' ? 'user' : 'pet'"
        >
          <!-- Pet reaction before message -->
          <div v-if="msg.role === 'assistant' && msg.reaction" class="pet-reaction">
            <span class="pet-reaction-emoji">{{ petEmoji }}</span>
            <span class="pet-reaction-text">{{ msg.reaction }}</span>
          </div>

          <div class="msg-row">
            <div class="msg-avatar" :class="msg.role === 'user' ? 'user' : 'pet'">
              {{ msg.role === 'user' ? '👤' : petEmoji }}
            </div>
            <div class="msg-bubble" :class="msg.role === 'user' ? 'user' : 'pet'">
              {{ msg.content }}
            </div>
          </div>
          <div class="msg-time" :style="msg.role === 'pet' ? 'margin-left: 38px' : ''">
            {{ msg.time }}
          </div>
        </div>

        <!-- Typing Indicator -->
        <div v-if="isTyping" class="typing-row">
          <div class="msg-avatar pet">{{ petEmoji }}</div>
          <div class="typing-dots">
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <div style="flex: 1;">
          <div class="input-suggestions">
            <div
              v-for="suggestion in quickReplies"
              :key="suggestion"
              class="suggestion-chip"
              @click="sendQuickReply(suggestion)"
            >
              {{ suggestion }}
            </div>
          </div>
          <div class="input-box-wrap">
            <textarea
              v-model="inputText"
              class="input-box"
              :placeholder="`和${petInfo?.name || '小可爱'}说点什么...`"
              rows="1"
              @input="autoGrow"
              @keydown="handleKeyDown"
            ></textarea>
          </div>
        </div>
        <button class="send-btn" @click="sendMessage" :disabled="loading">
          <svg fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24">
            <line x1="22" y1="2" x2="11" y2="13"/>
            <polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore } from '@/store'
import { chat as chatApi, pet as petApi } from '@/api'

const petStore = usePetStore()

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const isTyping = ref(false)
const currentReaction = ref('')
const messagesArea = ref(null)
const activeChatId = ref(1)

const chatHistory = ref([
  { id: 1, name: '今天的对话', avatar: '🐣', preview: '我们聊了很多关于...', time: '刚刚' },
  { id: 2, name: '宠物养成', avatar: '🐥', preview: '怎么让宠物更开心...', time: '10分钟前' },
  { id: 3, name: '游戏技巧', avatar: '🐦', preview: '分享一些游戏技巧', time: '昨天' }
])

const petInfo = computed(() => petStore.petInfo)

const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const quickReplies = ['今天心情怎么样？', '小福饿了吗？', '想你了~', '给我讲个故事', '一起玩游戏吗？']

const petReactions = [
  '歪头好奇中...', '眨眼微笑~', '开心摇摆！', '惊讶中...！', '害羞地低下头~'
]

const formatTime = () => {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesArea.value) {
      messagesArea.value.scrollTop = messagesArea.value.scrollHeight
    }
  })
}

const autoGrow = (e) => {
  e.target.style.height = 'auto'
  e.target.style.height = Math.min(e.target.scrollHeight, 100) + 'px'
}

const handleKeyDown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const getRandomReaction = () => {
  return petReactions[Math.floor(Math.random() * petReactions.length)]
}

const sendMessage = async () => {
  const content = inputText.value.trim()
  if (!content || loading.value) return

  inputText.value = ''
  document.querySelector('.input-box').style.height = 'auto'
  loading.value = true
  isTyping.value = true

  const userMessage = { role: 'user', content, time: formatTime() }
  messages.value.push(userMessage)
  scrollToBottom()

  try {
    const response = await chatApi.send(content)
    isTyping.value = false

    const reaction = getRandomReaction()
    messages.value.push({
      role: 'assistant',
      content: response,
      time: formatTime(),
      reaction: reaction
    })

    const petData = await petApi.getInfo()
    petStore.$patch({ petInfo: petData })
    scrollToBottom()
  } catch (err) {
    isTyping.value = false
    ElMessage.error('发送失败')
  } finally {
    loading.value = false
  }
}

const sendQuickReply = async (text) => {
  inputText.value = text
  await sendMessage()
}

const selectChat = (chat) => {
  activeChatId.value = chat.id
}

const createNewChat = () => {
  const newId = Math.max(...chatHistory.value.map(c => c.id)) + 1
  chatHistory.value.unshift({
    id: newId,
    name: '新对话',
    avatar: petEmoji.value,
    preview: '开始聊天吧...',
    time: '刚刚'
  })
  activeChatId.value = newId
  messages.value = []
}

onMounted(async () => {
  try {
    const [petData, history] = await Promise.all([
      petApi.getInfo(),
      chatApi.getHistory()
    ])
    petStore.$patch({ petInfo: petData })

    if (history && history.length > 0) {
      messages.value = history.map(msg => ({
        role: msg.role === 'user' ? 'user' : 'assistant',
        content: msg.content,
        time: msg.time || formatTime(),
        reaction: msg.role === 'assistant' ? getRandomReaction() : null
      }))
    } else {
      // Initial greeting
      currentReaction.value = `${petEmoji.value} 上线啦~ 有什么想聊的吗？`
    }
    scrollToBottom()
  } catch (err) {
    console.error('Failed to load:', err)
  }
})
</script>

<style scoped>
/* CSS Variables */
:root {
  --primary: #FFB3C6;
  --primary-dark: #FF8FAB;
  --primary-light: #FFD5E5;
  --secondary: #A8D8EA;
  --secondary-dark: #7EC8D8;
  --purple: #D5AAFF;
  --purple-light: #EDE4FF;
  --green: #B8F1CC;
  --bg: #F5EEF8;
  --bg-page: #EEE8F5;
  --border: #E8E0F0;
  --text: #4A3F55;
  --text-secondary: #7A6B8A;
  --text-muted: #A898B8;
  --radius: 16px;
  --radius-sm: 10px;
  --radius-full: 999px;
  --shadow: 0 2px 12px rgba(80, 60, 100, 0.06);
  --shadow-lg: 0 4px 20px rgba(80, 60, 100, 0.10);
}

.chat-page {
  display: flex;
  height: 100%;
  background: var(--bg-page);
  font-family: "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  color: var(--text);
  font-size: 14px;
  line-height: 1.6;
}

/* Left Sidebar */
.left-sidebar {
  width: 260px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-logo {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid var(--border);
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--primary), var(--purple));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.chat-list::-webkit-scrollbar {
  display: none;
}

.chat-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 150ms;
}

.chat-list-item:hover {
  background: var(--primary-light);
}

.chat-list-item.active {
  background: var(--primary-light);
}

.chat-list-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-light), var(--purple-light));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.chat-list-info {
  flex: 1;
  min-width: 0;
}

.chat-list-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
}

.chat-list-preview {
  font-size: 11px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-new-btn {
  padding: 8px 12px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--primary-dark);
  font-weight: 600;
  width: 100%;
  border-top: 1px solid var(--border);
  font-family: inherit;
}

.chat-new-btn:hover {
  background: var(--primary-light);
}

/* Main Chat Area */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Chat Header */
.chat-header {
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.chat-pet-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-light), var(--purple-light));
  border: 2px solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 2px 8px rgba(255, 179, 198, 0.3);
}

.chat-info {
  flex: 1;
}

.chat-pet-name {
  font-size: 15px;
  font-weight: 700;
}

.chat-pet-status {
  font-size: 11px;
  color: #4ECDC4;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ECDC4;
  animation: blink 2s ease-in-out infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.header-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  width: 34px;
  height: 34px;
  border-radius: var(--radius-sm);
  border: none;
  background: var(--bg-page);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 150ms;
}

.icon-btn:hover {
  background: var(--primary-light);
  color: var(--primary-dark);
}

.icon-btn svg {
  width: 16px;
  height: 16px;
}

/* Messages */
.messages-area {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--bg-page);
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

.messages-area::-webkit-scrollbar {
  display: none; /* Chrome/Safari/Opera */
}

.msg-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.msg-group.user {
  align-items: flex-end;
}

.msg-group.pet {
  align-items: flex-start;
}

.msg-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  animation: msgIn 300ms ease;
}

@keyframes msgIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.msg-group.user .msg-row {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.msg-avatar.pet {
  background: linear-gradient(135deg, var(--primary-light), var(--purple-light));
}

.msg-avatar.user {
  background: linear-gradient(135deg, var(--secondary), var(--primary-light));
}

.msg-bubble {
  max-width: 500px;
  padding: 10px 16px;
  font-size: 14px;
  line-height: 1.7;
  box-shadow: var(--shadow);
}

.msg-bubble.pet {
  background: var(--purple-light);
  border-radius: 4px 18px 18px 18px;
  color: var(--text);
}

.msg-bubble.user {
  background: linear-gradient(135deg, var(--primary-light), #FFD5E8);
  border-radius: 18px 4px 18px 18px;
  color: var(--text);
}

.msg-time {
  font-size: 10px;
  color: var(--text-muted);
  margin-top: 3px;
}

/* Pet Reaction */
.pet-reaction {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--radius-full);
  font-size: 11px;
  color: var(--text-muted);
  width: fit-content;
}

.pet-reaction-emoji {
  font-size: 16px;
}

.pet-reaction-text {
  font-style: italic;
}

/* Typing Indicator */
.typing-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.typing-dots {
  display: flex;
  gap: 3px;
  align-items: center;
  background: var(--purple-light);
  border-radius: 4px 18px 18px 18px;
  padding: 12px 16px;
}

.typing-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--purple);
  animation: typingBounce 1.2s ease-in-out infinite;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

/* Input Area */
.input-area {
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-top: 1px solid var(--border);
  display: flex;
  gap: 12px;
  align-items: flex-end;
  flex-shrink: 0;
}

.input-suggestions {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none;
}

.input-suggestions::-webkit-scrollbar {
  display: none;
}

.suggestion-chip {
  flex-shrink: 0;
  padding: 5px 12px;
  border-radius: var(--radius-full);
  background: var(--bg-page);
  border: 1px solid var(--border);
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;
}

.suggestion-chip:hover {
  background: var(--primary-light);
  border-color: var(--primary);
  color: var(--primary-dark);
}

.input-box-wrap {
  flex: 1;
  position: relative;
}

.input-box {
  width: 100%;
  background: var(--bg-page);
  border: 1.5px solid rgba(213, 170, 255, 0.3);
  border-radius: 20px;
  padding: 10px 16px;
  font-size: 14px;
  color: var(--text);
  outline: none;
  resize: none;
  font-family: inherit;
  line-height: 1.5;
  max-height: 100px;
  transition: border-color 200ms;
}

.input-box:focus {
  border-color: var(--purple);
  box-shadow: 0 0 0 3px rgba(213, 170, 255, 0.1);
}

.input-box::placeholder {
  color: var(--text-muted);
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, var(--primary), var(--purple));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 150ms;
  box-shadow: 0 4px 12px rgba(255, 179, 198, 0.4);
  flex-shrink: 0;
}

.send-btn:hover {
  filter: brightness(1.05);
  box-shadow: 0 6px 16px rgba(255, 179, 198, 0.5);
}

.send-btn:active {
  transform: scale(0.9);
}

.send-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.send-btn svg {
  width: 18px;
  height: 18px;
}
</style>
