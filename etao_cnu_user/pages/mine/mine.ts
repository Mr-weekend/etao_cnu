import { userLogin, userLogout, getCollectionCount, getUserLoginInfo, UserInfo, getBrowseHistoryCount, getUserGoodsCount, getBuyerOrderCount, getSellerOrderCount, getAllUncommentOrderCount } from '../../services/api';

interface UserStats {
  collectionCount: number;
  historyCount: number;
  followingCount: number;
  followerCount: number;
  publishedCount: number;
  soldCount: number;
  boughtCount: number;
  unCommentCount: number;
}

// 统一路由映射
const PAGE_PATHS = {
  favorite: '/pages/mine/favorite/favorite',
  history: '/pages/mine/history/history',
  published: '/pages/mine/my-publish/my-publish',
  sold: '/pages/mine/sold/sold',
  bought: '/pages/mine/bought/bought',
  uncomment: '/pages/mine/unComment/unComment',
  edit: '/pages/mine/edit/edit',
  auth: '/pages/mine/user-auth/user-auth'
};

Page({
  data:{
    userInfo:{} as UserInfo,
    userStats:{} as UserStats,
    isLoggedIn:false,
    isFirstLogin: true
  },
  onLoad(){
    this.loadUserInfo()
  },
  onShow() {
    // wx.showToast({
    //   title:"加载咯~",
    //   icon:"loading"
    // })
    this.loadUserInfo()
    if (this.data.isLoggedIn) {
      this.getUserStatsInfo()
    }
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 });
    }
  },
  // 微信登录
  async login() {
    // wx.showLoading({ title: '登录中...' });
    const loginResponse = await userLogin()
    if(loginResponse.code === 200){
      const token = loginResponse.data;
      wx.setStorageSync('token', token);
      // 登录成功后立即获取用户信息
      this.getUserInfo()
      this.getUserStatsInfo()

    }else{
      wx.showToast({
      title: loginResponse.message || '登录失败',
      icon: 'error'
    });
    }
  },
  //获取用户登录信息
  async getUserInfo(){
    try{
      const userLoginInfoResponse = await getUserLoginInfo()
      if(userLoginInfoResponse.code === 200){
        // 从本地存储获取isFirstLogin状态
        const isFirstLogin = wx.getStorageSync('isFirstLogin') !== false;
        // 只在首次登录时显示提示
        if (!this.data.isLoggedIn && isFirstLogin) {
          wx.showToast({ title: '登录成功', icon: 'success' });
          // 将状态保存到本地存储
          wx.setStorageSync('isFirstLogin', false);
        }
        this.setData({
          isLoggedIn: true,
          userInfo: userLoginInfoResponse.data,
          isFirstLogin: false
        })
        // 保存用户信息到本地存储
        wx.setStorageSync('userInfo', userLoginInfoResponse.data);
        //立即触发WebSocket连接
        const app = getApp()
        app.globalData.isLogin = true
        app.checkLoginStatus();
        app.connectWebSocket();
    }
    }catch (error) {
      console.error('请求失败:', error);
      wx.showToast({
        title: '请求失败',
        icon: 'error'
      });
    }
  },
  //获取用户统计数据
  async getUserStatsInfo() {
    try {
      const collectionRes = await getCollectionCount();
      const browseHistoryRes = await getBrowseHistoryCount();
      const publishGoodsRes = await getUserGoodsCount(this.data.userInfo.userId);
      const boughtCountRes = await getBuyerOrderCount();
      const soldCountRes = await getSellerOrderCount();
      const uncommentCountRes = await getAllUncommentOrderCount();
      if (publishGoodsRes.code === 200) {
        this.setData({
          'userStats.publishedCount': publishGoodsRes.data,
        });
      }

      if (collectionRes.code === 200) {
        this.setData({
          'userStats.collectionCount': collectionRes.data,
        });
      }

      if (browseHistoryRes.code === 200) {
        this.setData({
          'userStats.historyCount': browseHistoryRes.data,
        });
      }

      if (boughtCountRes.code === 200) {
        this.setData({
          'userStats.boughtCount': boughtCountRes.data,
        });
      }

      if (soldCountRes.code === 200) {
        this.setData({
          'userStats.soldCount': soldCountRes.data,
        });
      }

      if (uncommentCountRes.code === 200) {
        this.setData({
          'userStats.unCommentCount': uncommentCountRes.data,
        });
      }
    } catch (error) {
      console.error('获取统计数据失败:', error);
    }
  },
  //加载用户信息
  loadUserInfo(){
    const token = wx.getStorageSync('token')
    const isFirstLogin = wx.getStorageSync('isFirstLogin') !== false
    this.setData({ isFirstLogin });
    this.setData({
      isLoggedIn: false
    })
    if (token) {
      this.getUserStatsInfo();
      this.getUserInfo()
    }
  },
  //跳转到用户主页
  navigateToUserProfile(){
    // 检查是否已登录
    const token = wx.getStorageSync('token');
    if (!token) {
      // 直接调用登录
      this.login();
      return;
    }
    
    wx.navigateTo({
      url: `/pages/mine/user-home/user-home?userInfo=${encodeURIComponent(JSON.stringify(this.data.userInfo))}`
    });
  },
  // 统一的导航检查
  checkLoginAndNavigate(url: string) {
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    if (url === '/pages/mine/edit/edit') {
      const status = wx.getStorageSync('userInfo').status
      if (status === 0) {
        const violationReason = wx.getStorageSync('userInfo').violationReason
        wx.showToast({
          title:`账号已被封禁，违规原因：${violationReason}`,
          icon:'none'
        })
        return;
      }else if(status === 2){
        wx.showToast({
          title:'请先进行认证',
          icon:'none'
        })
        return;
      }
    }
    wx.navigateTo({ url });
  },
  // 简化的导航方法
  navigateTo(e: WechatMiniprogram.TouchEvent) {
    const path = e.currentTarget.dataset.path as keyof typeof PAGE_PATHS;
    const url = PAGE_PATHS[path];
    if (url) {
      this.checkLoginAndNavigate(url);
    } else {
      console.error('未找到对应的页面路径:', path);
    }
  },
  //退出登录
  logout(){
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const logoutResponse = await userLogout()
            if(logoutResponse.code === 200){
              // 重置isFirstLogin状态
              wx.setStorageSync('isFirstLogin', true);
              //断开连接
              const app = getApp()
              app.globalData.isLogin = true
              app.clearLoginStatus();
              // 更新页面状态
              this.setData({
                isLoggedIn: false,
                userInfo: {} as UserInfo,
                userStats: {} as UserStats,
                isFirstLogin: true
              });
            }
            wx.showToast({
              title: '已退出登录',
              icon: 'success'
            });
          } catch (error) {
            console.error('退出登录失败:', error);
            wx.showToast({
              title: '退出失败',
              icon: 'error'
            });
          }
        }
      }
    });
  },
})