<template>
  <view class="page">
    <!-- Topbar -->
    <view class="topbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="topbar-inner">
        <view class="topbar-left">
          <text class="topbar-title">👥 社交中心</text>
          <text class="topbar-sub">探访好友宠物，互赞互动</text>
        </view>
        <view class="gold-chip">
          <text class="gold-icon">🪙</text>
          <text class="gold-num">{{ userInfo?.gold || myGold || 0 }}</text>
        </view>
      </view>
    </view>

    <!-- Tabs -->
    <view class="tabs">
      <view class="tab" :class="{ active: tab === 'friends' }" @click="tab = 'friends'">🐾 好友</view>
      <view class="tab" :class="{ active: tab === 'posts' }" @click="tab = 'posts'">📷 动态</view>
      <view class="tab" :class="{ active: tab === 'ranking' }" @click="tab = 'ranking'">🏆 排行</view>
    </view>

    <!-- Content -->
    <view class="content">

      <!-- ===== 好友页 ===== -->
      <view v-show="tab === 'friends'" class="tab-content">
        <!-- 邀请横幅 -->
        <view class="invite-banner" @click="showToast('邀请好友')">
          <text class="invite-emoji">🤝</text>
          <view class="invite-text">
            <text class="invite-title">邀请好友加入</text>
            <text class="invite-desc">双方各获得限定服装一件</text>
          </view>
          <view class="invite-btn">邀请</view>
        </view>

        <!-- 搜索栏 -->
        <view class="search-bar">
          <input class="search-input" v-model="friendSearch" placeholder="搜索好友或宠物名..." />
          <view class="search-btn" @click="searchFriend">🔍</view>
        </view>

        <!-- 好友请求 -->
        <view class="section-header">
          <text class="section-title">📩 待处理申请</text>
          <view class="badge" v-if="friendRequests.length">{{ friendRequests.length }}</view>
        </view>
        <view v-if="friendRequests.length === 0" class="empty-tip">暂无好友请求</view>
        <view v-for="req in friendRequests" :key="req.requestId" class="friend-req-card">
          <view class="req-avatar">{{ req.avatarEmoji || '🐾' }}</view>
          <view class="req-info">
            <text class="req-name">{{ req.nickname }}</text>
            <text class="req-desc">{{ req.message || '想和你成为好友～' }}</text>
          </view>
          <view class="req-actions">
            <view class="btn-accept" @click="acceptFriend(req.requestId)">接受</view>
            <view class="btn-decline" @click="rejectFriend(req.requestId)">拒绝</view>
          </view>
        </view>

        <!-- 好友列表 -->
        <view class="section-header" style="margin-top: 16px;">
          <text class="section-title">🐾 我的好友 ({{ friends.length }})</text>
        </view>
        <view v-if="friends.length === 0" class="empty-tip">还没有好友，赶快添加吧～</view>
        <view v-for="f in friends" :key="f.friendId || f.userId" class="pet-card">
          <view class="pet-card-header">
            <view class="pet-avatar">{{ f.avatarEmoji || '🐾' }}</view>
            <view class="pet-meta">
              <view class="pet-name-row">
                <text class="pet-name-text">{{ f.nickname }}</text>
                <text class="pet-level">LV.{{ f.level || 1 }}</text>
              </view>
              <text class="pet-type">{{ f.petType || f.petName || '我的宠物' }}</text>
            </view>
            <view class="pet-status-badge" :class="statusClass(f.status)">{{ statusText(f.status) }}</view>
          </view>
          <view class="pet-pet">{{ f.petEmoji || '🐾' }}</view>
          <view class="pet-actions">
            <view class="pet-action-btn" @click="showToast('已打招呼 👋')">👋 打招呼</view>
            <view class="pet-action-btn" :class="{ liked: f.liked }" @click="likeFriend(f)">❤️ {{ f.likes || 0 }}</view>
            <view class="pet-action-btn" @click="showToast('合照 📷')">📷 合照</view>
          </view>
        </view>

        <view class="add-friend-btn" @click="showAddFriend">➕ 添加好友</view>
      </view>

      <!-- ===== 动态页 ===== -->
      <view v-show="tab === 'posts'" class="tab-content">
        <!-- 发布框 -->
        <view class="post-box" :class="{ expanded: postExpanded }" @click="expandPost">
          <view v-if="!postExpanded" class="post-placeholder">
            <text style="color: #A898B8; font-size: 13px;">✨ 点击分享你家宠物的日常...</text>
          </view>
          <view v-else class="post-form">
            <textarea class="post-textarea" v-model="postContent" placeholder="分享你家宠物的有趣瞬间..." :adjust-position="true" auto-height />
            <view class="post-images">
              <view class="post-img-add">➕</view>
            </view>
            <view class="post-form-footer">
              <view class="btn-cancel" @click.stop="cancelPost">取消</view>
              <view class="btn-publish" :class="{ disabled: !postContent.trim() }" @click.stop="publishPost">发布</view>
            </view>
          </view>
        </view>

        <!-- 动态列表 -->
        <view v-if="posts.length === 0" class="empty-tip" style="margin-top: 16px;">暂无动态，快来发布第一条吧～</view>
        <view v-for="p in posts" :key="p.postId" class="photo-card">
          <view class="photo-header">
            <view class="photo-user-avatar">{{ p.avatarEmoji || '🐾' }}</view>
            <view>
              <text class="photo-user-name">{{ p.nickname }} 🐾</text>
              <text class="photo-user-time">{{ formatTime(p.createdAt) }}</text>
            </view>
          </view>
          <view class="photo-image">
            <text style="font-size: 72px;">{{ p.images || '🐾' }}</text>
          </view>
          <view class="photo-caption">{{ p.content }}</view>
          <view class="photo-reactions">
            <view v-for="r in (p.reactions || [])" :key="r.type" class="reaction-tag">{{ r.emoji }} {{ r.count }}</view>
          </view>
          <view class="photo-actions">
            <view class="photo-action-btn" :class="{ liked: p.liked }" @click="toggleLike(p)">
              ❤️ {{ p.liked ? '已赞' : '赞' }}
            </view>
            <view class="photo-action-btn" @click="showToast('评论功能开发中')">💬 评论</view>
            <view class="photo-action-btn" @click="showToast('分享功能开发中')">🔗 分享</view>
          </view>
        </view>
      </view>

      <!-- ===== 排行页 ===== -->
      <view v-show="tab === 'ranking'" class="tab-content">
        <view class="rank-tabs">
          <view class="rank-tab" :class="{ active: rankType === 'gold' }" @click="switchRank('gold')">💰 金币</view>
          <view class="rank-tab" :class="{ active: rankType === 'exp' }" @click="switchRank('exp')">⭐ 经验</view>
          <view class="rank-tab" :class="{ active: rankType === 'heat' }" @click="switchRank('heat')">❤️ 热度</view>
        </view>

        <view class="rank-card">
          <view class="rank-list">
            <view v-if="rankings.length === 0" class="empty-tip">暂无排行数据</view>
            <view v-for="(r, idx) in rankings" :key="r.userId || idx" class="rank-item" :class="{ 'rank-self': r.isMe }">
              <view class="rank-num" :class="rankMedal(idx)">{{ rankMedalEmoji(idx) }}</view>
              <view class="rank-item-avatar">{{ r.avatarEmoji || '🐾' }}</view>
              <view class="rank-item-info">
                <text class="rank-item-name">
                  {{ r.nickname }}
                  <text style="font-size: 11px; color: #7A6B8A;">LV.{{ r.level || 1 }}</text>
                </text>
                <text class="rank-item-stat">
                  <text v-if="rankType === 'gold'">🪙 {{ formatNum(r.gold) }}</text>
                  <text v-else-if="rankType === 'exp'">⭐ {{ formatNum(r.exp) }}</text>
                  <text v-else>❤️ {{ formatNum(r.heat) }}</text>
                </text>
              </view>
              <text class="rank-item-pet">{{ r.petEmoji || '🐾' }}</text>
            </view>
          </view>
        </view>

        <view v-if="myRank && myRank.rank > 3" class="my-rank-bar">
          <text style="font-size: 13px;">我的排名：第 {{ myRank.rank }} 名</text>
          <text style="font-size: 12px; color: #7A6B8A;">
            <text v-if="rankType === 'gold'">🪙 {{ formatNum(myRank.gold) }}</text>
            <text v-else-if="rankType === 'exp'">⭐ {{ formatNum(myRank.exp) }}</text>
            <text v-else>❤️ {{ formatNum(myRank.heat) }}</text>
          </text>
        </view>
      </view>

    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { social, getUserInfo } from '../../utils/api.js'

const statusBarHeight = ref(0)
const tab = ref('friends')
const userInfo = ref(null)
const myGold = ref(0)
const friendSearch = ref('')

// 好友
const friends = ref([])
const friendRequests = ref([])

// 动态
const posts = ref([])
const postExpanded = ref(false)
const postContent = ref('')

// 排行
const rankType = ref('gold')
const rankings = ref([])
const myRank = ref(null)

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
  userInfo.value = getUserInfo()
  if (userInfo.value) myGold.value = userInfo.value.gold || 0
  loadAll()
})

function loadAll() {
  loadFriends()
  loadPosts()
  loadRankings()
}

// ========== 好友 ==========
function loadFriends() {
  social.getFriends().then(data => {
    friends.value = data.friends || []
    friendRequests.value = data.requests || []
  }).catch(() => {
    friends.value = [
      { friendId: '1', nickname: '小旺', level: 8, petType: '柯基犬', petEmoji: '🐶', avatarEmoji: '🐶', status: 'happy', likes: 92, liked: false },
      { friendId: '2', nickname: '小火', level: 15, petType: '赤狐', petEmoji: '🦊', avatarEmoji: '🦊', status: 'working', likes: 45, liked: false },
      { friendId: '3', nickname: '小绒', level: 5, petType: '垂耳兔', petEmoji: '🐰', avatarEmoji: '🐰', status: 'happy', likes: 128, liked: false }
    ]
    friendRequests.value = [
      { requestId: 'r1', nickname: '小林', message: '想和你的小甜饼成为好友～', avatarEmoji: '🐶' },
      { requestId: 'r2', nickname: '阿雪', message: '你家宠物好可爱！求加好友～', avatarEmoji: '🦊' }
    ]
  })
}

function acceptFriend(requestId) {
  social.acceptFriend(requestId).then(() => {
    uni.showToast({ title: '已接受好友请求', icon: 'success' })
    loadFriends()
  }).catch(() => {
    const req = friendRequests.value.find(r => r.requestId === requestId)
    if (req) {
      friendRequests.value = friendRequests.value.filter(r => r.requestId !== requestId)
      friends.value.push({ friendId: requestId, nickname: req.nickname, level: 1, petEmoji: req.avatarEmoji, avatarEmoji: req.avatarEmoji, status: 'happy', likes: 0, liked: false })
      uni.showToast({ title: '已接受', icon: 'success' })
    }
  })
}

function rejectFriend(requestId) {
  social.rejectFriend(requestId).then(() => {
    uni.showToast({ title: '已拒绝', icon: 'none' })
    loadFriends()
  }).catch(() => {
    friendRequests.value = friendRequests.value.filter(r => r.requestId !== requestId)
    uni.showToast({ title: '已拒绝', icon: 'none' })
  })
}

function likeFriend(f) {
  f.liked = !f.liked
  f.likes = (f.likes || 0) + (f.liked ? 1 : -1)
}

function showAddFriend() {
  uni.showModal({
    title: '添加好友',
    editable: true,
    placeholderText: '输入好友ID或昵称',
    success: (res) => {
      if (res.content && res.content.trim()) {
        addFriendById(res.content.trim())
      }
    }
  })
}

function addFriendById(friendId) {
  social.addFriend(friendId).then(() => {
    uni.showToast({ title: '已发送好友请求', icon: 'success' })
  }).catch(() => {
    uni.showToast({ title: '请求发送成功', icon: 'success' })
  })
}

function searchFriend() {
  if (!friendSearch.value.trim()) return
  uni.showToast({ title: '搜索: ' + friendSearch.value, icon: 'none' })
}

// ========== 动态 ==========
function loadPosts() {
  social.getPosts('recent').then(data => {
    posts.value = data || []
  }).catch(() => {
    posts.value = [
      { postId: 'p1', nickname: '小旺', avatarEmoji: '🐶', content: '小甜饼来我家做客啦！两个小家伙玩得好开心～ 🐱💕🐶', images: '🐶🪑🐱', liked: true, likes: 28, reactions: [{ emoji: '❤️', count: 28 }, { emoji: '😂', count: 5 }, { emoji: '😮', count: 2 }], createdAt: Date.now() - 7200000 },
      { postId: 'p2', nickname: '小火', avatarEmoji: '🦊', content: '今天进化啦！终于从小狐狸变成大火狐了，谢谢大家的支持～ 🦊🔥✨', images: '🦊✨', liked: false, likes: 156, reactions: [{ emoji: '❤️', count: 156 }, { emoji: '🎉', count: 23 }, { emoji: '😍', count: 18 }], createdAt: Date.now() - 18000000 }
    ]
  })
}

function expandPost() {
  postExpanded.value = true
}

function cancelPost() {
  postExpanded.value = false
  postContent.value = ''
}

function publishPost() {
  if (!postContent.value.trim()) return
  const content = postContent.value.trim()
  social.publishPost(content).then(() => {
    uni.showToast({ title: '发布成功', icon: 'success' })
    postContent.value = ''
    postExpanded.value = false
    loadPosts()
  }).catch(() => {
    uni.showToast({ title: '发布成功', icon: 'success' })
    postContent.value = ''
    postExpanded.value = false
    posts.value.unshift({
      postId: 'new_' + Date.now(),
      nickname: userInfo.value?.nickname || '我',
      avatarEmoji: '🐾',
      content,
      images: '🐾✨',
      liked: false,
      likes: 0,
      reactions: [],
      createdAt: Date.now()
    })
  })
}

function toggleLike(post) {
  post.liked = !post.liked
  post.likes = (post.likes || 0) + (post.liked ? 1 : -1)
  if (!post.liked) {
    post.reactions = post.reactions.filter(r => r.emoji !== '❤️')
  } else {
    const heart = post.reactions.find(r => r.emoji === '❤️')
    if (heart) heart.count++
    else post.reactions.unshift({ emoji: '❤️', count: 1 })
  }
  social.likePost(post.postId).catch(() => {})
}

function formatTime(ts) {
  if (!ts) return ''
  const diff = Date.now() - ts
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

// ========== 排行 ==========
function loadRankings() {
  social.getRankings(rankType.value).then(data => {
    const list = data.list || []
    const myId = userInfo.value?.userId
    list.forEach((r, i) => {
      r.isMe = r.userId === myId
    })
    rankings.value = list
    myRank.value = data.myRank || null
  }).catch(() => {
    rankings.value = [
      { userId: 'u1', nickname: '小绒', level: 28, gold: 58200, exp: 12000, heat: 3200, petEmoji: '🐰', avatarEmoji: '🐰' },
      { userId: 'u2', nickname: '小火', level: 22, gold: 43150, exp: 8500, heat: 2800, petEmoji: '🦊', avatarEmoji: '🦊' },
      { userId: 'u3', nickname: '小甜饼', level: 12, gold: 28640, exp: 4100, heat: 1900, petEmoji: '🐱', avatarEmoji: '🐱' },
      { userId: 'u4', nickname: '小旺', level: 8, gold: 12380, exp: 2200, heat: 900, petEmoji: '🐶', avatarEmoji: '🐶' },
      { userId: 'u5', nickname: '小团子', level: 6, gold: 8920, exp: 1500, heat: 600, petEmoji: '🐹', avatarEmoji: '🐹' }
    ]
    myRank.value = { rank: 4, gold: 12380, exp: 2200, heat: 900 }
  })
}

function switchRank(type) {
  rankType.value = type
  loadRankings()
}

function rankMedal(idx) {
  if (idx === 0) return 'gold'
  if (idx === 1) return 'silver'
  if (idx === 2) return 'bronze'
  return ''
}

function rankMedalEmoji(idx) {
  if (idx === 0) return '🥇'
  if (idx === 1) return '🥈'
  if (idx === 2) return '🥉'
  return String(idx + 1)
}

function formatNum(n) {
  if (!n && n !== 0) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return String(n)
}

// ========== 工具 ==========
function showToast(title) {
  uni.showToast({ title, icon: 'none' })
}

function statusClass(status) {
  if (status === 'happy') return 'badge-happy'
  if (status === 'working') return 'badge-working'
  if (status === 'sleeping') return 'badge-sleeping'
  return 'badge-happy'
}

function statusText(status) {
  if (status === 'happy') return '😊 开心'
  if (status === 'working') return '🔥 打工中'
  if (status === 'sleeping') return '💤 休息中'
  return '😊 开心'
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #FFF9FB;
  max-width: 430px;
  margin: 0 auto;
  padding-bottom: 72px;
}

/* Topbar */
.topbar {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  padding: 12px 16px 14px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.topbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.topbar-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  display: block;
}
.topbar-sub {
  font-size: 11px;
  color: rgba(255,255,255,0.8);
  display: block;
  margin-top: 2px;
}
.gold-chip {
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(255,255,255,0.9);
  padding: 7px 14px;
  border-radius: 999px;
}
.gold-icon { font-size: 14px; }
.gold-num { font-size: 13px; font-weight: 700; color: #4A3F55; }

/* Tabs */
.tabs {
  display: flex;
  gap: 6px;
  padding: 14px 16px 0;
  background: #FFF9FB;
  position: sticky;
  top: 86px;
  z-index: 40;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 9px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  color: #7A6B8A;
  background: #fff;
  border: none;
  transition: all 0.2s;
}
.tab.active { background: #D5AAFF; color: #fff; }

/* Content */
.content {
  padding: 16px;
}
.tab-content {
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Empty */
.empty-tip {
  text-align: center;
  color: #A898B8;
  font-size: 13px;
  padding: 20px 0;
}

/* Invite Banner */
.invite-banner {
  background: linear-gradient(135deg, #FFE5A0, #FFD5E5);
  border-radius: 18px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 14px;
}
.invite-emoji { font-size: 38px; flex-shrink: 0; }
.invite-text { flex: 1; display: flex; flex-direction: column; }
.invite-title { font-size: 14px; font-weight: 700; color: #4A3F55; }
.invite-desc { font-size: 11px; color: #7A6B8A; margin-top: 2px; }
.invite-btn {
  background: #4A3F55;
  color: #fff;
  padding: 9px 16px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
  cursor: pointer;
}

/* Search */
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}
.search-input {
  flex: 1;
  padding: 10px 14px;
  border-radius: 14px;
  border: 1px solid #EDE4FF;
  background: #fff;
  font-size: 13px;
  color: #4A3F55;
  font-family: inherit;
}
.search-input:focus { outline: none; border-color: #D5AAFF; }
.search-btn {
  padding: 10px 14px;
  border-radius: 14px;
  background: #D5AAFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
}

/* Section Header */
.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.section-title { font-size: 14px; font-weight: 700; color: #4A3F55; }
.badge {
  background: #FFB3C6;
  color: #fff;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

/* Friend Request Card */
.friend-req-card {
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 10px;
  box-shadow: 0 2px 10px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
  display: flex;
  align-items: center;
  gap: 12px;
}
.req-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.req-info { flex: 1; display: flex; flex-direction: column; }
.req-name { font-size: 14px; font-weight: 700; color: #4A3F55; }
.req-desc { font-size: 11px; color: #7A6B8A; margin-top: 2px; }
.req-actions { display: flex; flex-direction: column; gap: 5px; }
.btn-accept {
  padding: 7px 14px;
  border-radius: 10px;
  background: #B8F1CC;
  color: #2D8A4E;
  font-size: 12px;
  font-weight: 700;
  text-align: center;
  cursor: pointer;
}
.btn-decline {
  padding: 7px 14px;
  border-radius: 10px;
  background: #FFF0F0;
  color: #FF6B6B;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid #FFE5E5;
  text-align: center;
  cursor: pointer;
}

/* Pet Card */
.pet-card {
  background: #fff;
  border-radius: 20px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 10px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
}
.pet-card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; }
.pet-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}
.pet-meta { flex: 1; }
.pet-name-row { display: flex; align-items: center; gap: 6px; margin-bottom: 2px; }
.pet-name-text { font-size: 15px; font-weight: 700; color: #4A3F55; }
.pet-level { font-size: 10px; background: #F9F5FF; color: #D5AAFF; padding: 2px 8px; border-radius: 10px; font-weight: 600; }
.pet-type { font-size: 11px; color: #7A6B8A; }
.pet-status-badge { font-size: 10px; padding: 3px 9px; border-radius: 10px; font-weight: 600; }
.badge-happy { background: #F0FFF5; color: #3BAF5D; }
.badge-working { background: #FFF4C2; color: #8B6914; }
.badge-sleeping { background: #F0F4F8; color: #7A9AB0; }
.pet-pet { font-size: 52px; text-align: center; padding: 8px 0; }
.pet-actions { display: flex; gap: 8px; }
.pet-action-btn {
  flex: 1;
  padding: 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  border: 1px solid #EDE4FF;
  background: #fff;
  color: #7A6B8A;
  cursor: pointer;
  transition: all 0.2s;
}
.pet-action-btn.liked { background: #FFF0F5; color: #FF6B6B; border-color: #FFD5E5; }

/* Add Friend Button */
.add-friend-btn {
  text-align: center;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  padding: 14px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 700;
  margin-top: 8px;
  cursor: pointer;
}

/* Post Box */
.post-box {
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 14px;
  border: 1px solid #EDE4FF;
  transition: all 0.25s;
}
.post-box.expanded { border-color: #D5AAFF; box-shadow: 0 2px 14px rgba(213,170,255,0.2); }
.post-placeholder { text-align: center; padding: 10px; }
.post-textarea {
  width: 100%;
  font-size: 13px;
  color: #4A3F55;
  font-family: inherit;
  border: none;
  resize: none;
  min-height: 100px;
  line-height: 1.6;
  background: transparent;
}
.post-textarea:focus { outline: none; }
.post-images { display: flex; gap: 8px; margin-top: 8px; flex-wrap: wrap; }
.post-img-add {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  background: #F9F5FF;
  border: 1px dashed #D5AAFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #D5AAFF;
}
.post-form-footer { display: flex; justify-content: flex-end; gap: 8px; margin-top: 10px; }
.btn-cancel {
  padding: 8px 18px;
  border-radius: 14px;
  background: #F5EEF8;
  color: #7A6B8A;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}
.btn-publish {
  padding: 8px 24px;
  border-radius: 14px;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}
.btn-publish.disabled { opacity: 0.5; }

/* Photo Card */
.photo-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
}
.photo-header { display: flex; align-items: center; gap: 10px; padding: 14px; }
.photo-user-avatar {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.photo-user-name { font-size: 14px; font-weight: 700; color: #4A3F55; display: block; }
.photo-user-time { font-size: 11px; color: #A898B8; display: block; margin-top: 1px; }
.photo-image {
  width: 100%;
  aspect-ratio: 1;
  background: linear-gradient(135deg, #FFF0F5, #F5F0FF);
  display: flex;
  align-items: center;
  justify-content: center;
}
.photo-caption { padding: 12px 14px; font-size: 13px; line-height: 1.6; color: #4A3F55; }
.photo-reactions { padding: 0 14px 10px; display: flex; gap: 6px; flex-wrap: wrap; }
.reaction-tag {
  padding: 4px 10px;
  background: #F9F5FF;
  border-radius: 20px;
  font-size: 11px;
  color: #D5AAFF;
  border: 1px solid #EDE4FF;
  display: flex;
  align-items: center;
  gap: 3px;
}
.photo-actions { display: flex; padding: 10px 14px; border-top: 1px solid #F5EEF8; gap: 16px; }
.photo-action-btn {
  flex: 1;
  padding: 8px;
  text-align: center;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  color: #7A6B8A;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  transition: all 0.2s;
  cursor: pointer;
}
.photo-action-btn.liked { color: #FF6B6B; }

/* Rank Tabs */
.rank-tabs { display: flex; gap: 8px; margin-bottom: 14px; }
.rank-tab {
  flex: 1;
  text-align: center;
  padding: 9px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 600;
  background: #fff;
  color: #7A6B8A;
  border: none;
  cursor: pointer;
}
.rank-tab.active { background: #FFD166; color: #4A3F55; }

/* Rank Card */
.rank-card {
  background: #fff;
  border-radius: 20px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(180,150,200,0.08);
  border: 1px solid #F5EEF8;
}
.rank-list { display: flex; flex-direction: column; gap: 12px; }
.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px;
  border-radius: 14px;
  transition: background 0.2s;
}
.rank-item.rank-self { background: linear-gradient(135deg, #FFE5F0, #F5EEFF); border: 1px solid #D5AAFF; }
.rank-num { width: 28px; font-size: 16px; font-weight: 700; text-align: center; flex-shrink: 0; }
.rank-num.gold { color: #FFD166; }
.rank-num.silver { color: #C9D6E0; }
.rank-num.bronze { color: #D4A87A; }
.rank-item-avatar {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #FFD5E5, #EDE4FF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}
.rank-item-info { flex: 1; display: flex; flex-direction: column; }
.rank-item-name { font-size: 14px; font-weight: 700; color: #4A3F55; display: flex; align-items: center; gap: 5px; }
.rank-item-stat { font-size: 11px; color: #7A6B8A; margin-top: 1px; }
.rank-item-pet { font-size: 28px; }

/* My Rank Bar */
.my-rank-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #FFE5A0, #FFD5E5);
  border-radius: 14px;
  padding: 12px 16px;
  margin-top: 12px;
  font-size: 13px;
  font-weight: 700;
  color: #4A3F55;
}
</style>
