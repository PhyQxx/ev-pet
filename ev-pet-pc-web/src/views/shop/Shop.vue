<template>
  <div class="shop-page">
    <div class="shop-header card">
      <h2>宠物商店</h2>
      <div class="gold-info">
        <span class="gold-icon">🪙</span>
        <span class="gold-num">{{ userInfo?.gold || 0 }}</span>
      </div>
    </div>
    
    <div class="shop-categories">
      <el-radio-group v-model="category" @change="loadItems">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="food">食品</el-radio-button>
        <el-radio-button label="clothing">服装</el-radio-button>
        <el-radio-button label="accessory">配饰</el-radio-button>
        <el-radio-button label="bg">背景</el-radio-button>
      </el-radio-group>
    </div>
    
    <el-row :gutter="20" class="items-grid">
      <el-col :span="6" v-for="item in items" :key="item.id">
        <div class="item-card card" @click="buyItem(item)">
          <div class="item-icon">{{ getIcon(item.category) }}</div>
          <div class="item-info">
            <h4 class="item-name">{{ item.name }}</h4>
            <p class="item-desc">{{ item.description }}</p>
          </div>
          <div class="item-price">
            <span>🪙</span>
            <span class="price">{{ item.price }}</span>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-empty v-if="items.length === 0" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store'
import { shop as shopApi } from '../../api'

const userStore = useUserStore()
const category = ref('')
const items = ref([])

const userInfo = computed(() => userStore.userInfo)

import { computed } from 'vue'

onMounted(() => {
  loadItems()
})

const loadItems = async () => {
  try {
    items.value = await shopApi.getItems(category.value)
  } catch (err) {
    console.error('Failed to load items:', err)
  }
}

const getIcon = (cat) => {
  const map = { food: '🍖', clothing: '👗', accessory: '🎀', bg: '🖼️' }
  return map[cat] || '📦'
}

const buyItem = async (item) => {
  ElMessageBox.confirm(`确定购买 ${item.name} 吗？需要 ${item.price} 金币`, '购买确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await shopApi.buy(item.id)
      ElMessage.success('购买成功')
      loadItems()
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '购买失败')
    }
  })
}
</script>

<style scoped>
.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.gold-info {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #FFF4C2 0%, #FFE5A0 100%);
  padding: 12px 24px;
  border-radius: 24px;
}

.gold-icon { font-size: 24px; }
.gold-num { font-size: 20px; font-weight: 600; }

.shop-categories {
  margin-bottom: 24px;
}

.items-grid {
  margin-top: 20px;
}

.item-card {
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 24px;
}

.item-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.item-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.item-info {
  margin-bottom: 16px;
}

.item-name {
  font-size: 16px;
  margin-bottom: 8px;
}

.item-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.item-price {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--accent-yellow-light);
  padding: 8px 16px;
  border-radius: 16px;
}

.price {
  font-weight: 600;
}
</style>
