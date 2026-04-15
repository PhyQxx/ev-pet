# EV Pet 开发计划

> 版本：v1.0  
> 日期：2026-04-15  
> 状态：✅ 第二阶段进行中（前端开发中）

---

## 📋 项目信息

| 项目 | EV Pet - AI虚拟宠物养成平台 |
|------|---------------------------|
| 设计稿 | /root/.openclaw/workspace-des/prototype/ |
| 设计规范 | /root/.openclaw/workspace-des/DESIGN-SPEC-EVpet.md |
| PRD | /vol2/1000/我的文档/obsidian/PHY/项目/07.AI宠物/PRD-AI宠物系统.md |

---

## 🎯 MVP P0 功能范围

| # | 功能 | 优先级 | 说明 |
|---|------|--------|------|
| 1 | 用户登录（微信+手机号） | P0 | 微信SDK + 短信验证码 |
| 2 | 宠物展示与动画 | P0 | Lottie动画 |
| 3 | 养成系统（喂食/清洁/陪玩） | P0 | 三属性：体力/心情/饱食度 |
| 4 | AI对话（文字） | P0 | MiniMax API |
| 5 | 装扮系统（服装+配件） | P0 | 基础版 |
| 6 | 宠物进化（3阶段） | P0 | 外观解锁 |
| 7 | 宠物商店（免费道具） | P0 | 金币购买 |

---

## 🗓️ 开发排期表

### 第一阶段：项目框架（3天）

| 日期 | 任务 | 输出 |
|------|------|------|
| Day 1 | 搭建 uniapp 项目 + Vue3 + Vite | 项目工程 |
| Day 1 | 设计稿切片 + 资源提取 | 设计资源包 |
| Day 2 | 数据库设计 + 用户表/宠物表/道具表 | ER图 + SQL |
| Day 3 | 后端框架搭建 + JWT登录接口 | 登录API |

### 第二阶段：宠物核心（5天）

| 日期 | 任务 | 输出 |
|------|------|------|
| Day 4 | 宠物数据模型 + API | 宠物基础CRUD |
| Day 5 | 宠物展示页（动画） | 首页UI + 动画 |
| Day 6 | 养成操作（喂食/清洁/陪玩） | 养成API + UI |
| Day 7 | 三属性计算逻辑 | 属性系统 |
| Day 8 | 宠物进化触发逻辑 | 进化系统 |

### 第三阶段：AI对话（4天）

| 日期 | 任务 | 输出 |
|------|------|------|
| Day 9 | MiniMax API 接入 | 对话API |
| Day 10 | AI对话界面 | 聊天UI |
| Day 11 | 宠物动作联动 | 动画联动 |
| Day 12 | 内容安全过滤 | 安全审核 |

### 第四阶段：装扮+商店（4天）

| 日期 | 任务 | 输出 |
|------|------|------|
| Day 13 | 装扮系统前端 | 衣柜UI |
| Day 14 | 装扮API + 穿戴逻辑 | 装扮API |
| Day 15 | 商店界面 + 购买逻辑 | 商店UI + API |
| Day 16 | 道具使用逻辑 | 道具系统 |

### 第五阶段：联调+测试（4天）

| 日期 | 任务 | 输出 |
|------|------|------|
| Day 17 | 前后端联调 | 全流程通 |
| Day 18 | 功能测试 | 测试报告 |
| Day 19 | Bug修复 | 上线版本 |
| Day 20 | 部署上线 | 正式环境 |

---

## 📊 页面开发顺序

### 移动端（uniapp）- MVP优先

| 顺序 | 页面 | 文件 |
|------|------|------|
| 1 | 首页 | evpet-mobile-home.html |
| 2 | 养成页 | evpet-mobile-care.html |
| 3 | AI对话 | evpet-mobile-chat.html |
| 4 | 商店 | evpet-mobile-shop.html |
| 5 | 社交页 | evpet-mobile-social.html |
| 6 | 打工页 | evpet-mobile-work.html |
| 7 | 成就页 | evpet-mobile-achievement.html |

### PC Web（第二阶段）

| 顺序 | 页面 | 文件 |
|------|------|------|
| 1 | 仪表盘 | evpet-web-dashboard.html |
| 2 | AI对话 | evpet-web-chat.html |
| 3 | 养成中心 | evpet-web-growth.html |
| 4 | 装扮工作室 | evpet-web-studio.html |
| 5-9 | 其他页面 | 略 |

---

## 🔧 技术栈

| 端 | 技术 |
|----|------|
| 移动端 | uniapp + Vue3 + Vite |
| PC Web | Vue3 + Vite |
| 后端 | Node.js (待确认) |
| AI | MiniMax API |
| 数据库 | MySQL + Redis |
| 动画 | Lottie |

---

## ⚠️ 待确认事项

1. 后端技术栈：Java Spring Boot 还是 Node.js？
2. 先做移动端MVP还是PC Web MVP？
3. 宠物动画资源：Lottie/SVG还是前端自实现？
4. GitHub仓库创建（ev-pet项目）？

