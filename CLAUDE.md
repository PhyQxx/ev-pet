# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

EV Pet（AI虚拟宠物养成平台）— 包含四个子项目：

| 子项目 | 技术栈 | 端口 | 说明 |
|--------|--------|------|------|
| `ev-pet-backend` | Spring Boot 2.7.18 / Java 17 / Maven | 8099 | REST API 后端 |
| `ev-pet-admin` | Vue 3 + Element Plus + Vite 5 | 5177 | 管理后台 |
| `ev-pet-pc-web` | Vue 3 + Element Plus + Pinia + Lottie + Vite 5 | 8010 | PC端用户界面 |
| `ev-pet-uniapp` | UniApp (Vue 2 Options API) | — | 移动端（微信小程序/H5） |

所有前端项目通过 Vite 代理将 `/api` 转发到后端 8099 端口。

## 常用命令

### 后端（ev-pet-backend）
```bash
cd ev-pet-backend
mvn spring-boot:run              # 启动后端服务
mvn clean package                 # 打包
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # 指定环境运行
```

### 前端（ev-pet-admin / ev-pet-pc-web）
```bash
cd ev-pet-admin  # 或 cd ev-pet-pc-web
npm install
npm run dev       # 开发服务器
npm run build     # 生产构建
```

### 移动端（ev-pet-uniapp）
使用 HBuilderX 打开项目运行，或使用 uni-app CLI。

## 架构

### 后端分层
`Controller → Service → Mapper(MyBatis Plus) → MySQL`

- **Controller**：7个控制器，统一前缀 `/api/`，通过 `Authorization: Bearer <token>` 鉴权
- **Service**：核心业务逻辑在 `ChatService`（MiniMax AI 对话）、`PetService`（养成/进化）、`ShopService`（商店/装扮）、`WorkService`（打工）
- **Mapper**：10个 MyBatis Plus Mapper，无 XML，纯注解
- **Model**：10个实体类，对应 `users`、`pets`、`items`、`user_items`、`chat_messages`、`work_records`、`friends`、`posts`、`achievements`、`user_achievements` 表
- **DTO/VO**：`dto/` 存放入参（LoginDto, ChatDto, WorkDto），`vo/` 存放返回值（ApiResponse 统一包装）
- **Utils**：`JwtUtil`（JWT 令牌）、`ContentFilterUtil`（内容安全过滤）

### 前端（PC Web）
- **状态管理**：`src/store/index.js` — `useUserStore`（用户/宠物/token，持久化到 localStorage）、`usePetStore`（宠物操作）
- **API 层**：`src/api/index.js` — Axios 实例，请求拦截器加 Bearer token，401 自动跳转登录
- **路由守卫**：`src/router/index.js` — `beforeEach` 检查 token
- **布局**：`src/components/Layout.vue` — 左侧导航 + 顶部金币/用户信息

### 移动端（UniApp）
- **5个 TabBar 页**：首页、养成、对话、商店、我的
- **API 封装**：`utils/api.js` — 封装 `uni.request`，自动携带 token
- **页面路由**：`pages.json` 定义全部 9 个页面及 TabBar 配置

### 外部依赖
- **MySQL**：`ev_pet` 数据库（localhost:3306），无 SQL 迁移文件，表结构由 Model 类隐式定义
- **Redis**：localhost:6379
- **MiniMax API**：AI 对话功能，配置在 `application.yml` 中

### 认证流程
登录（微信/手机号）→ `AuthService` 创建用户+宠物（首次）→ `JwtUtil` 生成 JWT（7天有效）→ 前端存储 token 并在请求头携带

## gstack

使用 `/browse` skill 进行所有网页浏览操作。可用 skills：
`/office-hours`、`/plan-ceo-review`、`/plan-eng-review`、`/plan-design-review`、`/design-consultation`、`/design-shotgun`、`/design-html`、`/review`、`/ship`、`/land-and-deploy`、`/canary`、`/benchmark`、`/browse`、`/connect-chrome`、`/qa`、`/qa-only`、`/design-review`、`/setup-browser-cookies`、`/setup-deploy`、`/retro`、`/investigate`、`/document-release`、`/codex`、`/cso`、`/autoplan`、`/plan-devex-review`、`/devex-review`、`/careful`、`/freeze`、`/guard`、`/unfreeze`、`/gstack-upgrade`、`/learn`

## 注意事项

- 项目无测试框架、无 Lint、无 CI/CD 配置
- 数据库表结构无 SQL 文件，修改 Model 类后需手动同步数据库
- `application.yml` 中有硬编码的开发环境凭据，生产环境需替换
- 宠物进化分3阶段（100/500 经验值触发），养成属性随时间衰减
- API 统一返回 `ApiResponse<T>` 格式
