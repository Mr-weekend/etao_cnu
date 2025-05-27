import { getTotalUnreadCount } from 'services/api';
App({
  globalData: {
    userInfo: null,
    isLogin: false,
    socket: null,
    isConnecting: false, // 防止重复连接
    unreadMessages: [], // 全局消息缓存
    unreadMessageCount: 0,
    notifyDebounceTimer: null,  // 新增：通知防抖定时器
    lastNotifyTime: 0,  // 上次通知时间
    isNotifying: false,  // 是否正在执行通知
    lastRoute: '', // 添加上一个路由记录
    isUpdating: false // 添加更新状态标记
  },

  onLaunch() {
    this.checkLoginStatus();
    // 监听页面切换
    wx.onAppRoute((route) => {
      // 避免重复触发同一页面的更新
      if (this.globalData.lastRoute !== route.path) {
        this.globalData.lastRoute = route.path;
        this.updateUnreadCountOnRoute();
      }
    });
  },

  // 检查登录状态
  async checkLoginStatus() {
    try {
      const userInfo = wx.getStorageSync('userInfo');
      if (userInfo) {
        this.globalData.userInfo = userInfo;
        this.globalData.isLogin = true;
        // 重置未读消息数
        this.globalData.unreadMessageCount = 0;
        // 获取未读消息数
        await this.getUserTotalUnreadCount();
        this.connectWebSocket();
      }
    } catch (e) {
      console.error('登录状态检查失败', e);
    }
  },

  // 用户登录成功后调用
  async onLoginSuccess(userInfo) {
    this.globalData.userInfo = userInfo;
    this.globalData.isLogin = true;
    wx.setStorageSync('userInfo', userInfo);
    await this.getUserTotalUnreadCount();
    this.connectWebSocket();
    // this.notifyAllTabBars();
  },
  // 建立WebSocket连接
  connectWebSocket() {
    if (this.globalData.socket || !this.globalData.isLogin) return;

    const socket = wx.connectSocket({
      url: `ws://localhost:8989/chat/${this.globalData.userInfo.userId}`,
      success: () => {
        console.log('WebSocket连接成功');
        this.globalData.isConnecting = true;
      },
      fail: (err) => {
        console.error('WebSocket连接失败', err);
        this.reconnectSocket();
      }
    });

    if (this.globalData.isConnecting) {
      this.globalData.socket = socket;
      this.setupSocketEvents();
    }
  },

  // 页面路由切换时更新未读消息数
  async updateUnreadCountOnRoute() {
    if (!this.globalData.isLogin || this.globalData.isUpdating) return;
    
    try {
      this.globalData.isUpdating = true;
      const res = await getTotalUnreadCount();
      if (res.code === 200) {
        // 获取当前页面栈
        const pages = getCurrentPages();
        const currentPage = pages[pages.length - 1];
        
        // 如果当前页是消息页面，重置未读数
        if (currentPage && currentPage.route === 'pages/message/message') {
          if (this.globalData.unreadMessageCount !== 0) {
            this.globalData.unreadMessageCount = 0;
            this.updateAllTabBars(0);
          }
        } else {
          // 只有当未读数发生变化时才更新
          if (this.globalData.unreadMessageCount !== res.data) {
            this.globalData.unreadMessageCount = res.data;
            this.updateAllTabBars(res.data);
          }
        }
      }
    } catch (error) {
      console.error('更新未读消息数失败:', error);
    } finally {
      this.globalData.isUpdating = false;
    }
  },

  // 更新所有TabBar的未读消息数
  updateAllTabBars(count) {
    const pages = getCurrentPages();
    if (!pages || pages.length === 0) return;

    // 遍历所有页面并更新它们的TabBar
    pages.forEach((page, index) => {
      if (page && typeof page.getTabBar === 'function') {
        const tabBar = page.getTabBar();
        if (tabBar && tabBar.data.unreadCount !== count) {
          tabBar.setData({
            unreadCount: count
          });
        }
      }
    });
  },

  // 消息缓存与持久化
  cacheMessage(msg) {
    const messages = wx.getStorageSync('unreadMessages') || [];
    messages.push(msg);
    wx.setStorageSync('unreadMessages', messages);
    this.globalData.unreadMessages = messages;
  },

  // 断线重连
  reconnectSocket() {
    clearInterval(this.globalData.heartbeatTimer);
    setTimeout(() => this.connectWebSocket(), 5000); // 5秒后重连
  },

  // 获取用户总共未读消息数
  async getUserTotalUnreadCount() {
    const token = wx.getStorageSync('token');
    if (token) {
      try {
        const res = await getTotalUnreadCount();
        if (res.code === 200) {
          // 直接设置未读消息数
          this.globalData.unreadMessageCount = res.data;
          // 更新所有TabBar
          this.updateAllTabBars(res.data);
        }
      } catch (error) {
        console.error('获取未读消息数失败:', error);
      }
    }
  },

  // 清理登录状态
  clearLoginStatus() {
    wx.removeStorageSync('userInfo');
    wx.removeStorageSync('token');
    wx.removeStorageSync('unreadMessages');
    this.globalData.userInfo = null;
    this.globalData.unreadMessageCount = 0;
    if (this.globalData.socket) {
      this.globalData.socket.close();
      this.globalData.socket = null;
    }
    // this.notifyAllTabBars();
  },

  // 触发页面更新（不触发TabBar通知）
  triggerPageUpdate() {
    const pages = getCurrentPages();
    pages.forEach(page => {
      if (typeof page.onMessageUpdate === 'function') {
        page.onMessageUpdate();
      }
    });
  },
  
  // 触发全局更新
  triggerGlobalUpdate() {
    // 使用节流控制，避免频繁通知
    this.throttleNotify();
  },

  // WebSocket事件监听
  setupSocketEvents() {
    const { socket } = this.globalData;
    if (!socket) return;
    
    socket.onOpen(() => {
      console.log('WebSocket已连接');
    });

    socket.onMessage(async (res) => {
      console.log('收到消息:', res.data);
      const msg = JSON.parse(res.data);
      this.cacheMessage(msg);
      
      // 检查当前页面是否是消息页面
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      
      if (!currentPage || currentPage.route !== 'pages/message/message') {
        // 获取最新的未读消息总数并更新所有页面
        if (!this.globalData.isUpdating) {
          await this.updateUnreadCountOnRoute();
        }
      } else {
        // 如果在消息页面，重置未读消息数并刷新列表
        this.globalData.unreadMessageCount = 0;
        this.updateAllTabBars(0);
        // 触发消息页面的更新
        if (currentPage.refreshMessages) {
          currentPage.refreshMessages();
        }
      }
    });

    socket.onClose(() => {
      console.log('WebSocket已断开');
      this.reconnectSocket();
    });

    socket.onError((err) => {
      console.error('WebSocket错误:', err);
      this.reconnectSocket();
    });
  }
});  