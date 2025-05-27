import { publishGoods, PublishGoodsData, getCategoryList, Category, updateGoods, UpdateGoodsData, ImageUpdateType, getGoodsDetail } from '../../services/api';
Page({
  data: {
    images: [] as string[],
    description: '',
    price: '',
    maxImageCount: 9,
    categories: [] as Category[], // 闲置物品分类列表
    categoryIndex: -1, // 选中的分类索引
    isSubmitting: false,
    isEdit: false,
    goodsId: 0,
    editGoodsInfo: null as any, // 临时存储编辑闲置物品信息
    originalImages: [] as string[], // 保存原始图片列表
    deletedImages: [] as string[], // 保存要删除的图片列表
    imageUpdateType: 'keep' as ImageUpdateType // 默认保持原有图片
  },

  onLoad(options: any) {
    // 如果是编辑模式，先保存闲置物品信息
    if (options.type === 'edit' && options.goodsInfo) {
      const goodsInfo = JSON.parse(decodeURIComponent(options.goodsInfo));
      this.setData({
        editGoodsInfo: goodsInfo,
        isEdit: true,
        originalImages: goodsInfo.imageUrls || [] // 保存原始图片列表
      });
    }
    // 加载分类数据
    this.loadCategories();
  },

  // 加载闲置物品分类
  async loadCategories() {
    try {
      const res = await getCategoryList();
      if (res.code === 200) {
        this.setData({
          categories: res.data.list
        });
        
        // 如果是编辑模式，在分类加载完成后设置闲置物品信息
        if (this.data.isEdit && this.data.editGoodsInfo) {
          const goodsInfo = this.data.editGoodsInfo;
          const categoryIndex = this.data.categories.findIndex(
            item => item.categoryId === goodsInfo.categoryId
          );
          
          this.setData({
            goodsId: goodsInfo.goodsId,
            description: goodsInfo.description,
            price: goodsInfo.price.toString(),
            images: goodsInfo.imageUrls,
            categoryIndex: categoryIndex
          });
        }
      } else {
        throw new Error(res.message || '获取分类失败');
      }
    } catch (error) {
      console.error('获取分类列表失败:', error);
      wx.showToast({
        title: '获取分类失败',
        icon: 'error'
      });
    }
  },

  // 选择分类
  onCategoryChange(e: any) {
    this.setData({
      categoryIndex: parseInt(e.detail.value)
    });
  },

  // 删除图片时的处理
  removeImage(e: any) {
    const index = e.currentTarget.dataset.index;
    const { images, originalImages, deletedImages } = this.data;
    const removedImage = images[index];
    
    // 创建新的图片数组
    const updatedImages = [...images];
    updatedImages.splice(index, 1);
    
    // 如果删除的是原有图片，更新删除列表
    let updatedDeletedImages = [...deletedImages];
    if (originalImages.includes(removedImage)) {
      updatedDeletedImages.push(removedImage);
    }
    
    // 一次性更新所有状态
    this.setData({ 
      images: updatedImages,
      deletedImages: updatedDeletedImages
    }, () => {
      // 在数据更新完成后，再更新imageUpdateType
      this.setData({
        imageUpdateType: this.determineImageUpdateType()
      });
    });
    console.log('要删除的图片列表' + this.data.deletedImages);
    
  },
  
  // 添加图片时的处理
  chooseImage() {
    const { images, maxImageCount } = this.data;
    wx.chooseImage({
      count: maxImageCount - images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        // 先记录新添加的图片
        console.log('新添加的图片：', res.tempFilePaths);
        this.setData({
          images: [...images, ...res.tempFilePaths]
        }, () => {
          // 在数据更新完成后，再更新imageUpdateType
          this.setData({
            imageUpdateType: this.determineImageUpdateType()
          });
        });
      }
    });
  },

  // 判断图片更新类型
  determineImageUpdateType(): ImageUpdateType {
    const { originalImages, images, deletedImages, isEdit } = this.data;
    if (!isEdit) return 'keep';
    // 检查新上传的图片（一定是本地路径，以http://tmp/ 或 wxfile://开头）
    const newImages = images.filter(img => 
      (img.startsWith('http://tmp/') || img.startsWith('wxfile://'))
    );
    // 检查是否删除了所有原始图片
    const hasDeletedAllOriginal = originalImages.length > 0 && 
      originalImages.every(img => deletedImages.includes(img));
    // 检查当前图片是否只包含新上传的图片
    const onlyNewImages = images.length > 0 && newImages.length === images.length;
    // 如果删除了所有原图，且只有新上传的图片，则为替换模式
    if (hasDeletedAllOriginal && onlyNewImages) {
      return 'replace';
    }
    // 如果有新上传的图片，且删除了部分（或没删除）原图，则为混合模式
    if (newImages.length > 0 && deletedImages.length > 0) {
      return 'mixed';
    }
    // 如果只有新上传的图片，没有删除原图，则为追加模式
    if (newImages.length > 0 && deletedImages.length === 0) {
      return 'append';
    }
    // 如果只删除了图片，没有新上传的图片，则为mixed模式
    if (newImages.length === 0 && deletedImages.length > 0) {
      return 'mixed';
    }
    // 如果图片没有任何变化，则为保持模式
    return 'keep';
  },

  inputDescription(e: any) {
    this.setData({
      description: e.detail.value
    });
  },

  inputPrice(e: any) {
    let value = e.detail.value;
    // 如果输入的不是有效数字，直接返回空
    if (value === '' || isNaN(Number(value))) {
      this.setData({ price: '' });
      return;
    }
    // 限制小数点后两位
    if (value.indexOf('.') > -1) {
      let parts = value.split('.');
      if (parts[1].length > 2) {
        value = Number(value).toFixed(2);
      }
    }
    // 限制第一位不能为0（除非是小数）
    if (value.length > 1 && value[0] === '0' && value[1] !== '.') {
      value = value.substring(1);
    }

    this.setData({
      price: value
    });
  },

  async submitGoods() {
    const { 
      images, 
      description, 
      price, 
      categoryIndex, 
      categories, 
      isEdit, 
      goodsId,
      originalImages,
      deletedImages,
      imageUpdateType
    } = this.data;
    
    // 表单验证
    if (!images.length) {
      wx.showToast({ title: '请至少上传一张图片', icon: 'none' });
      return;
    }
    if (!description.trim()) {
      wx.showToast({ title: '请填写闲置物品描述', icon: 'none' });
      return;
    }
    if (!price || Number(price) <= 0) {
      wx.showToast({ title: '请输入正确的价格', icon: 'none' });
      return;
    }
    if (categoryIndex === -1) {
      wx.showToast({ title: '请选择闲置物品分类', icon: 'none' });
      return;
    }
    // 防止重复提交
    if (this.data.isSubmitting) return;
    this.setData({ isSubmitting: true });
    try {
      wx.showLoading({ title: isEdit ? '更新中...' : '发布中...' });
      const goodsData = {
        description: description.trim(),
        price: Number(price),
        categoryId: categories[categoryIndex].categoryId
      };
      let response;
      if (isEdit && goodsId) {
        // 更新闲置物品
        response = await updateGoods(
          goodsId,
          goodsData,
          imageUpdateType,
          images.filter(img => img.startsWith('http://tmp/') || img.startsWith('wxfile://')),
          deletedImages
        );
      } else {
        // 发布新闲置物品
        const publishData: PublishGoodsData = {
          ...goodsData,
          imageUrls: images
        };
        response = await publishGoods(publishData, images);
      }
      if (response.code === 200) {
        wx.showToast({
          title: isEdit ? '更新成功' : '发布成功',
          icon: 'success'
        });
        // 延迟返回，给用户看到成功提示的时间
        setTimeout(() => {
          if (isEdit) {
            // 获取页面栈
            const pages = getCurrentPages();
            // 获取上一个页面
            const prevPage = pages[pages.length - 2];
            // 如果上一页有 getGoodsDetail 方法，调用它刷新数据
            if (prevPage && prevPage.getGoodsDetail) {
              prevPage.getGoodsDetail(goodsId);
            }
          }
          // 返回上一页
          wx.navigateBack();
        }, 1500);
      } else {
        throw new Error(response.message || '操作失败');
      }
    } catch (error: any) {
      console.error(isEdit ? '更新闲置物品失败:' : '发布闲置物品失败:', error);
      wx.showToast({
        title: error.message || '操作失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
      this.setData({ isSubmitting: false });
    }
  },

  // 设置分类索引
  setCategoryIndex(categoryId: number) {
    const index = this.data.categories.findIndex(item => item.categoryId === categoryId);
    if (index !== -1) {
      this.setData({ categoryIndex: index });
    }
  }
}); 