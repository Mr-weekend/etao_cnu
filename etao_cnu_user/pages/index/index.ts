import { getGoodsList, getBannerList, getCategoryList, GoodsItem, Category } from '../../services/api';

Page({
  data: {
    bannerList: [
      { id: 1, imageUrl: '' },
      { id: 2, imageUrl: '' }
    ],
    categories: [] as Category[],
    goodsList: [] as GoodsItem[],
    page: 1,
    pageSize: 10,
    hasMore: true,
    isLoading: false,
    isRefreshing: false,
    currentCategoryId: 0, // 当前选中的分类ID，0表示全部
    selectedCategoryIndex: -1 // 当前选中的分类索引
  },

  onLoad() {
    console.log('onLoad');
    this.loadInitialData();
    const token = wx.getStorageSync('token');
    if(token){
      const app = getApp()
      app.globalData.isLogin = true
      app.checkLoginStatus();
    }
  },

  // 点击分类
  async onCategoryTap(e: any) {
    const categoryId = parseInt(e.currentTarget.dataset.categoryId);
    const index = e.currentTarget.dataset.index;
    
    // 如果点击的是当前分类，不做处理
    if (this.data.currentCategoryId === categoryId) {
      return;
    }

    this.setData({
      currentCategoryId: categoryId,
      selectedCategoryIndex: index,
      page: 1,
      hasMore: true,
      goodsList: [],
      isLoading: true
    });

    try {
      // 只有categoryId大于0时才传递
      const response = await getGoodsList(1, this.data.pageSize, categoryId > 0 ? categoryId : undefined);
      if (response.code === 200) {
        const isLastPage = response.data.page >= response.data.pages;
        const noMoreData = response.data.list.length === 0;

        this.setData({
          goodsList: response.data.list,
          hasMore: !isLastPage && !noMoreData,
          isLoading: false
        });

        if (response.data.list.length === 0) {
          wx.showToast({
            title: '暂无闲置物品',
            icon: 'none'
          });
        }
      }
    } catch (error) {
      console.error('获取分类闲置物品失败:', error);
      wx.showToast({
        title: '获取闲置物品失败',
        icon: 'error'
      });
      this.setData({ isLoading: false });
    }
  },

  // 上拉加载更多
  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    
    const nextPage = this.data.page + 1;
    this.setData({ isLoading: true });
    
    try {
      const response = await getGoodsList(
        nextPage, 
        this.data.pageSize, 
        this.data.currentCategoryId > 0 ? this.data.currentCategoryId : undefined
      );
      
      if (response.code === 200) {
        const isLastPage = response.data.page >= response.data.pages;
        const noMoreData = response.data.list.length === 0;
        
        const newList = response.data.list.filter(newItem => 
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
    } catch (error) {
      console.error('加载更多失败:', error);
      this.setData({ 
        isLoading: false,
        hasMore: false 
      });
    }
  },

  // 下拉刷新
  async onPullDownRefresh() {
    this.setData({
      isRefreshing: true,
      page: 1,
      hasMore: true,
      goodsList: [],
      categories: []
    });
    
    try {
      const goodsResponse = await getGoodsList(
        1, 
        this.data.pageSize, 
        this.data.currentCategoryId > 0 ? this.data.currentCategoryId : undefined
      );
      const categoryResponse = await getCategoryList()
      if (goodsResponse.code === 200 && categoryResponse.code === 200) {
        const isLastPage = goodsResponse.data.page >= goodsResponse.data.pages;
        const noMoreData = goodsResponse.data.list.length === 0;
        
        this.setData({
          goodsList: goodsResponse.data.list,
          hasMore: !isLastPage && !noMoreData,
          categories: categoryResponse.data.list
        });

        // wx.showToast({
        //   title: '刷新成功',
        //   icon: 'success'
        // });
      }else{
        console.error('刷新失败');
        wx.showToast({
          title: '刷新失败',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error('刷新失败:', error);
      wx.showToast({
        title: '刷新失败',
        icon: 'error'
      });
    } finally {
      this.setData({ isRefreshing: false });
      wx.stopPullDownRefresh();
    }
  },

  // 加载初始数据
  async loadInitialData() {
    try {
      console.log('Loading initial data...');
      wx.showLoading({
        title: '加载中...'
      });
      
      const [bannerList, goodsResponse, categoryResponse] = await Promise.all([
        getBannerList(),
        getGoodsList(1, this.data.pageSize),
        getCategoryList()
      ]);
      
      if (goodsResponse.code === 200 && categoryResponse.code === 200) {
        const isLastPage = goodsResponse.data.page >= goodsResponse.data.pages;
        const noMoreData = goodsResponse.data.list.length === 0;

        this.setData({
          bannerList: bannerList.length ? bannerList : this.data.bannerList,
          goodsList: goodsResponse.data.list,
          categories: categoryResponse.data.list,
          page: 1,
          hasMore: !isLastPage && !noMoreData
        });
      }
    } catch (error) {
      console.error('Error loading initial data:', error);
      throw error;
    } finally {
      wx.hideLoading();
    }
  },

  // 跳转到闲置物品详情
  navigateToDetail(e: any) {
    const goodsId = e.currentTarget.dataset.goodsId;
    if (!goodsId) {
      wx.showToast({
        title: '闲置物品ID不存在',
        icon: 'error'
      });
      return;
    }
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${goodsId}`
    });
  },

  // 跳转到发布页面
  navigateToPublish() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo')
    if (!token) {
      wx.showToast({
        title: '请先登录！',
        icon: 'none'
      });
      // 跳转到我的页面
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/mine/mine'
        });
      }, 1500);
      return;
    }else if(token && userInfo.status === 0){
      const violationReason = wx.getStorageSync('userInfo').violationReason
      wx.showToast({
        title:`账号已被封禁，违规原因：${violationReason}`,
        icon:'none'
      })
      return;
    }else if(token && userInfo.status === 2){
      wx.showToast({
        title: '请先进行认证',
        icon:'none'
      })
      return;
    }
    wx.navigateTo({
      url: '/pages/publish-goods/publish-goods'
    });
  },

  // 跳转到搜索页面
  navigateToSearch() {
    wx.navigateTo({
      url: '/pages/search/search'
    });
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({
        selected: 0
      });
    }
  }
}); 