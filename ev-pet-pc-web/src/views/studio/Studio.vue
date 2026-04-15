<template>
  <div class="studio-page">
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="preview-card card">
          <h3>装扮预览</h3>
          <div class="pet-preview">
            <span class="pet-emoji">{{ petEmoji }}</span>
            <div class="outfit-tags">
              <el-tag v-for="item in equippedItems" :key="item.id" size="small" closable @close="unequip(item)">
                {{ item.name }}
              </el-tag>
              <span v-if="equippedItems.length === 0" class="no-outfit">暂无装扮</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :span="12">
        <div class="wardrobe-card card">
          <h3>我的衣柜</h3>
          <el-tabs v-model="category">
            <el-tab-pane label="服装" name="clothing" />
            <el-tab-pane label="配饰" name="accessory" />
            <el-tab-pane label="背景" name="bg" />
          </el-tabs>
          
          <div class="outfit-grid">
            <div v-for="item in wardrobeItems" :key="item.id" class="outfit-item" :class="{ equipped: item.equipped }" @click="toggleEquip(item)">
              <div class="outfit-icon">{{ getIcon(item.category) }}</div>
              <span class="outfit-name">{{ item.name }}</span>
              <span v-if="item.equipped" class="equipped-badge">已穿戴</span>
            </div>
          </div>
          
          <div v-if="hasChanges" class="actions">
            <el-button type="primary" @click="saveOutfit">保存装扮</el-button>
            <el-button @click="resetOutfit">重置</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore } from '../../store'

const petStore = usePetStore()
const category = ref('clothing')
const hasChanges = ref(false)

const petEmoji = computed(() => {
  const stage = petStore.petInfo?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🐦'
})

const wardrobeItems = ref([
  { id: 1, name: '粉色连衣裙', category: 'clothing', equipped: false },
  { id: 2, name: '蓝色衬衫', category: 'clothing', equipped: false },
  { id: 3, name: '小帽子', category: 'accessory', equipped: false },
  { id: 4, name: '蝴蝶结', category: 'accessory', equipped: false },
  { id: 5, name: '粉色背景', category: 'bg', equipped: false },
  { id: 6, name: '星空背景', category: 'bg', equipped: false }
])

const equippedItems = computed(() => wardrobeItems.value.filter(i => i.equipped))

const getIcon = (cat) => {
  const map = { clothing: '👗', accessory: '🎀', bg: '🖼️' }
  return map[cat] || '📦'
}

const toggleEquip = (item) => {
  if (item.category === category.value) {
    // 同类别只能穿一件
    wardrobeItems.value
      .filter(i => i.category === item.category)
      .forEach(i => i.equipped = false)
    item.equipped = true
    hasChanges.value = true
  }
}

const unequip = (item) => {
  item.equipped = false
  hasChanges.value = true
}

const saveOutfit = () => {
  ElMessage.success('装扮已保存')
  hasChanges.value = false
}

const resetOutfit = () => {
  wardrobeItems.value.forEach(i => i.equipped = false)
  hasChanges.value = false
}
</script>

<style scoped>
.studio-page { padding: 0; }

.preview-card {
  text-align: center;
  padding: 32px;
}

.preview-card h3 { margin-bottom: 24px; }

.pet-preview {
  padding: 40px;
  background: linear-gradient(135deg, #FFD5E5 0%, #D5AAFF 100%);
  border-radius: var(--radius-lg);
}

.pet-emoji {
  font-size: 120px;
  display: block;
  margin-bottom: 24px;
}

.outfit-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.no-outfit {
  color: rgba(255,255,255,0.7);
  font-size: 14px;
}

.wardrobe-card h3 { margin-bottom: 16px; }

.outfit-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.outfit-item {
  padding: 20px;
  background: var(--bg-page);
  border-radius: var(--radius-md);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.outfit-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.outfit-item.equipped {
  background: linear-gradient(135deg, rgba(213, 170, 255, 0.3) 0%, rgba(255, 179, 198, 0.3) 100%);
  border: 2px solid var(--primary);
}

.outfit-icon {
  font-size: 40px;
  margin-bottom: 8px;
}

.outfit-name {
  font-size: 14px;
}

.equipped-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: var(--primary);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
}

.actions {
  margin-top: 24px;
  text-align: center;
}
</style>
