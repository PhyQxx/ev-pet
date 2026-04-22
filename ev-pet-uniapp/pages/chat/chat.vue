<template>
  <view class="chat-container">
    <!-- Header -->
    <header class="header">
      <view class="pet-mini-avatar">{{ petEmoji }}</view>
      <view class="header-info">
        <text class="pet-header-name">{{ petInfo?.name || '小福' }}</text>
        <text class="pet-header-status">
          <view class="status-dot"></view>
          在线 · AI 宠物
        </text>
      </view>
      <view class="header-actions">
        <button class="icon-btn" @click="showToast('语音通话')">
          <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12 19.79 19.79 0 0 1 1.61 3.18 2 2 0 0 1 3.6 1h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.91 8.6a16 16 0 0 0 6.06 6.06l1.06-.96a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
        </button>
        <button class="icon-btn" @click="showToast('宠物详情')">
          <svg fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        </button>
      </view>
    </header>

    <!-- Chat Messages -->
    <scroll-view class="chat-area" scroll-y :scroll-top="scrollTop" scroll-with-animation>
      <view class="msg-date-divider">今天</view>

      <view class="msg-pet" v-for="(msg, index) in messages" :key="index">
        <view class="msg-pet-avatar">{{ petEmoji }}</view>
        <view class="msg-pet-content">
          <view class="msg-bubble-pet">{{ msg.content }}</view>
          <view class="msg-reaction" v-if="msg.reactions && msg.reactions.length">
            <view class="reaction-chip" v-for="r in msg.reactions" :key="r.emoji" @click="addReaction(r)">
              <text class="emoji">{{ r.emoji }}</text>
              <text class="count">{{ r.count }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Typing Indicator -->
      <view class="typing-indicator" v-if="loading">
        <view class="msg-pet-avatar" style="width:32px;height:32px;font-size:18px;">{{ petEmoji }}</view>
        <view>
          <view class="typing-dots">
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
            <view class="typing-dot"></view>
          </view>
          <view class="typing-label">小福正在输入...</view>
        </view>
      </view>
    </scroll-view>

    <!-- Quick Replies -->
    <view class="quick-replies">
      <view class="quick-reply" v-for="reply in quickReplies" :key="reply" @click="sendQuickReply(reply)">{{ reply }}</view>
    </view>

    <!-- Input Area -->
    <view class="input-area">
      <textarea
        class="input-box"
        v-model="inputText"
        placeholder="和小福说点什么..."
        :rows="1"
        @input="autoGrow"
        @confirm="sendMessage"
        confirm-type="send"
      ></textarea>
      <button class="send-btn" @click="sendMessage">
        <svg fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
      </button>
    </view>
  </view>
</template>

<script>
import { chat as chatApi, pet as petApi } from '../../utils/api.js'

export default {
  data() {
    return {
      messages: [],
      inputText: '',
      loading: false,
      scrollTop: 0,
      petInfo: null,
      quickReplies: ['今天心情怎么样？', '小福饿了吗？', '想你了~', '给你取个新名字', '一起玩游戏吗？']
    }
  },
  computed: {
    petEmoji() {
      const stage = this.petInfo?.stage || 1
      if (stage === 1) return '🐣'
      if (stage === 2) return '🐥'
      if (stage === 3) return '🦊'
      return '🦊'
    }
  },
  onLoad() {
    this.loadHistory()
    this.loadPetInfo()
  },
  methods: {
    loadHistory() {
      chatApi.getHistory()
        .then(history => {
          this.messages = history || []
          this.scrollToBottom()
        })
        .catch(err => {
          // Add welcome message as fallback
          this.messages = [
            { role: 'assistant', content: '主人早上好呀~ 🌸 昨晚睡得好香，梦到我们去海边玩了！', reactions: [{ emoji: '❤️', count: 12 }, { emoji: '😊', count: 8 }] },
            { role: 'user', content: '小福早上好！今天天气怎么样？' },
            { role: 'assistant', content: '今天天气超好的！☀️ 气温22度，微风，很适合出去散步哦~ 主人今天有什么计划吗？', reactions: [{ emoji: '✨', count: 5 }] },
            { role: 'user', content: '今天想带你去打工赚金币！' },
            { role: 'assistant', content: '好呀好呀！💼 我已经准备好了，随时可以出发！打工可以获得金币，然后用金币给我买新衣服好不好？🦊✨', reactions: [{ emoji: '💰', count: 3 }, { emoji: '😆', count: 6 }] }
          ]
          this.scrollToBottom()
        })
    },
    loadPetInfo() {
      petApi.getInfo()
        .then(data => {
          this.petInfo = data
        })
        .catch(err => {})
    },
    sendMessage() {
      const content = this.inputText.trim()
      if (!content || this.loading) return

      this.inputText = ''
      this.loading = true

      // Add user message
      this.messages.push({ role: 'user', content })
      this.scrollToBottom()

      chatApi.send(content)
        .then(response => {
          this.loading = false
          this.messages.push({ role: 'assistant', content: response, reactions: [] })
          this.scrollToBottom()
          this.loadPetInfo()
        })
        .catch(err => {
          this.loading = false
          uni.showToast({ title: '发送失败', icon: 'none' })
        })
    },
    sendQuickReply(reply) {
      this.inputText = reply
      this.sendMessage()
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollTop = this.messages.length * 1000
      })
    },
    autoGrow(e) {
      // Handled natively by textarea
    },
    showToast(msg) {
      uni.showToast({ title: msg, icon: 'none' })
    },
    addReaction(r) {
      r.count++
    }
  }
}
</script>

<style scoped>
page {
  --primary: #FFB3C6;
  --primary-dark: #FF8FAB;
  --primary-light: #FFD5E5;
  --secondary: #A8D8EA;
  --purple: #D5AAFF;
  --purple-light: #EDE4FF;
  --yellow: #FFE5A0;
  --bg: #F5EEF8;
  --bg-page: #EEE8F5;
  --text: #4A3F55;
  --text-secondary: #7A6B8A;
  --text-muted: #A898B8;
  --radius: 20px;
  --radius-sm: 12px;
  --radius-full: 999px;
}

.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #EEE8F5;
}

/* Header */
.header {
  padding: 10px 16px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(16px);
  display: flex;
  align-items: center;
  gap: 12px;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid rgba(255,255,255,0.6);
}
.pet-mini-avatar {
  width: 36px; height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border: 2px solid white;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px;
  box-shadow: 0 2px 8px rgba(255,179,198,0.3);
  flex-shrink: 0;
}
.header-info { flex: 1; }
.pet-header-name { font-size: 15px; font-weight: 700; color: #4A3F55; display: block; }
.pet-header-status {
  font-size: 11px;
  color: #A898B8;
  display: flex; align-items: center; gap: 4px;
}
.status-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: #4ECDC4;
  animation: blinkDot 2s ease-in-out infinite;
}
@keyframes blinkDot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
.header-actions { display: flex; gap: 8px; }
.icon-btn {
  width: 34px; height: 34px;
  border-radius: 50%;
  border: none;
  background: #EEE8F5;
  color: #7A6B8A;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 150ms;
  padding: 0;
}
.icon-btn:active { transform: scale(0.92); background: #FFD5E5; }
.icon-btn svg { width: 16px; height: 16px; }

/* Chat Messages */
.chat-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px 16px 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  scroll-behavior: smooth;
}
.chat-area::-webkit-scrollbar { width: 0; }

.msg-date-divider {
  text-align: center;
  font-size: 11px;
  color: #A898B8;
  padding: 4px 0;
  position: relative;
}

.msg-pet {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  animation: msgIn 300ms ease;
}
@keyframes msgIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
.msg-pet-avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border: 1.5px solid white;
  display: flex; align-items: center; justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.msg-pet-content { display: flex; flex-direction: column; gap: 4px; max-width: 75%; }
.msg-bubble-pet {
  background: #EDE4FF;
  border-radius: 4px 20px 20px 20px;
  padding: 10px 14px;
  font-size: 14px;
  color: #4A3F55;
  line-height: 1.7;
  box-shadow: 0 2px 8px rgba(80,60,100,0.06);
  position: relative;
}
.msg-bubble-pet::before {
  content: '';
  position: absolute;
  bottom: 0; left: -6px;
  width: 12px; height: 12px;
  background: #EDE4FF;
  border-radius: 50%;
}
.msg-reaction {
  display: flex;
  gap: 6px;
  margin-left: 4px;
}
.reaction-chip {
  display: flex; align-items: center; gap: 3px;
  padding: 3px 8px;
  border-radius: 999px;
  background: white;
  font-size: 12px;
  box-shadow: 0 1px 4px rgba(80,60,100,0.08);
  border: 1px solid rgba(255,179,198,0.3);
  cursor: pointer;
  transition: all 150ms;
}
.reaction-chip .emoji { font-size: 14px; }
.reaction-chip .count { font-size: 11px; color: #7A6B8A; }
.reaction-chip:active { transform: scale(0.93); }

.msg-user {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  justify-content: flex-end;
  animation: msgIn 300ms ease;
}
.msg-user-content { display: flex; flex-direction: column; gap: 4px; max-width: 75%; align-items: flex-end; }
.msg-bubble-user {
  background: linear-gradient(135deg, #FFD5E5, #FFD5E8);
  border-radius: 20px 4px 20px 20px;
  padding: 10px 14px;
  font-size: 14px;
  color: #4A3F55;
  line-height: 1.7;
  box-shadow: 0 2px 8px rgba(255,179,198,0.2);
}
.msg-time { font-size: 10px; color: #A898B8; }

/* Typing Indicator */
.typing-indicator {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  animation: msgIn 300ms ease;
}
.typing-dots {
  display: flex; gap: 3px; align-items: center;
  background: #EDE4FF;
  border-radius: 4px 20px 20px 20px;
  padding: 12px 16px;
}
.typing-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  background: #D5AAFF;
  animation: typingBounce 1.2s ease-in-out infinite;
}
.typing-dot:nth-child(2) { animation-delay: 0.2s; }
.typing-dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}
.typing-label { font-size: 11px; color: #A898B8; margin-top: 4px; }

/* Quick Replies */
.quick-replies {
  padding: 6px 16px 8px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: calc(6px + env(safe-area-inset-bottom));
}
.quick-replies::-webkit-scrollbar { display: none; }
.quick-reply {
  flex-shrink: 0;
  padding: 7px 14px;
  border-radius: 999px;
  background: rgba(255,255,255,0.9);
  border: 1.5px solid #FFD5E5;
  color: #FF8FAB;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms;
  white-space: nowrap;
  font-family: inherit;
}
.quick-reply:active { background: #FFD5E5; transform: scale(0.96); }

/* Input Area */
.input-area {
  padding: 8px 16px 12px;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(16px);
  border-top: 1px solid rgba(255,255,255,0.8);
  display: flex;
  gap: 10px;
  align-items: flex-end;
  padding-bottom: calc(8px + env(safe-area-inset-bottom));
}
.input-box {
  flex: 1;
  background: #EEE8F5;
  border: 1.5px solid rgba(213,170,255,0.3);
  border-radius: 20px;
  padding: 10px 16px;
  font-size: 14px;
  color: #4A3F55;
  outline: none;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  transition: border-color 200ms;
  line-height: 1.5;
}
.input-box:focus { border-color: #D5AAFF; box-shadow: 0 0 0 3px rgba(213,170,255,0.1); }
.input-box::placeholder { color: #A898B8; }
.send-btn {
  width: 44px; height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: white;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: all 150ms;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(255,179,198,0.4);
  padding: 0;
}
.send-btn:active { transform: scale(0.9); box-shadow: 0 2px 6px rgba(255,179,198,0.3); }
.send-btn svg { width: 18px; height: 18px; }
</style>
