<template>
  <div class="goods-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>闲置管理</span>
          <div class="search-area">
            <el-input
              v-model="searchKeyword"
              placeholder="关键字"
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
            <el-select v-model="searchStatus" placeholder="闲置状态" clearable style="width: 120px; margin-left: 10px" @change="handleSearch">
              <el-option label="已发布" value="published" />
              <el-option label="已售出" value="sold" />
              <el-option label="已下架" value="removed" />
            </el-select>
            <el-select v-model="searchCategoryId" placeholder="闲置分类" clearable style="width: 120px; margin-left: 10px" @change="handleSearch">
              <el-option
                v-for="item in categoryOptions"
                :key="item.categoryId"
                :label="item.categoryName"
                :value="item.categoryId"
              />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="goodsList" v-loading="loading">
        <el-table-column prop="goodsId" label="闲置ID" width="100" />
        <el-table-column label="闲置信息" min-width="250">
          <template #default="{ row }">
            <div class="goods-info">
              <el-image
                v-if="row.imageUrls && row.imageUrls.length"
                :src="row.imageUrls[0]"
                :preview-src-list="row.imageUrls"
                :initial-index="0"
                fit="cover"
                class="goods-image"
                preview-teleported
                hide-on-click-modal
              />
              <span v-else class="no-image">无图片</span>
              <div class="goods-detail">
                <div class="description">
                    {{ row.description }}
                </div>
                <div class="price">¥{{ row.price }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : row.status === 'sold' ? 'warning' : 'info'">
              {{ row.status === 'published' ? '已发布' : row.status === 'sold' ? '已售出' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="collectCount" label="收藏量" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="170" />
        <el-table-column label="发布者" width="180">
          <template #default="{ row }">
            <div class="user-info">
              <el-image
                v-if="row.publisher && row.publisher.avatarUrl"
                :src="row.publisher.avatarUrl"
                :preview-src-list="[row.publisher.avatarUrl]"
                class="avatar-image"
                preview-teleported
                hide-on-click-modal
              />
              <span v-else class="no-avatar">无头像</span>
              <div class="user-detail">
                <div class="name">{{ row.publisher?.userName || '未知用户' }}</div>
                <div class="id">ID: {{ row.publisher?.userId || '未知' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
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

    <!-- 闲置详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="闲置详情"
      width="800px"
      destroy-on-close
    >
      <div v-if="currentGoods" class="goods-detail-dialog">
        <div class="goods-section">
          <div class="image-gallery">
            <div class="image-list">
              <el-image
                v-for="(url, index) in currentGoods.imageUrls"
                :key="index"
                :src="url"
                fit="contain"
                class="preview-image"
                :preview-src-list="currentGoods.imageUrls"
                :initial-index="index"
                preview-teleported
                hide-on-click-modal
              />
            </div>
          </div>
          
          <div class="info-section">
            <div class="info-item">
              <span class="label">闲置ID：</span>
              <span class="value">{{ currentGoods.goodsId }}</span>
            </div>
            <div class="info-item">
              <span class="label">闲置描述：</span>
              <span class="value">{{ currentGoods.description }}</span>
            </div>
            <div class="info-item">
              <span class="label">价格：</span>
              <span class="value price">¥{{ currentGoods.price }}</span>
            </div>
            <div class="info-item">
              <span class="label">分类：</span>
              <span class="value">{{ currentGoods.categoryName }}</span>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag :type="currentGoods.status === 'published' ? 'success' : 'info'">
                {{ currentGoods.status === 'published' ? '已发布' : '已售出' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">浏览量：</span>
              <span class="value">{{ currentGoods.viewCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">收藏量：</span>
              <span class="value">{{ currentGoods.collectCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">发布时间：</span>
              <span class="value">{{ currentGoods.publishTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">发布者：</span>
              <div class="user-info">
                <el-image
                  v-if="currentGoods.publisher && currentGoods.publisher.avatarUrl"
                  :src="currentGoods.publisher.avatarUrl"
                  :preview-src-list="[currentGoods.publisher.avatarUrl]"
                  class="avatar-image"
                  preview-teleported
                  hide-on-click-modal
                />
                <span v-else class="no-avatar">无头像</span>
                <div class="user-detail">
                  <div class="name">{{ currentGoods.publisher?.userName || '未知用户' }}</div>
                  <div class="id">ID: {{ currentGoods.publisher?.userId || '未知' }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { goodsApi, categoryApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'GoodsPage',
  components: {
    Search
  },
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const searchStatus = ref('')
    const searchCategoryId = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const goodsList = ref([])
    const categoryOptions = ref([])
    const detailDialogVisible = ref(false)
    const currentGoods = ref(null)

    const getCategoryOptions = async () => {
      try {
        const res = await categoryApi.getList({ page: 1, size: 100 })
        categoryOptions.value = res.data.list
      } catch (error) {
        console.error(error)
      }
    }

    const getGoodsList = async () => {
      loading.value = true
      try {
        const res = searchKeyword.value || searchStatus.value || searchCategoryId.value
          ? await goodsApi.search({
              page: currentPage.value,
              size: pageSize.value,
              keyword: searchKeyword.value,
              status: searchStatus.value,
              categoryId: searchCategoryId.value
            })
          : await goodsApi.getList({
              page: currentPage.value,
              size: pageSize.value,
              status: searchStatus.value
            })
        goodsList.value = res.data.list
        total.value = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      getGoodsList()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getGoodsList()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getGoodsList()
    }

    const handleView = (row) => {
      currentGoods.value = row
      detailDialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该闲置吗？', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        await goodsApi.delete(row.goodsId)
        ElMessage.success('删除成功')
        getGoodsList()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getCategoryOptions()
      getGoodsList()
    })

    return {
      loading,
      searchKeyword,
      searchStatus,
      searchCategoryId,
      currentPage,
      pageSize,
      total,
      goodsList,
      categoryOptions,
      detailDialogVisible,
      currentGoods,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleView,
      handleDelete
    }
  }
}
</script>

<style scoped lang="scss">
.goods-container {
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
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        word-break: break-all;
      }
      
      .price {
        color: #f56c6c;
        font-weight: bold;
        font-size: 16px;
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
      object-fit: cover;
      
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

.goods-detail-dialog {
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
}
</style> 