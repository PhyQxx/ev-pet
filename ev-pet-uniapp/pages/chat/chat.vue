<template>
  <view class="chat-container">
    <!-- 聊天区域 -->
    <scroll-view class="chat-list" scroll-y :scroll-top="scrollTop" scroll-with-animation>
      <view class="chat-item" v-for="(msg, index) in messages" :key="index" 
            :class="{ 'user': msg.role === 'user', 'pet': msg.role === 'assistant' }">
        <view class="avatar pet-avatar" v-if="msg.role === 'assistant'">
          <text>🐣</text>
        </view>
        <view class="message-bubble">
          <text class="message-text">{{ msg.content }}</text>
        </view>
        <view class="avatar user-avatar" v-if="msg.role === 'user'">
          <text>👤</text>
        </view>
      </view>
      
      <!-- 加载中 -->
      <view class="chat-item pet" v-if="loading">
        <view class="avatar pet-avatar">
          <text>🐣</text>
        </view>
        <view class="message-bubble loading">
          <text class="loading-text">...</text>
        </view>
      </view>
    </scroll-view>
    
    <!-- 宠物状态提示 -->
    <view class="pet-status-bar" v-if="petInfo">
      <text class="status-hint">{{ statusHint }}</text>
    </view>
    
    <!-- 输入区域 -->
    <view class="input-area">
      <view class="quick-replies">
        <view class="quick-btn" v-for="(reply, i) in quickReplies" :key="i" @click="sendQuickReply(reply)">
          {{ reply }}
        </view>
      </view>
      <view class="input-row">
        <input class="chat-input" v-model="inputText" placeholder="和宠物说点什么..." 
               confirm-type="send" @confirm="sendMessage" />
        <button class="send-btn" @click="sendMessage" :disabled="!inputText.trim()">
          <text>发送</text>
        </button>
      </view>
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
      quickReplies: ['你好~', '我饿了', '陪我玩', '今天开心', '唱首歌']
    }
  },
  computed: {
    statusHint() {
      if (!this.petInfo) return ''
      if (this.petInfo.health < 30) return '💤 宠物有点累了'
      if (this.petInfo.fullness < 30) return '🍖 宠物肚子饿了'
      if (this.petInfo.mood < 30) return '😢 宠物心情不好'
      return '✨ 宠物状态很好'
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
        })
        .catch(err => {
          console.error('Failed to load history:', err)
        })
    },
    loadPetInfo() {
      petApi.getInfo()
        .then(data => {
          this.petInfo = data
        })
        .catch(err => {
          console.error('Failed to load pet info:', err)
        })
    },
    sendMessage() {
      const content = this.inputText.trim()
      if (!content || this.loading) return
      
      this.inputText = ''
      this.loading = true
      
      // 添加用户消息
      this.messages.push({ role: 'user', content })
      this.scrollToBottom()
      
      chatApi.send(content)
        .then(response => {
          this.loading = false
          this.messages.push({ role: 'assistant', content: response })
          this.scrollToBottom()
          // 刷新宠物状态
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
    }
  }
}
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #EDE4FF;
}

.chat-list {
  flex: 1;
  padding: 30rpx;
}

.chat-item {
  display: flex;
  margin-bottom: 30rpx;
  align-items: flex-start;
}

.chat-item.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  flex-shrink: 0;
}

.pet-avatar {
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
}

.user-avatar {
  background: linear-gradient(135deg, #A8D8EA 0%, #7EC8D8 100%);
}

.message-bubble {
  max-width: 500rpx;
  padding: 24rpx 30rpx;
  border-radius: 30rpx;
  margin: 0 20rpx;
  font-size: 30rpx;
  line-height: 1.5;
  word-break: break-all;
}

.chat-item.pet .message-bubble {
  background: #FFFFFF;
  color: #4A3F55;
  border-bottom-left-radius: 8rpx;
}

.chat-item.user .message-bubble {
  background: linear-gradient(135deg, #D5AAFF 0%, #C48FFF 100%);
  color: #FFFFFF;
  border-bottom-right-radius: 8rpx;
}

.loading {
  background: rgba(255, 255, 255, 0.8);
}

.loading-text {
  color: #A898B8;
}

.pet-status-bar {
  padding: 16rpx 30rpx;
  background: rgba(255, 255, 255, 0.5);
}

.status-hint {
  font-size: 24rpx;
  color: #7A6B8A;
}

.input-area {
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.quick-replies {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.quick-btn {
  background: #EDE4FF;
  color: #7A6B8A;
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
}

.input-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.chat-input {
  flex: 1;
  height: 80rpx;
  background: #F8F8F8;
  border-radius: 40rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.send-btn {
  width: 120rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #D5AAFF 0%, #C48FFF 100%);
  border-radius: 40rpx;
  color: #FFFFFF;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.send-btn[disabled] {
  background: #E0E0E0;
  color: #A898B8;
}
</style>
