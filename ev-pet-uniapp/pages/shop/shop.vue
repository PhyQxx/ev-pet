<template>
  <view class="shop-container">
    <!-- Header -->
    <view class="shop-header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="header-top">
        <view class="back-btn" @click="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="header-title">🛒 商店</view>
        <view class="coin-chip">
          <text class="coin-icon">🪙</text>
          <text class="coin-num">{{ userInfo?.gold || 0 }}</text>
        </view>
      </view>
      <view class="header-sub">用金币和钻石购买道具，装扮你的宠物</view>
    </view>

    <!-- Currency Bar -->
    <view class="currency-bar">
      <view class="currency-chip">
        <text class="currency-icon">🪙</text>
        <text class="currency-val">{{ userInfo?.gold || 0 }}</text>
      </view>
      <view class="currency-chip">
        <text class="currency-icon">💎</text>
        <text class="currency-val">{{ userInfo?.diamond || 0 }}</text>
      </view>
      <view class="currency-chip">
        <text class="currency-icon">🎫</text>
        <text class="currency-val">{{ userInfo?.coupon || 0 }}</text>
      </view>
    </view>

    <!-- Banner -->
    <view class="banner">
      <text class="banner-emoji">🎁</text>
      <view class="banner-text">
        <view class="banner-title">新人专属礼包</view>
        <view class="banner-desc">500钻石+限定服装，仅¥6.8</view>
      </view>
      <view class="banner-btn" @click="buyPack">获取</view>
    </view>

    <!-- Tabs -->
    <view class="tabs">
      <view
        v-for="tab in categoryTabs"
        :key="tab.key"
        class="tab"
        :class="{ active: currentCategory === tab.key }"
        @click="switchCategory(tab.key)"
      >{{ tab.icon }} {{ tab.label }}</view>
    </view>

    <!-- Content -->
    <scroll-view class="content" scroll-y>
      <!-- Featured Deals -->
      <view class="section" v-if="currentCategory === 'all' || currentCategory === 'food'">
        <view class="section-header">
          <view class="section-title">🔥 限时折扣</view>
          <view class="countdown">剩余 <text class="countdown-val">{{ countdown }}</text></view>
        </view>
        <view class="deal-card" v-for="deal in deals" :key="deal.id" @click="buyDeal(deal)">
          <view class="deal-left">
            <text class="deal-emoji">{{ deal.emoji }}</text>
            <text class="deal-name">{{ deal.name }}</text>
          </view>
          <view class="deal-right">
            <text class="deal-title">{{ deal.title }}</text>
            <text class="deal-desc">{{ deal.desc }}</text>
            <view class="deal-price">
              <text class="deal-price-current">{{ deal.priceIcon }} {{ deal.currentPrice }}</text>
              <text class="deal-price-old">{{ deal.priceIcon }} {{ deal.oldPrice }}</text>
            </view>
            <text class="deal-countdown">库存 {{ deal.stock }} 件</text>
          </view>
          <view class="deal-buy">购买</view>
        </view>
      </view>

      <!-- Section: Food / Items -->
      <view class="section">
        <view class="section-header" v-if="currentCategory === 'all'">
          <view class="section-title">{{ currentTabObj.icon }} {{ currentTabObj.label }}</view>
        </view>
        <view class="item-grid">
          <view
            v-for="item in filteredItems"
            :key="item.id"
            class="item-card"
            :class="{ hot: item.badge === 'hot', new: item.badge === 'new', owned: item.owned, locked: item.locked }"
            @click="onItemClick(item)"
          >
            <text class="item-emoji">{{ item.emoji }}</text>
            <text class="item-name">{{ item.name }}</text>
            <text class="item-desc">{{ item.desc }}</text>
            <view class="item-price">
              <text class="price-icon">{{ item.priceIcon }}</text>
              <text class="price-num">{{ item.price }}</text>
            </view>
            <view class="item-buy-btn" :class="{ owned: item.owned, locked: item.locked }">
              {{ item.owned ? '已拥有' : item.locked ? '🔒 解锁' : '购买' }}
            </view>
          </view>
        </view>
      </view>

      <!-- Packs Section -->
      <view class="section" v-if="currentCategory === 'all' || currentCategory === 'packs'">
        <view class="section-header">
          <view class="section-title">📦 特惠礼包</view>
        </view>
        <scroll-view class="pack-scroll" scroll-x>
          <view v-for="pack in packs" :key="pack.id" class="pack-card" :class="{ featured: pack.featured }" @click="buyPack(pack)">
            <text class="pack-emoji">{{ pack.emoji }}</text>
            <text class="pack-name">{{ pack.name }}</text>
            <text class="pack-desc">{{ pack.desc }}</text>
            <view class="pack-items">
              <view v-for="(pi, idx) in pack.items" :key="idx" class="pack-item">
                <text class="pack-item-emoji">{{ pi.emoji }}</text>
                <text class="pack-item-text">{{ pi.text }}</text>
              </view>
            </view>
            <view class="pack-price">¥{{ pack.price }} <text class="old" v-if="pack.oldPrice">¥{{ pack.oldPrice }}</text></view>
            <view class="pack-buy-btn">购买</view>
          </view>
        </scroll-view>
      </view>

      <!-- Lucky Draw -->
      <view class="section" v-if="currentCategory === 'all' || currentCategory === 'packs'">
        <view class="section-header">
          <view class="section-title">🎰 幸运转盘</view>
        </view>
        <view class="luckydraw-card" @click="showToast('每日免费抽奖')">
          <text class="luckydraw-emoji">🎰</text>
          <view class="luckydraw-text">
            <text class="luckydraw-title">每日免费抽奖</text>
            <text class="luckydraw-desc">限定时装等你拿</text>
          </view>
          <view class="luckydraw-btn">免费抽奖 →</view>
        </view>
        <text class="draw-times">今日剩余次数：<text class="times-val">3</text> 次</text>
      </view>

      <view style="height: 40px;"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onShow } from 'vue'
import { shop as shopApi, getUserInfo } from '../../utils/api.js'

const statusBarHeight = ref(0)
const userInfo = ref(null)
const currentCategory = ref('all')

const categoryTabs = [
  { key: 'all', label: '全部', icon: '📦' },
  { key: 'food', label: '食物', icon: '🍖' },
  { key: 'clothing', label: '服装', icon: '👗' },
  { key: 'toy', label: '玩具', icon: '🎾' },
  { key: 'accessory', label: '装饰', icon: '🎀' },
]

const countdown = ref('23:41')

const currentTabObj = computed(() =>
  categoryTabs.find(t => t.key === currentCategory.value) || categoryTabs[0]
)

const deals = ref([
  { id: 1, emoji: '🍰', name: '限定甜品', title: '豪华蛋糕大餐', desc: '恢复饱食+80，心情+50', priceIcon: '🪙', currentPrice: 280, oldPrice: 450, stock: 23 },
  { id: 2, emoji: '🎀', name: '今日配饰', title: '樱花发卡', desc: '限定配饰，先到先得', priceIcon: '💎', currentPrice: 50, oldPrice: 120, stock: 8 },
])

const packs = ref([
  { id: 1, emoji: '🍖', name: '饱食补给包', desc: '3天无限量投喂', price: 6.8, oldPrice: null, featured: false, items: [
    { emoji: '🍗', text: '高级猫粮 ×30' }, { emoji: '🪙', text: '金币 ×500' }, { emoji: '⭐', text: '经验 ×200' }
  ]},
  { id: 2, emoji: '👑', name: '豪华装扮礼包', desc: '含限定服装+头像框', price: 18.8, oldPrice: 38, featured: true, items: [
    { emoji: '👗', text: '小王子服 ×1' }, { emoji: '🎀', text: '星光冠 ×1' }, { emoji: '🖼️', text: '限定头像框' }, { emoji: '💎', text: '钻石 ×200' }
  ]},
  { id: 3, emoji: '🚀', name: '月卡·急速成长', desc: '每日领取+经验+30%', price: 30, oldPrice: 50, featured: false, items: [
    { emoji: '🪙', text: '每日金币 ×200' }, { emoji: '🍗', text: '每日猫粮 ×5' }, { emoji: '✨', text: '经验 +30%' }, { emoji: '🛡️', text: '无广告' }
  ]},
])

const items = ref([
  // Food
  { id: 1, category: 'food', emoji: '🍗', name: '黄金猫粮', desc: '饱食+40，心情+10', price: 80, priceIcon: '🪙', badge: 'hot', owned: false, locked: false },
  { id: 2, category: 'food', emoji: '🍣', name: '鲜美三文鱼', desc: '饱食+55，心情+20', price: 150, priceIcon: '🪙', badge: 'new', owned: false, locked: false },
  { id: 3, category: 'food', emoji: '🧁', name: '小黄鸭蛋糕', desc: '饱食+35，心情+15', price: 60, priceIcon: '🪙', badge: null, owned: true, locked: false },
  { id: 4, category: 'food', emoji: '🥕', name: '新鲜胡萝卜', desc: '饱食+20，纯天然', price: 30, priceIcon: '🪙', badge: null, owned: false, locked: false },
  { id: 5, category: 'food', emoji: '🍎', name: '苹果派', desc: '饱食+30，心情+15', price: 50, priceIcon: '🪙', badge: null, owned: false, locked: false },
  { id: 6, category: 'food', emoji: '🧃', name: '营养果汁', desc: '饱食+25，体力+10', price: 45, priceIcon: '🪙', badge: null, owned: false, locked: false },
  // Clothing
  { id: 7, category: 'clothing', emoji: '👑', name: '小王子服', desc: '稀有 · 帅气风格', price: 50, priceIcon: '💎', badge: null, owned: true, locked: false },
  { id: 8, category: 'clothing', emoji: '🎀', name: '粉色蓬蓬裙', desc: '可爱 · 少女风', price: 120, priceIcon: '🪙', badge: null, owned: false, locked: false },
  { id: 9, category: 'clothing', emoji: '🧥', name: '秋冬大衣', desc: '史诗 · 保暖时尚', price: 80, priceIcon: '💎', badge: 'hot', owned: false, locked: false },
  { id: 10, category: 'clothing', emoji: '✨', name: '星之魔法使', desc: '传说 · 限定皮肤', price: 200, priceIcon: '💎', badge: null, owned: false, locked: true },
  // Toy
  { id: 11, category: 'toy', emoji: '🎾', name: '魔法网球', desc: '心情+30，体力-5', price: 40, priceIcon: '🪙', badge: 'new', owned: false, locked: false },
  { id: 12, category: 'toy', emoji: '🧸', name: '柔软抱枕', desc: '心情+20，安心入睡', price: 60, priceIcon: '🪙', badge: null, owned: false, locked: false },
  { id: 13, category: 'toy', emoji: '🎁', name: '神秘礼盒', desc: '随机玩具限定', price: 100, priceIcon: '💎', badge: 'hot', owned: false, locked: false },
  // Accessory
  { id: 14, category: 'accessory', emoji: '🌸', name: '樱花发卡', desc: '今日限定 · 8件', price: 30, priceIcon: '💎', badge: 'new', owned: false, locked: false },
  { id: 15, category: 'accessory', emoji: '🎀', name: '粉色蝴蝶结', desc: '经典配饰', price: 20, priceIcon: '🪙', badge: null, owned: true, locked: false },
  { id: 16, category: 'accessory', emoji: '🧣', name: '薄荷围巾', desc: '可爱系 · 保暖', price: 60, priceIcon: '🪙', badge: null, owned: false, locked: false },
  { id: 17, category: 'accessory', emoji: '⭐', name: '星光冠', desc: '传说 · LV.20', price: 150, priceIcon: '💎', badge: null, owned: false, locked: true },
])

const filteredItems = computed(() => {
  if (currentCategory.value === 'all') return items.value
  if (currentCategory.value === 'food') return items.value.filter(i => i.category === 'food')
  if (currentCategory.value === 'clothing') return items.value.filter(i => i.category === 'clothing')
  if (currentCategory.value === 'toy') return items.value.filter(i => i.category === 'toy')
  if (currentCategory.value === 'accessory') return items.value.filter(i => i.category === 'accessory')
  return items.value
})

onMounted(() => {
  const info = uni.getSystemInfoSync()
  statusBarHeight.value = info.statusBarHeight || 0
  userInfo.value = getUserInfo()
  loadItems()
  startCountdown()
})

function loadItems() {
  shopApi.getItems(currentCategory.value === 'all' ? '' : currentCategory.value)
    .then(data => {
      if (data && data.length) {
        items.value = data.map(item => ({
          ...item,
          emoji: getItemEmoji(item.category),
          priceIcon: item.priceType === 'diamond' ? '💎' : '🪙',
          badge: item.badge || null,
          owned: item.owned || false,
          locked: item.locked || false,
        }))
      }
    })
    .catch(() => {})
}

function switchCategory(key) {
  currentCategory.value = key
}

function getItemEmoji(category) {
  const map = { food: '🍖', clothing: '👗', toy: '🎾', accessory: '🎀', bg: '🖼️' }
  return map[category] || '📦'
}

function goBack() {
  uni.navigateBack()
}

function showToast(title) {
  uni.showToast({ title, icon: 'none' })
}

function onItemClick(item) {
  if (item.locked) {
    showToast('等级不足，无法购买')
    return
  }
  if (item.owned) {
    showToast('已拥有该物品')
    return
  }
  uni.showModal({
    title: '购买确认',
    content: `确定购买 ${item.name} 吗？需要 ${item.price} ${item.priceIcon === '💎' ? '钻石' : '金币'}`,
    success: (res) => {
      if (res.confirm) {
        shopApi.buy(item.id)
          .then(() => {
            uni.showToast({ title: '购买成功', icon: 'success' })
            item.owned = true
          })
          .catch(err => {
            uni.showToast({ title: err.message || '购买失败', icon: 'none' })
          })
      }
    }
  })
}

function buyDeal(deal) {
  uni.showModal({
    title: '限时折扣',
    content: `确定购买 ${deal.title} 吗？`,
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '购买成功！', icon: 'success' })
      }
    }
  })
}

function buyPack(pack) {
  uni.showModal({
    title: '购买礼包',
    content: `确定购买 ${pack.name} 吗？`,
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '购买成功！', icon: 'success' })
      }
    }
  })
}

function startCountdown() {
  let total = 23 * 60 + 41
  setInterval(() => {
    total--
    if (total < 0) total = 24 * 60
    const h = Math.floor(total / 60)
    const m = total % 60
    countdown.value = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
  }, 60000)
}
</script>

<style scoped>
.shop-container {
  min-height: 100vh;
  background: var(--bg, #FFF8FA);
  max-width: 430px;
  margin: 0 auto;
}

.shop-header {
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  padding: 12px 16px 14px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.header-top {
  display: flex;
  align-items: center;
  gap: 10px;
}
.back-btn {
  width: 32px;
  height: 32px;
  background: rgba(255,255,255,0.25);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.back-icon {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}
.header-title {
  flex: 1;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}
.coin-chip {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255,255,255,0.9);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}
.coin-icon { font-size: 14px; }
.coin-num { color: #4A3F55; }
.header-sub {
  font-size: 12px;
  color: rgba(255,255,255,0.8);
  margin-top: 2px;
}

.currency-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid #F5EEF8;
}
.currency-chip {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  background: #FAFAFA;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid #EDE4FF;
}
.currency-icon { font-size: 13px; }
.currency-val { color: #4A3F55; }

.banner {
  margin: 14px 16px 0;
  background: linear-gradient(135deg, #FFE5A0, #FFD5E5);
  border-radius: 18px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.banner-emoji { font-size: 38px; flex-shrink: 0; }
.banner-text { flex: 1; }
.banner-title { font-size: 14px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; }
.banner-desc { font-size: 12px; color: #7A6B8A; }
.banner-btn {
  background: #4A3F55;
  color: #fff;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.tabs {
  display: flex;
  gap: 6px;
  padding: 14px 16px 0;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 8px 4px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid #EDE4FF;
  background: #fff;
  color: #7A6B8A;
  transition: all 0.2s;
  white-space: nowrap;
}
.tab.active {
  background: #D5AAFF;
  color: #fff;
  border-color: #D5AAFF;
}

.content {
  padding: 14px 16px 80px;
  height: calc(100vh - 400px);
}

.section { margin-bottom: 20px; }
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.section-title {
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4A3F55;
}
.section-title::before {
  content: '';
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, #FFB3C6, #D5AAFF);
  border-radius: 2px;
}
.countdown { font-size: 11px; color: #FF6B6B; }
.countdown-val { font-weight: 700; }

.deal-card {
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(180,150,200,0.06);
  border: 1px solid #F5EEF8;
}
.deal-left { text-align: center; }
.deal-emoji { font-size: 34px; margin-bottom: 4px; display: block; }
.deal-name { font-size: 10px; font-weight: 600; color: #7A6B8A; }
.deal-right { flex: 1; }
.deal-title { font-size: 14px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; display: block; }
.deal-desc { font-size: 11px; color: #7A6B8A; margin-bottom: 4px; display: block; }
.deal-price { display: flex; align-items: center; gap: 6px; margin-bottom: 2px; }
.deal-price-current { font-size: 16px; font-weight: 700; color: #FF6B6B; }
.deal-price-old { font-size: 11px; color: #A898B8; text-decoration: line-through; }
.deal-countdown { font-size: 11px; color: #7A6B8A; }
.deal-buy {
  padding: 8px 14px;
  border-radius: 12px;
  background: #FF6B6B;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
.item-card {
  background: #fff;
  border-radius: 18px;
  padding: 14px 12px;
  text-align: center;
  border: 1px solid #F5EEF8;
  box-shadow: 0 2px 8px rgba(180,150,200,0.06);
  position: relative;
  overflow: hidden;
  transition: all 0.2s;
}
.item-card:active { transform: scale(0.97); }
.item-card.hot::before { content: '🔥'; position: absolute; top: 5px; left: 5px; font-size: 11px; }
.item-card.new::before { content: '🆕'; position: absolute; top: 5px; left: 5px; font-size: 10px; background: #FF6B6B; color: #fff; padding: 1px 4px; border-radius: 5px; }
.item-card.owned { border-color: #B8F1CC; }
.item-card.owned::after { content: '✓'; position: absolute; top: 5px; left: 5px; width: 16px; height: 16px; background: #B8F1CC; color: #fff; border-radius: 50%; font-size: 9px; display: flex; align-items: center; justify-content: center; }
.item-card.locked { opacity: 0.65; }
.item-emoji { font-size: 38px; margin-bottom: 6px; display: block; }
.item-name { font-size: 13px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; display: block; }
.item-desc { font-size: 10px; color: #7A6B8A; margin-bottom: 6px; line-height: 1.4; min-height: 26px; display: block; }
.item-price { display: flex; align-items: center; justify-content: center; gap: 3px; font-size: 14px; font-weight: 700; margin-bottom: 6px; }
.price-icon { font-size: 13px; }
.price-num { color: #4A3F55; }
.item-buy-btn {
  width: 100%;
  padding: 7px;
  border-radius: 10px;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}
.item-buy-btn.owned { background: #B8F1CC; color: #2D8A4E; }
.item-buy-btn.locked { background: #E0E0E0; color: #A898B8; cursor: not-allowed; }

.pack-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;
}
.pack-scroll::-webkit-scrollbar { display: none; }
.pack-card {
  min-width: 200px;
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  text-align: center;
  border: 2px solid transparent;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(180,150,200,0.07);
}
.pack-card.featured { border-color: #FFD166; background: linear-gradient(180deg, #FFFBEF 0%, #fff 60%); }
.pack-emoji { font-size: 38px; margin-bottom: 4px; display: block; }
.pack-name { font-size: 13px; font-weight: 700; color: #4A3F55; margin-bottom: 2px; display: block; }
.pack-desc { font-size: 11px; color: #7A6B8A; margin-bottom: 8px; display: block; }
.pack-items { text-align: left; background: #FAFAFA; border-radius: 10px; padding: 8px 10px; margin-bottom: 10px; }
.pack-item { display: flex; align-items: center; gap: 5px; font-size: 11px; padding: 2px 0; }
.pack-item-emoji { font-size: 13px; }
.pack-item-text { color: #7A6B8A; }
.pack-price { font-size: 20px; font-weight: 700; color: #4A3F55; margin-bottom: 8px; }
.old { font-size: 11px; color: #A898B8; text-decoration: line-through; margin-left: 4px; }
.pack-buy-btn {
  width: 100%;
  padding: 8px;
  border-radius: 12px;
  background: linear-gradient(135deg, #FFD166, #FFB3C6);
  color: #4A3F55;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.luckydraw-card {
  background: linear-gradient(135deg, #FFE5A0, #FFD5E5);
  border-radius: 18px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 8px;
}
.luckydraw-emoji { font-size: 48px; flex-shrink: 0; }
.luckydraw-text { flex: 1; }
.luckydraw-title { font-size: 15px; font-weight: 700; color: #4A3F55; display: block; margin-bottom: 2px; }
.luckydraw-desc { font-size: 12px; color: #7A6B8A; display: block; }
.luckydraw-btn {
  background: #4A3F55;
  color: #fff;
  padding: 9px 16px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  cursor: pointer;
}
.draw-times { font-size: 11px; color: #A898B8; display: block; text-align: center; }
.times-val { color: #FF6B6B; font-weight: 700; }
</style>
