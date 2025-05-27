<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/goods">
          <el-icon><Goods /></el-icon>
          <span>闲置管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/auth">
          <el-icon><Check /></el-icon>
          <span>认证审核</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        
        <div class="header-right">
          
          <el-dropdown @command="handleCommand">
            
            <span class="el-dropdown-link">
              <el-avatar>管理员</el-avatar>
              {{ adminInfo.adminName }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { adminApi } from '@/api'
import { ElMessage } from 'element-plus'
import logo from '../../public/etao_cnu_welcome.png'
import {
  DataLine,
  User,
  Goods,
  List,
  Menu,
  Check,
  ArrowDown
} from '@element-plus/icons-vue'

export default {
  name: 'LayoutPage',
  components: {
    DataLine,
    User,
    Goods,
    List,
    Menu,
    Check,
    ArrowDown
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const adminInfo = ref({})

    const activeMenu = computed(() => route.path)

    const handleCommand = async (command) => {
      if (command === 'profile') {
        router.push('/profile')
      } else if (command === 'logout') {
        try {
          await adminApi.logout()
          localStorage.removeItem('adminToken')
          ElMessage.success('退出成功')
          router.push('/login')
        } catch (error) {
          console.error(error)
        }
      }
    }

    const getAdminInfo = async () => {
      try {
        const res = await adminApi.getInfo()
        adminInfo.value = res.data
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getAdminInfo()
    })

    return {
      adminInfo,
      activeMenu,
      handleCommand,
      logo
    }
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  .el-aside {
    background-color: #304156;
    .el-menu {
      border-right: none;
    }
  }
  .el-header {
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 20px;
    .header-right {
      .el-dropdown-link {
        cursor: pointer;
        display: flex;
        align-items: center;
      }
    }
  }
  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style> 