<template>
  <div class="stat-card">
    <div class="stat-icon" :style="{ background: color }">{{ icon }}</div>
    <div class="stat-content">
      <div class="stat-val">{{ formattedValue }}</div>
      <div class="stat-label">{{ label }}</div>
      <div v-if="change !== null" :class="['stat-change', change >= 0 ? 'up' : 'down']">
        {{ change >= 0 ? '↑' : '↓' }} {{ Math.abs(change) }}%
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  icon: { type: String, required: true },
  color: { type: String, default: '#FFF0F5' },
  value: { type: [Number, String], required: true },
  label: { type: String, required: true },
  change: { type: Number, default: null },
  format: { type: String, default: 'number' }
})

const formattedValue = computed(() => {
  if (typeof props.value === 'string') return props.value
  if (props.format === 'currency') return '¥' + props.value.toLocaleString()
  if (props.format === 'decimal') return props.value.toFixed(2)
  return props.value.toLocaleString()
})
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-val {
  font-size: 22px;
  font-weight: 800;
  color: #1a1a2e;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.stat-change {
  font-size: 11px;
  font-weight: 600;
  margin-top: 2px;
}

.stat-change.up {
  color: #27ae60;
}

.stat-change.down {
  color: #e74c3c;
}
</style>
