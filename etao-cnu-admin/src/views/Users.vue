<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>用户管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="用户名搜索"
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
      </template>
      
      <el-table :data="userList" v-loading="loading">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="userName" label="用户名" width="150" />
        <el-table-column label="头像" width="100">
          <template #default="{ row }">
            <el-image
              :size="40"
              :src="row.avatarUrl"
              :preview-src-list="[row.avatarUrl]"
              preview-teleported
              hide-on-click-modal
              class="avatar-image"
            />
          </template>
        </el-table-column>
        <el-table-column prop="userProfile" label="简介" width="150">
          <template #default="{ row }">
              {{ row.userProfile === null ? '暂无' : row.userProfile }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="100">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : row.status === 1 ? 'success' : 'info'">
              {{ row.status === 0 ? '违规' : row.status === 1 ? '正常' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="violationReason" label="封禁原因" />
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
              {{ row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1 || row.status === 2"
              type="danger"
              size="small"
              @click="handleBan(row)"
            >
              封禁
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleUnban(row)"
            >
              解禁
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

    <!-- 封禁原因对话框 -->
    <el-dialog
      v-model="banDialogVisible"
      title="封禁用户"
      width="500px"
    >
      <el-form
        ref="banFormRef"
        :model="banForm"
        :rules="banRules"
        label-width="100px"
      >
        <el-form-item label="封禁原因" prop="violationReason">
          <el-input
            v-model="banForm.violationReason"
            type="textarea"
            :rows="3"
            placeholder="请输入封禁原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="banDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBan">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'UsersPage',
  components: {
    Search
  },
  setup() {
    const loading = ref(false)
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const userList = ref([])

    const banDialogVisible = ref(false)
    const banFormRef = ref(null)
    const currentUserId = ref(null)

    const banForm = reactive({
      violationReason: ''
    })

    const banRules = {
      violationReason: [
        { required: true, message: '请输入封禁原因', trigger: 'blur' }
      ]
    }

    const getUserList = async () => {
      loading.value = true
      try {
        const res = await userApi.getList({
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value
        })
        userList.value = res.data.list
        total.value = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      getUserList()
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getUserList()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getUserList()
    }

    const handleBan = (row) => {
      currentUserId.value = row.userId
      banDialogVisible.value = true
    }

    const confirmBan = async () => {
      if (!banFormRef.value) return
      
      try {
        await banFormRef.value.validate()
        await userApi.banUser(currentUserId.value, banForm)
        ElMessage.success('封禁成功')
        banDialogVisible.value = false
        banForm.violationReason = ''
        getUserList()
      } catch (error) {
        console.error(error)
      }
    }

    const handleUnban = async (row) => {
      try {
        await ElMessageBox.confirm('确定要解禁该用户吗？', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        await userApi.unbanUser(row.userId)
        ElMessage.success('解禁成功')
        getUserList()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getUserList()
    })

    return {
      loading,
      searchKeyword,
      currentPage,
      pageSize,
      total,
      userList,
      banDialogVisible,
      banFormRef,
      banForm,
      banRules,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleBan,
      confirmBan,
      handleUnban
    }
  }
}
</script>

<style scoped lang="scss">
.users-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

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

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 