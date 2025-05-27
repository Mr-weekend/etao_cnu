import { getOrderDetail, getIsCommentAsBuyer, getIsCommentAsSeller, confirmOrder, deleteOrder, cancelOrder } from '../../../services/api';
interface OrderDetailData {
  orderId: number;
  orderStatus: 'completed' | 'canceled' | 'waiting';
  createTime: string;
  completedTime: string;
  buyer: {
    userId: number;
    userName: string;
    avatarUrl: string;
  };
  goodsInfo: {
    goodsId: number;
    description: string;
    price: number;
    imageUrls: string[];
    publisher: {
      userId: number;
      userName: string;
      avatarUrl: string;
      userProfile: string | null;
    }
  };
}

Page({
  data: {
    loading: true,
    orderId: 0,
    orderDetail: {} as OrderDetailData,
    isCurrentUserBuyer: false,
    isDeleting: false,
    buyerCommented: false,
    sellerCommented: false
  },

  onLoad(options: any) {
    if (options.id) {
      const orderId = parseInt(options.id);
      this.setData({
        orderId
      });
      this.getOrderDetail(orderId);
    } else {
      wx.showToast({
        title: '订单ID不存在',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  onPullDownRefresh() {
    this.getOrderDetail(this.data.orderId);
    wx.stopPullDownRefresh();
  },

  async getOrderDetail(orderId: number) {
    try {
      this.setData({ loading: true });
      // 获取当前登录用户
      const userInfo = wx.getStorageSync('userInfo');
      const currentUserId = userInfo ? userInfo.userId : 0;
      
      const res = await getOrderDetail(orderId);
      console.log('订单详情数据:', res.data);
      
      if (res.code === 200) {
        const isCurrentUserBuyer = currentUserId === res.data.buyer.userId;
        const orderDetail: OrderDetailData = {
          orderId: res.data.orderId,
          orderStatus: res.data.status as 'completed' | 'canceled' | 'waiting',
          completedTime: res.data.completedAt,
          createTime: res.data.createdAt || '',
          buyer: {
            userId: res.data.buyer.userId,
            userName: res.data.buyer.userName,
            avatarUrl: res.data.buyer.avatarUrl
          },
          goodsInfo: {
            goodsId: res.data.goodsInfo.goodsId,
            description: res.data.goodsInfo.description,
            price: res.data.goodsInfo.price,
            imageUrls: res.data.goodsInfo.imageUrls || [],
            publisher: {
              userId: res.data.goodsInfo.publisher.userId,
              userName: res.data.goodsInfo.publisher.userName,
              avatarUrl: res.data.goodsInfo.publisher.avatarUrl,
              userProfile: res.data.goodsInfo.publisher.userProfile
            }
          },
        };
        
        this.setData({
          orderDetail,
          isCurrentUserBuyer,
          loading: false
        });
        this.checkCommentStatus();
      } else {
        throw new Error(res.message || '获取订单详情失败');
      }
    } catch (error) {
      console.error('获取订单详情失败', error);
      wx.showToast({
        title: '获取订单详情失败',
        icon: 'none'
      });
      this.setData({ loading: false });
    }
  },

  async checkCommentStatus() {
    try {
      const buyerRes = await getIsCommentAsBuyer(this.data.orderId);
      const sellerRes = await getIsCommentAsSeller(this.data.orderId);
      if (buyerRes.code === 200 && sellerRes.code === 200) {
        this.setData({
          buyerCommented: buyerRes.data,
          sellerCommented: sellerRes.data
        });
      }
    } catch (error) {
      console.error('获取评价状态失败:', error);
    }
  },

  // 取消订单
  async handleCancelOrder() {
    if (!this.data.isCurrentUserBuyer) {
      wx.showToast({
        title: '只有买家才能取消订单',
        icon: 'none'
      });
      return;
    }
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '取消中...' });
            const result = await cancelOrder(this.data.orderId);
            
            if (result.code === 200) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              });
              this.getOrderDetail(this.data.orderId);
            } else {
              throw new Error(result.message || '取消订单失败');
            }
          } catch (error) {
            console.error('取消订单失败', error);
            wx.showToast({
              title: '取消订单失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  },

  // 确认收货
  async handleConfirmOrder() {
    if (!this.data.isCurrentUserBuyer) {
      wx.showToast({
        title: '只有买家才能确认收货',
        icon: 'none'
      });
      return;
    }
    
    wx.showModal({
      title: '确认收货',
      content: '确认已收到闲置物品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '处理中...' });
            const result = await confirmOrder(this.data.orderId);
            
            if (result.code === 200) {
              wx.showToast({
                title: '确认收货成功',
                icon: 'success'
              });
              this.getOrderDetail(this.data.orderId);
            } else {
              throw new Error(result.message || '确认收货失败');
            }
          } catch (error) {
            console.error('确认收货失败', error);
            wx.showToast({
              title: '确认收货失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
          }
        }
      }
    });
  },

  // 删除订单
  async handleDeleteOrder() {
    this.setData({ isDeleting: true });
    
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个订单吗？删除后不可恢复',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '删除中...' });
            const result = await deleteOrder(this.data.orderId);
            
            if (result.code === 200) {
              wx.showToast({
                title: '订单已删除',
                icon: 'success'
              });
              
              // 返回上一页
              setTimeout(() => {
                wx.navigateBack();
              }, 1500);
            } else {
              throw new Error(result.message || '删除订单失败');
            }
          } catch (error) {
            console.error('删除订单失败', error);
            wx.showToast({
              title: '删除订单失败',
              icon: 'none'
            });
          } finally {
            wx.hideLoading();
            this.setData({ isDeleting: false });
          }
        }
      }
    });
  },

  navigateToComment() {
    const { orderId, isCurrentUserBuyer, buyerCommented, sellerCommented } = this.data;
    // 如果当前用户是买家
    if (isCurrentUserBuyer) {
      // 买家已评价
      if (buyerCommented) {
        wx.navigateTo({
          url: `/pages/mine/comment-detail/comment-detail?orderId=${orderId}`
        });
      } else {
        // 买家未评价
        wx.navigateTo({
          url: `/pages/mine/publish-comment/publish-comment?orderId=${orderId}&type=buyer`
        });
      }
    } else {
      // 当前用户是卖家
      if (sellerCommented) {
        wx.navigateTo({
          url: `/pages/mine/comment-detail/comment-detail?orderId=${orderId}`
        });
      } else {
        wx.navigateTo({
          url: `/pages/mine/publish-comment/publish-comment?orderId=${orderId}&type=seller`
        });
      }
    }
  },

  // 联系对方
  contactUser() {
    const { orderDetail, isCurrentUserBuyer } = this.data;
    const targetUserId = isCurrentUserBuyer ? 
      orderDetail.goodsInfo.publisher.userId : 
      orderDetail.buyer.userId;
    const targetUserName = isCurrentUserBuyer ? 
      orderDetail.goodsInfo.publisher.userName : 
      orderDetail.buyer.userName;
    
    wx.navigateTo({
      url: `/pages/chat/chat?targetUserId=${targetUserId}&targetUserName=${targetUserName}`
    });
  },

  // 复制订单号
  copyOrderId() {
    wx.setClipboardData({
      data: String(this.data.orderId),
      success: () => {
        wx.showToast({
          title: '订单号已复制',
          icon: 'success'
        });
      }
    });
  },

  // 查看闲置物品详情
  viewGoodsDetail() {
    const { goodsInfo } = this.data.orderDetail;
    wx.navigateTo({
      url: `/pages/goods/detail/detail?id=${goodsInfo.goodsId}`
    });
  },

  // 查看用户资料
  viewUserProfile(e: WechatMiniprogram.TouchEvent) {
    const { userId } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/profile/index/index?userId=${userId}`
    });
  },
  
}); 