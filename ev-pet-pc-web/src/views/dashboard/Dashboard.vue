<template>
  <div class="dashboard-page">
    <!-- 欢迎语 -->
    <div class="welcome-bar">
      <span class="welcome-text">欢迎回来，{{ userInfo?.nickname || '小明' }}！</span>
    </div>

    <!-- 4个统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon stat-icon-gold">💰</div>
        <div class="stat-body">
          <div class="stat-value">{{ userInfo?.gold || 2580 }}</div>
          <div class="stat-label">金币余额</div>
        </div>
        <div class="stat-badge up">↑ 本周+520</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon-exp">⭐</div>
        <div class="stat-body">
          <div class="stat-value">{{ userInfo?.exp || 1850 }}</div>
          <div class="stat-label">经验值</div>
        </div>
        <div class="stat-badge">进化进度 62%</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon-diary">📝</div>
        <div class="stat-body">
          <div class="stat-value">42</div>
          <div class="stat-label">日记记录</div>
        </div>
        <div class="stat-badge up">↑ 本周+7</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon-achievement">🏆</div>
        <div class="stat-body">
          <div class="stat-value">8</div>
          <div class="stat-label">成就达成</div>
        </div>
        <div class="stat-badge">再获 3 个解锁</div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="content-grid">
      <!-- 左侧：宠物卡片 + 心情图表 -->
      <div class="left-col">
        <!-- 宠物状态卡 -->
        <div class="ev-card pet-card">
          <div class="pet-header">
            <div class="pet-avatar">
              <span class="pet-emoji" :style="{ animation: 'petFloat 3s ease-in-out infinite' }">{{ petEmoji }}</span>
            </div>
            <div class="pet-info">
              <div class="pet-name-row">
                <span class="pet-name">{{ petInfo?.name || '小福' }}</span>
                <span class="pet-level">Lv.{{ petInfo?.level || 12 }}</span>
              </div>
              <div class="pet-mood-row">
                <span class="pet-mood-emoji">{{ moodEmoji }}</span>
                <span class="pet-mood-text">当前{{ moodText }} · {{ stageText }}</span>
              </div>
            </div>
          </div>

          <!-- 属性条 -->
          <div class="attr-list">
            <div class="attr-row">
              <span class="attr-icon">⚡</span>
              <span class="attr-name">体力</span>
              <div class="attr-bar-wrap">
                <div class="attr-bar-bg">
                  <div
                    class="attr-bar-fill health"
                    :class="{ low: (petInfo?.health || 78) < 30 }"
                    :style="{ width: (petInfo?.health || 78) + '%' }"
                  ></div>
                </div>
              </div>
              <span class="attr-num">{{ petInfo?.health || 78 }}</span>
            </div>
            <div class="attr-row">
              <span class="attr-icon">🍎</span>
              <span class="attr-name">饱食</span>
              <div class="attr-bar-wrap">
                <div class="attr-bar-bg">
                  <div
                    class="attr-bar-fill fullness"
                    :class="{ low: (petInfo?.fullness || 55) < 30 }"
                    :style="{ width: (petInfo?.fullness || 55) + '%' }"
                  ></div>
                </div>
              </div>
              <span class="attr-num">{{ petInfo?.fullness || 55 }}</span>
            </div>
            <div class="attr-row">
              <span class="attr-icon">❤️</span>
              <span class="attr-name">心情</span>
              <div class="attr-bar-wrap">
                <div class="attr-bar-bg">
                  <div
                    class="attr-bar-fill mood"
                    :class="{ low: (petInfo?.mood || 92) < 30 }"
                    :style="{ width: (petInfo?.mood || 92) + '%' }"
                  ></div>
                </div>
              </div>
              <span class="attr-num">{{ petInfo?.mood || 92 }}</span>
            </div>
          </div>

          <!-- 快捷照料按钮 -->
          <div class="quick-actions">
            <button class="quick-btn" @click="doAction('feed')" :disabled="loading">
              <span class="quick-btn-icon">🍖</span>
              <span class="quick-btn-text">喂食</span>
              <span class="quick-btn-sub">饱食+25</span>
            </button>
            <button class="quick-btn" @click="doAction('bath')" :disabled="loading">
              <span class="quick-btn-icon">🚿</span>
              <span class="quick-btn-text">清洁</span>
              <span class="quick-btn-sub">体力-5</span>
            </button>
            <button class="quick-btn" @click="doAction('play')" :disabled="loading">
              <span class="quick-btn-icon">🎾</span>
              <span class="quick-btn-text">陪玩</span>
              <span class="quick-btn-sub">心情+30</span>
            </button>
            <button class="quick-btn" @click="doAction('work')" :disabled="loading">
              <span class="quick-btn-icon">💼</span>
              <span class="quick-btn-text">打工</span>
              <span class="quick-btn-sub">金币+50</span>
            </button>
          </div>
        </div>

        <!-- 心情趋势 -->
        <div class="ev-card mood-chart-card">
          <h3 class="card-title">心情趋势（近 7 天）</h3>
          <div class="chart-wrap">
            <div class="chart-bar-item" v-for="(v, i) in moodData" :key="i">
              <div class="chart-bar-bg">
                <div class="chart-bar-fill" :style="{ height: v + '%' }"></div>
              </div>
              <span class="chart-label">{{ moodLabels[i] }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：任务 + AI对话 + 底部卡片 -->
      <div class="right-col">
        <!-- 今日任务 -->
        <div class="ev-card tasks-card">
          <div class="tasks-header">
            <h3 class="card-title">今日任务</h3>
            <span class="tasks-counter">3/5 完成</span>
          </div>
          <div class="task-list">
            <div class="task-item" v-for="task in dailyTasks" :key="task.id">
              <div class="task-icon-wrap" :class="'icon-' + task.iconColor">{{ task.icon }}</div>
              <div class="task-body">
                <span class="task-name">{{ task.name }}</span>
                <span class="task-reward">💰 {{ task.reward }}</span>
              </div>
              <div class="task-status" :class="'status-' + task.status">{{ task.statusText }}</div>
            </div>
          </div>
        </div>

        <!-- AI对话CTA -->
        <div class="ev-card ai-cta-card">
          <div class="ai-cta-content">
            <div class="ai-cta-icon">🦊</div>
            <div class="ai-cta-text">
              <h4>和小福聊聊</h4>
              <p>AI 智能对话 · 实时互动</p>
            </div>
          </div>
          <button class="ev-btn-primary ai-cta-btn" @click="$router.push('/chat')">
            开始对话 ✨
          </button>
        </div>

        <!-- 底部两个小卡片 -->
        <div class="bottom-cards">
          <!-- 金币充值 -->
          <div class="ev-card mini-card recharge-card">
            <div class="recharge-info">
              <span class="recharge-icon">💰</span>
              <span class="recharge-value">{{ userInfo?.gold || 2580 }} 金币余额</span>
            </div>
            <button class="ev-btn-secondary btn-sm" @click="$router.push('/shop')">充值</button>
          </div>

          <!-- 成就进度 -->
          <div class="ev-card mini-card achievement-card">
            <h4 class="mini-card-title">🏆 成就进度</h4>
            <div class="achievement-badges">
              <div class="ach-badge new">
                <span>🆕</span>
                <span class="ach-name">初次见面</span>
                <span class="ach-pct">100%</span>
              </div>
              <div class="ach-badge">
                <span>⭐⭐</span>
                <span class="ach-name">7天连续</span>
                <span class="ach-pct">57%</span>
              </div>
              <div class="ach-badge">
                <span>💎</span>
                <span class="ach-name">收集10种服装</span>
                <span class="ach-pct">30%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore, usePetStore } from '../../store'
import { pet as petApi } from '../../api'

const userStore = useUserStore()
const petStore = usePetStore()
const loading = ref(false)

const petInfo = computed(() => petStore.petInfo)
const userInfo = computed(() => userStore.userInfo)

const petEmoji = computed(() => {
  const stage = petInfo.value?.stage || 1
  return stage === 1 ? '🐣' : stage === 2 ? '🐥' : '🦊'
})

const moodEmoji = computed(() => {
  const m = petInfo.value?.mood || 100
  if (m >= 80) return '😊'; if (m >= 60) return '🙂'
  if (m >= 40) return '😐'; if (m >= 20) return '😢'; return '😭'
})

const moodText = computed(() => {
  const m = petInfo.value?.mood || 100
  if (m >= 80) return '很开心'; if (m >= 60) return '开心'
  if (m >= 40) return '一般'; if (m >= 20) return '有点难过'; return '很不开心'
})

const stageText = computed(() => {
  const s = petInfo.value?.stage || 1
  return s === 1 ? '幼年期' : s === 2 ? '成长期' : '活泼期'
})

const moodData = ref([85, 72, 90, 68, 78, 92, 88])
const moodLabels = ['4/9', '4/10', '4/11', '4/12', '4/13', '4/14', '4/15']

const dailyTasks = ref([
  { id: 1, icon: '🍖', name: '喂食宠物 3 次',    reward: 20, iconColor: 'food',    status: 'done',    statusText: '已完成' },
  { id: 2, icon: '💬', name: '和宠物聊天 5 次',  reward: 30, iconColor: 'chat',    status: 'done',    statusText: '已完成' },
  { id: 3, icon: '🎾', name: '陪玩 1 次',        reward: 50, iconColor: 'play',    status: 'active',  statusText: '进行中' },
  { id: 4, icon: '⭐', name: '分享宠物到社交平台', reward: 100, iconColor: 'share',  status: 'pending', statusText: '待完成' },
  { id: 5, icon: '🛁', name: '清洁宠物 1 次',    reward: 15, iconColor: 'clean',   status: 'pending', statusText: '待完成' },
])

const actionMap = {
  feed: { api: 'feed',   msg: '喂食成功~' },
  bath: { api: 'bath',  msg: '清洁完成~' },
  play: { api: 'play',  msg: '好开心~' },
  work: { api: 'work',  msg: '打工完成，获得金币！' },
}

const doAction = async (type) => {
  const { api, msg } = actionMap[type]
  loading.value = true
  try {
    const data = await petApi[api]()
    petStore.$patch({ petInfo: data })
    ElMessage.success(msg)
  } catch {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const data = await petApi.getInfo()
    petStore.$patch({ petInfo: data })
  } catch (err) {
    console.error('load pet info error', err)
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.dashboard-page {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes petFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

// 欢迎语
.welcome-bar {
  margin-bottom: 20px;
}

.welcome-text {
  font-size: 15px;
  color: $ev-text-sec;
}

// ── 统计卡片 ────────────────────────────────────────────────
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  @include ev-card;
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;

  &.stat-icon-gold   { background: linear-gradient(135deg, $ev-yellow, $ev-yellow-dark); }
  &.stat-icon-exp    { background: linear-gradient(135deg, $ev-purple, #C48FFF); }
  &.stat-icon-diary  { background: linear-gradient(135deg, $ev-secondary, $ev-secondary-dark); }
  &.stat-icon-achievement { background: linear-gradient(135deg, $ev-green, #8FE0A8); }
}

.stat-body { flex: 1; }

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: $ev-text;
  line-height: 1.2;
}

.stat-label { font-size: 13px; color: $ev-text-muted; margin-top: 2px; }

.stat-badge {
  font-size: 11px;
  color: $ev-text-muted;
  background: $ev-bg;
  padding: 4px 10px;
  border-radius: $ev-radius-full;
  white-space: nowrap;

  &.up { color: #5DA06B; background: rgba(#5DA06B, 0.1); font-weight: 600; }
}

// ── 主内容网格 ────────────────────────────────────────────
.content-grid {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 20px;
}

.left-col, .right-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// ── 通用卡片 ────────────────────────────────────────────────
.ev-card {
  background: white;
  border-radius: $ev-radius;
  padding: 20px;
  box-shadow: $ev-shadow;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: $ev-text;
  margin: 0 0 14px 0;
}

// ── 宠物卡片 ────────────────────────────────────────────────
.pet-card { padding: 24px; }

.pet-header {
  display: flex;
  gap: 16px;
  margin-bottom: 18px;
}

.pet-avatar {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, $ev-yellow, $ev-yellow-dark);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.pet-emoji { font-size: 40px; }

.pet-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.pet-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-name { font-size: 18px; font-weight: 700; color: $ev-text; }
.pet-level { font-size: 11px; font-weight: 700; color: $ev-text-muted; background: $ev-bg; padding: 2px 8px; border-radius: $ev-radius-full; }

.pet-mood-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pet-mood-emoji { font-size: 16px; }
.pet-mood-text { font-size: 13px; color: $ev-text-sec; }

// 属性条
.attr-list { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }

.attr-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.attr-icon { font-size: 14px; width: 18px; text-align: center; }

.attr-name {
  font-size: 12px;
  color: $ev-text-muted;
  width: 30px;
}

.attr-bar-wrap { flex: 1; }

.attr-bar-bg {
  height: 8px;
  background: rgba(0, 0, 0, 0.08);
  border-radius: 4px;
  overflow: hidden;
  max-width: 140px;
}

.attr-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 600ms ease;

  &.health  { background: $ev-gradient-health; }
  &.fullness { background: $ev-gradient-fullness; }
  &.mood    { background: $ev-gradient-mood; }
  &.low     { animation: pulseLow 1s ease-in-out infinite; }
}

@keyframes pulseLow {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.attr-num {
  font-size: 13px;
  font-weight: 600;
  color: $ev-text;
  width: 24px;
  text-align: right;
}

// 快捷照料按钮
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  background: $ev-bg;
  border: 1px solid transparent;
  border-radius: $ev-radius-sm;
  cursor: pointer;
  transition: all 150ms;
  font-family: inherit;

  &:hover {
    border-color: $ev-primary;
    background: $ev-primary-light;
  }

  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.quick-btn-icon { font-size: 20px; }
.quick-btn-text { font-size: 13px; font-weight: 600; color: $ev-text; }
.quick-btn-sub  { font-size: 10px; color: $ev-text-muted; }

// ── 心情趋势图 ─────────────────────────────────────────────
.mood-chart-card { padding: 20px; }

.chart-wrap {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  height: 100px;
}

.chart-bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  height: 100%;
}

.chart-bar-bg {
  flex: 1;
  width: 100%;
  background: rgba($ev-primary, 0.1);
  border-radius: 4px 4px 0 0;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.chart-bar-fill {
  width: 100%;
  background: $ev-gradient-primary;
  border-radius: 4px 4px 0 0;
  transition: height 500ms ease;
  min-height: 4px;
}

.chart-label { font-size: 10px; color: $ev-text-muted; white-space: nowrap; }

// ── 今日任务 ───────────────────────────────────────────────
.tasks-card { padding: 20px; }

.tasks-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.tasks-counter {
  font-size: 12px;
  color: $ev-purple;
  font-weight: 600;
  background: rgba($ev-purple, 0.1);
  padding: 3px 10px;
  border-radius: $ev-radius-full;
}

.task-list { display: flex; flex-direction: column; gap: 10px; }

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;

  &.icon-food  { background: rgba($ev-yellow, 0.25); }
  &.icon-chat  { background: rgba($ev-primary, 0.15); }
  &.icon-play  { background: rgba($ev-purple, 0.15); }
  &.icon-share { background: rgba($ev-secondary, 0.2); }
  &.icon-clean { background: rgba($ev-green, 0.2); }
}

.task-body { flex: 1; display: flex; flex-direction: column; gap: 2px; }

.task-name { font-size: 13px; font-weight: 500; color: $ev-text; }
.task-reward { font-size: 11px; color: $ev-text-muted; }

.task-status {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: $ev-radius-full;
  font-weight: 600;

  &.status-done   { background: rgba(#5DA06B, 0.2); color: #5DA06B; }
  &.status-active { background: rgba($ev-purple, 0.2); color: $ev-purple; }
  &.status-pending { background: rgba($ev-text-muted, 0.1); color: $ev-text-muted; }
}

// ── AI对话CTA ─────────────────────────────────────────────
.ai-cta-card {
  background: linear-gradient(135deg, $ev-purple-light, $ev-primary-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.ai-cta-content { display: flex; align-items: center; gap: 14px; }
.ai-cta-icon { font-size: 40px; }
.ai-cta-text h4 { font-size: 16px; font-weight: 700; color: $ev-text; margin: 0 0 2px; }
.ai-cta-text p  { font-size: 12px; color: $ev-text-sec; margin: 0; }

// ── 按钮 ────────────────────────────────────────────────────
.ev-btn-primary {
  @include ev-btn-primary;
  padding: 10px 22px;
  border-radius: $ev-radius-sm;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.ev-btn-secondary {
  @include ev-btn-secondary;
  padding: 8px 16px;
  border-radius: $ev-radius-sm;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  font-family: inherit;
  display: inline-flex;
  align-items: center;
  gap: 6px;

  &.btn-sm { padding: 6px 14px; font-size: 12px; }
}

.ai-cta-btn { flex-shrink: 0; }

// ── 底部卡片 ────────────────────────────────────────────────
.bottom-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.mini-card { padding: 16px; }

.recharge-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.recharge-icon { font-size: 18px; }
.recharge-value { font-size: 13px; color: $ev-text; font-weight: 600; }

.mini-card-title {
  font-size: 14px;
  font-weight: 600;
  color: $ev-text;
  margin: 0 0 10px 0;
}

.achievement-badges { display: flex; flex-direction: column; gap: 8px; }

.ach-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;

  &.new span:first-child { font-size: 14px; }

  .ach-name { flex: 1; color: $ev-text-sec; font-size: 12px; }
  .ach-pct  { font-weight: 700; color: $ev-text; font-size: 12px; }
}
</style>
