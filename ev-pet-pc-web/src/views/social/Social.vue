<template>
  <div class="social-page">
    <!-- Topbar -->
    <div class="topbar">
      <div class="topbar-logo">
        <div class="topbar-logo-icon">🐾</div>
        <span class="topbar-logo-text">EV Pet · 社交中心</span>
      </div>
      <div class="topbar-right">
        <span>👤 {{ userStore.userInfo?.nickname || '裴浩宇' }}</span>
        <div class="topbar-avatar">{{ petStore.petInfo?.emoji || '🐱' }}</div>
      </div>
    </div>

    <!-- Layout -->
    <div class="layout">
      <!-- Left Sidebar: Friends List -->
      <aside class="left-bar">
        <div class="left-bar-title">👥 我的好友 <span style="font-size:12px;color:#7A6B8A;font-weight:400;">({{ friends.length }})</span></div>
        <div class="search-bar">
          <input v-model="friendSearch" type="text" class="search-input" placeholder="搜索好友...">
          <button class="search-btn">🔍</button>
        </div>

        <template v-for="group in friendGroups" :key="group.label">
          <div class="friend-section-label">{{ group.icon }} {{ group.label }} ({{ group.friends.length }})</div>
          <div
            v-for="friend in group.friends"
            :key="friend.id"
            class="friend-item"
            :class="{ active: selectedFriend?.id === friend.id }"
            @click="selectFriend(friend)"
          >
            <div class="friend-avatar" :style="{ background: friend.bgColor }">
              {{ friend.petEmoji }}
              <div v-if="friend.online" class="friend-online-dot"></div>
            </div>
            <div class="friend-info">
              <div class="friend-name">{{ friend.name }}</div>
              <div class="friend-pet">{{ friend.petType }} · LV.{{ friend.level }}</div>
            </div>
            <span class="pet-status" :class="getStatusClass(friend.status)">{{ getStatusEmoji(friend.status) }}</span>
          </div>
        </template>

        <button class="add-friend-btn" @click="showAddFriend">
          <span>+</span> 添加好友
        </button>
      </aside>

      <!-- Main Content -->
      <main class="main-content">
        <div class="page-title">👥 社交中心</div>
        <div class="page-sub">探访好友宠物，互赞互动，记录美好时刻</div>

        <!-- Page Tabs -->
        <div class="page-tabs">
          <div class="page-tab" :class="{ active: activeTab === 'friends' }" @click="activeTab = 'friends'">🐾 好友</div>
          <div class="page-tab" :class="{ active: activeTab === 'photos' }" @click="activeTab = 'photos'">📷 动态</div>
          <div class="page-tab" :class="{ active: activeTab === 'ranking' }" @click="activeTab = 'ranking'">🏆 排行</div>
        </div>

        <!-- ===== 好友页 ===== -->
        <div v-show="activeTab === 'friends'" class="page active">
          <div class="invite-banner">
            <div class="invite-emoji">🤝</div>
            <div class="invite-text">
              <div class="invite-title">邀请好友加入</div>
              <div class="invite-desc">双方各获得限定服装一件</div>
            </div>
            <button class="invite-btn" @click="showAddFriend">📱 邀请</button>
          </div>

          <!-- Friend Requests -->
          <div v-if="friendRequests.length > 0" class="friend-req">
            <div class="friend-req-header">
              📩 待处理申请 <span class="friend-req-badge">{{ friendRequests.length }}</span>
            </div>
            <div class="req-list">
              <div v-for="req in friendRequests" :key="req.id" class="req-item">
                <div class="req-avatar" :style="{ background: req.bgColor }">{{ req.petEmoji }}</div>
                <div class="req-info">
                  <div class="req-name">{{ req.name }}</div>
                  <div class="req-desc">{{ req.message }}</div>
                </div>
                <div class="req-actions">
                  <button class="req-accept" @click="acceptRequest(req)">接受</button>
                  <button class="req-decline" @click="declineRequest(req)">拒绝</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Pet Cards Grid -->
          <div class="pet-cards-grid">
            <div v-for="friend in filteredFriends" :key="friend.id" class="pet-card">
              <div class="pet-card-header">
                <div class="pet-avatar" :style="{ background: friend.bgColor }">{{ friend.petEmoji }}</div>
                <div class="pet-meta">
                  <div class="pet-name">
                    {{ friend.petName }}
                    <span class="pet-level">LV.{{ friend.level }}</span>
                  </div>
                  <div class="pet-type">{{ friend.petType }}</div>
                </div>
                <span class="pet-status" :class="getStatusClass(friend.status)">
                  {{ getStatusText(friend.status) }}
                </span>
              </div>
              <div class="pet-display">
                <span class="pet-display-emoji">{{ friend.petEmoji }}</span>
                <div class="pet-display-name">{{ friend.petName }}</div>
                <div class="pet-display-level">{{ friend.petType }} · LV.{{ friend.level }} {{ friend.stage }}</div>
              </div>
              <div class="pet-card-actions">
                <button class="pet-action-btn" @click="greetPet(friend)">👋 打招呼</button>
                <button class="pet-action-btn" :class="{ liked: friend.liked }" @click="likePet(friend)">
                  ❤️ {{ friend.likes }}
                </button>
                <button class="pet-action-btn primary" @click="photoWithPet(friend)">📷 合照</button>
              </div>
            </div>
          </div>
        </div>

        <!-- ===== 动态页 ===== -->
        <div v-show="activeTab === 'photos'" class="page">
          <div v-for="post in posts" :key="post.id" class="photo-card">
            <div class="photo-header">
              <div class="photo-user-avatar" :style="{ background: post.bgColor }">{{ post.petEmoji }}</div>
              <div>
                <div class="photo-user-name">{{ post.userName }} 🐾</div>
                <div class="photo-user-time">{{ post.time }} · {{ post.location }}</div>
              </div>
            </div>
            <div class="photo-image" :style="{ background: post.imageBg }">
              {{ post.imageContent }}
            </div>
            <div class="photo-caption">{{ post.caption }}</div>
            <div class="photo-tags">
              <span v-for="tag in post.tags" :key="tag.label" class="photo-tag">
                {{ tag.emoji }} {{ tag.label }}
              </span>
            </div>
            <div class="photo-actions">
              <button class="photo-action-btn" :class="{ liked: post.liked }" @click="likePost(post)">
                ❤️ 赞
              </button>
              <button class="photo-action-btn" @click="commentPost(post)">💬 评论</button>
              <button class="photo-action-btn" @click="sharePost(post)">🔗 分享</button>
            </div>
          </div>
        </div>

        <!-- ===== 排行页 ===== -->
        <div v-show="activeTab === 'ranking'" class="page">
          <div class="rank-tabs-row">
            <div class="rank-tab" :class="{ active: rankType === 'gold' }" @click="rankType = 'gold'">
              💰 金币
            </div>
            <div class="rank-tab" :class="{ active: rankType === 'exp' }" @click="rankType = 'exp'">
              ⭐ 经验
            </div>
            <div class="rank-tab" :class="{ active: rankType === 'heat' }" @click="rankType = 'heat'">
              ❤️ 热度
            </div>
          </div>

          <div class="pet-card" style="padding:0;overflow:hidden;">
            <!-- Top 3 Podium -->
            <div class="podium">
              <div class="podium-item silver">
                <div class="podium-medal">🥈</div>
                <div class="podium-avatar" style="background:#D5AAFF;">🦊</div>
                <div class="podium-name">小火</div>
                <div class="podium-val">{{ (rankType === 'gold' ? '43,150' : rankType === 'exp' ? '12,800' : '8,920') }} {{ rankType === 'gold' ? '🪙' : rankType === 'exp' ? '⭐' : '❤️' }}</div>
              </div>
              <div class="podium-item gold">
                <div class="podium-medal">🥇</div>
                <div class="podium-avatar" style="background:#FFE5A0;">🐰</div>
                <div class="podium-name">小绒</div>
                <div class="podium-val">{{ (rankType === 'gold' ? '58,200' : rankType === 'exp' ? '18,500' : '15,640') }} {{ rankType === 'gold' ? '🪙' : rankType === 'exp' ? '⭐' : '❤️' }}</div>
              </div>
              <div class="podium-item bronze">
                <div class="podium-medal">🥉</div>
                <div class="podium-avatar" style="background:#A8D8EA;">🐱</div>
                <div class="podium-name">小甜饼</div>
                <div class="podium-val">{{ (rankType === 'gold' ? '28,640' : rankType === 'exp' ? '9,200' : '6,580') }} {{ rankType === 'gold' ? '🪙' : rankType === 'exp' ? '⭐' : '❤️' }}</div>
              </div>
            </div>

            <div class="rank-table-wrapper">
              <table class="rank-table">
                <thead>
                  <tr>
                    <th>排名</th>
                    <th>用户</th>
                    <th>{{ rankType === 'gold' ? '金币' : rankType === 'exp' ? '经验' : '热度' }}</th>
                    <th>等级</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in rankList" :key="item.id">
                    <td style="font-size:13px;font-weight:700;color:#A898B8;">{{ index + 4 }}</td>
                    <td>
                      <div class="rank-user">
                        <div class="rank-avatar" :style="{ background: item.bgColor }">{{ item.petEmoji }}</div>
                        <div>
                          <div class="rank-name">{{ item.name }}</div>
                          <div class="rank-pet">{{ item.petType }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="rank-gold-val">{{ item.value }} {{ rankType === 'gold' ? '🪙' : rankType === 'exp' ? '⭐' : '❤️' }}</td>
                    <td style="text-align:right;font-size:12px;color:#7A6B8A;">LV.{{ item.level }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>

      <!-- Right Sidebar -->
      <aside class="right-bar">
        <!-- Online Friends -->
        <div class="right-section">
          <div class="right-section-title">🟢 在线好友</div>
          <div class="online-list">
            <div v-for="friend in onlineFriends" :key="friend.id" class="online-friend">
              <div class="online-avatar" :style="{ background: friend.bgColor }">
                {{ friend.petEmoji }}
                <div class="online-dot"></div>
              </div>
              <div class="online-info">
                <div class="online-name">{{ friend.name }}</div>
                <div class="online-status">{{ getStatusText(friend.status) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Photos -->
        <div class="right-section">
          <div class="right-section-title">📷 最新动态</div>
          <div class="mini-photo">
            <div v-for="(photo, idx) in recentPhotos" :key="idx" class="mini-photo-item" :style="{ background: photo.bg }">
              {{ photo.emoji }}
            </div>
          </div>
        </div>

        <!-- Wealth Ranking -->
        <div class="right-section">
          <div class="right-section-title">💰 财富排行</div>
          <div class="rank-list">
            <div v-for="(item, idx) in wealthRank" :key="item.id" class="rank-item">
              <div class="rank-num" :class="{ gold: idx === 0, silver: idx === 1, bronze: idx === 2 }">{{ idx + 1 }}</div>
              <div class="rank-avatar" :style="{ background: item.bgColor }">{{ item.petEmoji }}</div>
              <div class="rank-info">
                <div class="rank-name">{{ item.name }}</div>
                <div class="rank-pet">{{ item.petType }}</div>
              </div>
              <div class="rank-val">{{ item.gold.toLocaleString() }}</div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- Add Friend Modal -->
    <div class="modal-overlay" :class="{ show: showAddFriendModal }" @click.self="showAddFriendModal = false">
      <div class="modal">
        <div class="modal-handle"></div>
        <div class="modal-emoji">🔍</div>
        <div class="modal-title">添加好友</div>
        <div class="modal-sub">输入好友ID或昵称搜索</div>
        <input v-model="searchFriendInput" type="text" class="input-field" placeholder="搜索好友ID或昵称..." style="margin-bottom:16px;">
        <button class="modal-start-btn" @click="searchFriend">搜索</button>
        <div class="modal-cancel" @click="showAddFriendModal = false">取消</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore, usePetStore } from '../../store'
import { social as socialApi } from '../../api/index.js'

const userStore = useUserStore()
const petStore = usePetStore()

const activeTab = ref('friends')
const rankType = ref('gold')
const friendSearch = ref('')
const showAddFriendModal = ref(false)
const searchFriendInput = ref('')
const selectedFriend = ref(null)

const friends = ref([])
const friendRequests = ref([])
const posts = ref([])
const rankList = ref([])

const wealthRank = computed(() =>
  [...friends.value]
    .sort((a, b) => b.level - a.level)
    .slice(0, 5)
    .map(f => ({
      ...f,
      gold: f.level * 1000 + Math.floor(Math.random() * 5000)
    }))
)

const recentPhotos = ref([])

const onlineFriends = computed(() => friends.value.filter(f => f.online))
const offlineFriends = computed(() => friends.value.filter(f => !f.online))

const friendGroups = computed(() => [
  { label: '在线', icon: '🟢', friends: onlineFriends.value },
  { label: '离线', icon: '⚪', friends: offlineFriends.value }
])

const filteredFriends = computed(() => {
  if (!friendSearch.value) return friends.value
  return friends.value.filter(f =>
    f.name.includes(friendSearch.value) || f.petName.includes(friendSearch.value)
  )
})

const getStatusClass = (status) => {
  return {
    happy: 'status-happy',
    working: 'status-working',
    sleeping: 'status-sleeping'
  }[status] || 'status-sleeping'
}

const getStatusEmoji = (status) => {
  return { happy: '😊', working: '🔥', sleeping: '💤' }[status] || '💤'
}

const getStatusText = (status) => {
  return { happy: '😊 开心', working: '🔥 打工中', sleeping: '💤 睡眠中' }[status] || '💤'
}

const selectFriend = (friend) => {
  selectedFriend.value = friend
}

const greetPet = (friend) => {
  ElMessage.success(`向 ${friend.petName}打了个招呼！`)
}

const likePet = (friend) => {
  friend.liked = !friend.liked
  friend.likes += friend.liked ? 1 : -1
  ElMessage.success(friend.liked ? `给 ${friend.petName} 点了个赞！` : '取消了点赞')
}

const photoWithPet = (friend) => {
  ElMessage.success(`和 ${friend.petName} 合照了一张！📸`)
}

const loadSocialData = async () => {
  try {
    // 加载好友列表
    const friendRes = await socialApi.getFriends()
    if (friendRes.code === 200 && friendRes.data) {
      friends.value = (friendRes.data.friends || []).map(f => ({
        id: f.id,
        name: f.nickname || '未知',
        petName: f.petName || f.nickname || '未知',
        petEmoji: f.petEmoji || '🐾',
        petType: f.petType || '宠物',
        level: f.level || 1,
        status: f.status || 'sleeping',
        bgColor: f.bgColor || '#FFD5E5',
        online: f.online || false,
        likes: f.likes || 0,
        liked: f.liked || false,
        stage: f.stage || '幼年期'
      }))
    }
  } catch (e) {
    console.error('加载好友列表失败', e)
  }

  try {
    // 加载动态
    const postsRes = await socialApi.getPosts('recent')
    if (postsRes.code === 200 && postsRes.data) {
      posts.value = (postsRes.data.posts || []).map(p => ({
        id: p.id,
        userName: p.userName || '匿名',
        petEmoji: p.petEmoji || '🐾',
        bgColor: p.bgColor || '#FFD5E5',
        time: p.createTime ? formatTimeAgo(p.createTime) : '刚刚',
        location: p.location || '',
        imageContent: p.imageContent || '',
        imageBg: p.imageBg || 'linear-gradient(135deg, #FFF0F5, #F5F0FF)',
        caption: p.content || '',
        tags: p.tags || [],
        liked: p.liked || false
      }))
    }
  } catch (e) {
    console.error('加载动态失败', e)
  }

  try {
    // 加载排行榜
    const rankRes = await socialApi.getRankings(rankType.value)
    if (rankRes.code === 200 && rankRes.data) {
      rankList.value = (rankRes.data.rankings || []).map((r, idx) => ({
        id: r.id || idx,
        name: r.nickname || '未知',
        petEmoji: r.petEmoji || '🐾',
        petType: r.petType || '宠物',
        level: r.level || 1,
        value: r.value || 0,
        bgColor: ['#FFD5E5', '#D5AAFF', '#FFE5A0', '#A8D8EA', '#B8F1CC'][idx % 5]
      }))
    }
  } catch (e) {
    console.error('加载排行榜失败', e)
  }
}

const formatTimeAgo = (dateStr) => {
  const now = Date.now()
  const then = new Date(dateStr).getTime()
  const diff = Math.floor((now - then) / 1000)
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  return `${Math.floor(diff / 86400)}天前`
}

const likePost = async (post) => {
  post.liked = !post.liked
  ElMessage.success(post.liked ? '点赞成功！' : '取消点赞')
  try {
    await socialApi.likePost(post.id)
  } catch (e) {
    console.error('点赞失败', e)
  }
}

const commentPost = (post) => {
  ElMessage.info('评论功能开发中...')
}

const sharePost = (post) => {
  ElMessage.success('分享链接已复制！')
}

const showAddFriend = () => {
  showAddFriendModal.value = true
}

const searchFriend = async () => {
  if (!searchFriendInput.value) {
    ElMessage.warning('请输入好友ID或昵称')
    return
  }
  ElMessage.info(`搜索 "${searchFriendInput.value}"...`)
}

const acceptRequest = async (req) => {
  friendRequests.value = friendRequests.value.filter(r => r.id !== req.id)
  ElMessage.success(`已接受 ${req.name} 的好友申请！`)
  try {
    await socialApi.acceptFriend(req.id)
  } catch (e) {
    console.error('接受好友请求失败', e)
  }
}

const declineRequest = async (req) => {
  friendRequests.value = friendRequests.value.filter(r => r.id !== req.id)
  ElMessage.info(`已拒绝 ${req.name} 的好友申请`)
  try {
    await socialApi.rejectFriend(req.id)
  } catch (e) {
    console.error('拒绝好友请求失败', e)
  }
}

onMounted(() => {
  loadSocialData()
})
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.social-page {
  min-height: 100vh;
  background: $ev-bg-page;
}

.topbar {
  background: linear-gradient(135deg, $ev-text, #6B5B8A);
  padding: 14px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
}

.topbar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.topbar-logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
}

.topbar-logo-text {
  font-size: 14px;
  font-weight: 700;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #fff;
  font-size: 13px;
}

.topbar-avatar {
  width: 30px;
  height: 30px;
  background: $ev-purple;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}

.layout {
  display: grid;
  grid-template-columns: 280px 1fr 320px;
  min-height: calc(100vh - 60px);
}

.left-bar {
  background: #fff;
  border-right: 1px solid $ev-purple-light;
  padding: 20px 14px;
}

.left-bar-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-bar {
  display: flex;
  gap: 6px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid $ev-purple-light;
  background: #FAFAFA;
  font-size: 12px;
  font-family: inherit;
  color: $ev-text;
}

.search-input:focus {
  outline: none;
  border-color: $ev-purple;
}

.search-btn {
  padding: 8px 12px;
  border-radius: 10px;
  background: $ev-purple;
  color: #fff;
  border: none;
  font-size: 12px;
  cursor: pointer;
}

.friend-section-label {
  font-size: 11px;
  font-weight: 700;
  color: #A898B8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  padding: 0 4px;
}

.friend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2px;
}

.friend-item:hover {
  background: #F9F5FF;
}

.friend-item.active {
  background: #F9F5FF;
  border: 1px solid $ev-purple-light;
}

.friend-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  position: relative;
}

.friend-online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #B8F1CC;
  border-radius: 50%;
  border: 2px solid #fff;
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.friend-pet {
  font-size: 11px;
  color: #7A6B8A;
}

.pet-status {
  font-size: 10px;
  padding: 1px 7px;
  border-radius: 8px;
}

.status-happy {
  background: #F0FFF5;
  color: #3BAF5D;
}

.status-working {
  background: #FFFBEF;
  color: #8B6914;
}

.status-sleeping {
  background: #F0F4F8;
  color: #7A9AB0;
}

.add-friend-btn {
  width: 100%;
  margin-top: 16px;
  padding: 10px;
  background: #fff;
  border: 1px dashed $ev-border;
  border-radius: $ev-radius-sm;
  color: #7A6B8A;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.add-friend-btn:hover {
  border-color: $ev-purple;
  color: $ev-purple;
}

.main-content {
  padding: 20px 24px;
  overflow-y: auto;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}

.page-sub {
  font-size: 13px;
  color: #7A6B8A;
  margin-bottom: 20px;
}

.page-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 20px;
}

.page-tab {
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
}

.page-tab:hover {
  border-color: $ev-purple;
  color: $ev-purple;
}

.page-tab.active {
  background: $ev-purple;
  color: #fff;
  border-color: $ev-purple;
}

.invite-banner {
  background: linear-gradient(135deg, #FFE5A0, #FFD5E5);
  border-radius: 18px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.invite-emoji {
  font-size: 40px;
  flex-shrink: 0;
}

.invite-text {
  flex: 1;
}

.invite-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 2px;
  color: $ev-text;
}

.invite-desc {
  font-size: 12px;
  color: #7A6B8A;
}

.invite-btn {
  background: $ev-text;
  color: #fff;
  padding: 10px 20px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  white-space: nowrap;
}

.friend-req {
  background: #fff;
  border-radius: $ev-radius;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.friend-req-header {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.friend-req-badge {
  background: $ev-primary;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 700;
}

.req-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.req-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FAFAFA;
  border-radius: 14px;
}

.req-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.req-info {
  flex: 1;
}

.req-name {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 2px;
}

.req-desc {
  font-size: 12px;
  color: #7A6B8A;
}

.req-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.req-accept {
  padding: 7px 14px;
  border-radius: 10px;
  background: #B8F1CC;
  color: #2D8A4E;
  font-size: 12px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.req-decline {
  padding: 7px 14px;
  border-radius: 10px;
  background: #FFF0F0;
  color: #FF6B6B;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid #FFE5E5;
  cursor: pointer;
}

.pet-cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.pet-card {
  background: #fff;
  border-radius: 20px;
  padding: 22px;
  margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.pet-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.pet-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  flex-shrink: 0;
}

.pet-meta {
  flex: 1;
}

.pet-name {
  font-size: 15px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
}

.pet-level {
  font-size: 10px;
  background: #F9F5FF;
  color: $ev-purple;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

.pet-type {
  font-size: 11px;
  color: #7A6B8A;
}

.pet-display {
  text-align: center;
  padding: 12px 0;
}

.pet-display-emoji {
  font-size: 72px;
  display: block;
}

.pet-display-name {
  font-size: 16px;
  font-weight: 700;
  margin-top: 6px;
}

.pet-display-level {
  font-size: 12px;
  color: #7A6B8A;
  margin-top: 2px;
}

.pet-card-actions {
  display: flex;
  gap: 10px;
}

.pet-action-btn {
  flex: 1;
  padding: 10px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.pet-action-btn:hover {
  border-color: $ev-purple;
  color: $ev-purple;
}

.pet-action-btn.primary {
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  border: none;
}

.pet-action-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(213, 170, 255, 0.3);
}

.pet-action-btn.liked {
  background: #FFF0F0;
  color: #FF6B6B;
  border-color: #FFD5E5;
}

.photo-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  border: 1px solid $ev-bg;
}

.photo-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
}

.photo-user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.photo-user-name {
  font-size: 14px;
  font-weight: 700;
}

.photo-user-time {
  font-size: 11px;
  color: #A898B8;
}

.photo-image {
  width: 100%;
  aspect-ratio: 16/9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72px;
  position: relative;
}

.photo-caption {
  padding: 14px 20px;
  font-size: 13px;
  line-height: 1.6;
}

.photo-tags {
  padding: 0 20px 12px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.photo-tag {
  padding: 4px 10px;
  background: #F9F5FF;
  border-radius: 20px;
  font-size: 11px;
  color: $ev-purple;
  border: 1px solid $ev-purple-light;
  display: flex;
  align-items: center;
  gap: 3px;
}

.photo-actions {
  display: flex;
  padding: 12px 20px;
  border-top: 1px solid $ev-bg;
  gap: 20px;
}

.photo-action-btn {
  flex: 1;
  padding: 9px;
  text-align: center;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  background: #fff;
  color: #7A6B8A;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s;
}

.photo-action-btn:hover {
  background: #F9F5FF;
  color: $ev-purple;
}

.photo-action-btn.liked {
  color: #FF6B6B;
}

.rank-tabs-row {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.rank-tab {
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
}

.rank-tab.active {
  background: #FFD166;
  color: $ev-text;
  border-color: #FFD166;
}

.podium {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 12px;
  padding: 24px 20px;
  background: linear-gradient(180deg, #FFF9FB, #fff);
  border-bottom: 1px solid $ev-bg;
}

.podium-item {
  text-align: center;
}

.podium-item.silver .podium-medal,
.podium-item.bronze .podium-medal {
  font-size: 36px;
  margin-bottom: 4px;
}

.podium-item.gold .podium-medal {
  font-size: 42px;
  margin-bottom: 4px;
}

.podium-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin: 0 auto 6px;
  border: 2px solid;
}

.podium-item.gold .podium-avatar {
  width: 64px;
  height: 64px;
  font-size: 34px;
  border: 3px solid #FFD166;
}

.podium-item.silver .podium-avatar {
  border-color: #C9A9FF;
}

.podium-item.bronze .podium-avatar {
  border-color: #7EC8D8;
}

.podium-name {
  font-size: 13px;
  font-weight: 700;
}

.podium-val {
  font-size: 11px;
  color: #7A6B8A;
}

.podium-item.gold .podium-name {
  font-size: 14px;
}

.podium-item.gold .podium-val {
  font-size: 12px;
  color: #D4A000;
  font-weight: 700;
}

.rank-table-wrapper {
  padding: 16px 20px;
}

.rank-table {
  width: 100%;
  border-collapse: collapse;
}

.rank-table th {
  text-align: left;
  padding: 8px 0;
  font-size: 11px;
  color: #A898B8;
  font-weight: 700;
}

.rank-table td {
  padding: 10px 0;
  border-bottom: 1px solid $ev-bg;
}

.rank-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rank-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.rank-name {
  font-weight: 600;
}

.rank-pet {
  font-size: 11px;
  color: #7A6B8A;
}

.rank-gold-val {
  font-weight: 700;
  color: #D4A000;
}

.right-bar {
  background: #fff;
  border-left: 1px solid $ev-purple-light;
  padding: 20px 14px;
  overflow-y: auto;
}

.right-section {
  margin-bottom: 24px;
}

.right-section-title {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.right-section-title::before {
  content: '';
  width: 3px;
  height: 12px;
  background: linear-gradient(180deg, $ev-primary, $ev-purple);
  border-radius: 2px;
}

.online-list {
  display: flex;
  flex-direction: column;
}

.online-friend {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  border-bottom: 1px solid $ev-bg;
}

.online-friend:last-child {
  border-bottom: none;
}

.online-avatar {
  position: relative;
}

.online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 9px;
  height: 9px;
  background: #B8F1CC;
  border-radius: 50%;
  border: 1.5px solid #fff;
}

.online-info {
  flex: 1;
}

.online-name {
  font-size: 12px;
  font-weight: 600;
}

.online-status {
  font-size: 11px;
  color: #7A6B8A;
}

.mini-photo {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
  margin-bottom: 8px;
}

.mini-photo-item {
  aspect-ratio: 1;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  cursor: pointer;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 12px;
  transition: background 0.2s;
  cursor: pointer;
}

.rank-item:hover {
  background: #FAFAFA;
}

.rank-num {
  font-size: 16px;
  font-weight: 700;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.rank-num.gold {
  color: #FFD166;
}

.rank-num.silver {
  color: #C9D6E0;
}

.rank-num.bronze {
  color: #D4A87A;
}

.rank-info {
  flex: 1;
  min-width: 0;
}

.rank-val {
  font-size: 13px;
  font-weight: 700;
  text-align: right;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 63, 85, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
}

.modal-overlay.show {
  opacity: 1;
  pointer-events: all;
}

.modal {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  width: 400px;
  transform: scale(0.95);
  transition: transform 0.3s;
}

.modal-overlay.show .modal {
  transform: scale(1);
}

.modal-handle {
  width: 40px;
  height: 4px;
  background: #E0E0E0;
  border-radius: 2px;
  margin: 0 auto 20px;
}

.modal-emoji {
  font-size: 48px;
  text-align: center;
  margin-bottom: 12px;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 4px;
}

.modal-sub {
  font-size: 13px;
  color: #7A6B8A;
  text-align: center;
  margin-bottom: 20px;
}

.input-field {
  width: 100%;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid $ev-purple-light;
  font-size: 14px;
  font-family: inherit;
  color: $ev-text;
  background: #FAFAFA;
  transition: all 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: $ev-purple;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(213, 170, 255, 0.1);
}

.modal-start-btn {
  width: 100%;
  padding: 13px;
  border-radius: 16px;
  background: linear-gradient(135deg, $ev-primary, $ev-purple);
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  border: none;
  cursor: pointer;
}

.modal-cancel {
  text-align: center;
  margin-top: 10px;
  font-size: 12px;
  color: #A898B8;
  cursor: pointer;
}

.page {
  display: none;
}

.page.active {
  display: block;
}
</style>
