<template>
  <div class="not-found">
    <div class="not-found-content">
      <span class="emoji">🐾</span>
      <h1>404</h1>
      <p>页面走丢了...</p>
      <router-link to="/" class="back-btn">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFF5F5 0%, #FFF0F5 100%);
}

.not-found-content {
  text-align: center;
}

.emoji {
  font-size: 80px;
  display: block;
  margin-bottom: 24px;
}

h1 {
  font-size: 72px;
  color: #FFB3C6;
  margin: 0 0 16px;
}

p {
  font-size: 20px;
  color: #7A6B8A;
  margin: 0 0 32px;
}

.back-btn {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #FFB3C6 0%, #FF8FAB 100%);
  color: white;
  border-radius: 24px;
  text-decoration: none;
  font-size: 16px;
  transition: transform 0.2s;
}

.back-btn:hover {
  transform: scale(1.05);
}
</style>
