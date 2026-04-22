<template>
  <div class="admin-page">
    <div class="page-title">🛒 商品管理</div>
    <div class="page-sub">管理商店道具、服装、礼包的上架与定价</div>

    <!-- Toolbar -->
    <div class="card" style="margin-bottom:20px;">
      <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;">
        <div style="display:flex;gap:8px;">
          <el-select v-model="categoryFilter" placeholder="全部分类" style="width:130px;" clearable @change="loadItems">
            <el-option label="全部分类" value="" />
            <el-option label="食品" value="food" />
            <el-option label="服装" value="clothing" />
            <el-option label="配饰" value="accessory" />
            <el-option label="特效" value="effect" />
            <el-option label="礼包" value="bundle" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="全部状态" style="width:120px;" clearable @change="loadItems">
            <el-option label="全部状态" value="" />
            <el-option label="上架中" value="1" />
            <el-option label="已下架" value="0" />
          </el-select>
        </div>
        <button class="btn btn-primary" @click="openAddModal">+ 添加商品</button>
      </div>
    </div>

    <!-- Items Table -->
    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>商品</th>
              <th>分类</th>
              <th>价格</th>
              <th>销量</th>
              <th>库存</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredItems" :key="item.id">
              <td>
                <div style="display:flex;align-items:center;gap:8px;">
                  <span style="font-size:22px;">{{ item.icon }}</span>
                  <div>
                    <div style="font-weight:600;">{{ item.name }}</div>
                    <div style="font-size:11px;color:#A898B8;">{{ item.desc }}</div>
                  </div>
                </div>
              </td>
              <td><span :class="['badge', item.categoryBadge]">{{ item.categoryName }}</span></td>
              <td>
                <span v-if="item.currency === 'coin'">🪙 {{ item.price }}</span>
                <span v-else-if="item.currency === 'gem'">💎 {{ item.price }}</span>
                <span v-else style="color:#D4A000;font-weight:600;">¥{{ item.price }}</span>
              </td>
              <td>{{ item.sales.toLocaleString() }}</td>
              <td>
                <span v-if="item.stock === -1" style="color:#A898B8;">无限</span>
                <span v-else-if="item.stock === 0" style="color:#FF6B6B;">售罄</span>
                <span v-else>{{ item.stock }}</span>
              </td>
              <td>
                <span :class="['badge', item.status === 1 ? 'badge-green' : 'badge-red']">
                  {{ item.status === 1 ? '上架' : '下架' }}
                </span>
              </td>
              <td>
                <span class="action-link" @click="openEditModal(item)">编辑</span>
                <span class="action-link" :style="{ color: item.status === 1 ? '#FF6B6B' : '#3BAF5D' }" @click="toggleStatus(item)">
                  {{ item.status === 1 ? '下架' : '上架' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Item Modal -->
    <el-dialog v-model="showModal" :title="isEdit ? '编辑商品' : '+ 添加商品'" width="480px">
      <div class="form-item">
        <div class="form-label">商品名称</div>
        <el-input v-model="itemForm.name" placeholder="输入商品名称" />
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">分类</div>
          <el-select v-model="itemForm.category" style="width:100%;">
            <el-option label="食品" value="food" />
            <el-option label="服装" value="clothing" />
            <el-option label="配饰" value="accessory" />
            <el-option label="特效" value="effect" />
            <el-option label="礼包" value="bundle" />
          </el-select>
        </div>
        <div class="form-item">
          <div class="form-label">货币类型</div>
          <el-select v-model="itemForm.currency" style="width:100%;">
            <el-option label="金币 🪙" value="coin" />
            <el-option label="钻石 💎" value="gem" />
            <el-option label="人民币 ¥" value="rmb" />
          </el-select>
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">价格</div>
          <el-input-number v-model="itemForm.price" :min="0" style="width:100%;" />
        </div>
        <div class="form-item">
          <div class="form-label">库存</div>
          <el-input-number v-model="itemForm.stock" :min="-1" style="width:100%;" placeholder="无限填-1" />
        </div>
      </div>
      <div class="form-item">
        <div class="form-label">商品描述</div>
        <el-input v-model="itemForm.desc" placeholder="商品简短描述" />
      </div>
      <div class="form-item">
        <div class="form-label">图标 Emoji</div>
        <el-input v-model="itemForm.icon" placeholder="如: 🍗 🎀 👗" />
      </div>
      <template #footer>
        <button class="btn" @click="showModal = false">取消</button>
        <button class="btn btn-primary" @click="saveItem">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { itemAdmin } from '@/api/index.js'

const categoryFilter = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const currentItem = ref(null)
const loading = ref(false)

const itemForm = ref({
  name: '',
  category: 'food',
  currency: 'coin',
  price: 0,
  stock: -1,
  desc: '',
  icon: ''
})

const allItems = ref([])
const total = ref(0)

const filteredItems = computed(() => {
  return allItems.value
})

const loadItems = async () => {
  loading.value = true
  try {
    const params = { page: 1, pageSize: 100 }
    if (categoryFilter.value) params.category = categoryFilter.value
    if (statusFilter.value) params.status = parseInt(statusFilter.value)
    const res = await itemAdmin.list(params)
    if (res.code === 200 && res.data) {
      const categoryMap = { food: '食品', clothing: '服装', accessory: '配饰', effect: '特效', bundle: '礼包' }
      const categoryBadgeMap = { food: 'badge-yellow', clothing: 'badge-purple', accessory: 'badge-blue', effect: 'badge-purple', bundle: 'badge-red' }
      const currencyMap = { coin: 'coin', gem: 'gem', rmb: 'rmb' }
      allItems.value = (res.data.list || []).map(item => ({
        id: item.id,
        name: item.name || '',
        icon: item.icon || '📦',
        desc: item.description || '',
        category: item.category || 'food',
        categoryName: categoryMap[item.category] || item.category,
        categoryBadge: categoryBadgeMap[item.category] || 'badge-blue',
        currency: currencyMap[item.category] || 'coin',
        price: item.price || 0,
        sales: 0,
        stock: -1,
        status: item.status !== undefined ? item.status : 1
      }))
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载商品失败', e)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  isEdit.value = false
  itemForm.value = { name: '', category: 'food', currency: 'coin', price: 0, stock: -1, desc: '', icon: '' }
  showModal.value = true
}

const openEditModal = (item) => {
  isEdit.value = true
  currentItem.value = item
  itemForm.value = { ...item }
  showModal.value = true
}

const saveItem = async () => {
  if (!itemForm.value.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  try {
    const data = {
      name: itemForm.value.name,
      category: itemForm.value.category,
      price: itemForm.value.price,
      description: itemForm.value.desc,
      icon: itemForm.value.icon,
      isFree: false,
      status: 1
    }
    let res
    if (isEdit.value) {
      res = await itemAdmin.update(currentItem.value.id, data)
    } else {
      res = await itemAdmin.create(data)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '商品已更新' : '商品已添加')
      showModal.value = false
      loadItems()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const toggleStatus = async (item) => {
  try {
    const res = await itemAdmin.toggleStatus(item.id)
    if (res.code === 200) {
      ElMessage.success(res.data || (item.status === 1 ? '商品已下架' : '商品已上架'))
      loadItems()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.admin-page { display: block; }
</style>
