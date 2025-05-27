import { publishComment, getOrderDetail, OrderDetail } from '../../services/api';
Page({
  data: {
    orderId: 0,
    commentType: 1, // 默认好评
    content: '',
    isAnonymous: 0, // 默认不匿名
    images: [] as string[],
    orderDetail: {} as OrderDetail,
    loading: true,
    submitting: false,
    error: ''
  },

  onLoad(options: any) {
    if (options.orderId) {
      const orderId = parseInt(options.orderId);
      this.setData({
        orderId
      });
      
      this.getOrderDetail(orderId);
    } else {
      this.setData({
        loading: false,
        error: '参数错误'
      });
    }
  },

  async getOrderDetail(orderId: number) {
    try {
      this.setData({ loading: true });
      const res = await getOrderDetail(orderId);
      
      if (res.code === 200) {
        this.setData({
          orderDetail: res.data,
          loading: false
        });
      } else {
        throw new Error(res.message || '获取订单详情失败');
      }
    } catch (error) {
      console.error('获取订单详情失败:', error);
      this.setData({
        loading: false,
        error: '获取订单详情失败'
      });
    }
  },

  // 选择评价类型
  onSelectCommentType(e: WechatMiniprogram.TouchEvent) {
    const { type } = e.currentTarget.dataset;
    this.setData({
      commentType: parseInt(type)
    });
  },

  // 输入评价内容
  onInputContent(e: WechatMiniprogram.Input) {
    this.setData({
      content: e.detail.value
    });
  },

  // 切换匿名评价
  onChangeAnonymous() {
    this.setData({
      isAnonymous: this.data.isAnonymous === 0 ? 1 : 0
    });
  },

  // 上传图片
  onChooseImage() {
    const { images } = this.data;
    const remainCount = 9 - images.length;
    
    if (remainCount <= 0) {
      wx.showToast({
        title: '最多上传9张图片',
        icon: 'none'
      });
      return;
    }
    
    wx.chooseImage({
      count: remainCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({
          images: [...images, ...res.tempFilePaths]
        });
      }
    });
  },

  // 预览图片
  onPreviewImage(e: WechatMiniprogram.TouchEvent) {
    const { index } = e.currentTarget.dataset;
    const { images } = this.data;
    
    wx.previewImage({
      current: images[index],
      urls: images
    });
  },

  // 删除图片
  onDeleteImage(e: WechatMiniprogram.TouchEvent) {
    const { index } = e.currentTarget.dataset;
    const { images } = this.data;
    
    this.setData({
      images: images.filter((_, i) => i !== index)
    });
  },

  // 提交评价
  async submitComment() {
    const { orderId, commentType, content, isAnonymous, images } = this.data;
    
    if (!content.trim()) {
      wx.showToast({
        title: '请输入评价内容',
        icon: 'none'
      });
      return;
    }
    
    try {
      this.setData({ submitting: true });
      
      const params = {
        orderId,
        type: commentType,
        content,
        isAnonymous,
      };
      const res = await publishComment(params, images.length > 0 ? images : undefined);
      if (res.code === 200) {
        wx.showToast({
          title: '评价成功',
          icon: 'success'
        });
        
        // 延迟返回
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        throw new Error(res.message || '评价失败');
      }
    } catch (error) {
      console.error('评价失败:', error);
      wx.showToast({
        title: '评价失败',
        icon: 'none'
      });
    } finally {
      this.setData({ submitting: false });
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
})