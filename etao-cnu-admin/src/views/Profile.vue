<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="用户名" prop="adminName">
          <el-input v-model="form.adminName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdateName">修改用户名</el-button>
        </el-form-item>
        
        <el-divider>修改密码</el-divider>
        
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProfilePage',
  setup() {
    const formRef = ref(null)
    const form = reactive({
      adminName: '',
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const rules = {
      adminName: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }

    const getAdminInfo = async () => {
      try {
        const res = await adminApi.getInfo()
        form.adminName = res.data.adminName
      } catch (error) {
        console.error(error)
      }
    }

    const handleUpdateName = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validateField('adminName')
        await adminApi.updateAdminName({
          newAdminName: form.adminName
        })
        ElMessage.success('修改用户名成功')
        setTimeout(() => {
          window.location.reload()
        }, 1500);
      } catch (error) {
        console.error(error)
      }
    }

    const handleUpdatePassword = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        await adminApi.updatePassword({
          oldPassword: form.oldPassword,
          newPassword: form.newPassword
        })
        ElMessage.success('修改密码成功')
        form.oldPassword = ''
        form.newPassword = ''
        form.confirmPassword = ''
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getAdminInfo()
    })

    return {
      formRef,
      form,
      rules,
      handleUpdateName,
      handleUpdatePassword
    }
  }
}
</script>

<style scoped lang="scss">
.profile-container {
  .profile-form {
    max-width: 500px;
    margin: 0 auto;
  }
}
</style> 