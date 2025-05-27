<template>
  <div class="login-page">
    <div class="content">
      <div class="title">
        <span>
          <el-image style="width: 50%; height: 50%" :src="Welcomelogo" />
          后台管理平台
        </span>
      </div>
      <div class="form-box">
        <div class="login-form">
          <div class="login-title">管理员登录</div>
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules">
            <el-form-item prop="adminName">
              <el-input
                v-model="loginForm.adminName"
                placeholder="请输入用户名"
                type="text"
                tabindex="1"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                placeholder="请输入密码"
                type="password"
                tabindex="2"
                :prefix-icon="Lock"
              />
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="remember" label="记住我" />
            </el-form-item>
            <el-button 
              :loading="loading" 
              type="primary" 
              style="width:100%;margin-bottom:20px;" 
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form>
        </div>
      </div>
      <div class="bottom-text">
        
        Copyright © etaoCnu All Rights Reserved 
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api'
import Welcomelogo from '../../public/etao_cnu_welcome.png'
export default {
  name: 'LoginPage',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const loading = ref(false)
    const remember = ref(false)

    const loginForm = reactive({
      adminName: '',
      password: ''
    })

    const loginRules = {
      adminName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, message: '用户名长度不能小于3位', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码不能少于6位', trigger: 'blur' }
      ]
    }

    // 获取缓存的登录信息
    const getLoginInfo = () => {
      const info = localStorage.getItem('login-info')
      if (info) {
        try {
          const data = JSON.parse(info)
          remember.value = true
          loginForm.adminName = data.adminName
          loginForm.password = data.password
        } catch (error) {
          console.warn(error)
        }
      }
    }

    // 保存登录信息
    const saveLoginInfo = () => {
      if (remember.value) {
        localStorage.setItem('login-info', JSON.stringify(loginForm))
      } else {
        localStorage.removeItem('login-info')
      }
    }

    // 登录
    const handleLogin = () => {
      if (!loginForm.adminName) {
        ElMessage.error('请输入账号')
        return
      }
      if (!loginForm.password) {
        ElMessage.error('请输入密码')
        return
      }

      loginFormRef.value?.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await adminApi.login({
              adminName: loginForm.adminName,
              password: loginForm.password
            })
            
            if (res.code === 200) {
              // 保存 token
              localStorage.setItem('adminToken', res.data)
              saveLoginInfo()
              ElMessage.success('登录成功')
              router.push('/')
            } else {
              ElMessage.error(res.data.message || '登录失败')
            }
          } catch (error) {
            console.error(error)
            ElMessage.error('登录失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    // 初始化时获取缓存的登录信息
    getLoginInfo()

    return {
      loginFormRef,
      loginForm,
      loginRules,
      loading,
      remember,
      handleLogin,
      User,
      Lock,
      Welcomelogo
    }
  }
}
</script>

<style lang="scss">
.login-page {
  width: 100%;
  height: 100vh;
  background-color: #2253dc;
  background-image: linear-gradient(45deg, #2253dc 0%, #4fb8f9 99%);
  position: relative;
  z-index: 1;
  overflow: hidden;
  
  &::after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url("@/assets/admin-login-bg.jpg");
    background-size: cover;
    background-position: center center;
    z-index: 2;
  }

  .content {
    position: absolute;
    z-index: 3;
    top: 50%;
    right: 10%;
    width: 100%;
    max-width: 480px;
    transform: translateY(-50%);

    .form-box {
      background-color: #81c7fa;
      border: solid 2px #3b9be5;
      padding: 10px;
      width: 100%;
      margin-bottom: 40px;
      border-radius: 4px;

      .login-form {
        background-color: #fff;
        padding: 40px 40px 30px;
        border-radius: 2px;

        .el-input {
          height: 40px;
          
          input {
            height: 40px;
            color: #606266;
            &:focus {
              border-color: #409eff;
            }
          }
          
          .el-input__prefix {
            left: 10px;
          }
        }

        .el-form-item {
          margin-bottom: 25px;
          
          &:last-child {
            margin-bottom: 15px;
          }
        }
      }

      .login-title {
        font-size: 22px;
        line-height: 22px;
        color: var(--el-color-primary);
        margin-bottom: 30px;
        text-align: center;
      }
    }

    .title {
      text-align: center;
      font-size: 28px;
      color: #fff;
      margin-bottom: 24px;
      
      span {
        line-height: 1;
      }
    }

    .bottom-text {
      width: 100%;
      max-width: 500px;
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      font-weight: 400;
      line-height: 22px;
      margin: 0 auto;
      text-align: center;
    }
  }
}
@media screen and (max-width: 500px) {
  .login-page {
    .content {
      right: 0;
      padding: 0 15px;
      
      .form-box {
        .login-form {
          padding: 30px 20px 20px;
        }
      }
    }
  }
}
</style> 