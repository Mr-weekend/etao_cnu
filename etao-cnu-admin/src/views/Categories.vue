<template>
  <div class="categories-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">添加分类</el-button>
        </div>
      </template>
      
      <el-table :data="categoryList" v-loading="loading">
        <el-table-column prop="categoryId" label="分类ID" width="200" />
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column label="分类图标" width="210">
          <template #default="{ row }">
            <el-image
              v-if="row.icon"
              :src="row.icon"
              :preview-src-list="[row.icon]"
              preview-teleported
              hide-on-click-modal
              fit="cover"
              style="width: 50px; height: 50px"
            />
            <span v-else>无图标</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="220" />
        <el-table-column label="操作" width="400" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
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

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加分类' : '编辑分类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-upload
            class="avatar-uploader"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleIconChange"
            :before-upload="beforeIconUpload"
          >
            <img v-if="previewUrl" :src="previewUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { categoryApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'CategoriesPage',
  components: {
    Plus
  },
  setup() {
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const categoryList = ref([])

    const dialogVisible = ref(false)
    const dialogType = ref('add')
    const formRef = ref(null)
    const currentCategoryId = ref(null)

    const form = reactive({
      categoryName: '',
      icon: ''
    })

    const previewUrl = ref('')

    const rules = {
      categoryName: [
        { required: true, message: '请输入分类名称', trigger: 'blur' }
      ]
    }

    const getCategoryList = async () => {
      loading.value = true
      try {
        const res = await categoryApi.getList({
          page: currentPage.value,
          size: pageSize.value
        })
        categoryList.value = res.data.list
        total.value = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      getCategoryList()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      getCategoryList()
    }

    const handleAdd = () => {
      dialogType.value = 'add'
      form.categoryName = ''
      form.icon = null
      previewUrl.value = ''
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogType.value = 'edit'
      currentCategoryId.value = row.categoryId
      form.categoryName = row.categoryName
      form.icon = null
      previewUrl.value = row.icon
      dialogVisible.value = true
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        await categoryApi.delete(row.categoryId)
        ElMessage.success('删除成功')
        getCategoryList()
      } catch (error) {
        console.error(error)
      }
    }

    const handleIconChange = (file) => {
      // 保存文件对象
      form.icon = file.raw
      // 创建预览URL
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        previewUrl.value = reader.result
      }
    }

    const beforeIconUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        ElMessage.error('上传图标只能是图片格式!')
      }
      if (!isLt2M) {
        ElMessage.error('上传图标大小不能超过 2MB!')
      }
      return isImage && isLt2M
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        if (dialogType.value === 'add') {
          const formData = new FormData()
          formData.append('categoryName', form.categoryName)
          if (form.icon) {
            formData.append('icon', form.icon)
          }
          await categoryApi.add(formData)
          ElMessage.success('添加成功')
        } else {
          await categoryApi.updateName(currentCategoryId.value, {
            categoryName: form.categoryName
          })
          if (form.icon) {
            const formData = new FormData()
            formData.append('icon', form.icon)
            await categoryApi.updateIcon(currentCategoryId.value, formData)
          }
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getCategoryList()
      } catch (error) {
        console.error(error)
      }
    }

    onMounted(() => {
      getCategoryList()
    })

    return {
      loading,
      currentPage,
      pageSize,
      total,
      categoryList,
      dialogVisible,
      dialogType,
      formRef,
      form,
      rules,
      previewUrl,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      handleIconChange,
      beforeIconUpload,
      handleSubmit
    }
  }
}
</script>

<style scoped lang="scss">
.categories-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
      
      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
  }
  
  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }
}
</style> 