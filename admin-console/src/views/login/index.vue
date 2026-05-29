<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Ship, Lock, User } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: () => t('login.usernameRequired'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: () => t('login.passwordRequired'), trigger: 'blur' }
  ]
})

async function handleLogin() {
  const form = formRef.value
  if (!form) return

  await form.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(loginForm.username, loginForm.password)
      await userStore.fetchProfile()
      ElMessage.success(t('common.success'))
      router.push('/dashboard')
    } catch {
      ElMessage.error(t('common.error'))
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-page">
    <!-- Left Panel -->
    <div class="login-left">
      <div class="brand-section">
        <div class="brand-logo">
          <el-icon :size="48" color="#fff"><Ship /></el-icon>
        </div>
        <h1 class="brand-name">{{ t('login.brandName') }}</h1>
        <p class="brand-tagline">{{ t('login.brandTagline') }}</p>
      </div>

      <div class="feature-list">
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="20"><Ship /></el-icon>
          </div>
          <div class="feature-text">
            <div class="feature-title">{{ t('login.feature1Title') }}</div>
            <div class="feature-desc">{{ t('login.feature1Desc') }}</div>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="20"><Ship /></el-icon>
          </div>
          <div class="feature-text">
            <div class="feature-title">{{ t('login.feature2Title') }}</div>
            <div class="feature-desc">{{ t('login.feature2Desc') }}</div>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">
            <el-icon :size="20"><Ship /></el-icon>
          </div>
          <div class="feature-text">
            <div class="feature-title">{{ t('login.feature3Title') }}</div>
            <div class="feature-desc">{{ t('login.feature3Desc') }}</div>
          </div>
        </div>
      </div>

      <div class="login-footer">
        <p>{{ t('login.copyright') }}</p>
      </div>
    </div>

    <!-- Right Panel -->
    <div class="login-right">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">{{ t('login.title') }}</h2>
          <p class="login-subtitle">{{ t('login.subtitle') }}</p>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          label-width="0"
          size="large"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="t('login.username')"
              :prefix-icon="User"
              class="login-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              :placeholder="t('login.password')"
              :prefix-icon="Lock"
              show-password
              class="login-input"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ t('login.loginBtn') }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-tips">
          <el-icon :size="14" class="tips-icon"><Info-Filled /></el-icon>
          <span>{{ t('login.demoAccount') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

/* Left Panel */
.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 60px;
  background: linear-gradient(160deg, #1a3a5c 0%, #0d2137 100%);
  color: #fff;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 80%;
  height: 100%;
  background: radial-gradient(ellipse, rgba(64, 158, 255, 0.08) 0%, transparent 70%);
  pointer-events: none;
}

.brand-section {
  position: relative;
  z-index: 1;
}

.brand-logo {
  width: 72px;
  height: 72px;
  background: rgba(64, 158, 255, 0.15);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.brand-name {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.brand-tagline {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.feature-list {
  position: relative;
  z-index: 1;
  margin-top: 60px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.feature-icon {
  width: 44px;
  height: 44px;
  background: rgba(64, 158, 255, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  border: 1px solid rgba(64, 158, 255, 0.15);
}

.feature-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.login-footer {
  position: relative;
  z-index: 1;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.login-footer p {
  margin: 0;
}

/* Right Panel */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 48px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.login-header {
  margin-bottom: 32px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  padding: 4px 12px;
}

.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  margin-top: 8px;
}

.login-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 12px;
  color: #909399;
}

.tips-icon {
  color: #909399;
}

/* Responsive */
@media (max-width: 900px) {
  .login-left {
    display: none;
  }

  .login-right {
    flex: 1;
    padding: 24px;
  }

  .login-card {
    padding: 32px 24px;
  }
}
</style>
