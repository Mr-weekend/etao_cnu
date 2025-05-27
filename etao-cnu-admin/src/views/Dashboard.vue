<template>
  <div class="admin-dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-order">
          <template #header>
            <div class="card-header">
              <span>总订单数</span>
              <el-tooltip content="所有已创建的订单总数" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="card-content">
            <div class="left-part">
              <div class="number">
                {{ orderCount }}
              </div>
              <div class="label">订单</div>
            </div>
            <div class="icon-wrapper order">
              <el-icon class="icon"><ShoppingCart /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-user">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
              <el-tooltip content="平台注册用户总数" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="card-content">
            <div class="left-part">
              <div class="number">
                {{ userCount }}
              </div>
              <div class="label">用户</div>
            </div>
            <div class="icon-wrapper user">
              <el-icon class="icon"><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-goods">
          <template #header>
            <div class="card-header">
              <span>总闲置数</span>
              <el-tooltip content="所有闲置物品总数" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="card-content">
            <div class="left-part">
              <div class="number">
                {{ goodsCount }}
              </div>
              <div class="label">闲置</div>
            </div>
            <div class="icon-wrapper goods">
              <el-icon class="icon"><Goods /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card card-category">
          <template #header>
            <div class="card-header">
              <span>总分类数</span>
              <el-tooltip content="当前可用的闲置分类总数" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="card-content">
            <div class="left-part">
              <div class="number">
                {{ categoryCount }}
              </div>
              <div class="label">分类</div>
            </div>
            <div class="icon-wrapper category">
              <el-icon class="icon"><Menu /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="18">
        <el-card shadow="hover" class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-text">
              <h2>欢迎使用易淘重师后台管理平台</h2>
              <p>今天是 {{ currentDate }}，祝您生活愉快！</p>
            </div>
            <div class="quick-stats">
              <div class="stat-item">
                <el-icon class="stat-icon"><Timer /></el-icon>
                <span class="stat-label">当前时间</span>
                <div class="stat-value">{{ currentTime }}</div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Calendar /></el-icon>
                <span class="stat-label">在线时长</span>
                <div class="stat-value">{{ onlineTime }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="quick-entry">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="entry-list">
            <div class="entry-item" @click="$router.push('/users')">
              <el-icon class="entry-icon"><User /></el-icon>
              <span>用户管理</span>
            </div>
            <div class="entry-item" @click="$router.push('/goods')">
              <el-icon class="entry-icon"><Goods /></el-icon>
              <span>闲置管理</span>
            </div>
            <div class="entry-item" @click="$router.push('/orders')">
              <el-icon class="entry-icon"><ShoppingCart /></el-icon>
              <span>订单管理</span>
            </div>
            <div class="entry-item" @click="$router.push('/categories')">
              <el-icon class="entry-icon"><Menu /></el-icon>
              <span>分类管理</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { orderApi, userApi, goodsApi, categoryApi } from '@/api'
import { ShoppingCart, User, Goods, Menu, InfoFilled, Timer, Calendar } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

export default {
  name: 'DashboardPage',
  components: {
    ShoppingCart,
    User,
    Goods,
    Menu,
    InfoFilled,
    Timer,
    Calendar
  },
  setup() {
    const orderCount = ref(0)
    const userCount = ref(0)
    const goodsCount = ref(0)
    const categoryCount = ref(0)
    const currentTime = ref('')
    const currentDate = ref('')
    const onlineTime = ref('00:00:00')
    let timer = null
    let startTime = Date.now()

    const updateTime = () => {
      const now = dayjs()
      currentTime.value = now.format('HH:mm:ss')
      currentDate.value = now.format('YYYY年MM月DD日')
      
      const diff = Math.floor((Date.now() - startTime) / 1000)
      const hours = Math.floor(diff / 3600)
      const minutes = Math.floor((diff % 3600) / 60)
      const seconds = diff % 60
      onlineTime.value = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    }

    const getData = async () => {
      try {
        const [orderRes, userRes, goodsRes, categoryRes] = await Promise.all([
          orderApi.getCount(),
          userApi.getList({ page: 1, size: 1 }),
          goodsApi.getList({ page: 1, size: 1 }),
          categoryApi.getList({ page: 1, size: 1 })
        ])
        
        orderCount.value = orderRes.data
        userCount.value = userRes.data.total
        goodsCount.value = goodsRes.data.total
        categoryCount.value = categoryRes.data.total
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getData()
      updateTime()
      timer = setInterval(updateTime, 1000)
    })

    onUnmounted(() => {
      if (timer) {
        clearInterval(timer)
      }
    })

    return {
      orderCount,
      userCount,
      goodsCount,
      categoryCount,
      currentTime,
      currentDate,
      onlineTime
    }
  }
}
</script>

<style scoped lang="scss">
.admin-dashboard-container {
  .data-card {
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .el-icon {
        font-size: 16px;
        color: #909399;
        cursor: help;
      }
    }
    
    .card-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 0;
      
      .left-part {
        .number {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          line-height: 1;
          margin-bottom: 8px;
        }
        
        .label {
          font-size: 14px;
          color: #909399;
        }
      }
      
      .icon-wrapper {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s;
        
        .icon {
          font-size: 32px;
          color: #fff;
        }
        
        &.order {
          background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
        }
        
        &.user {
          background: linear-gradient(135deg, #722ed1 0%, #eb2f96 100%);
        }
        
        &.goods {
          background: linear-gradient(135deg, #52c41a 0%, #13c2c2 100%);
        }
        
        &.category {
          background: linear-gradient(135deg, #fa8c16 0%, #faad14 100%);
        }
      }
    }
    
    &:hover {
      .icon-wrapper {
        transform: scale(1.1) rotate(12deg);
      }
    }
  }
}

.welcome-card {
  height: 200px;
  
  .welcome-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    
    .welcome-text {
      h2 {
        margin: 0 0 10px 0;
        font-size: 24px;
        background: linear-gradient(to right, #1890ff, #36cfc9);
        -webkit-background-clip: text;
        color: transparent;
      }
      
      p {
        margin: 0;
        color: #606266;
      }
    }
    
    .quick-stats {
      display: flex;
      gap: 40px;
      margin-top: 20px;
      
      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .stat-icon {
          font-size: 24px;
          color: #409EFF;
          margin-bottom: 8px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .stat-value {
          font-size: 20px;
          color: #303133;
          font-family: monospace;
        }
      }
    }
  }
}

.quick-entry {
  height: 200px;
  
  .entry-list {
    display: grid;
    grid-template-columns: repeat(2, 2fr);
    // gap: 16px;
    
    .entry-item {
      height: 60px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover {
        background-color: #f5f7fa;
        transform: translateY(-2px);
      }
      
      .entry-icon {
        font-size: 24px;
        color: #409EFF;
        margin-bottom: 4px;
      }
      
      span {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style> 