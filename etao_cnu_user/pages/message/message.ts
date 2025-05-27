import { getMessageList, ChatList, getGoodsDetail,GoodsItem, getUserInfo, getGoodsUnreadCount, getTotalUnreadCount, UserInfo, markMessagesAsRead } from '../../services/api';
interface IMessage extends ChatList {
  goods?: GoodsItem;
  user?: UserInfo;
}

Page({
  data: {
    messages:[] as IMessage[],
    isLogin: false,
    totalUnread: 0,
    activeTab: 'chat',
    isRefreshing: false,
    hasMore: true,
    page: 1,
    size: 10,
  },

  onLoad() {
    this.getMessages();
    // 添加页面显示监听
    wx.onAppShow(() => {
      if (this.data.isLogin) {
        this.getMessages();
      }
    });
  },

  onShow() {
    const token = wx.getStorageSync('token');
    if(token){
      // 进入消息页面时，重置全局未读消息数
      const app = getApp();
      app.globalData.unreadMessageCount = 0;
      // 刷新消息列表和未读消息数
      this.refreshMessages();
      this.setData({
        isLogin: true
      });
    } else {
      this.setData({
        isLogin: false
      });
    }
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 1
      });
      const token = wx.getStorageSync('token');
      if(!token){
        wx.showToast({
          title: '请先登录！',
          icon: 'none'
        });
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/mine/mine'
          });
        }, 1500);
        return;
      }
    }
  },

  onHide() {
    // 页面隐藏时，重新获取未读消息数并更新全局状态
    this.updateGlobalUnreadCount();
  },

  // 更新全局未读消息数
  async updateGlobalUnreadCount() {
    try {
      const res = await getTotalUnreadCount();
      if (res.code === 200) {
        const app = getApp();
        app.globalData.unreadMessageCount = res.data;
        app.updateAllTabBars(res.data);
      }
    } catch (error) {
      console.error('更新全局未读消息数失败:', error);
    }
  },

  // 新增刷新消息列表的方法
  async refreshMessages() {
    await this.getMessages();
    await this.updateUnreadCount();
  },

  // 获取消息列表
  async getMessages(type: string = 'chat') {
    try {
      const userInfo = wx.getStorageSync('userInfo')
      if(userInfo){
        const { page, size } = this.data;
        const messageListRes = await getMessageList(page, size);
    
        if (messageListRes.code === 200) {
          // 使用Promise.all并行请求
          const messagesWithDetails = await Promise.all(
            messageListRes.data.list.map(async (msg: ChatList) => {
              try {
                // 获取闲置物品详情
                const goodsRes = await getGoodsDetail(msg.goodsId);
                // 获取用户信息
                const userRes = await getUserInfo(msg.otherUserId);
                // 获取该闲置物品的未读消息数
                const unreadRes = await getGoodsUnreadCount({
                  senderId: msg.otherUserId,
                  goodsId: msg.goodsId
                });
    
                return {
                  ...msg,
                  goods: goodsRes.code === 200 ? goodsRes.data : null,
                  user: userRes.code === 200 ? userRes.data : null,
                  unreadCount: unreadRes.code === 200 ? unreadRes.data : msg.unreadCount
                } as IMessage;
              } catch (error) {
                console.error('获取消息详情失败:', error);
                return msg as IMessage;
              }
            })
          );
    
          this.setData({
            messages: messagesWithDetails
          });
        }
      }
    } catch (error) {
      console.error('获取消息失败:', error);
    }
  },

  // 更新未读消息总数
  async updateUnreadCount() {
    try {
      const res = await getTotalUnreadCount();
      if(res.code === 200){
        // 更新页面显示的未读消息数
        this.setData({
          totalUnread: res.data
        });
        // 更新全局未读消息数
        const app = getApp();
        app.globalData.unreadMessageCount = res.data;
        // 更新自定义tabBar的未读消息数
        const tabBar = this.getTabBar();
        if (tabBar) {
          tabBar.setData({
            unreadCount: res.data
          });
        }
        // 通知上一个页面更新未读消息数
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        if (prevPage && typeof prevPage.getTabBar === 'function') {
          const prevTabBar = prevPage.getTabBar();
          if (prevTabBar) {
            prevTabBar.setData({
              unreadCount: res.data
            });
          }
        }
      }
    } catch (error) {
      console.error('获取未读消息数失败:', error);
    }
  },

  // 进入聊天页面
  async navigateToChat(e: any) {
    const { userid, goodsid } = e.currentTarget.dataset;
    try {
      // // 标记消息为已读
      // await markMessagesAsRead({
      //   senderId: userid,
      //   goodsId: goodsid
      // });
      
      // 更新未读消息数
      this.updateUnreadCount();
      
      // 导航到聊天页面
      wx.navigateTo({
        url: `/pages/chat/chat?userId=${userid}&goodsId=${goodsid}`
      });
    } catch (error) {
      console.error('标记消息已读失败:', error);
      // 即使标记失败，也允许进入聊天页面
      wx.navigateTo({
        url: `/pages/chat/chat?userId=${userid}&goodsId=${goodsid}`
      });
    }
  },

  // 下拉刷新
  async onPullDownRefresh() {
    console.log('开始下拉刷新');
    this.setData({
      isRefreshing: true
    });
    
    try {
      await this.getMessages(this.data.activeTab);
      wx.showToast({
        title: '刷新成功',
        icon: 'success',
        duration: 1000
      });
    } catch (error) {
      console.error('刷新失败:', error);
      wx.showToast({
        title: '刷新失败',
        icon: 'error',
        duration: 1000
      });
    } finally {
      this.setData({ isRefreshing: false });
      wx.stopPullDownRefresh();
    }
  }
}); 