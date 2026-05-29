<template>
  <span :class="['status-badge', `status-${type}`]">{{ label }}</span>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'default',
    validator: (v) => ['success', 'danger', 'warning', 'info', 'purple', 'default'].includes(v)
  },
  label: { type: String, required: true }
})
</script>

<style scoped>
.status-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  line-height: 20px;
  white-space: nowrap;
}

.status-success {
  background: rgba(39, 174, 96, 0.12);
  color: #27ae60;
}

.status-danger {
  background: rgba(231, 76, 60, 0.12);
  color: #e74c3c;
}

.status-warning {
  background: rgba(243, 156, 18, 0.12);
  color: #f39c12;
}

.status-info {
  background: rgba(52, 152, 219, 0.12);
  color: #3498db;
}

.status-purple {
  background: rgba(142, 68, 173, 0.12);
  color: #8e44ad;
}

.status-default {
  background: rgba(149, 165, 166, 0.12);
  color: #7f8c8d;
}
</style>
