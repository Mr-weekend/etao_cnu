import { getCommentByOrderId, CommentDetail } from '../../services/api';

Page({
  data: {
    orderId: 0,
    loading: true,
    commentDetail: {} as CommentDetail,
    error: ''
  },

  onLoad(options: any) {
    if (options.orderId) {
      const orderId = parseInt(options.orderId);
      this.setData({ orderId });
      this.getCommentDetail(orderId);
    } else {
      this.setData({
        loading: false,
        error: '订单ID不存在'
      });
    }
  },

  onPullDownRefresh() {
    this.getCommentDetail(this.data.orderId);
    wx.stopPullDownRefresh();
  },

  async getCommentDetail(orderId: number) {
    try {
      this.setData({ loading: true });
      const res = await getCommentByOrderId(orderId);
      
      if (res.code === 200) {
        console.log('调用接口的');
        console.log(res.data);
        
        this.setData({
          commentDetail: res.data,
          loading: false
        });
      } else {
        throw new Error(res.message || '获取评价详情失败');
      }
      console.log(this.data.commentDetail);
      
    } catch (error) {
      console.error('获取评价详情失败:', error);
      this.setData({
        loading: false,
        error: '获取评价详情失败'
      });
      wx.showToast({
        title: '获取评价详情失败',
        icon: 'none'
      });
    }
  },

  // 预览评价图片
  previewImage(e: WechatMiniprogram.TouchEvent) {
    const { urls, current } = e.currentTarget.dataset;
    if (urls && urls.length > 0) {
      wx.previewImage({
        urls,
        current
      });
    }
  },

  // 查看用户信息
  viewUserInfo(e: WechatMiniprogram.TouchEvent) {
    const { userid } = e.currentTarget.dataset;
    if (userid) {
      wx.navigateTo({
        url: `/pages/profile/index/index?userId=${userid}`
      });
    }
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
})