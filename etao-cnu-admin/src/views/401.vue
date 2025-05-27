<template>
  <div class="unauthorized-container">
    <el-result
      icon="warning"
      title="未授权"
      sub-title="当前登录已过期，请重新登录"
    >
      <template #extra>
        <el-button type="primary" @click="handleBack">返回登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'UnauthorizedPage',
  setup() {
    const router = useRouter()

    const handleBack = () => {
      router.push('/login')
    }

    return {
      handleBack
    }
  }
}
</script>

<style scoped lang="scss">
.unauthorized-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 