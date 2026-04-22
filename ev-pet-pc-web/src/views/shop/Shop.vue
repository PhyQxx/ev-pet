<template>
  <div class="shop-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">商店</h1>
        <p class="page-subtitle">用金币和钻石购买食物、服装、道具，装扮你的宠物</p>
      </div>
    </div>

    <!-- Currency Bar -->
    <div class="currency-bar">
      <div class="currency-card">
        <div class="currency-icon">🪙</div>
        <div class="currency-info">
          <div class="currency-name">金币</div>
          <div class="currency-value">{{ userInfo?.gold?.toLocaleString() || '12,580' }}</div>
        </div>
      </div>
      <div class="currency-card">
        <div class="currency-icon">💎</div>
        <div class="currency-info">
          <div class="currency-name">钻石</div>
          <div class="currency-value">{{ userInfo?.diamond || 328 }}</div>
        </div>
      </div>
      <div class="currency-card">
        <div class="currency-icon">🎫</div>
        <div class="currency-info">
          <div class="currency-name">代币券</div>
          <div class="currency-value">{{ userInfo?.coupon || 5 }}</div>
        </div>
      </div>
    </div>

    <!-- Banner -->
    <div class="banner">
      <div class="banner-emoji">🎁</div>
      <div class="banner-text">
        <h2>新人专属礼包</h2>
        <p>仅需 ¥6.8，即可获得 500 钻石 + 限定服装「星之魔法使」</p>
      </div>
      <button class="banner-btn">立即获取 →</button>
    </div>

    <!-- Daily Deals -->
    <div class="deal-section">
      <div class="deal-card">
        <div class="deal-left">
          <div class="deal-emoji">🍰</div>
          <div class="deal-name">限定甜品</div>
        </div>
        <div class="deal-right">
          <div class="deal-title">豪华蛋糕大餐</div>
          <div class="deal-desc">恢复饱食度 +80，心情 +50</div>
          <div class="deal-price">
            <span class="deal-price-current">🪙 280</span>
            <span class="deal-price-old">🪙 450</span>
          </div>
          <div class="deal-countdown">剩余 <span>{{ countdown }}</span> 自动刷新</div>
        </div>
        <button class="deal-buy" @click="buyDealItem(1, 280, '🪙')">购买</button>
      </div>
      <div class="deal-card">
        <div class="deal-left">
          <div class="deal-emoji">🎀</div>
          <div class="deal-name">今日配饰</div>
        </div>
        <div class="deal-right">
          <div class="deal-title">樱花发卡</div>
          <div class="deal-desc">限定配饰，先到先得</div>
          <div class="deal-price">
            <span class="deal-price-current">💎 50</span>
            <span class="deal-price-old">💎 120</span>
          </div>
          <div class="deal-countdown">剩余 <span>{{ countdown }}</span></div>
        </div>
        <button class="deal-buy" @click="buyDealItem(2, 50, '💎')">购买</button>
      </div>
    </div>

    <!-- Packages -->
    <div class="section-title">特惠礼包</div>
    <div class="pack-grid">
      <div class="pack-card">
        <div class="pack-emoji">🍖</div>
        <div class="pack-name">饱食补给包</div>
        <div class="pack-desc">3天无限量投喂</div>
        <div class="pack-items">
          <div class="pack-item"><span class="pack-item-emoji">🍗</span> 高级猫粮 ×30</div>
          <div class="pack-item"><span class="pack-item-emoji">🪙</span> 金币 ×500</div>
          <div class="pack-item"><span class="pack-item-emoji">⭐</span> 经验 ×200</div>
        </div>
        <div class="pack-price">¥6.8</div>
        <button class="pack-buy-btn">购买</button>
      </div>
      <div class="pack-card featured">
        <div class="pack-emoji">👑</div>
        <div class="pack-name">豪华装扮礼包</div>
        <div class="pack-desc">含限定服装 + 头像框</div>
        <div class="pack-items">
          <div class="pack-item"><span class="pack-item-emoji">👗</span> 小王子服 ×1</div>
          <div class="pack-item"><span class="pack-item-emoji">🎀</span> 星光冠 ×1</div>
          <div class="pack-item"><span class="pack-item-emoji">🖼️</span> 限定头像框</div>
          <div class="pack-item"><span class="pack-item-emoji">💎</span> 钻石 ×200</div>
        </div>
        <div class="pack-price">¥18.8 <span class="old">¥38</span></div>
        <button class="pack-buy-btn">购买</button>
      </div>
      <div class="pack-card premium">
        <div class="pack-emoji">🚀</div>
        <div class="pack-name">月卡 · 急速成长</div>
        <div class="pack-desc">每日领取 + 经验加成 30%</div>
        <div class="pack-items">
          <div class="pack-item"><span class="pack-item-emoji">🪙</span> 每日金币 ×200</div>
          <div class="pack-item"><span class="pack-item-emoji">🍗</span> 每日猫粮 ×5</div>
          <div class="pack-item"><span class="pack-item-emoji">✨</span> 经验 +30%</div>
          <div class="pack-item"><span class="pack-item-emoji">🛡️</span> 无广告</div>
        </div>
        <div class="pack-price">¥30 <span class="old">¥50</span></div>
        <button class="pack-buy-btn">购买</button>
      </div>
    </div>

    <!-- Category Tabs -->
    <div class="tabs">
      <div class="tab" :class="{ active: category === '' }" @click="changeCategory('')">🍖 全部</div>
      <div class="tab" :class="{ active: category === 'food' }" @click="changeCategory('food')">🍖 食物</div>
      <div class="tab" :class="{ active: category === 'clothing' }" @click="changeCategory('clothing')">👗 服装</div>
      <div class="tab" :class="{ active: category === 'accessory' }" @click="changeCategory('accessory')">🎀 配饰</div>
      <div class="tab" :class="{ active: category === 'decoration' }" @click="changeCategory('decoration')">✨ 特效</div>
      <div class="tab" :class="{ active: category === 'consumable' }" @click="changeCategory('consumable')">🧴 消耗品</div>
    </div>

    <!-- Item Grid -->
    <div class="item-grid">
      <div
        v-for="item in items"
        :key="item.id"
        class="item-card"
        :class="{ owned: item.owned, hot: item.hot, new: item.new }"
        @click="buyItem(item)"
      >
        <div class="item-emoji">{{ getIcon(item.category) }}</div>
        <div class="item-name">{{ item.name }}</div>
        <div class="item-desc">{{ item.description }}</div>
        <div class="item-price">
          <span class="item-price-icon">🪙</span>
          {{ item.price }}
        </div>
        <button class="item-buy-btn">{{ item.owned ? '已拥有' : '购买' }}</button>
      </div>
    </div>

    <el-empty v-if="items.length === 0" description="暂无商品" />

    <!-- Bottom Banner -->
    <div class="bottom-banner">
      <div class="bottom-banner-emoji">🎰</div>
      <div>
        <h3>幸运转盘</h3>
        <p>每日免费抽奖，限定时装等你拿</p>
      </div>
      <button class="bottom-banner-btn">立即抽奖 →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store'
import { shop as shopApi } from '../../api'

const userStore = useUserStore()
const category = ref('')
const items = ref([])
const countdown = ref('23:41')

const userInfo = computed(() => userStore.userInfo)

onMounted(() => {
  loadItems()
  startCountdown()
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

let countdownTimer = null

const startCountdown = () => {
  countdownTimer = setInterval(() => {
    const now = new Date()
    const midnight = new Date()
    midnight.setHours(24, 0, 0, 0)
    const diff = midnight - now
    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    countdown.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`
  }, 1000)
}

const loadItems = async () => {
  try {
    const data = await shopApi.getItems(category.value)
    items.value = data.length ? data : getMockItems()
  } catch (err) {
    items.value = getMockItems()
  }
}

const getMockItems = () => [
  { id: 1, name: '黄金猫粮', description: '恢复饱食 +40，心情 +10', price: 80, category: 'food', owned: false, hot: true, new: false },
  { id: 2, name: '鲜美三文鱼', description: '恢复饱食 +55，心情 +20', price: 150, category: 'food', owned: false, hot: false, new: true },
  { id: 3, name: '限定甜品盒', description: '恢复饱食 +30，经验 +30', price: 120, category: 'food', owned: false, hot: false, new: false },
  { id: 4, name: '小黄鸭蛋糕', description: '恢复饱食 +35，心情 +15', price: 60, category: 'food', owned: true, hot: false, new: false },
  { id: 5, name: '小王子服', description: '稀有服装，尊贵象征', price: 500, category: 'clothing', owned: false, hot: true, new: false },
  { id: 6, name: '粉色蓬蓬裙', description: '可爱蓬蓬裙，少女心满满', price: 280, category: 'clothing', owned: false, hot: false, new: true },
  { id: 7, name: '星光皇冠', description: '限定发饰，闪耀全场', price: 380, category: 'accessory', owned: false, hot: false, new: true },
  { id: 8, name: '樱花发卡', description: '春季限定，浪漫樱花', price: 120, category: 'accessory', owned: false, hot: true, new: false }
]

const changeCategory = (cat) => {
  category.value = cat
  loadItems()
}

const getIcon = (cat) => {
  const map = { food: '🍖', clothing: '👗', accessory: '🎀', decoration: '✨', consumable: '🧴' }
  return map[cat] || '📦'
}

const buyItem = async (item) => {
  if (item.owned) {
    ElMessage.info('您已拥有此商品')
    return
  }
  ElMessageBox.confirm(`确定购买 ${item.name} 吗？需要 ${item.price} 金币`, '购买确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await shopApi.buy(item.id)
      ElMessage.success('购买成功')
      item.owned = true
      loadItems()
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '购买失败')
    }
  }).catch(() => {})
}

const buyDealItem = (id, price, currency) => {
  ElMessageBox.confirm(`确定购买此商品吗？需要 ${currency} ${price}`, '购买确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await shopApi.buy(id)
      ElMessage.success('购买成功')
    } catch (err) {
      ElMessage.error('购买失败，金币或钻石不足')
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.shop-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
  color: $ev-text;
}

.page-subtitle {
  font-size: 14px;
  color: $ev-text-sec;
}

/* Currency Bar */
.currency-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.currency-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid $ev-bg;
  box-shadow: 0 2px 8px rgba(180, 150, 200, 0.07);
}

.currency-icon {
  font-size: 22px;
}

.currency-name {
  font-size: 11px;
  color: $ev-text-sec;
}

.currency-value {
  font-size: 18px;
  font-weight: 700;
  color: $ev-text;
}

/* Banner */
.banner {
  background: $ev-gradient-banner;
  border-radius: 20px;
  padding: 28px 32px;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  overflow: hidden;
  color: #fff;
}

.banner::before {
  content: '';
  position: absolute;
  right: -20px;
  top: -20px;
  width: 140px;
  height: 140px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.banner-text {
  flex: 1;
  position: relative;
  z-index: 1;
}

.banner-emoji {
  font-size: 64px;
  position: relative;
  z-index: 1;
}

.banner h2 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}

.banner p {
  font-size: 13px;
  opacity: 0.85;
}

.banner-btn {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #fff;
  padding: 10px 22px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  font-family: inherit;
}

.banner-btn:hover {
  background: #fff;
  color: $ev-purple;
}

/* Section Title */
.section-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;

  &::before {
    content: '';
    width: 4px;
    height: 18px;
    background: $ev-gradient-primary;
    border-radius: 2px;
  }
}

/* Daily Deals */
.deal-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 28px;
}

.deal-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid $ev-bg;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  transition: all 0.2s;
  cursor: pointer;
}

.deal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(180, 150, 200, 0.13);
}

.deal-left {
  text-align: center;
}

.deal-emoji {
  font-size: 40px;
  margin-bottom: 4px;
}

.deal-name {
  font-size: 12px;
  font-weight: 600;
  color: $ev-text;
}

.deal-right {
  flex: 1;
}

.deal-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 4px;
  color: $ev-text;
}

.deal-desc {
  font-size: 11px;
  color: $ev-text-sec;
  margin-bottom: 8px;
}

.deal-price {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.deal-price-current {
  font-size: 18px;
  font-weight: 700;
  color: #FF6B6B;
}

.deal-price-old {
  font-size: 12px;
  color: $ev-text-muted;
  text-decoration: line-through;
}

.deal-countdown {
  font-size: 11px;
  color: $ev-text-sec;

  span {
    color: #FF6B6B;
    font-weight: 700;
  }
}

.deal-buy {
  padding: 7px 16px;
  border-radius: 10px;
  background: #FF6B6B;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  font-family: inherit;
  transition: all 0.2s;

  &:hover {
    background: darken(#FF6B6B, 8%);
  }
}

/* Packages */
.pack-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-bottom: 28px;
}

.pack-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  text-align: center;
  border: 2px solid transparent;
  transition: all 0.2s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.pack-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(180, 150, 200, 0.15);
}

.pack-card.featured {
  border-color: $ev-yellow-dark;
  background: linear-gradient(180deg, #FFFBEF 0%, #fff 60%);
}

.pack-card.featured::before {
  content: '⭐ 推荐';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: $ev-yellow-dark;
  color: $ev-text;
  font-size: 11px;
  font-weight: 700;
  padding: 5px;
}

.pack-card.premium {
  border-color: $ev-purple;
  background: linear-gradient(180deg, #F5F0FF 0%, #fff 60%);
}

.pack-emoji {
  font-size: 48px;
  margin-bottom: 10px;
  margin-top: 8px;
}

.pack-name {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
  color: $ev-text;
}

.pack-desc {
  font-size: 12px;
  color: $ev-text-sec;
  margin-bottom: 12px;
}

.pack-items {
  text-align: left;
  background: #FAFAFA;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 14px;
}

.pack-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding: 3px 0;
  color: $ev-text;
}

.pack-item-emoji {
  font-size: 14px;
}

.pack-price {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 12px;
  color: $ev-text;

  .old {
    font-size: 13px;
    color: $ev-text-muted;
    text-decoration: line-through;
    margin-left: 6px;
  }
}

.pack-buy-btn {
  width: 100%;
  padding: 10px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  font-family: inherit;
}

.pack-card.featured .pack-buy-btn {
  background: linear-gradient(135deg, $ev-yellow-dark, $ev-primary);
  color: $ev-text;
}

.pack-card:not(.featured):not(.premium) .pack-buy-btn {
  background: $ev-purple;
  color: #fff;
}

.pack-card.premium .pack-buy-btn {
  background: $ev-gradient-primary;
  color: #fff;
}

/* Category Tabs */
.tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab {
  padding: 8px 18px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: $ev-text-sec;
  transition: all 0.2s;

  &:hover {
    border-color: $ev-purple;
    color: $ev-purple;
  }
}

.tab.active {
  background: $ev-purple;
  color: #fff;
  border-color: $ev-purple;
  font-weight: 700;
}

/* Item Grid */
.item-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 32px;
}

.item-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px 16px;
  text-align: center;
  border: 1px solid $ev-bg;
  box-shadow: 0 2px 10px rgba(180, 150, 200, 0.07);
  transition: all 0.2s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(180, 150, 200, 0.14);
  border-color: $ev-purple;
}

.item-card.hot::before {
  content: '🔥';
  position: absolute;
  top: 8px;
  left: 8px;
  font-size: 14px;
}

.item-card.new::before {
  content: '✨';
  position: absolute;
  top: 8px;
  left: 8px;
  font-size: 14px;
}

.item-emoji {
  font-size: 44px;
  margin-bottom: 10px;
}

.item-name {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 4px;
  color: $ev-text;
}

.item-desc {
  font-size: 11px;
  color: $ev-text-sec;
  margin-bottom: 8px;
  line-height: 1.4;
}

.item-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 8px;
  color: $ev-text;
}

.item-price-icon {
  font-size: 14px;
}

.item-buy-btn {
  width: 100%;
  padding: 8px;
  border-radius: 12px;
  background: $ev-gradient-primary;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 4px 14px rgba($ev-purple, 0.3);
  }
}

.item-card.owned .item-buy-btn {
  background: $ev-green;
  color: #2D8A4E;
}

/* Bottom Banner */
.bottom-banner {
  background: linear-gradient(135deg, $ev-yellow 0%, $ev-primary-light 100%);
  border-radius: 20px;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: $ev-text;
  margin-bottom: 28px;
}

.bottom-banner-emoji {
  font-size: 48px;
}

.bottom-banner h3 {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 2px;
}

.bottom-banner p {
  font-size: 13px;
  opacity: 0.8;
}

.bottom-banner-btn {
  margin-left: auto;
  background: $ev-text;
  color: #fff;
  padding: 10px 22px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  font-family: inherit;
  transition: all 0.2s;

  &:hover {
    background: lighten($ev-text, 10%);
  }
}
</style>
