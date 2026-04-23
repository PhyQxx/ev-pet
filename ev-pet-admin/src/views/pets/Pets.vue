<template>
  <div class="admin-page">
    <div class="page-title">🐾 宠物管理</div>
    <div class="page-sub">查看、管理所有玩家的虚拟宠物</div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <input class="filter-input" placeholder="搜索宠物名 / 主人昵称 / 宠物ID..." />
      <select class="filter-select">
        <option selected>全部分类</option>
        <option>🐱 猫系</option>
        <option>🐶 犬系</option>
        <option>🐰 兔系</option>
        <option>🦊 狐系</option>
        <option>🐹 鼠系</option>
      </select>
      <select class="filter-select">
        <option selected>全部状态</option>
        <option>正常</option>
        <option>休眠</option>
        <option>变异</option>
      </select>
      <button class="btn btn-primary">🔍 搜索</button>
      <button class="btn btn-outline">导出</button>
    </div>

    <!-- Stats Cards -->
    <div class="stat-row">
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFF0F5;">🐾</div>
        <div class="stat-val">2,890</div>
        <div class="stat-label">宠物总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#F0FFF5;">🐱</div>
        <div class="stat-val">1,204</div>
        <div class="stat-label">猫系宠物</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FFE5A0;">🦊</div>
        <div class="stat-val">686</div>
        <div class="stat-label">狐系宠物</div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#F5F0FF;">⭐</div>
        <div class="stat-val">15</div>
        <div class="stat-label">变异宠物</div>
      </div>
    </div>

    <!-- Pet Table -->
    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>宠物</th>
              <th>主人</th>
              <th>分类</th>
              <th>等级</th>
              <th>进化阶段</th>
              <th>状态</th>
              <th>最后活跃</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="pet in pets" :key="pet.id">
              <td>
                <div class="pet-cell">
                  <div class="pet-avatar" :style="{ background: pet.bg }">{{ pet.emoji }}</div>
                  <div>
                    <div class="pet-name">{{ pet.name }}</div>
                    <div class="pet-id">ID: {{ pet.id }}</div>
                  </div>
                </div>
              </td>
              <td>{{ pet.owner }}</td>
              <td>{{ pet.type }}</td>
              <td>LV.{{ pet.level }}</td>
              <td>{{ pet.evolution }}</td>
              <td><span :class="['badge', pet.statusClass]">{{ pet.status }}</span></td>
              <td>{{ pet.lastActive }}</td>
              <td>
                <span class="action-link">详情</span>
                <span class="action-link" style="color:#FF6B6B;">休眠</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pagination">
        <span>显示 1-5 条，共 5 条</span>
        <div class="page-btns">
          <button class="btn btn-sm" disabled>上一页</button>
          <button class="btn btn-sm" disabled>下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const pets = ref([
  { id: 10001, name: '小福', emoji: '🦊', bg: '#FFD5E5', owner: '裴浩宇', type: '🦊 狐系', level: 12, evolution: '成长期', status: '正常', statusClass: 'badge-green', lastActive: '今天 18:00' },
  { id: 10002, name: '小布', emoji: '🐱', bg: '#D5AAFF', owner: '阿雪', type: '🐱 猫系', level: 18, evolution: '完全体', status: '正常', statusClass: 'badge-green', lastActive: '今天 16:20' },
  { id: 10003, name: '旺财', emoji: '🐶', bg: '#FFE5A0', owner: '小林', type: '🐶 犬系', level: 7, evolution: '幼年期', status: '正常', statusClass: 'badge-green', lastActive: '昨天 22:30' },
  { id: 10004, name: '小绒', emoji: '🐹', bg: '#A8D8EA', owner: '小华', type: '🐹 鼠系', level: 2, evolution: '幼年期', status: '休眠', statusClass: 'badge-yellow', lastActive: '3天前' },
  { id: 10005, name: '小火', emoji: '🦊', bg: '#FFACA8', owner: '小玲', type: '🦊 狐系', level: 5, evolution: '成长期', status: '变异', statusClass: 'badge-purple', lastActive: '今天 10:00' },
])
</script>

<style scoped>
.filter-bar { display: flex; gap: 10px; align-items: center; margin-bottom: 16px; }
.filter-input { flex: 1; padding: 8px 12px; border: 1px solid #E8DFF0; border-radius: 8px; font-size: 13px; max-width: 280px; }
.filter-select { padding: 8px 12px; border: 1px solid #E8DFF0; border-radius: 8px; background: #fff; font-size: 13px; }
.stat-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 16px; }
.stat-card { background: #fff; border-radius: 16px; padding: 18px; box-shadow: 0 2px 10px rgba(180,150,200,0.07); border: 1px solid #F5EEF8; }
.stat-icon { width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 16px; margin-bottom: 8px; }
.stat-val { font-size: 22px; font-weight: 700; }
.stat-label { font-size: 12px; color: #7A6B8A; margin-top: 2px; }
.pet-cell { display: flex; align-items: center; gap: 8px; }
.pet-avatar { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px; }
.pet-name { font-weight: 600; font-size: 13px; }
.pet-id { font-size: 11px; color: #A898B8; }
.pagination { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; font-size: 13px; color: #7A6B8A; }
.page-btns { display: flex; gap: 6px; }
/* badge/action overrides specific to Pets page */
.badge { padding: 2px 8px; border-radius: 6px; font-size: 11px; font-weight: 600; display: inline-block; }
.badge-green { background: #E8F5EE; color: #2DA44E; }
.badge-yellow { background: #FFF8E6; color: #D4A000; }
.badge-purple { background: #F5EEFF; color: #8B5CF6; }
.action-link { color: #D5AAFF; cursor: pointer; font-size: 12px; margin-right: 6px; font-weight: 500; }
</style>
