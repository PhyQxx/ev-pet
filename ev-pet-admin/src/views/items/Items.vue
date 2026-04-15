<template>
  <div class="items-page">
    <div class="toolbar">
      <el-button type="primary" @click="showAddDialog">添加商品</el-button>
      <div class="filter-area">
        <el-select placeholder="商品分类" v-model="categoryFilter" @change="loadItems">
          <el-option label="全部" value="" />
          <el-option label="食品" value="food" />
          <el-option label="服装" value="clothing" />
          <el-option label="配饰" value="accessory" />
          <el-option label="背景" value="bg" />
        </el-select>
      </div>
    </div>
    
    <el-table :data="items" stripe class="items-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="{ row }">
          <div class="item-info-cell">
            <span class="item-icon">{{ getIcon(row.category) }}</span>
            <div class="item-detail">
              <span class="item-name">{{ row.name }}</span>
              <span class="item-desc">{{ row.description }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="分类" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ getCategoryName(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="金币价格" width="120">
        <template #default="{ row }">
          <span class="price">🪙 {{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="rmbPrice" label="RMB价格" width="120">
        <template #default="{ row }">
          <span v-if="row.rmbPrice > 0">¥{{ row.rmbPrice }}</span>
          <span v-else class="free-tag">免费</span>
        </template>
      </el-table-column>
      <el-table-column label="效果" width="150">
        <template #default="{ row }">
          <span class="effect">{{ row.effect || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="editItem(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteItem(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑商品弹窗 -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑商品' : '添加商品'" width="500px">
      <el-form :model="itemForm" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="itemForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="itemForm.category">
            <el-option label="食品" value="food" />
            <el-option label="服装" value="clothing" />
            <el-option label="配饰" value="accessory" />
            <el-option label="背景" value="bg" />
          </el-select>
        </el-form-item>
        <el-form-item label="金币价格">
          <el-input-number v-model="itemForm.price" :min="0" />
        </el-form-item>
        <el-form-item label="RMB价格">
          <el-input-number v-model="itemForm.rmbPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="效果">
          <el-input v-model="itemForm.effect" placeholder='如: {"fullness": 20}' />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="itemForm.icon" placeholder="请输入图标emoji" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const categoryFilter = ref('')
const showDialog = ref(false)
const isEdit = ref(false)

const items = ref([
  { id: 1, name: '小面包', description: '恢复10点饱食度', category: 'food', price: 10, rmbPrice: 0, effect: '{"fullness": 10}', icon: '🍞', status: 1 },
  { id: 2, name: '小饼干', description: '恢复20点饱食度', category: 'food', price: 20, rmbPrice: 0, effect: '{"fullness": 20}', icon: '🍪', status: 1 },
  { id: 3, name: '苹果', description: '恢复30点饱食度', category: 'food', price: 30, rmbPrice: 1, effect: '{"fullness": 30}', icon: '🍎', status: 1 },
  { id: 4, name: '生日蛋糕', description: '恢复50点饱食度+5点心情', category: 'food', price: 100, rmbPrice: 6, effect: '{"fullness": 50, "mood": 5}', icon: '🎂', status: 1 },
  { id: 5, name: '粉色连衣裙', description: '可爱的粉色裙子', category: 'clothing', price: 500, rmbPrice: 30, effect: '{"appearance": "pink_dress"}', icon: '👗', status: 1 },
  { id: 6, name: '小帽子', description: '时尚的小帽子', category: 'accessory', price: 300, rmbPrice: 18, effect: '{"appearance": "hat"}', icon: '🎀', status: 1 }
])

const itemForm = ref({
  id: null,
  name: '',
  description: '',
  category: 'food',
  price: 0,
  rmbPrice: 0,
  effect: '',
  icon: ''
})

const getIcon = (cat) => {
  const map = { food: '🍖', clothing: '👗', accessory: '🎀', bg: '🖼️' }
  return map[cat] || '📦'
}

const getCategoryName = (cat) => {
  const map = { food: '食品', clothing: '服装', accessory: '配饰', bg: '背景' }
  return map[cat] || cat
}

const loadItems = () => {
  console.log('Load items:', categoryFilter.value)
}

const showAddDialog = () => {
  isEdit.value = false
  itemForm.value = { id: null, name: '', description: '', category: 'food', price: 0, rmbPrice: 0, effect: '', icon: '' }
  showDialog.value = true
}

const editItem = (item) => {
  isEdit.value = true
  itemForm.value = { ...item }
  showDialog.value = true
}

const saveItem = () => {
  if (!itemForm.value.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  ElMessage.success(isEdit.value ? '商品已更新' : '商品已添加')
  showDialog.value = false
  loadItems()
}

const deleteItem = (item) => {
  ElMessageBox.confirm(`确定要删除商品 ${item.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('商品已删除')
    loadItems()
  })
}

const toggleStatus = (item) => {
  ElMessage.success(`商品已${item.status === 1 ? '启用' : '禁用'}`)
}

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.items-page { padding: 0; }

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.filter-area .el-select { width: 150px; }

.items-table { background: white; border-radius: 8px; }

.item-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon { font-size: 28px; }

.item-detail {
  display: flex;
  flex-direction: column;
}

.item-name { font-weight: 600; }
.item-desc { font-size: 12px; color: #7A6B8A; }

.price { font-weight: 600; }
.free-tag { color: #67C23A; font-weight: 600; }
.effect { font-size: 12px; color: #7A6B8A; }
</style>
