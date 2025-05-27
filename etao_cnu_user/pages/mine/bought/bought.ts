import { getBuyerOrders, OrderDetail, searchBuyerOrders, getOrderDetail, getIsCommentAsBuyer, getCommentByOrderId, confirmOrder, cancelOrder, deleteOrder } from '../../../services/api';

Page({
  data: {
    boughtList: [] as OrderDetail[],
    isLoading: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    keyword: '',
    commentStatus: {} as Record<number, boolean> // 记录订单是否已评价
  },

  onLoad() {
    this.loadBoughtList();
  },

  // 下拉刷新
  async onPullDownRefresh() {
    this.setData({
      page: 1,
      hasMore: true,
      boughtList: []
    });
    await this.loadBoughtList();
    wx.stopPullDownRefresh();
  },

  // 上拉加载更多
  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    const nextPage = this.data.page + 1;
    await this.loadMoreBought(nextPage);
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
        boughtList: []
      });
      await this.loadBoughtList();
      return;
    }

    this.setData({ isLoading: true });
    try {
      const res = await searchBuyerOrders(this.data.keyword, 1, this.data.pageSize);
      if (res.code === 200) {
        const orderList = res.data.list;
        // 检查每个订单的评价状态
        const commentStatusPromises = orderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        
        this.setData({
          boughtList: orderList,
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
      boughtList: []
    });
    this.loadBoughtList();
  },

  async loadBoughtList() {
    try {
      this.setData({ isLoading: true });
      const res = await getBuyerOrders(this.data.page, this.data.pageSize);
      if (res.code === 200) {
        const orderList = res.data.list; 
        // 检查每个订单的评价状态
        const commentStatusPromises = orderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        this.setData({
          boughtList: orderList,
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
      console.error('加载买到闲置物品列表失败:', error);
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
      const res = await getIsCommentAsBuyer(orderId);
      if (res.code === 200) {
        console.log('检查评价状态' + res.data);
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

  // 加载更多买到的闲置物品
  async loadMoreBought(nextPage: number) {
    try {
      this.setData({ isLoading: true });
      const res = await getBuyerOrders(nextPage, this.data.pageSize);
      if (res.code === 200) {
        const newOrderList = res.data.list;
        // 检查新加载的订单的评价状态
        const commentStatusPromises = newOrderList.map(order => 
          this.checkCommentStatus(order.orderId)
        );
        await Promise.all(commentStatusPromises);
        
        if (newOrderList.length > 0) {
          this.setData({
            boughtList: [...this.data.boughtList, ...newOrderList],
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
      console.error('加载更多买到闲置物品失败:', error);
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 联系卖家
  contactSeller(e: WechatMiniprogram.TouchEvent) {
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
        url: `/pages/publish-comment/publish-comment?orderId=${orderId}&type=buyer`
      });
    }
  },

  // 跳转到订单详情
  navigateToOrderDetail(e: WechatMiniprogram.TouchEvent) {
    const { orderid } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/mine/order-detail/order-detail?id=${orderid}`
    });
  },

  // 跳转到闲置物品详情
  navigateToGoodsDetail(e: WechatMiniprogram.TouchEvent) {
    const { goodsid } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/goods/detail/detail?id=${goodsid}`
    });
  },

  // 跳转到卖家主页
  navigateToSeller(e: WechatMiniprogram.TouchEvent) {
    const { userInfo } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/mine/user-home/user-home?userInfo=${encodeURIComponent(JSON.stringify(userInfo))}`
    });
  },
  
  // 确认收货
  confirmReceive(e: WechatMiniprogram.TouchEvent) {
    const { orderid } = e.currentTarget.dataset;
    wx.showModal({
      title: '确认收货',
      content: '确认已收到闲置物品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            this.setData({ isLoading: true });
            const result = await this.confirmOrder(orderid);
            if (result) {
              wx.showToast({
                title: '确认收货成功',
                icon: 'success'
              });
              // 刷新列表
              this.setData({
                page: 1,
                boughtList: []
              });
              this.loadBoughtList();
            }
          } catch (error) {
            console.error('确认收货失败:', error);
          } finally {
            this.setData({ isLoading: false });
          }
        }
      }
    });
  },
  
  // 调用确认收货接口
  async confirmOrder(orderId: number): Promise<boolean> {
    try {
      const res = await confirmOrder(orderId);
      if (res.code === 200) {
        return true;
      }
      wx.showToast({
        title: res.message || '确认收货失败',
        icon: 'none'
      });
      return false;
    } catch (error) {
      console.error('确认收货失败:', error);
      wx.showToast({
        title: '确认收货失败',
        icon: 'none'
      });
      return false;
    }
  },

  // 取消订单
  cancelOrder(e: WechatMiniprogram.TouchEvent) {
    const { orderid } = e.currentTarget.dataset;
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            this.setData({ isLoading: true });
            const result = await cancelOrder(orderid);
            if (result.code === 200) {
              wx.showToast({
                title: '取消订单成功',
                icon: 'success'
              });
              // 刷新列表
              this.setData({
                page: 1,
                boughtList: []
              });
              this.loadBoughtList();
            } else {
              wx.showToast({
                title: result.message || '取消订单失败',
                icon: 'none'
              });
            }
          } catch (error) {
            console.error('取消订单失败:', error);
            wx.showToast({
              title: '取消订单失败',
              icon: 'none'
            });
          } finally {
            this.setData({ isLoading: false });
          }
        }
      }
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
                boughtList: []
              });
              this.loadBoughtList();
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