<template>
  <div class="auth-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>认证审核</span>
        </div>
      </template>
      
      <el-table :data="authList" v-loading="loading">
        <el-table-column prop="userId" label="用户ID" width="200" />
        <el-table-column prop="userName" label="用户名" width="200" />
        <el-table-column label="认证图片" width="200">
          <template #default="{ row }">
            <el-image
              :src="row.authImage"
              :preview-src-list="[row.authImage]"
              preview-teleported
              hide-on-click-modal
              fit="cover"
              class="auth-image"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              @click="handlePass(row)"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              驳回
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

    <!-- 驳回原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="驳回原因"
      width="500px"
    >
      <el-form
        ref="rejectFormRef"
        :model="rejectForm"
        :rules="rejectRules"
        label-width="100px"
      >
        <el-form-item label="驳回原因" prop="reason">
          <el-input
            v-model="rejectForm.rejectReason"
            type="textarea"
            :rows="3"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AuthPage',
  setup() {
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const authList = ref([])

    const rejectDialogVisible = ref(false)
    const rejectFormRef = ref(null)
    const currentUserId = ref(null)

    const rejectForm = reactive({
      rejectReason: ''
    })

    const rejectRules = {
      rejectReason: [
        { required: true, message: '请输入驳回原因', trigger: 'blur' }
      ]
    }

    const getAuthList = async () => {
      loading.value = true
      try {
        const res = await userApi.getAuthList()
        authList.value = res.data.list
        total.value = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getAuthList()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getAuthList()
    }

    const handlePass = async (row) => {
      try {
        await ElMessageBox.confirm('确定要通过该用户的认证申请吗？', '提示', {
          type: 'warning',
          confirmButtonText:'确定',
          cancelButtonText:'取消'
        })
        await userApi.passAuth(row.userId)
        ElMessage.success('审核通过成功')
        getAuthList()
      } catch (error) {
        console.error(error)
      }
    }

    const handleReject = (row) => {
      currentUserId.value = row.userId
      rejectForm.rejectReason = ''
      rejectDialogVisible.value = true
    }

    const confirmReject = async () => {
      if (!rejectFormRef.value) return
      
      try {
        await rejectFormRef.value.validate()
        // await userApi.rejectAuth(currentUserId.value, {
        //   reason: rejectForm.reason
        // })
        console.log(rejectForm);
        
        await userApi.rejectAuth(currentUserId.value, rejectForm)
        ElMessage.success('驳回成功')
        rejectDialogVisible.value = false
        getAuthList()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getAuthList()
    })

    return {
      loading,
      currentPage,
      pageSize,
      total,
      authList,
      rejectDialogVisible,
      rejectFormRef,
      rejectForm,
      rejectRules,
      handleSizeChange,
      handleCurrentChange,
      handlePass,
      handleReject,
      confirmReject
    }
  }
}
</script>

<style scoped lang="scss">
.auth-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .auth-image {
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
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 