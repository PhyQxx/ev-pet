<template>
  <view class="backpack-container">
    <!-- Header -->
    <view class="bp-header">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="header-title">🎒 我的背包</view>
      <view class="header-count">共 {{ items.length }} 件</view>
    </view>

    <!-- Category Tabs -->
    <view class="category-tabs">
      <view
        v-for="cat in categories"
        :key="cat.key"
        class="tab-item"
        :class="{ active: activeCategory === cat.key }"
        @click="activeCategory = cat.key"
      >
        <text class="tab-icon">{{ cat.icon }}</text>
        <text class="tab-text">{{ cat.label }}</text>
      </view>
    </view>

    <!-- Items Grid -->
    <view class="items-grid" v-if="filteredItems.length > 0">
      <view
        v-for="item in filteredItems"
        :key="item.id"
        class="item-card"
        @click="selectItem(item)"
      >
        <view class="item-icon">{{ item.icon || '🎁' }}</view>
        <view class="item-name">{{ item.name }}</view>
        <view class="item-qty">x{{ item.quantity || 1 }}</view>
        <view class="item-desc">{{ item.description || '' }}</view>
        <view class="item-actions" v-if="item.usable">
          <view class="use-btn" @click.stop="useItem(item)">使用</view>
        </view>
      </view>
    </view>

    <!-- Empty State -->
    <view class="empty-state" v-else>
      <text class="empty-icon">🎒</text>
      <text class="empty-text">背包空空如也</text>
      <text class="empty-hint">去商店逛逛吧~</text>
      <view class="shop-btn" @click="goShop">去商店</view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { shop as shopApi, getUserInfo } from '@/utils/api.js'

const items = ref([])
const activeCategory = ref('all')

const categories = [
  { key: 'all', label: '全部', icon: '📦' },
  { key: 'food', label: '食物', icon: '🍖' },
  { key: 'toy', label: '玩具', icon: '🎾' },
  { key: 'decoration', label: '装饰', icon: '🎀' },
  { key: 'gift', label: '礼包', icon: '🎁' }
]

const filteredItems = computed(() => {
  if (activeCategory.value === 'all') return items.value
  return items.value.filter(item => item.category === activeCategory.value)
})

const loadBackpack = async () => {
  try {
    const res = await shopApi.getBackpack()
    if (Array.isArray(res)) {
      items.value = res
    } else if (res?.items) {
      items.value = res.items
    } else if (res?.list) {
      items.value = res.list
    }
  } catch (e) {
    console.error('加载背包失败', e)
  }
}

const selectItem = (item) => {
  uni.showModal({
    title: item.icon + ' ' + item.name,
    content: item.description || '暂无描述',
    showCancel: false
  })
}

const useItem = async (item) => {
  try {
    await shopApi.use(item.itemId || item.id)
    uni.showToast({ title: '使用成功', icon: 'success' })
    loadBackpack()
  } catch (e) {
    uni.showToast({ title: '使用失败', icon: 'none' })
  }
}

const goBack = () => {
  uni.navigateBack()
}

const goShop = () => {
  uni.switchTab({ url: '/pages/shop/shop' })
}

onMounted(() => {
  loadBackpack()
})
</script>

<style scoped>
.backpack-container {
  min-height: 100vh;
  background: #F8F4FF;
  padding-bottom: 40rpx;
}

.bp-header {
  background: linear-gradient(135deg, #EDE4FF, #D5AAFF);
  padding: 20rpx 24rpx 24rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.back-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 28rpx;
  color: #4A3F55;
}

.header-title {
  font-size: 32rpx;
  font-weight: 800;
  color: #2C2040;
  flex: 1;
}

.header-count {
  font-size: 24rpx;
  color: #6B5B8A;
}

.category-tabs {
  display: flex;
  padding: 20rpx 24rpx;
  gap: 12rpx;
  overflow-x: auto;
  white-space: nowrap;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 20rpx;
  border-radius: 24rpx;
  background: #fff;
  font-size: 24rpx;
  color: #7A6B8A;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}

.tab-item.active {
  background: #D5AAFF;
  color: #fff;
  font-weight: 600;
}

.tab-icon {
  font-size: 24rpx;
}

.items-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 20rpx;
  gap: 16rpx;
}

.item-card {
  width: calc(50% - 8rpx);
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}

.item-icon {
  font-size: 56rpx;
  margin-bottom: 8rpx;
}

.item-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #2C2040;
  text-align: center;
}

.item-qty {
  font-size: 22rpx;
  color: #B8A0CC;
  margin-top: 4rpx;
}

.item-desc {
  font-size: 20rpx;
  color: #999;
  text-align: center;
  margin-top: 6rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-actions {
  margin-top: 12rpx;
  width: 100%;
}

.use-btn {
  background: linear-gradient(135deg, #FFB3C6, #FF8FAB);
  color: #fff;
  font-size: 24rpx;
  font-weight: 600;
  text-align: center;
  padding: 10rpx 0;
  border-radius: 12rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
}

.empty-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #4A3F55;
}

.empty-hint {
  font-size: 24rpx;
  color: #B8A0CC;
  margin-top: 8rpx;
}

.shop-btn {
  margin-top: 32rpx;
  background: linear-gradient(135deg, #FFB3C6, #D5AAFF);
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  padding: 16rpx 48rpx;
  border-radius: 24rpx;
}
</style>
