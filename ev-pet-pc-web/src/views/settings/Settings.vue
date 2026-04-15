<template>
  <div class="settings-page">
    <el-row :gutter="24">
      <el-col :span="16">
        <div class="settings-card card">
          <h3>个人设置</h3>
          
          <el-form label-width="100px">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <div class="current-avatar">{{ userInfo?.nickname?.charAt(0) || '我' }}</div>
                <el-button size="small">更换头像</el-button>
              </div>
            </el-form-item>
            
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" />
            </el-form-item>
            
            <el-form-item label="性别">
              <el-radio-group v-model="userInfo.gender">
                <el-radio label="secret">保密</el-radio>
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="生日">
              <el-date-picker v-model="userInfo.birthday" type="date" placeholder="选择日期" />
            </el-form-item>
            
            <el-form-item label="简介">
              <el-input v-model="userInfo.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." />
            </el-form-item>
          </el-form>
          
          <div class="form-actions">
            <el-button type="primary" @click="saveProfile">保存设置</el-button>
          </div>
        </div>
        
        <div class="settings-card card">
          <h3>通知设置</h3>
          <el-form label-width="120px">
            <el-form-item label="系统通知">
              <el-switch v-model="settings.systemNotify" />
            </el-form-item>
            <el-form-item label="宠物状态提醒">
              <el-switch v-model="settings.petNotify" />
            </el-form-item>
            <el-form-item label="好友动态">
              <el-switch v-model="settings.friendNotify" />
            </el-form-item>
            <el-form-item label="活动推送">
              <el-switch v-model="settings.activityNotify" />
            </el-form-item>
          </el-form>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="account-card card">
          <h3>账号安全</h3>
          <div class="account-item">
            <span class="account-label">绑定手机</span>
            <span class="account-value">{{ userInfo.phone || '未绑定' }}</span>
            <el-button size="small" type="primary" plain>修改</el-button>
          </div>
          <div class="account-item">
            <span class="account-label">微信绑定</span>
            <span class="account-value">已绑定</span>
            <el-button size="small" type="primary" plain>解绑</el-button>
          </div>
          <div class="account-item">
            <span class="account-label">修改密码</span>
            <el-button size="small" type="primary" plain>修改</el-button>
          </div>
        </div>
        
        <div class="about-card card">
          <h3>关于</h3>
          <div class="about-info">
            <p>EV Pet v1.0.0</p>
            <p class="about-desc">AI虚拟宠物养成平台</p>
          </div>
          <div class="about-links">
            <a href="#">用户协议</a>
            <a href="#">隐私政策</a>
            <a href="#">联系客服</a>
          </div>
        </div>
        
        <div class="logout-card card">
          <el-button type="danger" plain class="logout-btn" @click="logout">退出登录</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({
  nickname: userStore.userInfo?.nickname || '用户',
  gender: 'secret',
  birthday: '',
  bio: '',
  phone: '138****8888'
})

const settings = ref({
  systemNotify: true,
  petNotify: true,
  friendNotify: false,
  activityNotify: true
})

const saveProfile = () => {
  ElMessage.success('设置已保存')
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style scoped>
.settings-page { padding: 0; }

.settings-card { margin-bottom: 24px; }
.settings-card h3 { margin-bottom: 24px; }

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-avatar {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #FFB3C6 0%, #D5AAFF 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  font-weight: 600;
}

.form-actions {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #F0F0F0;
}

.account-card h3, .about-card h3, .logout-card { margin-bottom: 16px; }

.account-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #F0F0F0;
}

.account-label {
  width: 100px;
  color: var(--text-secondary);
}

.account-value {
  flex: 1;
  color: var(--text-primary);
}

.about-info {
  text-align: center;
  padding: 16px 0;
}

.about-info p { margin-bottom: 4px; }
.about-desc { color: var(--text-secondary); font-size: 12px; }

.about-links {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.about-links a {
  color: var(--primary);
  text-decoration: none;
  font-size: 12px;
}

.logout-btn { width: 100%; }
</style>
