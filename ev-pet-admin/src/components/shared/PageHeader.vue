<template>
  <div class="page-header">
    <div class="page-title">{{ icon }} {{ title }}</div>
    <div v-if="subtitle" class="page-sub">{{ subtitle }}</div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '' },
  title: { type: String, required: true },
  subtitle: { type: String, default: '' }
})
</script>

<style scoped>
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 22px;
  font-weight: 800;
  color: #1a1a2e;
  letter-spacing: -0.5px;
}

.page-sub {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}
</style>
