<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>订单管理</span>
          <div class="search-area">
            <el-input
              v-model="searchKeyword"
              placeholder="闲置关键字或用户ID"
              style="width: 200px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <el-table :data="orderList" v-loading="loading">
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column label="闲置物品信息" min-width="300">
          <template #default="{ row }">
            <div class="goods-info">
              <el-image
                v-if="row.goodsInfo.imageUrls && row.goodsInfo.imageUrls.length"
                :src="row.goodsInfo.imageUrls[0]"
                :preview-src-list="row.goodsInfo.imageUrls"
                :initial-index="0"
                fit="cover"
                class="goods-image"
                preview-teleported
                hide-on-click-modal
              />
              <span v-else class="no-image">无图片</span>
              <div class="goods-detail">
                <div class="description">{{ row.goodsInfo.description }}</div>
                <div class="price">¥{{ row.goodsInfo.price }}</div>
                <div class="category">分类：{{ row.goodsInfo.categoryName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="买家信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-image
                v-if="row.buyer && row.buyer.avatarUrl"
                :src="row.buyer.avatarUrl"
                :preview-src-list="[row.buyer.avatarUrl]"
                class="avatar-image"
                preview-teleported
                hide-on-click-modal
              />
              <span v-else class="no-avatar">无头像</span>
              <div class="user-detail">
                <div class="name">{{ row.buyer?.userName || '未知用户' }}</div>
                <div class="id">ID: {{ row.buyer?.userId || '未知' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="卖家信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-image
                v-if="row.goodsInfo.publisher && row.goodsInfo.publisher.avatarUrl"
                :src="row.goodsInfo.publisher.avatarUrl"
                :preview-src-list="[row.goodsInfo.publisher.avatarUrl]"
                class="avatar-image"
                preview-teleported
                hide-on-click-modal
              />
              <span v-else class="no-avatar">无头像</span>
              <div class="user-detail">
                <div class="name">{{ row.goodsInfo.publisher?.userName || '未知用户' }}</div>
                <div class="id">ID: {{ row.goodsInfo.publisher?.userId || '未知' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : row.status === 'waiting' ? 'warning' : 'info'">
              {{ row.status === 'completed' ? '已完成' : row.status === 'waiting' ? '待收货' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
      destroy-on-close
    >
      <div v-if="currentOrder" class="order-detail-dialog">
        <div class="section-title">闲置物品信息</div>
        <div class="goods-section">
          <div class="image-gallery">
            <div class="image-list">
              <el-image
                v-for="(url, index) in currentOrder.goodsInfo.imageUrls"
                :key="index"
                :src="url"
                fit="contain"
                class="preview-image"
                :preview-src-list="currentOrder.goodsInfo.imageUrls"
                :initial-index="index"
                preview-teleported
                hide-on-click-modal
              />
            </div>
          </div>
          
          <div class="info-section">
            <div class="info-item">
              <span class="label">闲置物品ID：</span>
              <span class="value">{{ currentOrder.goodsInfo.goodsId }}</span>
            </div>
            <div class="info-item">
              <span class="label">闲置物品描述：</span>
              <span class="value">{{ currentOrder.goodsInfo.description }}</span>
            </div>
            <div class="info-item">
              <span class="label">价格：</span>
              <span class="value price">¥{{ currentOrder.goodsInfo.price }}</span>
            </div>
            <div class="info-item">
              <span class="label">分类：</span>
              <span class="value">{{ currentOrder.goodsInfo.categoryName }}</span>
            </div>
          </div>
        </div>

        <div class="section-title">买家信息</div>
        <div class="user-section">
          <div class="user-card">
            <el-image
              v-if="currentOrder.buyer && currentOrder.buyer.avatarUrl"
              :src="currentOrder.buyer.avatarUrl"
              :preview-src-list="[currentOrder.buyer.avatarUrl]"
              class="avatar-image"
              preview-teleported
              hide-on-click-modal
            />
            <span v-else class="no-avatar">无头像</span>
            <div class="user-info">
              <div class="name">{{ currentOrder.buyer?.userName || '未知用户' }}</div>
              <div class="id">ID: {{ currentOrder.buyer?.userId || '未知' }}</div>
            </div>
          </div>
        </div>

        <div class="section-title">卖家信息</div>
        <div class="user-section">
          <div class="user-card">
            <el-image
              v-if="currentOrder.goodsInfo.publisher && currentOrder.goodsInfo.publisher.avatarUrl"
              :src="currentOrder.goodsInfo.publisher.avatarUrl"
              :preview-src-list="[currentOrder.goodsInfo.publisher.avatarUrl]"
              class="avatar-image"
              preview-teleported
              hide-on-click-modal
            />
            <span v-else class="no-avatar">无头像</span>
            <div class="user-info">
              <div class="name">{{ currentOrder.goodsInfo.publisher?.userName || '未知用户' }}</div>
              <div class="id">ID: {{ currentOrder.goodsInfo.publisher?.userId || '未知' }}</div>
            </div>
          </div>
        </div>

        <div class="section-title">订单信息</div>
        <div class="order-info">
          <div class="info-item">
            <span class="label">订单ID：</span>
            <span class="value">{{ currentOrder.orderId }}</span>
          </div>
          <div class="info-item">
            <span class="label">订单状态：</span>
            <el-tag :type="currentOrder.status === 'completed' ? 'success' : currentOrder.status === 'waiting' ? 'warning' : 'info'">
              {{ currentOrder.status === 'completed' ? '已完成' : currentOrder.status === 'waiting' ? '待收货' : '已取消' }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value">{{ currentOrder.createdAt }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'OrdersPage',
  components: {
    Search
  },
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const orderList = ref([])
    const detailDialogVisible = ref(false)
    const currentOrder = ref(null)

    const getOrderList = async () => {
      loading.value = true
      try {
        let res
        if (searchKeyword.value) {
          // 尝试作为用户ID搜索
          if (/^\d+$/.test(searchKeyword.value)) {
            res = await orderApi.searchByUser({
              userId: searchKeyword.value,
              page: currentPage.value,
              size: pageSize.value
            })
          } else {
            // 作为闲置物品关键字搜索
            res = await orderApi.searchByGoods({
              goodsKeyword: searchKeyword.value,
              page: currentPage.value,
              size: pageSize.value
            })
          }
        } else {
          res = await orderApi.getList({
            page: currentPage.value,
            size: pageSize.value
          })
        }
        orderList.value = res.data.list
        total.value = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      getOrderList()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getOrderList()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getOrderList()
    }

    const handleView = (row) => {
      currentOrder.value = row
      detailDialogVisible.value = true
    }

    onMounted(() => {
      getOrderList()
    })

    return {
      loading,
      searchKeyword,
      currentPage,
      pageSize,
      total,
      orderList,
      detailDialogVisible,
      currentOrder,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleView
    }
  }
}
</script>

<style scoped lang="scss">
.orders-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-area {
    display: flex;
    align-items: center;
  }
  
  .goods-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .goods-image {
      width: 80px;
      height: 80px;
      border-radius: 4px;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.05);
      }
    }
    
    .no-image {
      width: 80px;
      height: 80px;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #909399;
      border-radius: 4px;
    }
    
    .goods-detail {
      flex: 1;
      
      .description {
        margin-bottom: 8px;
        font-size: 14px;
        color: #303133;
      }
      
      .price {
        color: #f56c6c;
        font-weight: bold;
        font-size: 16px;
        margin-bottom: 4px;
      }
      
      .category {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .avatar-image {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      cursor: pointer;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.1);
      }
    }
    
    .no-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #909399;
      font-size: 12px;
    }
    
    .user-detail {
      .name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .id {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.order-detail-dialog {
  .section-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin: 20px 0 15px;
    padding-left: 10px;
    border-left: 4px solid #409EFF;
  }
  
  .goods-section {
    .image-gallery {
      margin-bottom: 20px;
      
      .image-list {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;
        
        .preview-image {
          width: 200px;
          height: 200px;
          border-radius: 4px;
          cursor: pointer;
          transition: transform 0.3s;
          
          &:hover {
            transform: scale(1.05);
          }
        }
      }
    }
    
    .info-section {
      .info-item {
        margin-bottom: 15px;
        display: flex;
        align-items: flex-start;
        
        .label {
          width: 100px;
          color: #909399;
        }
        
        .value {
          flex: 1;
          color: #303133;
          
          &.price {
            color: #f56c6c;
            font-weight: bold;
            font-size: 16px;
          }
        }
      }
    }
  }
  
  .user-section {
    .user-card {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 8px;
      
      .avatar-image {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        cursor: pointer;
        transition: transform 0.3s;
        
        &:hover {
          transform: scale(1.1);
        }
      }
      
      .no-avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background-color: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 14px;
      }
      
      .user-info {
        .name {
          font-size: 16px;
          color: #303133;
          margin-bottom: 8px;
        }
        
        .id {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
  
  .order-info {
    .info-item {
      margin-bottom: 15px;
      display: flex;
      align-items: center;
      
      .label {
        width: 100px;
        color: #909399;
      }
      
      .value {
        flex: 1;
        color: #303133;
      }
    }
  }
}
</style> 