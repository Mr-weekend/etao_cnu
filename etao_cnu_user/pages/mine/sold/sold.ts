import { getSellerOrders, OrderDetail, searchSellerOrders, getOrderDetail, getIsCommentAsSeller, getCommentByOrderId, deleteOrder } from '../../../services/api';

Page({
  data: {
    soldList: [] as OrderDetail[],
    isLoading: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    keyword: '',
    commentStatus: {} as Record<number, boolean> // 记录订单是否已评价
  },

  onLoad() {
    this.loadSoldList();
  },

  // 下拉刷新
  async onPullDownRefresh() {
    this.setData({
      page: 1,
      hasMore: true,
      soldList: []
    });
    await this.loadSoldList();
    wx.stopPullDownRefresh();
  },

  // 上拉加载更多
  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    const nextPage = this.data.page + 1;
    await this.loadMoreSold(nextPage);
  },

  // 输入搜索关键词
  onInputKeyword(e: WechatMiniprogram.Input) {
    this.setData({
      keyword: e.detail.value
    });
  },

  // 搜索订单
  async searchOrders() {
    if (!this.data.keyword.trim()) {
      // 如果关键词为空，则加载全部列表
      this.setData({
        page: 1,
        soldList: []
      });
      await this.loadSoldList();
      return;
    }

    this.setData({ isLoading: true });
    try {
      const res = await searchSellerOrders(this.data.keyword, 1, this.data.pageSize);
      if (res.code === 200) {
        const orderList = res.data.list;
        console.log(orderList);
        
        // 检查每个订单的评价状态
        const commentStatusPromises = orderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        
        this.setData({
          soldList: orderList,
          hasMore: orderList.length === this.data.pageSize,
          page: 1
        });
      } else {
        wx.showToast({
          title: res.message || '搜索失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('搜索订单失败:', error);
      wx.showToast({
        title: '搜索失败',
        icon: 'none'
      });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 清除搜索
  clearSearch() {
    this.setData({
      keyword: '',
      page: 1,
      soldList: []
    });
    this.loadSoldList();
  },

  // 加载卖出的闲置物品列表
  async loadSoldList() {
    try {
      this.setData({ isLoading: true });
      const res = await getSellerOrders(this.data.page, this.data.pageSize);
      
      if (res.code === 200) {
        const orderList = res.data.list;
        
        // 检查每个订单的评价状态
        const commentStatusPromises = orderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        
        this.setData({
          soldList: orderList,
          isLoading: false,
          hasMore: orderList.length === this.data.pageSize
        });
      } else {
        wx.showToast({
          title: '获取订单失败',
          icon: 'none'
        });
        this.setData({ isLoading: false });
      }
    } catch (error) {
      console.error('加载卖出闲置物品列表失败:', error);
      this.setData({ isLoading: false });
      wx.showToast({
        title: '获取订单失败',
        icon: 'none'
      });
    }
  },

  // 检查订单评价状态
  async checkCommentStatus(orderId: number) {
    try {
      const res = await getIsCommentAsSeller(orderId);
      if (res.code === 200) {
        this.setData({
          [`commentStatus.${orderId}`]: res.data
        });
        return res.data;
      }
      return false;
    } catch (error) {
      console.error('获取评价状态失败:', error);
      return false;
    }
  },

  // 加载更多卖出的闲置物品
  async loadMoreSold(nextPage: number) {
    try {
      this.setData({ isLoading: true });
      const res = await getSellerOrders(nextPage, this.data.pageSize);
      
      if (res.code === 200) {
        const newOrderList = res.data.list;
        
        // 检查新加载的订单的评价状态
        const commentStatusPromises = newOrderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        
        if (newOrderList.length > 0) {
          this.setData({
            soldList: [...this.data.soldList, ...newOrderList],
            page: nextPage,
            hasMore: newOrderList.length === this.data.pageSize
          });
        } else {
          this.setData({ hasMore: false });
        }
      } else {
        this.setData({ hasMore: false });
      }
    } catch (error) {
      console.error('加载更多卖出闲置物品失败:', error);
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 联系买家
  contactBuyer(e: WechatMiniprogram.TouchEvent) {
    const { userId, goodsId } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/chat/chat?userId=${userId}&goodsId=${goodsId}`
    });
  },

  // 跳转到评价页面
  navigateToComment(e: WechatMiniprogram.TouchEvent) {
    const { orderId, hascomment } = e.currentTarget.dataset;
    const isCommented = hascomment === true;
    
    if (isCommented) {
      // 已评价，跳转到评价详情页
      wx.navigateTo({
        url: `/pages/comment-detail/comment-detail?orderId=${orderId}`
      });
    } else {
      // 未评价，跳转到发表评价页
      wx.navigateTo({
        url: `/pages/publish-comment/publish-comment?orderId=${orderId}`
      });
    }
  },

  navigateToOrderDetail(e: WechatMiniprogram.TouchEvent) {
    console.log('点击了订单');
    
    const { orderid } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/mine/order-detail/order-detail?id=${orderid}`
    });
  },

  navigateToGoodsDetail(e: WechatMiniprogram.TouchEvent) {
    console.log('点击了');
    
    const { goodsid } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/goods/detail/detail?id=${goodsid}`
    });
  },

  navigateToBuyer(e: WechatMiniprogram.TouchEvent) {
    const { userInfo } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/mine/user-home/user-home?userInfo=${encodeURIComponent(JSON.stringify(userInfo))}`
    });
  },

  // 长按删除订单
  handleLongPress(e: WechatMiniprogram.TouchEvent) {
    const { orderid } = e.currentTarget.dataset;
    wx.showModal({
      title: '删除订单',
      content: '确定要删除该订单吗？删除后无法恢复',
      success: async (res) => {
        if (res.confirm) {
          try {
            this.setData({ isLoading: true });
            const result = await deleteOrder(orderid);
            if (result.code === 200) {
              wx.showToast({
                title: '删除订单成功',
                icon: 'success'
              });
              // 刷新列表
              this.setData({
                page: 1,
                soldList: []
              });
              this.loadSoldList();
            } else {
              wx.showToast({
                title: result.message || '删除订单失败',
                icon: 'none'
              });
            }
          } catch (error) {
            console.error('删除订单失败:', error);
            wx.showToast({
              title: '删除订单失败',
              icon: 'none'
            });
          } finally {
            this.setData({ isLoading: false });
          }
        }
      }
    });
  }
}); 