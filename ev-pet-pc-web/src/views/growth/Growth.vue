<template>
  <div class="growth-page">
    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-title">养成中心</h1>
      <p class="page-subtitle">查看宠物成长进度、解锁新技能与进化阶段</p>
    </div>

    <!-- Pet Profile Card -->
    <div class="pet-profile-card">
      <div class="pet-avatar-wrap">
        <div class="pet-avatar">{{ petEmoji }}</div>
        <div class="pet-level-badge">LV.{{ petInfo?.level || 12 }}</div>
      </div>
      <div class="pet-info">
        <div class="pet-name">{{ petInfo?.name || '小甜饼' }}</div>
        <div class="pet-type">🐱 {{ petInfo?.breed || '布偶猫' }} · {{ currentStageName }}</div>
        <div class="pet-exp-bar-wrap">
          <span class="pet-exp-label">经验值</span>
          <div class="pet-exp-bar">
            <div class="pet-exp-fill" :style="{ width: expPercent + '%' }"></div>
          </div>
          <span class="pet-exp-num">{{ petInfo?.exp || 682 }} / {{ petInfo?.nextEvolutionExp || 1000 }}</span>
        </div>
        <div class="pet-attributes">
          <div class="pet-attr">
            <span class="pet-attr-label">饱食度</span>
            <div class="pet-attr-bar">
              <div class="pet-attr-fill" :style="{ width: (petInfo?.fullness || 85) + '%', background: '#FFE5A0' }"></div>
            </div>
          </div>
          <div class="pet-attr">
            <span class="pet-attr-label">心情</span>
            <div class="pet-attr-bar">
              <div class="pet-attr-fill" :style="{ width: (petInfo?.mood || 92) + '%', background: '#B8F1CC' }"></div>
            </div>
          </div>
          <div class="pet-attr">
            <span class="pet-attr-label">体力</span>
            <div class="pet-attr-bar">
              <div class="pet-attr-fill" :style="{ width: (petInfo?.health || 60) + '%', background: '#A8D8EA' }"></div>
            </div>
          </div>
          <div class="pet-attr">
            <span class="pet-attr-label">清洁度</span>
            <div class="pet-attr-bar">
              <div class="pet-attr-fill" :style="{ width: (petInfo?.cleanliness || 75) + '%', background: '#D5AAFF' }"></div>
            </div>
          </div>
        </div>
      </div>
      <div class="pet-actions">
        <button class="btn-interact" :class="{ active: feedingActive }" @click="toggleFeed">
          🍖 喂食
        </button>
        <button class="btn-interact" @click="handlePlay">
          🎮 陪玩
        </button>
        <button class="btn-interact" @click="handleBath">
          🛁 洗澡
        </button>
        <button class="btn-interact" @click="handleRest">
          💤 休息
        </button>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: #FFF0F5;">📅</div>
        <div class="stat-value">{{ growthStats?.feedDays || 42 }}</div>
        <div class="stat-label">已喂养天数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F0FFF5;">⭐</div>
        <div class="stat-value">{{ skills.filter(s => s.unlocked).length || 8 }}</div>
        <div class="stat-label">已解锁技能</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #FFFBEF;">🔥</div>
        <div class="stat-value">{{ growthStats?.loginDays || 5 }}</div>
        <div class="stat-label">连续登录天数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #F0F8FF;">🌟</div>
        <div class="stat-value">{{ (petInfo?.totalExp || 15680).toLocaleString() }}</div>
        <div class="stat-label">总获得经验</div>
      </div>
    </div>

    <!-- Growth Section -->
    <div class="growth-section">
      <div>
        <!-- Evolution Card -->
        <div class="card evo-card">
          <div class="section-title">进化阶段</div>
          <div class="evolution-stages">
            <div class="evo-stage" :class="{ current: currentStage === 1 }">
              <div class="evo-icon">🥚</div>
              <div class="evo-name">蛋期</div>
              <div class="evo-level">Lv.1</div>
            </div>
            <div class="evo-stage" :class="{ current: currentStage === 2 }">
              <div class="evo-icon">🐱</div>
              <div class="evo-name">幼年期</div>
              <div class="evo-level">Lv.5–15</div>
            </div>
            <div class="evo-stage" :class="{ current: currentStage === 3 }">
              <div class="evo-icon">😺</div>
              <div class="evo-name">成长期</div>
              <div class="evo-level">Lv.16–30</div>
            </div>
            <div class="evo-stage" :class="{ current: currentStage === 4 }">
              <div class="evo-icon">👑</div>
              <div class="evo-name">完全体</div>
              <div class="evo-level">Lv.31+</div>
            </div>
          </div>
          <div class="evo-progress-row">
            <div class="evo-progress-box">
              <div class="evo-progress-label">进化进度</div>
              <div class="evo-progress-bar-wrap">
                <div class="evo-progress-bar">
                  <div class="evo-progress-fill" :style="{ width: evolutionProgress + '%' }"></div>
                </div>
                <span class="evo-progress-percent">{{ evolutionProgress }}%</span>
              </div>
              <div class="evo-progress-hint">再升{{ levelsToEvolve }}级即可进化至{{ nextStageName }}</div>
            </div>
            <div class="evo-preview-box">
              <div class="evo-preview-icon">{{ nextStageEmoji }}</div>
              <div class="evo-preview-info">
                <div class="evo-preview-title">下阶段形态预览</div>
                <div class="evo-preview-desc">😺 等待解锁</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Attribute Details Card -->
        <div class="card attr-card">
          <div class="section-title">属性详情</div>
          <div class="attr-list">
            <div class="attr-item">
              <div class="attr-icon" style="background: #FFF8E1;">🍖</div>
              <div class="attr-info">
                <div class="attr-name">饱食度</div>
                <div class="attr-bar-outer">
                  <div class="attr-bar-inner" :style="{ width: (petInfo?.fullness || 85) + '%', background: '#FFE5A0' }"></div>
                </div>
              </div>
              <div class="attr-num" style="color: #D4A000;">{{ petInfo?.fullness || 85 }}</div>
            </div>
            <div class="attr-item">
              <div class="attr-icon" style="background: #F0FFF5;">😊</div>
              <div class="attr-info">
                <div class="attr-name">心情</div>
                <div class="attr-bar-outer">
                  <div class="attr-bar-inner" :style="{ width: (petInfo?.mood || 92) + '%', background: '#B8F1CC' }"></div>
                </div>
              </div>
              <div class="attr-num" style="color: #3BAF5D;">{{ petInfo?.mood || 92 }}</div>
            </div>
            <div class="attr-item">
              <div class="attr-icon" style="background: #F0F8FF;">⚡</div>
              <div class="attr-info">
                <div class="attr-name">体力</div>
                <div class="attr-bar-outer">
                  <div class="attr-bar-inner" :style="{ width: (petInfo?.health || 60) + '%', background: '#A8D8EA' }"></div>
                </div>
              </div>
              <div class="attr-num" style="color: #4A9FBF;">{{ petInfo?.health || 60 }}</div>
            </div>
            <div class="attr-item">
              <div class="attr-icon" style="background: #F5F0FF;">✨</div>
              <div class="attr-info">
                <div class="attr-name">清洁度</div>
                <div class="attr-bar-outer">
                  <div class="attr-bar-inner" :style="{ width: (petInfo?.cleanliness || 75) + '%', background: '#D5AAFF' }"></div>
                </div>
              </div>
              <div class="attr-num" style="color: #9B6FD4;">{{ petInfo?.cleanliness || 75 }}</div>
            </div>
            <div class="attr-item">
              <div class="attr-icon" style="background: #FFF0F5;">💜</div>
              <div class="attr-info">
                <div class="attr-name">亲密度</div>
                <div class="attr-bar-outer">
                  <div class="attr-bar-inner" :style="{ width: intimacy + '%', background: '#FFB3C6' }"></div>
                </div>
              </div>
              <div class="attr-num" style="color: #E06080;">{{ intimacy }}</div>
            </div>
          </div>
        </div>

        <!-- Skill Card -->
        <div class="card skill-card">
          <div class="section-title">技能树</div>
          <div class="ability-grid">
            <div v-for="skill in skills" :key="skill.id" class="ability-item" :class="{ locked: !skill.unlocked, unlocked: skill.unlocked }">
              <div class="ability-emoji">{{ skill.unlocked ? skill.icon : '🔒' }}</div>
              <div class="ability-name">{{ skill.name }}</div>
              <div class="ability-desc">{{ skill.desc }}</div>
              <div class="ability-req" v-if="!skill.unlocked">{{ skill.req }}</div>
            </div>
          </div>
        </div>

        <!-- Learned Skills -->
        <div class="card learned-card">
          <div class="section-title">已习得技能</div>
          <div class="skill-list">
            <div v-for="s in learnedSkills" :key="s.id" class="skill-item" :class="{ upgraded: s.level > 1 }">
              <div class="skill-emoji">{{ s.icon }}</div>
              <div class="skill-info">
                <div class="skill-name">{{ s.name }}</div>
                <div class="skill-desc">{{ s.desc }}</div>
              </div>
              <div class="skill-lv">Lv.{{ s.level }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel -->
      <div>
        <!-- Activity Card -->
        <div class="card activity-card">
          <div class="section-title">今日活动</div>
          <div class="activity-today">
            <div v-for="(act, idx) in todayActivities" :key="idx" class="activity-item">
              <div class="activity-dot" :style="{ background: act.color }"></div>
              <div class="activity-text">{{ act.text }}</div>
              <div class="activity-time">{{ act.time }}</div>
            </div>
          </div>
        </div>

        <!-- Growth Timeline -->
        <div class="card history-card">
          <div class="section-title">成长记录</div>
          <div class="history-list">
            <div v-for="(record, idx) in growthRecords" :key="idx" class="history-item">
              <div class="history-icon">{{ record.icon }}</div>
              <div class="history-content">
                <div class="history-text">{{ record.text }}</div>
                <div class="history-time">{{ record.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Level Up Modal -->
    <div class="modal-overlay" :class="{ show: showLevelUpModal }">
      <div class="modal">
        <div class="modal-emoji">{{ levelUpEmoji }}</div>
        <h2>升级啦！</h2>
        <p>恭喜！{{ petInfo?.name || '小甜饼' }} 升到了 Lv.{{ petInfo?.level || 12 }}</p>
        <div class="modal-stats">
          <div class="modal-stat">
            <div class="modal-stat-val">+50</div>
            <div class="modal-stat-label">经验值</div>
          </div>
          <div class="modal-stat">
            <div class="modal-stat-val">+5</div>
            <div class="modal-stat-label">亲密度</div>
          </div>
        </div>
        <button class="modal-btn" @click="showLevelUpModal = false">太棒了！</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { usePetStore } from '../../store'
import { pet as petApi } from '../../api'

const petStore = usePetStore()
const petInfo = computed(() => petStore.petInfo)

const feedingActive = ref(false)
const showLevelUpModal = ref(false)
const levelUpEmoji = ref('🎉')

const currentStage = computed(() => petInfo.value?.stage || 2)

const stageNames = { 1: '蛋期', 2: '幼年期', 3: '成长期', 4: '完全体' }
const stageEmojis = { 1: '🥚', 2: '🐱', 3: '😺', 4: '👑' }
const currentStageName = computed(() => stageNames[currentStage.value] || '幼年期')
const nextStageName = computed(() => stageNames[currentStage.value + 1] || '成长期')
const nextStageEmoji = computed(() => stageEmojis[currentStage.value + 1] || '😺')

const petEmoji = computed(() => stageEmojis[currentStage.value] || '🐱')

const expPercent = computed(() => {
  const exp = petInfo.value?.exp || 682
  const max = petInfo.value?.nextEvolutionExp || 1000
  return Math.min(100, (exp / max) * 100)
})

const evolutionProgress = computed(() => {
  const level = petInfo.value?.level || 12
  if (currentStage.value === 1) return Math.min(100, level * 100)
  if (currentStage.value === 2) return Math.min(100, (level - 5) * 10)
  if (currentStage.value === 3) return Math.min(100, (level - 16) * (100 / 15))
  return 100
})

const levelsToEvolve = computed(() => {
  const level = petInfo.value?.level || 12
  if (currentStage.value === 1) return 5 - level
  if (currentStage.value === 2) return 16 - level
  if (currentStage.value === 3) return 31 - level
  return 0
})

const intimacy = ref(55)

const growthStats = ref({
  feedDays: 42,
  loginDays: 5,
  totalExp: 15680
})

const skills = ref([
  { id: 1, name: '撒娇', icon: '💕', desc: '增加主人好感', req: 'Lv.5解锁', unlocked: true },
  { id: 2, name: '卖萌', icon: '🥰', desc: '获得额外金币', req: 'Lv.8解锁', unlocked: true },
  { id: 3, name: '打滚', icon: '🔄', desc: '恢复体力', req: 'Lv.10解锁', unlocked: true },
  { id: 4, name: '握手', icon: '🤝', desc: '社交技能', req: 'Lv.15解锁', unlocked: false },
  { id: 5, name: '跳跃', icon: '⬆️', desc: '解锁新动作', req: 'Lv.20解锁', unlocked: false },
  { id: 6, name: '翻滚', icon: '🔃', desc: '可爱技能', req: 'Lv.25解锁', unlocked: false }
])

const learnedSkills = ref([
  { id: 1, name: '撒娇', icon: '💕', desc: '增加与主人的亲密度', level: 3 },
  { id: 2, name: '卖萌', icon: '🥰', desc: '每日可触发一次', level: 2 },
  { id: 3, name: '打滚', icon: '🔄', desc: '恢复10点体力', level: 1 }
])

const todayActivities = ref([
  { text: '完成喂食任务，+20经验', time: '14:30', color: '#FFE5A0' },
  { text: '连续登录第5天', time: '10:15', color: '#B8F1CC' },
  { text: '洗澡完成，清洁度+30', time: '20:00', color: '#A8D8EA' },
  { text: '陪玩互动，心情+25', time: '15:30', color: '#D5AAFF' }
])

const growthRecords = ref([
  { icon: '🍖', text: '完成喂食任务，获得+20经验', time: '今天 14:30' },
  { icon: '⭐', text: '连续登录第5天，获得成就', time: '今天 10:15' },
  { icon: '🛁', text: '洗澡完成，清洁度+30', time: '昨天 20:00' },
  { icon: '🎮', text: '陪玩互动，心情+25', time: '昨天 15:30' },
  { icon: '📈', text: '宠物升级到Lv.12', time: '03-15 12:00' }
])

onMounted(async () => {
  try {
    const data = await petApi.getInfo()
    petStore.$patch({ petInfo: data })
  } catch (err) {
    console.error(err)
  }
})

const toggleFeed = () => {
  feedingActive.value = !feedingActive.value
}

const handlePlay = async () => {
  try {
    const data = await petApi.play()
    petStore.$patch({ petInfo: data })
    ElMessage.success('陪玩成功！心情+25')
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const handleBath = async () => {
  try {
    const data = await petApi.bath()
    petStore.$patch({ petInfo: data })
    ElMessage.success('洗澡完成！清洁度+30')
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const handleRest = () => {
  ElMessage.info('宠物正在休息中...')
}
</script>

<style lang="scss" scoped>
@import '@/styles/ev-pet.scss';

.growth-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: $ev-text;
  margin-bottom: 4px;
}

.page-subtitle {
  font-size: 14px;
  color: $ev-text-sec;
  margin: 0;
}

/* Pet Profile Card */
.pet-profile-card {
  background: $ev-gradient-primary;
  border-radius: 24px;
  padding: 28px 32px;
  display: flex;
  align-items: center;
  gap: 32px;
  margin-bottom: 28px;
  position: relative;
  overflow: hidden;
  color: #fff;
}

.pet-profile-card::before {
  content: '';
  position: absolute;
  right: -30px;
  top: -30px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.pet-profile-card::after {
  content: '';
  position: absolute;
  right: 60px;
  bottom: -40px;
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.pet-avatar-wrap {
  position: relative;
  flex-shrink: 0;
  z-index: 1;
}

.pet-avatar {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 52px;
  position: relative;
  z-index: 1;
}

.pet-level-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background: $ev-yellow-dark;
  color: $ev-text;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 20px;
  border: 2px solid #fff;
  z-index: 2;
}

.pet-info {
  flex: 1;
  position: relative;
  z-index: 1;
}

.pet-name {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 2px;
}

.pet-type {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 14px;
}

.pet-exp-bar-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.pet-exp-label {
  font-size: 12px;
  opacity: 0.85;
  white-space: nowrap;
}

.pet-exp-bar {
  flex: 1;
  height: 10px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 10px;
  overflow: hidden;
}

.pet-exp-fill {
  height: 100%;
  background: $ev-yellow-dark;
  border-radius: 10px;
  transition: width 1s ease;
}

.pet-exp-num {
  font-size: 12px;
  opacity: 0.85;
}

.pet-attributes {
  display: flex;
  gap: 20px;
}

.pet-attr {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.pet-attr-label {
  font-size: 11px;
  opacity: 0.75;
}

.pet-attr-bar {
  width: 80px;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  overflow: hidden;
}

.pet-attr-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.5s ease;
}

.pet-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.btn-interact {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: #fff;
  padding: 10px 20px;
  border-radius: 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  font-family: inherit;
}

.btn-interact:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-1px);
}

.btn-interact.active {
  background: #fff;
  color: $ev-purple;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 28px;
}

.stat-card {
  background: #fff;
  border-radius: 20px;
  padding: 22px;
  box-shadow: 0 2px 12px rgba(180, 150, 200, 0.08);
  border: 1px solid $ev-bg;
  transition: all 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(180, 150, 200, 0.14);
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 12px;
  color: $ev-text-sec;
}

/* Growth Section */
.growth-section {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 20px;
  margin-bottom: 28px;
}

.card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(180, 150, 200, 0.08);
  border: 1px solid $ev-bg;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;

  &::before {
    content: '';
    width: 4px;
    height: 18px;
    background: $ev-gradient-primary;
    border-radius: 2px;
  }
}

/* Evolution */
.evo-card {
  margin-bottom: 20px;
}

.evolution-stages {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.evo-stage {
  flex: 1;
  text-align: center;
  position: relative;
}

.evo-stage::after {
  content: '→';
  position: absolute;
  right: -8px;
  top: 18px;
  color: $ev-purple;
  font-size: 16px;
  font-weight: 700;
}

.evo-stage:last-child::after {
  display: none;
}

.evo-stage.current .evo-icon {
  border-color: $ev-primary;
  background: #FFF0F5;
}

.evo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 8px;
  background: $ev-bg;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  border: 3px solid $ev-purple-light;
  transition: all 0.3s;
}

.evo-name {
  font-size: 12px;
  font-weight: 600;
  color: $ev-text-sec;
}

.evo-level {
  font-size: 11px;
  color: $ev-text-muted;
}

.evo-stage.current .evo-name {
  color: $ev-purple;
}

.evo-progress-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.evo-progress-box {
  flex: 1;
  min-width: 200px;
  background: #F9F5FF;
  border-radius: 12px;
  padding: 14px;
  border: 1px solid $ev-purple-light;
}

.evo-progress-label {
  font-size: 12px;
  color: $ev-text-sec;
  margin-bottom: 6px;
}

.evo-progress-bar-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.evo-progress-bar {
  flex: 1;
  height: 8px;
  background: $ev-purple-light;
  border-radius: 8px;
  overflow: hidden;
}

.evo-progress-fill {
  height: 100%;
  background: $ev-gradient-primary;
  border-radius: 8px;
}

.evo-progress-percent {
  font-size: 13px;
  font-weight: 700;
  color: $ev-purple;
}

.evo-progress-hint {
  font-size: 11px;
  color: $ev-text-muted;
  margin-top: 4px;
}

.evo-preview-box {
  background: #FFF9FB;
  border-radius: 12px;
  padding: 14px;
  border: 1px solid $ev-primary-light;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 160px;
}

.evo-preview-icon {
  font-size: 28px;
}

.evo-preview-title {
  font-size: 12px;
  font-weight: 600;
}

.evo-preview-desc {
  font-size: 11px;
  color: $ev-text-sec;
}

/* Attribute Details */
.attr-card {
  margin-bottom: 20px;
}

.attr-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.attr-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.attr-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.attr-info {
  flex: 1;
}

.attr-name {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
}

.attr-bar-outer {
  height: 8px;
  background: #F0EDF5;
  border-radius: 8px;
  overflow: hidden;
}

.attr-bar-inner {
  height: 100%;
  border-radius: 8px;
  transition: width 0.8s ease;
}

.attr-num {
  font-size: 12px;
  font-weight: 700;
  width: 36px;
  text-align: right;
}

/* Ability Grid */
.ability-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.ability-item {
  background: #F9F5FF;
  border-radius: 14px;
  padding: 16px;
  border: 1px solid $ev-purple-light;
  text-align: center;
  transition: all 0.2s;
  cursor: pointer;
}

.ability-item:hover {
  border-color: $ev-purple;
  transform: translateY(-2px);
}

.ability-item.locked {
  opacity: 0.45;
}

.ability-item.unlocked {
  border-color: $ev-green;
  background: #F0FFF5;
}

.ability-emoji {
  font-size: 28px;
  margin-bottom: 6px;
}

.ability-name {
  font-size: 12px;
  font-weight: 600;
  color: $ev-text;
  margin-bottom: 2px;
}

.ability-desc {
  font-size: 10px;
  color: $ev-text-sec;
}

.ability-req {
  font-size: 10px;
  color: $ev-text-muted;
  margin-top: 4px;
}

/* Skill List */
.skill-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skill-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #F9F5FF;
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid $ev-purple-light;
}

.skill-item.upgraded {
  border-color: $ev-yellow-dark;
  background: #FFFBEF;
}

.skill-emoji {
  font-size: 22px;
}

.skill-info {
  flex: 1;
}

.skill-name {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 2px;
}

.skill-desc {
  font-size: 11px;
  color: $ev-text-sec;
}

.skill-lv {
  font-size: 12px;
  font-weight: 700;
  color: $ev-purple;
  padding: 4px 10px;
  background: $ev-purple-light;
  border-radius: 20px;
}

.skill-item.upgraded .skill-lv {
  color: #8B6914;
  background: #FFF4C2;
}

/* Activity Card */
.activity-card {
  margin-bottom: 20px;
}

.activity-today {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 12px;
  background: #FAFAFA;
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.activity-text {
  font-size: 12px;
  flex: 1;
}

.activity-time {
  font-size: 10px;
  color: $ev-text-muted;
  white-space: nowrap;
}

/* History List */
.history-card {
  margin-bottom: 20px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 320px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #FAFAFA;
}

.history-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.history-content {
  flex: 1;
}

.history-text {
  font-size: 12px;
  line-height: 1.5;
}

.history-time {
  font-size: 10px;
  color: $ev-text-muted;
  margin-top: 2px;
}

/* Level Up Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 63, 85, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
}

.modal-overlay.show {
  opacity: 1;
  pointer-events: all;
}

.modal {
  background: #fff;
  border-radius: 24px;
  padding: 36px;
  width: 400px;
  text-align: center;
  transform: scale(0.9);
  transition: transform 0.3s;
}

.modal-overlay.show .modal {
  transform: scale(1);
}

.modal-emoji {
  font-size: 64px;
  margin-bottom: 12px;
}

.modal h2 {
  font-size: 22px;
  color: $ev-text;
  margin-bottom: 6px;
}

.modal p {
  font-size: 14px;
  color: $ev-text-sec;
  margin-bottom: 20px;
}

.modal-stats {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.modal-stat {
  text-align: center;
}

.modal-stat-val {
  font-size: 24px;
  font-weight: 700;
  color: $ev-purple;
}

.modal-stat-label {
  font-size: 11px;
  color: $ev-text-sec;
}

.modal-btn {
  background: $ev-gradient-primary;
  color: #fff;
  border: none;
  padding: 12px 32px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s;
  font-family: inherit;
}

.modal-btn:hover {
  transform: scale(1.03);
}

/* Learned Card */
.learned-card {
  margin-bottom: 20px;
}
</style>
