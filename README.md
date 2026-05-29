# EV Pet - AI 虚拟宠物养成平台

基于 AI 技术的虚拟宠物养成平台，支持多端访问（PC Web、移动端、管理后台），提供宠物养成、AI 对话、社交互动、打工系统等丰富功能。

## 项目架构

```
ev-pet/
├── ev-pet-backend/     # 后端服务 (Spring Boot)
├── ev-pet-admin/       # 管理后台 (Vue 3 + Element Plus)
├── ev-pet-pc-web/      # PC 端用户界面 (Vue 3 + Element Plus)
└── ev-pet-uniapp/      # 移动端 (UniApp - 微信小程序/H5)
```
## 技术栈

### 后端 (ev-pet-backend)

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Java | 17 | 编程语言 |
| MyBatis Plus | 3.5.4 | ORM 框架 |
| MySQL | - | 数据库 |
| Redis | - | 缓存 |
| JWT (jjwt) | 0.12.3 | 认证令牌 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Lombok | - | 代码简化工具 |

### 前端 (ev-pet-admin / ev-pet-pc-web)

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 前端框架 |
| Element Plus | 2.5+ | UI 组件库 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.2+ | 路由管理 |
| Axios | 1.6+ | HTTP 客户端 |
| ECharts | 5.4+ | 图表库 |
| Vite | 5.0+ | 构建工具 |
| Lottie | 5.12+ | 动画库 (仅 PC Web) |

### 移动端 (ev-pet-uniapp)

| 技术 | 版本 | 说明 |
|------|------|------|
| UniApp | 3.0+ | 跨平台框架 |
| Vue | 2.x | 前端框架 (Options API) |
| Webpack | 5.106+ | 构建工具 |

## 功能模块

### 核心功能

- **宠物养成**：喂食、清洁、玩耍等互动，经验值累积，3 阶段进化系统
- **AI 对话**：基于 MiniMax API 的智能对话，内容安全过滤
- **商店系统**：道具购买、装扮系统、宠物装扮
- **打工系统**：宠物打工赚取金币，多种工作类型
- **成就系统**：丰富的成就徽章，解锁奖励
- **社交系统**：好友互动、动态发布、社区交流

### 管理后台

- **用户管理**：用户列表、状态管理、权限控制
- **宠物管理**：宠物数据查看、属性调整
- **物品管理**：道具配置、上架下架
- **内容管理**：公告发布、活动管理
- **数据统计**：用户增长、活跃度、收入报表
- **系统配置**：全局参数设置、AI 模型配置

## 快速开始

### 环境要求

- **Java**: JDK 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 1. 后端启动

```bash
# 进入后端目录
cd ev-pet-backend

# 创建数据库
mysql -u root -p
CREATE DATABASE ev_pet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 修改配置文件
# vim src/main/resources/application.yml
# 配置数据库连接、Redis 连接、JWT 密钥等

# 安装依赖并启动
mvn clean install
mvn spring-boot:run
```

后端服务将启动在 `http://localhost:19081`

### 2. 管理后台启动

```bash
# 进入管理后台目录
cd ev-pet-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

管理后台将启动在 `http://localhost:5177`

### 3. PC 端启动

```bash
# 进入 PC 端目录
cd ev-pet-pc-web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

PC 端将启动在 `http://localhost:8010`

### 4. 移动端启动

```bash
# 进入移动端目录
cd ev-pet-uniapp

# 安装依赖
npm install

# H5 构建
npm run build:h5

# 或使用 HBuilderX 打开项目运行
```

## 项目配置

### 后端配置 (application.yml)

```yaml
server:
  port: 19081

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/ev_pet?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0

jwt:
  secret: your-secret-key-change-in-production
  expiration: 604800000  # 7天（毫秒）

# MiniMax AI 配置
minimax:
  api-key: your-api-key
  group-id: your-group-id
```

### 前端配置

前端项目通过 Vite 代理配置，将 `/api` 请求转发到后端服务：

```javascript
// vite.config.js
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:19081',
        changeOrigin: true
      }
    }
  }
})
```

## 数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| pets | 宠物表 |
| items | 物品表 |
| user_items | 用户物品关联表 |
| chat_messages | 聊天记录表 |
| work_records | 打工记录表 |
| friends | 好友关系表 |
| posts | 动态帖子表 |
| achievements | 成就表 |
| user_achievements | 用户成就关联表 |

## API 接口

### 认证相关

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/userinfo` - 获取用户信息

### 宠物相关

- `GET /api/pet/info` - 获取宠物信息
- `POST /api/pet/feed` - 喂食
- `POST /api/pet/clean` - 清洁
- `POST /api/pet/play` - 玩耍

### 商店相关

- `GET /api/shop/items` - 获取商品列表
- `POST /api/shop/buy` - 购买商品
- `POST /api/shop/equip` - 装备道具

### 社交相关

- `GET /api/social/friends` - 获取好友列表
- `POST /api/social/add-friend` - 添加好友
- `GET /api/social/posts` - 获取动态列表
- `POST /api/social/publish` - 发布动态

### 打工相关

- `GET /api/work/jobs` - 获取工作列表
- `POST /api/work/start` - 开始打工
- `POST /api/work/complete` - 完成打工

## 开发说明

### 后端架构

```
Controller → Service → Mapper → Database
    ↓           ↓         ↓
  参数校验    业务逻辑   数据访问
```

- **Controller**: 处理 HTTP 请求，参数校验
- **Service**: 核心业务逻辑
- **Mapper**: 数据库操作（MyBatis Plus）
- **DTO/VO**: 数据传输对象和视图对象

### 前端架构

```
Views → Components → Store → API
  ↓         ↓         ↓      ↓
页面视图   UI组件    状态管理  接口调用
```

- **Views**: 页面级组件
- **Components**: 可复用 UI 组件
- **Store**: Pinia 状态管理
- **API**: Axios 接口封装

## 部署指南

### 后端部署

```bash
# 打包
mvn clean package

# 运行
java -jar target/ev-pet-backend-1.0.0.jar

# 或使用 Docker
docker build -t ev-pet-backend .
docker run -p 19081:19081 ev-pet-backend
```

### 前端部署

```bash
# PC Web
cd ev-pet-pc-web
npm run build
# 将 dist/ 目录部署到 Nginx 或其他 Web 服务器

# 管理后台
cd ev-pet-admin
npm run build
# 将 dist/ 目录部署到 Nginx 或其他 Web 服务器
```

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态资源
    location / {
        root /path/to/ev-pet-pc-web/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://localhost:19081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 注意事项

1. **安全配置**：生产环境务必修改 JWT 密钥、数据库密码等敏感配置
2. **AI API**：需要配置有效的 MiniMax API Key 才能使用 AI 对话功能
3. **数据库**：首次运行需手动创建数据库，表结构由 MyBatis Plus 自动生成
4. **跨域配置**：开发环境已配置 CORS，生产环境需在 Nginx 中配置

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系开发团队。
