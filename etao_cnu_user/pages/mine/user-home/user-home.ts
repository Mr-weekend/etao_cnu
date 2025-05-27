import { getUserLoginInfo, getUserInfo, UserInfo, getUserGoods, GoodsItem, getUserGoodsCount, getUserComments, getUserCommentCount, CommentDetail } from '../../../services/api';

interface IUserHome {
  userInfo: IUserInfo,
  isMyself: boolean;
}

interface IUserInfo {
  userId: number;
  userName: string;
  gender: string;
  avatarUrl: string;
  userProfile: string;
  status: number,
  violationReason: string;
}


Page({
  data: {
    userHome: {} as IUserHome,
    activeTab: 'goods', // 当前选中的选项卡
    reviewType: 'buyer', // 评价类型
    goodsList: [] as GoodsItem[], // 闲置物品列表
    reviewList: [] as CommentDetail[], // 评价列表
    isLoading: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    userId: '',
    goodsCount: 0, // 用户发布的闲置物品数量
    reviewCount: 0 // 用户收到的评价数量
  },

  onLoad(options: any) {
    let userInfo;
    
    // 处理从其他页面传来的用户信息
    if (options.userInfo) {
      try {
        userInfo = JSON.parse(decodeURIComponent(options.userInfo));
        console.log('传来的用户信息：');
        console.log(userInfo);
        if(userInfo.gender === 1){
          userInfo.gender = '男生'
        }else{
          userInfo.gender = '女生'
        }
        if(userInfo.userProfile === null){
          userInfo.userProfile = '这家伙很懒，没有填写个人简介。'
        }
      } catch (error) {
        console.error('解析用户信息失败:', error);
      }
    }
    
    // 如果有 userId，直接使用
    if (options.userId) {
      this.setData({ userId: options.userId });
    } else if (userInfo && userInfo.id) {
      this.setData({ userId: userInfo.id });
    }

    // 获取当前登录用户信息
    const currentUserInfo = wx.getStorageSync('userInfo');
    const currentUserId = currentUserInfo ? currentUserInfo.userId : '';
    // 如果有传入的用户信息，直接使用
    if (userInfo) {
      this.setData({
        userHome: {
          userInfo,
          isMyself: currentUserId === userInfo.userId
        }
      });
      
      // 获取用户发布的闲置物品数量和评价数量
      this.getUserCounts(userInfo.userId);
    }
    // 设置导航栏标题
    wx.setNavigationBarTitle({
      title: this.data.userHome.isMyself ? '我的主页' : '用户主页'
    });

    // 加载用户动态
    this.loadTabData();
  },

  // 获取用户发布的闲置物品数量和评价数量
  async getUserCounts(userId: number) {
    try {
      // 获取用户发布的闲置物品数量
      const goodsCountRes = await getUserGoodsCount(userId);
      if (goodsCountRes.code === 200) {
        this.setData({
          goodsCount: goodsCountRes.data
        });
      }

      // 获取用户收到的评价数量
      const reviewCountRes = await getUserCommentCount(userId);
      if (reviewCountRes.code === 200) {
        this.setData({
          reviewCount: reviewCountRes.data
        });
      }
    } catch (error) {
      console.error('获取用户数据统计失败:', error);
    }
  },

  async onPullDownRefresh() {
    this.setData({
      page: 1,
      hasMore: true,
    });
    await Promise.all([
      this.loadUserInfo(),
      this.loadTabData()
    ]);
    wx.stopPullDownRefresh();
  },

  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    const nextPage = this.data.page + 1;

    try {
      this.setData({ isLoading: true });
      
      if (this.data.activeTab === 'goods') {
        const userId = this.data.userHome.userInfo.userId;
        const goodsResponse = await getUserGoods(userId, nextPage, this.data.pageSize);

        if (goodsResponse.code === 200) {
          const isLastPage = goodsResponse.data.page >= goodsResponse.data.pages;
          const noMoreData = goodsResponse.data.list.length === 0;
          
          const newList = goodsResponse.data.list.filter(newItem => 
            !this.data.goodsList.some(existingItem => existingItem.goodsId === newItem.goodsId)
          );
          
          this.setData({
            goodsList: [...this.data.goodsList, ...newList],
            page: nextPage,
            hasMore: !isLastPage && !noMoreData,
            isLoading: false
          });
        } else {
          this.setData({ 
            isLoading: false,
            hasMore: false 
          });
        }
      } else if (this.data.activeTab === 'reviews') {
        const userId = this.data.userHome.userInfo.userId;
        const reviewsResponse = await getUserComments(userId, nextPage, this.data.pageSize);
        
        if (reviewsResponse.code === 200) {
          const isLastPage = reviewsResponse.data.page >= reviewsResponse.data.pages;
          const noMoreData = reviewsResponse.data.list.length === 0;
          
          this.setData({
            reviewList: [...this.data.reviewList, ...reviewsResponse.data.list],
            page: nextPage,
            hasMore: !isLastPage && !noMoreData,
            isLoading: false
          });
        } else {
          this.setData({ 
            isLoading: false,
            hasMore: false 
          });
        }
      }
    } catch (error) {
      console.error('加载更多失败:', error);
      this.setData({ 
        isLoading: false,
        hasMore: false 
      });
    }
  },

  // 加载用户信息
  async loadUserInfo() {
    try {
      const currentUserInfo = wx.getStorageSync('userInfo');
      const currentUserId = currentUserInfo ? currentUserInfo.id : '';
      console.log("加载用户信息咯");
      
      const userInfo: IUserHome = {
        userInfo: currentUserInfo,
        isMyself: this.data.userId === currentUserId
      };
      
      // 更新导航栏标题
      wx.setNavigationBarTitle({
        title: userInfo.isMyself ? '我的主页' : '用户主页'
      });
    } catch (error) {
      console.error('加载用户资料失败:', error);
    }
  },

  // 切换选项卡
  switchTab(e: any) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab,
      page: 1,
      hasMore: true
    });
    this.loadTabData();
  },

  // 切换评价类型
  switchReviewType(e: any) {
    const type = e.currentTarget.dataset.type;
    this.setData({
      reviewType: type,
      page: 1,
      hasMore: true
    });
    this.loadReviewList();
  },

  // 加载选项卡数据
  async loadTabData() {
    const { activeTab } = this.data;
    switch (activeTab) {
      case 'goods':
        await this.loadGoodsList();
        break;
      case 'reviews':
        await this.loadReviewList();
        break;
    }
  },

  // 加载闲置物品列表
  async loadGoodsList() {
    try {
      this.setData({ isLoading: true });
      const userId = this.data.userHome.userInfo.userId;
      const goodsResponse = await getUserGoods(userId, 1, this.data.pageSize);
      if(goodsResponse.code === 200){
        const isLastPage = goodsResponse.data.page >= goodsResponse.data.pages;
        const noMoreData = goodsResponse.data.list.length === 0;
        this.setData({
          goodsList: goodsResponse.data.list,
          isLoading: false,
          hasMore: !isLastPage && !noMoreData
        })
      } else {
        this.setData({ 
          isLoading: false,
          hasMore: false 
        });
      }
    } catch (error) {
      console.error('加载闲置物品列表失败:', error);
      this.setData({ isLoading: false });
    }
  },

  // 加载评价列表
  async loadReviewList() {
    try {
      this.setData({ isLoading: true });
      const userId = this.data.userHome.userInfo.userId;
      const reviewsResponse = await getUserComments(userId, 1, this.data.pageSize);
      if (reviewsResponse.code === 200) {
        const isLastPage = reviewsResponse.data.page >= reviewsResponse.data.pages;
        const noMoreData = reviewsResponse.data.list.length === 0;
        
        this.setData({
          reviewList: reviewsResponse.data.list,
          isLoading: false,
          hasMore: !isLastPage && !noMoreData
        });
      } else {
        this.setData({ 
          isLoading: false,
          hasMore: false 
        });
      }
    } catch (error) {
      console.error('加载评价列表失败:', error);
      this.setData({ isLoading: false });
    }
  },

  // 跳转到闲置物品详情
  navigateToGoodsDetail(e: any) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${id}`
    });
  },
  // 跳转到评价详情页
  navigateToCommentDetail(e: any){
    const {orderId} = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/comment-detail/comment-detail?orderId=${orderId}`
    });
  },

  // 跳转到编辑资料页面
  navigateToEdit() {
    wx.navigateTo({
      url: '/pages/profile/edit/edit'
    });
  },

  // 跳转到用户主页
  navigateToUser(e: any) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/profile/user-profile/user-profile?id=${id}`
    });
  },

  // 格式化时间
  formatTime(time: string): string {
    if (!time) return '';
    
    const date = new Date(time);
    const now = new Date();
    const diffMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffMinutes < 60) {
      return `${diffMinutes}分钟前`;
    } else if (diffMinutes < 24 * 60) {
      return `${Math.floor(diffMinutes / 60)}小时前`;
    } else {
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    }
  }
}); 