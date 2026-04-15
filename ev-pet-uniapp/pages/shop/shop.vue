<template>
  <view class="shop-container">
    <view class="shop-header">
      <view class="shop-title">宠物商店</view>
      <view class="gold-display">
        <text class="gold-icon">🪙</text>
        <text class="gold-num">{{ userInfo?.gold || 0 }}</text>
      </view>
    </view>
    
    <!-- 分类标签 -->
    <scroll-view class="category-tabs" scroll-x>
      <view class="tab" :class="{ active: category === '' }" @click="switchCategory('')">全部</view>
      <view class="tab" :class="{ active: category === 'food' }" @click="switchCategory('food')">食品</view>
      <view class="tab" :class="{ active: category === 'clothing' }" @click="switchCategory('clothing')">服装</view>
      <view class="tab" :class="{ active: category === 'accessory' }" @click="switchCategory('accessory')">配饰</view>
      <view class="tab" :class="{ active: category === 'bg' }" @click="switchCategory('bg')">背景</view>
    </scroll-view>
    
    <!-- 商品列表 -->
    <scroll-view class="items-grid" scroll-y>
      <view class="item-card" v-for="item in items" :key="item.id" @click="buyItem(item)">
        <view class="item-icon">
          <text>{{ getItemEmoji(item.category) }}</text>
        </view>
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text class="item-desc">{{ item.description }}</text>
        </view>
        <view class="item-price">
          <text class="price-icon">🪙</text>
          <text class="price-num">{{ item.price }}</text>
        </view>
      </view>
      
      <view class="empty-tip" v-if="items.length === 0">
        <text>暂无商品</text>
      </view>
    </scroll-view>
    
    <!-- 底部导航已由tabBar提供 -->
  </view>
</template>

<script>
import { shop as shopApi, getUserInfo } from '../../utils/api.js'

export default {
  data() {
    return {
      category: '',
      items: [],
      userInfo: null
    }
  },
  onLoad() {
    this.userInfo = getUserInfo()
    this.loadItems()
  },
  methods: {
    loadItems() {
      shopApi.getItems(this.category)
        .then(items => {
          this.items = items || []
        })
        .catch(err => {
          console.error('Failed to load items:', err)
        })
    },
    switchCategory(cat) {
      this.category = cat
      this.loadItems()
    },
    getItemEmoji(category) {
      const map = {
        'food': '🍖',
        'clothing': '👗',
        'accessory': '🎀',
        'bg': '🖼️'
      }
      return map[category] || '📦'
    },
    buyItem(item) {
      uni.showModal({
        title: '购买确认',
        content: `确定购买 ${item.name} 吗？需要 ${item.price} 金币`,
        success: (res) => {
          if (res.confirm) {
            shopApi.buy(item.id)
              .then(() => {
                uni.showToast({ title: '购买成功', icon: 'success' })
                this.loadItems()
              })
              .catch(err => {
                uni.showToast({ title: err.message || '购买失败', icon: 'none' })
              })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.shop-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFF4C2 0%, #FFD5E5 100%);
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx;
}

.shop-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #4A3F55;
}

.gold-display {
  display: flex;
  align-items: center;
  background: #FFFFFF;
  padding: 16rpx 30rpx;
  border-radius: 30rpx;
  box-shadow: 0 5rpx 20rpx rgba(0, 0, 0, 0.1);
}

.gold-icon {
  font-size: 36rpx;
}

.gold-num {
  font-size: 32rpx;
  font-weight: 600;
  color: #4A3F55;
  margin-left: 10rpx;
}

.category-tabs {
  white-space: nowrap;
  padding: 0 30rpx 30rpx;
}

.tab {
  display: inline-block;
  padding: 16rpx 36rpx;
  background: #FFFFFF;
  border-radius: 30rpx;
  margin-right: 20rpx;
  font-size: 28rpx;
  color: #7A6B8A;
}

.tab.active {
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  color: #FFFFFF;
}

.items-grid {
  padding: 0 30rpx 30rpx;
  height: calc(100vh - 300rpx);
}

.item-card {
  background: #FFFFFF;
  border-radius: 30rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.05);
}

.item-icon {
  width: 100rpx;
  height: 100rpx;
  background: #FFF4C2;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50rpx;
}

.item-info {
  flex: 1;
  margin-left: 24rpx;
}

.item-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #4A3F55;
}

.item-desc {
  font-size: 24rpx;
  color: #A898B8;
  margin-top: 6rpx;
}

.item-price {
  display: flex;
  align-items: center;
  background: #FFF4C2;
  padding: 10rpx 20rpx;
  border-radius: 20rpx;
}

.price-icon {
  font-size: 28rpx;
}

.price-num {
  font-size: 28rpx;
  font-weight: 600;
  color: #4A3F55;
  margin-left: 6rpx;
}

.empty-tip {
  text-align: center;
  padding: 100rpx;
  color: #A898B8;
  font-size: 28rpx;
}
</style>
