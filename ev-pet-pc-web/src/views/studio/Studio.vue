<template>
  <div class="studio-page">
    <h1 class="page-title">装扮工作室</h1>
    <p class="page-subtitle">为宠物更换服装、颜色、配件，打造独一无二的伙伴</p>

    <div class="studio-layout">
      <!-- Left: Preview + Items -->
      <div>
        <!-- Pet Preview -->
        <div class="card pet-preview-area">
          <div class="pet-preview-card">
            <div class="pet-preview-avatar">{{ petEmoji }}</div>
            <div class="pet-preview-name">{{ petName }}</div>
            <div class="pet-preview-type">{{ petType }} · {{ petLevel }}</div>
            <div class="pet-tags">
              <span v-for="item in equippedItems" :key="item.id" class="pet-tag equipped">
                {{ item.icon }} {{ item.name }}
              </span>
              <span v-if="equippedItems.length === 0" class="pet-tag">未佩戴配件</span>
            </div>
          </div>
          <div class="preview-buttons">
            <button class="btn-secondary">📷 截图分享</button>
            <button class="btn-secondary btn-reset" @click="resetOutfit">🔄 重置默认</button>
          </div>
        </div>

        <!-- Customize Tabs -->
        <div class="card">
          <div class="tabs">
            <div v-for="tab in tabs" :key="tab.id" class="tab" :class="{ active: activeTab === tab.id }" @click="activeTab = tab.id">
              {{ tab.icon }} {{ tab.label }}
            </div>
          </div>

          <!-- Costumes -->
          <div v-show="activeTab === 'costumes'" class="tab-content">
            <div class="item-grid">
              <div
                v-for="item in costumes"
                :key="item.id"
                class="item-card"
                :class="{ equipped: item.equipped, locked: !item.unlocked }"
                @click="toggleEquip(item, 'costumes')"
              >
                <div v-if="!item.unlocked" class="lock-overlay">🔒</div>
                <div class="item-emoji">{{ item.icon }}</div>
                <div class="item-name">{{ item.name }}</div>
                <div class="item-rarity" :class="'rarity-' + item.rarityType">{{ item.rarity }}</div>
                <button class="item-action-btn">
                  {{ item.equipped ? '已穿戴 ✓' : (!item.unlocked ? '🔒 解锁' : '穿戴') }}
                </button>
              </div>
            </div>
          </div>

          <!-- Hats -->
          <div v-show="activeTab === 'hats'" class="tab-content">
            <div class="item-grid">
              <div
                v-for="item in hats"
                :key="item.id"
                class="item-card"
                :class="{ equipped: item.equipped, locked: !item.unlocked }"
                @click="toggleEquip(item, 'hats')"
              >
                <div v-if="!item.unlocked" class="lock-overlay">🔒</div>
                <div class="item-emoji">{{ item.icon }}</div>
                <div class="item-name">{{ item.name }}</div>
                <div class="item-rarity" :class="'rarity-' + item.rarityType">{{ item.rarity }}</div>
                <button class="item-action-btn">
                  {{ item.equipped ? '已穿戴 ✓' : (!item.unlocked ? '🔒 解锁' : '穿戴') }}
                </button>
              </div>
            </div>
          </div>

          <!-- Colors -->
          <div v-show="activeTab === 'colors'" class="tab-content">
            <p class="color-tip">🎨 选择宠物主体颜色（共 {{ colorOptions.length }} 种配色方案）</p>
            <div class="palette-grid">
              <div v-for="color in colorOptions" :key="color.name" class="palette-item">
                <div
                  class="palette-swatch"
                  :class="{ active: selectedColor === color.name }"
                  :style="{ background: color.bg }"
                  @click="selectedColor = color.name"
                ></div>
                <div class="palette-name">{{ color.name }}</div>
              </div>
            </div>
            <div class="color-presets">
              <div
                v-for="preset in colorPresets"
                :key="preset.name"
                class="color-preset"
                :class="{ active: selectedPreset === preset.name }"
                :style="{ background: preset.bg, color: preset.text, borderColor: preset.border }"
                @click="selectedPreset = preset.name"
              >
                {{ preset.emoji }} {{ preset.name }}
              </div>
            </div>
          </div>

          <!-- Effects -->
          <div v-show="activeTab === 'effects'" class="tab-content">
            <div class="item-grid">
              <div
                v-for="item in effects"
                :key="item.id"
                class="item-card"
                :class="{ equipped: item.equipped, locked: !item.unlocked }"
                @click="toggleEquip(item, 'effects')"
              >
                <div v-if="!item.unlocked" class="lock-overlay">🔒</div>
                <div class="item-emoji">{{ item.icon }}</div>
                <div class="item-name">{{ item.name }}</div>
                <div class="item-rarity" :class="'rarity-' + item.rarityType">{{ item.rarity }}</div>
                <button class="item-action-btn">
                  {{ item.equipped ? '已启用 ✓' : (!item.unlocked ? '🔒 解锁' : '启用') }}
                </button>
              </div>
            </div>
          </div>

          <!-- Backgrounds -->
          <div v-show="activeTab === 'backgrounds'" class="tab-content">
            <div class="item-grid">
              <div
                v-for="item in backgrounds"
                :key="item.id"
                class="item-card"
                :class="{ equipped: item.equipped, locked: !item.unlocked }"
                @click="toggleEquip(item, 'backgrounds')"
              >
                <div v-if="!item.unlocked" class="lock-overlay">🔒</div>
                <div class="item-emoji">{{ item.icon }}</div>
                <div class="item-name">{{ item.name }}</div>
                <div class="item-rarity" :class="'rarity-' + item.rarityType">{{ item.rarity }}</div>
                <button class="item-action-btn">
                  {{ item.equipped ? '已使用 ✓' : (!item.unlocked ? '🔒 解锁' : '使用') }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel -->
      <div class="acc-panel">
        <!-- Currently Equipped -->
        <div class="card">
          <div class="acc-section-title">📋 当前穿戴</div>
          <div class="equipped-list">
            <div v-for="item in equippedItems" :key="item.id" class="equipped-item">
              <div class="equipped-item-icon">{{ item.icon }}</div>
              <div class="equipped-item-info">
                <div class="equipped-item-name">{{ item.name }}</div>
                <div class="equipped-item-slot">{{ item.slot }}</div>
              </div>
              <div class="equipped-item-remove" @click="unequip(item)">✕</div>
            </div>
            <div v-if="equippedItems.length === 0" class="equipped-empty">暂无穿戴</div>
          </div>
        </div>

        <!-- Recently Used -->
        <div class="card">
          <div class="acc-section-title">🕐 最近使用</div>
          <div class="color-history">
            <div v-for="history in colorHistory" :key="history.name" class="color-history-item">
              <div class="color-history-dot" :style="{ background: history.color }"></div>
              <div class="color-history-text">{{ history.name }}配色</div>
              <div class="color-history-time">{{ history.time }}</div>
            </div>
          </div>
        </div>

        <!-- Save Preset -->
        <div class="card">
          <div class="acc-section-title">💾 保存方案</div>
          <input v-model="presetName" type="text" class="input-field" placeholder="输入方案名称...">
          <button class="save-btn" @click="savePreset" :disabled="!hasChanges">💾 保存当前装扮</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore, useUserStore } from '../../store'
import { shop as shopApi } from '../../api'

const petStore = usePetStore()
const userStore = useUserStore()

const activeTab = ref('costumes')
const presetName = ref('')
const selectedColor = ref('樱花粉')
const selectedPreset = ref('马卡龙')

const tabs = [
  { id: 'costumes', label: '服装', icon: '👗' },
  { id: 'hats', label: '发饰', icon: '🎀' },
  { id: 'colors', label: '颜色', icon: '🎨' },
  { id: 'effects', label: '特效', icon: '✨' },
  { id: 'backgrounds', label: '背景', icon: '🖼️' }
]

const colorOptions = [
  { name: '樱花粉', bg: '#FFB3C6' },
  { name: '天空蓝', bg: '#A8D8EA' },
  { name: '奶油黄', bg: '#FFE5A0' },
  { name: '薰衣草', bg: '#D5AAFF' },
  { name: '薄荷绿', bg: '#B8F1CC' },
  { name: '淡粉色', bg: '#FFD5E5' },
  { name: '冰川蓝', bg: '#C9E8F7' },
  { name: '柠檬黄', bg: '#FFF0BA' },
  { name: '浅紫藤', bg: '#E8D5F5' },
  { name: '蜜桃橙', bg: '#FFD4A8' }
]

const colorPresets = [
  { name: '马卡龙', emoji: '🌸', bg: '#FFE5A0', text: '#4A3F55', border: '#FFD166' },
  { name: '暗夜黑', emoji: '🌑', bg: '#4A3F55', text: '#fff', border: '#4A3F55' },
  { name: '奶油白', emoji: '⬜', bg: 'linear-gradient(135deg,#fff,#eee)', text: '#4A3F55', border: '#ccc' },
  { name: '幻彩渐变', emoji: '🌈', bg: 'linear-gradient(135deg,#FFB3C6,#A8D8EA)', text: '#fff', border: '#FFB3C6' }
]

const colorHistory = [
  { name: '樱花粉', color: '#FFB3C6', time: '今天' },
  { name: '奶油黄', color: '#FFE5A0', time: '昨天' },
  { name: '薰衣草', color: '#D5AAFF', time: '4月12日' }
]

const petName = computed(() => petStore.petInfo?.name || '小甜饼')
const petLevel = computed(() => `Lv.${petStore.petInfo?.level || 12}`)
const petType = computed(() => {
  const stage = petStore.petInfo?.stage || 1
  const types = ['布偶猫', '英短', '橘猫']
  return types[stage - 1] || '布偶猫'
})

const petEmoji = computed(() => {
  const stage = petStore.petInfo?.stage || 1
  const map = { 1: '🥚', 2: '🐱', 3: '😺', 4: '👑' }
  return map[stage] || '🐱'
})

const costumes = ref([
  { id: 1, name: '小王子服', icon: '👑', rarity: '稀有', rarityType: 'rare', equipped: true, unlocked: true, slot: '服装' },
  { id: 2, name: '粉色蓬蓬裙', icon: '🎀', rarity: '普通', rarityType: 'common', equipped: false, unlocked: true, slot: '服装' },
  { id: 3, name: '科学家外套', icon: '🥼', rarity: '稀有', rarityType: 'rare', equipped: false, unlocked: true, slot: '服装' },
  { id: 4, name: '秋冬大衣', icon: '🧥', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '服装' },
  { id: 5, name: '和服浴衣', icon: '👘', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '服装' },
  { id: 6, name: '墨镜酷装', icon: '🕶️', rarity: '传说', rarityType: 'legend', equipped: false, unlocked: false, slot: '服装' },
  { id: 7, name: '小丑套装', icon: '🎭', rarity: '稀有', rarityType: 'rare', equipped: false, unlocked: true, slot: '服装' },
  { id: 8, name: '星之魔法使', icon: '✨', rarity: '传说', rarityType: 'legend', equipped: false, unlocked: false, slot: '服装' }
])

const hats = ref([
  { id: 101, name: '粉色蝴蝶结', icon: '🎀', rarity: '普通', rarityType: 'common', equipped: true, unlocked: true, slot: '发饰' },
  { id: 102, name: '小皇冠', icon: '👑', rarity: '稀有', rarityType: 'rare', equipped: false, unlocked: true, slot: '发饰' },
  { id: 103, name: '魔术帽', icon: '🎩', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '发饰' },
  { id: 104, name: '星光冠', icon: '⭐', rarity: '传说', rarityType: 'legend', equipped: false, unlocked: false, slot: '发饰' }
])

const effects = ref([
  { id: 201, name: '星星拖尾', icon: '💫', rarity: '稀有', rarityType: 'rare', equipped: true, unlocked: true, slot: '特效' },
  { id: 202, name: '樱花飘落', icon: '🌸', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '特效' },
  { id: 203, name: '雪花环绕', icon: '❄️', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '特效' },
  { id: 204, name: '彩虹光晕', icon: '🌈', rarity: '传说', rarityType: 'legend', equipped: false, unlocked: false, slot: '特效' }
])

const backgrounds = ref([
  { id: 301, name: '温馨小屋', icon: '🏠', rarity: '普通', rarityType: 'common', equipped: true, unlocked: true, slot: '背景' },
  { id: 302, name: '樱花树下', icon: '🌸', rarity: '稀有', rarityType: 'rare', equipped: false, unlocked: true, slot: '背景' },
  { id: 303, name: '星空夜晚', icon: '🌙', rarity: '史诗', rarityType: 'epic', equipped: false, unlocked: true, slot: '背景' },
  { id: 304, name: '彩虹桥', icon: '🌈', rarity: '传说', rarityType: 'legend', equipped: false, unlocked: false, slot: '背景' }
])

const equippedItems = computed(() => {
  const allItems = [...costumes.value, ...hats.value, ...effects.value, ...backgrounds.value]
  return allItems.filter(item => item.equipped)
})

const hasChanges = computed(() => equippedItems.value.length > 0)

const getItemsByCategory = (category) => {
  const map = {
    costumes: costumes.value,
    hats: hats.value,
    effects: effects.value,
    backgrounds: backgrounds.value
  }
  return map[category] || []
}

const toggleEquip = (item, category) => {
  if (!item.unlocked) {
    ElMessage.info('该物品尚未解锁')
    return
  }

  // Same category items - only one can be equipped
  const items = getItemsByCategory(category)
  items.forEach(i => i.equipped = false)
  item.equipped = true

  // Call API to equip
  shopApi.equip(item.id).catch(err => {
    console.error('Failed to equip item:', err)
  })
}

const unequip = (item) => {
  item.equipped = false
  shopApi.equip(item.id).catch(err => {
    console.error('Failed to unequip item:', err)
  })
}

const resetOutfit = () => {
  costumes.value.forEach(i => i.equipped = false)
  hats.value.forEach(i => i.equipped = false)
  effects.value.forEach(i => i.equipped = false)
  backgrounds.value.forEach(i => i.equipped = false)
  ElMessage.success('已重置为默认装扮')
}

const savePreset = () => {
  if (!presetName.value.trim()) {
    ElMessage.warning('请输入方案名称')
    return
  }
  ElMessage.success(`装扮方案「${presetName.value}」已保存`)
  presetName.value = ''
}
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.studio-page {
  padding: 0;
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
  margin-bottom: 28px;
}

/* Layout */
.studio-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
}

/* Card */
.card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: $ev-shadow;
  border: 1px solid $ev-border;
  margin-bottom: 24px;
}

/* Pet Preview Card */
.pet-preview-card {
  background: linear-gradient(135deg, #FFF0F5 0%, #F5F0FF 100%);
  border-radius: 24px;
  padding: 40px 32px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2px solid $ev-primary-light;
  margin-bottom: 16px;
  position: relative;
  overflow: hidden;
}

.pet-preview-card::before {
  content: '';
  position: absolute;
  top: -40px;
  right: -40px;
  width: 120px;
  height: 120px;
  background: rgba($ev-purple, 0.15);
  border-radius: 50%;
}

.pet-preview-avatar {
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  border: 4px solid $ev-primary;
  margin-bottom: 16px;
  transition: all 0.3s;
  position: relative;
  z-index: 1;
}

.pet-preview-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
  position: relative;
  z-index: 1;
  color: $ev-text;
}

.pet-preview-type {
  font-size: 12px;
  color: $ev-text-sec;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
}

.pet-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.pet-tag {
  padding: 4px 12px;
  background: $ev-purple-light;
  border-radius: 20px;
  font-size: 11px;
  color: $ev-purple;
  border: 1px solid $ev-purple-light;
}

.pet-tag.equipped {
  background: $ev-primary-light;
  color: #FF6B9D;
  border-color: $ev-primary-light;
}

/* Preview Buttons */
.preview-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

/* Buttons */
.btn-secondary {
  flex: 1;
  padding: 12px;
  border-radius: 14px;
  background: $ev-purple-light;
  color: $ev-purple;
  font-size: 13px;
  font-weight: 700;
  border: 2px solid $ev-purple-light;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.btn-secondary:hover {
  background: $ev-purple;
  color: #fff;
}

.btn-reset {
  background: #F0FFF5;
  color: #3BAF5D;
  border-color: $ev-green;
}

.btn-reset:hover {
  background: #3BAF5D;
  color: #fff;
}

/* Tabs */
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
  gap: 14px;
}

.item-card {
  background: #FAFAFA;
  border-radius: 16px;
  padding: 16px 12px;
  text-align: center;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.item-card:hover {
  border-color: $ev-purple;
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(180, 150, 200, 0.12);
}

.item-card.equipped {
  border-color: $ev-primary;
  background: #FFF9FB;
}

.item-card.locked {
  opacity: 0.5;
  cursor: not-allowed;
}

.item-card.locked::after {
  content: '🔒';
  position: absolute;
  top: 6px;
  right: 6px;
  font-size: 12px;
}

.lock-overlay {
  display: none;
}

.item-emoji {
  font-size: 36px;
  margin-bottom: 8px;
}

.item-name {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 2px;
  color: $ev-text;
}

.item-rarity {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  display: inline-block;
  margin-bottom: 8px;
}

.rarity-common {
  background: #F0EDF5;
  color: $ev-text-sec;
}

.rarity-rare {
  background: #E8F4FF;
  color: #4A90D9;
}

.rarity-epic {
  background: #F5E8FF;
  color: #9B6FD4;
}

.rarity-legend {
  background: #FFF4C2;
  color: #8B6914;
}

.item-action-btn {
  margin-top: 8px;
  padding: 5px 0;
  width: 100%;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid $ev-purple-light;
  background: #fff;
  color: $ev-text-sec;
  transition: all 0.2s;
  font-family: inherit;
}

.item-card.equipped .item-action-btn {
  background: $ev-primary;
  color: #fff;
  border-color: $ev-primary;
}

.item-card:not(.equipped):not(.locked):hover .item-action-btn {
  background: $ev-purple;
  color: #fff;
  border-color: $ev-purple;
}

/* Color Palette */
.color-tip {
  font-size: 13px;
  color: $ev-text-sec;
  margin-bottom: 14px;
}

.palette-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.palette-item {
  text-align: center;
  cursor: pointer;
}

.palette-swatch {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 14px;
  margin-bottom: 6px;
  border: 3px solid transparent;
  transition: all 0.2s;

  &:hover {
    transform: scale(1.08);
    border-color: $ev-text;
  }
}

.palette-swatch.active {
  border-color: $ev-text;
  box-shadow: 0 0 0 3px rgba($ev-text, 0.2);
}

.palette-name {
  font-size: 11px;
  font-weight: 600;
  color: $ev-text;
}

.color-presets {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 16px;
}

.color-preset {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;

  &:hover {
    transform: scale(1.05);
  }
}

.color-preset.active {
  border-color: $ev-text;
}

/* Right Panel - Accessories */
.acc-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.acc-section-title {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: $ev-text;
}

/* Currently Equipped */
.equipped-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.equipped-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #FAFAFA;
  border-radius: 12px;
  border: 1px solid #F0EDF5;
}

.equipped-item-icon {
  font-size: 22px;
}

.equipped-item-info {
  flex: 1;
}

.equipped-item-name {
  font-size: 12px;
  font-weight: 600;
  color: $ev-text;
}

.equipped-item-slot {
  font-size: 10px;
  color: $ev-text-muted;
}

.equipped-item-remove {
  font-size: 12px;
  color: $ev-text-muted;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 6px;
  transition: all 0.2s;

  &:hover {
    background: #FFE5E5;
    color: #FF6B6B;
  }
}

.equipped-empty {
  text-align: center;
  font-size: 12px;
  color: $ev-text-muted;
  padding: 20px;
}

/* Color History */
.color-history {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.color-history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: #FAFAFA;
  border-radius: 10px;
}

.color-history-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  flex-shrink: 0;
}

.color-history-text {
  font-size: 11px;
  flex: 1;
  color: $ev-text;
}

.color-history-time {
  font-size: 10px;
  color: $ev-text-muted;
}

/* Input Field */
.input-field {
  width: 100%;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid $ev-purple-light;
  font-size: 13px;
  font-family: inherit;
  color: $ev-text;
  margin-bottom: 10px;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: $ev-purple;
  }

  &::placeholder {
    color: $ev-text-muted;
  }
}

/* Save Button */
.save-btn {
  width: 100%;
  padding: 14px;
  border-radius: 16px;
  background: $ev-gradient-primary;
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba($ev-purple, 0.4);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style>
