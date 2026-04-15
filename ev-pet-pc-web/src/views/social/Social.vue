<template>
  <div class="social-page">
    <el-row :gutter="24">
      <el-col :span="8">
        <div class="friends-card card">
          <h3>好友列表</h3>
          <div class="search-box">
            <el-input placeholder="搜索好友..." prefix-icon="Search" />
          </div>
          <div class="friend-list">
            <div v-for="friend in friends" :key="friend.id" class="friend-item">
              <div class="friend-avatar">{{ friend.petEmoji }}</div>
              <div class="friend-info">
                <span class="friend-name">{{ friend.name }}</span>
                <span class="friend-pet">{{ friend.petName }}</span>
              </div>
              <el-button size="small" @click="visitPet(friend)">访问</el-button>
            </div>
          </div>
          <el-button class="add-friend-btn" type="primary" plain>+ 添加好友</el-button>
        </div>
      </el-col>
      
      <el-col :span="16">
        <div class="social-feed-card card">
          <h3>动态广场</h3>
          <div class="feed-tabs">
            <el-radio-group v-model="feedFilter">
              <el-radio-button label="recent">最新</el-radio-button>
              <el-radio-button label="hot">热门</el-radio-button>
              <el-radio-button label="follow">关注</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="feed-list">
            <div v-for="post in filteredPosts" :key="post.id" class="feed-item">
              <div class="feed-header">
                <div class="feed-user">
                  <span class="user-avatar">{{ post.petEmoji }}</span>
                  <div class="user-info">
                    <span class="user-name">{{ post.userName }}</span>
                    <span class="post-time">{{ post.time }}</span>
                  </div>
                </div>
              </div>
              <div class="feed-content">
                <p>{{ post.content }}</p>
                <div v-if="post.image" class="feed-image">
                  <span class="pet-image">{{ post.petEmoji }}</span>
                </div>
              </div>
              <div class="feed-actions">
                <span class="action-btn" @click="like(post)">
                  <span>{{ post.liked ? '❤️' : '🤍' }}</span>
                  <span>{{ post.likes }}</span>
                </span>
                <span class="action-btn">
                  <span>💬</span>
                  <span>{{ post.comments }}</span>
                </span>
                <span class="action-btn">
                  <span>🔗</span>
                  <span>分享</span>
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="24" class="mt-6">
      <el-col :span="24">
        <div class="leaderboard-card card">
          <h3>排行榜</h3>
          <el-tabs v-model="rankType">
            <el-tab-pane label="等级" name="level" />
            <el-tab-pane label="金币" name="gold" />
            <el-tab-pane label="宠物" name="pet" />
          </el-tabs>
          <div class="rank-list">
            <div v-for="(item, index) in rankings" :key="item.id" class="rank-item">
              <div class="rank-num" :class="{ top3: index < 3 }">{{ index + 1 }}</div>
              <div class="rank-avatar">{{ item.petEmoji }}</div>
              <div class="rank-info">
                <span class="rank-name">{{ item.name }}</span>
                <span class="rank-pet">Lv.{{ item.level }} · {{ item.petStage }}阶段</span>
              </div>
              <div class="rank-value">{{ item.value }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const feedFilter = ref('recent')
const rankType = ref('level')

const friends = ref([
  { id: 1, name: '小明', petName: '小白', petEmoji: '🐥' },
  { id: 2, name: '小红', petName: '小粉', petEmoji: '🐣' },
  { id: 3, name: '小华', petName: '大星', petEmoji: '🐦' }
])

const posts = ref([
  { id: 1, userName: '小明', petEmoji: '🐥', content: '今天我的宠物进化啦！开心~', likes: 52, comments: 8, time: '10分钟前', liked: false },
  { id: 2, userName: '小红', petEmoji: '🐣', content: '新买的衣服好可爱呀', likes: 38, comments: 5, time: '30分钟前', liked: true },
  { id: 3, userName: '小华', petEmoji: '🐦', content: '打工赚了好多金币', likes: 24, comments: 3, time: '1小时前', liked: false }
])

const filteredPosts = computed(() => posts.value)

const rankings = ref([
  { id: 1, name: '小明', petEmoji: '🐥', level: 25, petStage: '3', value: '25级' },
  { id: 2, name: '小华', petEmoji: '🐦', level: 22, petStage: '3', value: '22级' },
  { id: 3, name: '小红', petEmoji: '🐣', level: 18, petStage: '2', value: '18级' },
  { id: 4, name: '阿月', petEmoji: '🐥', level: 15, petStage: '2', value: '15级' },
  { id: 5, name: '阿华', petEmoji: '🐣', level: 12, petStage: '1', value: '12级' }
])

const visitPet = (friend) => {
  console.log('Visit', friend)
}

const like = (post) => {
  post.liked = !post.liked
  post.likes += post.liked ? 1 : -1
}
</script>

<style scoped>
.social-page { padding: 0; }
.mt-6 { margin-top: 24px; }

.friends-card h3, .social-feed-card h3, .leaderboard-card h3 { margin-bottom: 16px; }

.search-box { margin: 16px 0; }

.friend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.friend-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 12px;
}

.friend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.friend-name { font-size: 14px; font-weight: 600; }
.friend-pet { font-size: 12px; color: var(--text-secondary); }

.add-friend-btn { width: 100%; margin-top: 16px; }

.feed-tabs { margin-bottom: 16px; }

.feed-item {
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.feed-user { display: flex; align-items: center; gap: 12px; }

.user-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.user-name { font-weight: 600; display: block; }
.post-time { font-size: 12px; color: var(--text-secondary); }

.feed-content p { margin-bottom: 12px; }

.feed-image {
  background: linear-gradient(135deg, #FFD5E5 0%, #D5AAFF 100%);
  border-radius: var(--radius-md);
  padding: 40px;
  text-align: center;
}

.pet-image { font-size: 80px; }

.feed-actions {
  display: flex;
  gap: 24px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #F0F0F0;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: var(--text-secondary);
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
}

.rank-num {
  width: 32px;
  font-weight: 700;
  color: var(--text-secondary);
}

.rank-num.top3 { color: #FFD700; font-size: 18px; }

.rank-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FFE5A0 0%, #FFD166 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin: 0 12px;
}

.rank-info { flex: 1; }
.rank-name { font-weight: 600; display: block; }
.rank-pet { font-size: 12px; color: var(--text-secondary); }
.rank-value { font-weight: 600; color: var(--accent-purple); }
</style>
