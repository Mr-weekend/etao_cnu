import { getCategoryList, GoodsItem, Category, SearchGoodsParams, searchGoodsWithParams } from '../../services/api';

Page({
  data: {
    keyword: '',
    categories: [] as Category[],
    goodsList: [] as GoodsItem[],
    page: 1,
    size: 10,
    hasMore: true,
    isLoading: false,
    isLoadingMore: false,
    hasSearched: false,
    // 搜索历史
    searchHistory: [] as string[],
    // 筛选相关
    filterShowing: '', // 当前显示的筛选面板：'category' | 'price' | ''
    currentFilter: '', // 当前激活的筛选项
    selectedCategoryId: 0,
    selectedCategory: null as {categoryId: number, categoryName: string} | null,
    minPrice: '',
    maxPrice: '',
    priceSort: 0, // 0-不排序, 1-升序, 2-降序
    priceSortText: '',
    
    // 搜索参数
    searchParams: {
      keyword: '',
      categoryId: undefined,
      priceRankType: undefined,
      bottomPrice: undefined,
      topPrice: undefined,
      page: 1,
      size: 10
    } as SearchGoodsParams
  },

  onLoad(options: any) {
    // 加载搜索历史
    this.loadSearchHistory();
    // 加载分类数据
    this.loadCategories();
    // 如果从其他页面带有关键字参数
    if (options.keyword) {
      this.setData({
        keyword: options.keyword,
        'searchParams.keyword': options.keyword
      });
      this.search();
    }
  },
  // 加载搜索历史
  loadSearchHistory() {
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({
      searchHistory: history
    });
  },
  // 保存搜索历史
  saveSearchHistory(keyword: string) {
    if (!keyword.trim()) return;
    // 获取当前历史
    let history = wx.getStorageSync('searchHistory') || [];
    // 如果已存在，先移除
    history = history.filter((item: string) => item !== keyword);
    // 添加到开头
    history.unshift(keyword);
    // 只保留最近10条
    if (history.length > 10) {
      history = history.slice(0, 10);
    }
    // 保存到本地
    wx.setStorageSync('searchHistory', history);
    // 更新数据
    this.setData({
      searchHistory: history
    });
  },
  // 清除单条历史记录
  clearHistoryItem(e: any) {
    const index = e.currentTarget.dataset.index;
    const history = [...this.data.searchHistory];
    history.splice(index, 1);
    
    wx.setStorageSync('searchHistory', history);
    this.setData({
      searchHistory: history
    });
  },

  // 清空所有历史记录
  clearAllHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清空所有搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory');
          this.setData({
            searchHistory: []
          });
        }
      }
    });
  },

  // 点击历史记录项
  onHistoryItemTap(e: any) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({
      keyword: keyword,
      'searchParams.keyword': keyword
    });
    this.search();
  },

  // 加载分类
  async loadCategories() {
    try {
      const res = await getCategoryList();
      if (res.code === 200) {
        this.setData({
          categories: res.data.list
        });
      }
    } catch (error) {
      console.error('获取分类失败:', error);
    }
  },

  // 输入关键词
  onKeywordInput(e: any) {
    this.setData({
      keyword: e.detail.value
    });
  },

  // 清空关键词
  clearKeyword() {
    this.setData({
      keyword: '',
      'searchParams.keyword': '',
      hasSearched: false,
      goodsList: []
    });
  },

  // 切换筛选面板
  toggleFilter(e: any) {
    const filter = e.currentTarget.dataset.filter;
    // 如果点击的是当前已经打开的筛选项，则关闭
    if (this.data.filterShowing === filter) {
      this.setData({
        filterShowing: '',
        currentFilter: this.data.currentFilter // 保持当前激活的筛选项
      });
    } else {
      this.setData({
        filterShowing: filter,
        currentFilter: filter
      });
    }
  },

  // 选择分类
  selectCategory(e: any) {
    const categoryId = parseInt(e.currentTarget.dataset.id);
    const categoryName = e.currentTarget.dataset.name;
    
    this.setData({
      selectedCategoryId: categoryId,
      selectedCategory: categoryId === 0 ? null : {
        categoryId,
        categoryName
      },
      'searchParams.categoryId': categoryId === 0 ? undefined : categoryId,
      filterShowing: '' // 关闭筛选面板
    });
    
    // 重新搜索
    this.resetAndSearch();
  },

  // 设置最低价格
  onMinPriceInput(e: any) {
    this.setData({
      minPrice: e.detail.value
    });
  },

  // 设置最高价格
  onMaxPriceInput(e: any) {
    this.setData({
      maxPrice: e.detail.value
    });
  },

  // 确认价格区间
  confirmPriceRange() {
    const { minPrice, maxPrice } = this.data;
    
    this.setData({
      'searchParams.bottomPrice': minPrice ? parseFloat(minPrice) : undefined,
      'searchParams.topPrice': maxPrice ? parseFloat(maxPrice) : undefined,
      filterShowing: '' // 关闭筛选面板
    });
    
    // 重新搜索
    this.resetAndSearch();
  },

  // 切换价格排序
  togglePriceSort() {
    // 0-不排序, 1-升序, 2-降序
    let newSort = (this.data.priceSort + 1) % 3;
    let sortText = '';
    switch (newSort) {
      case 0:
        sortText = '';
        break;
      case 1:
        sortText = '↑';
        break;
      case 2:
        sortText = '↓';
        break;
    }
    this.setData({
      priceSort: newSort,
      priceSortText: sortText,
      'searchParams.priceRankType': newSort === 0 ? undefined : newSort - 1
    });
    
    // 重新搜索
    this.resetAndSearch();
  },

  // 重置搜索并执行
  resetAndSearch() {
    this.setData({
      page: 1,
      'searchParams.page': 1,
      goodsList: [],
      hasMore: true
    });
    this.search();
  },

  // 执行搜索
  async search() {
    const { keyword, isLoading } = this.data;
    
    if (!keyword.trim()) {
      wx.showToast({
        title: '请输入搜索关键词',
        icon: 'none'
      });
      return;
    }
    if (isLoading) return;
    // 保存到搜索历史
    this.saveSearchHistory(keyword);
    this.setData({
      isLoading: true,
      hasSearched: true
    });
    try {
      // 构建实际请求参数，只包含有效值
      const requestParams: any = {
        keyword: keyword,
        page: this.data.page,
        size: this.data.size
      };
      // 只有当有值时才添加参数
      if (this.data.searchParams.categoryId !== undefined && this.data.searchParams.categoryId > 0) {
        requestParams.categoryId = this.data.searchParams.categoryId;
      }
      if (this.data.searchParams.priceRankType !== undefined && this.data.searchParams.priceRankType !== null) {
        requestParams.priceRankType = this.data.searchParams.priceRankType;
      }
      if (this.data.searchParams.bottomPrice !== undefined && this.data.searchParams.bottomPrice !== null) {
        requestParams.bottomPrice = this.data.searchParams.bottomPrice;
      }
      if (this.data.searchParams.topPrice !== undefined && this.data.searchParams.topPrice !== null) {
        requestParams.topPrice = this.data.searchParams.topPrice;
      }
      const res = await searchGoodsWithParams(requestParams);
      if (res.code === 200) {
        const { list, page, pages } = res.data;
        this.setData({
          goodsList: list || [],
          page: page,
          hasMore: page < pages
        });
      } else {
        wx.showToast({
          title: '搜索失败',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error('搜索失败:', error);
      wx.showToast({
        title: '搜索失败',
        icon: 'error'
      });
    } finally {
      this.setData({
        isLoading: false
      });
    }
  },

  // 加载更多
  async loadMore() {
    if (!this.data.hasMore || this.data.isLoadingMore) return;
    const nextPage = this.data.page + 1;
    this.setData({
      isLoadingMore: true
    });
    try {
      // 构建请求参数，只包含有效值
      const requestParams: any = {
        keyword: this.data.keyword,
        page: nextPage,
        size: this.data.size
      };
      // 只有当有值时才添加参数
      if (this.data.searchParams.categoryId !== undefined && this.data.searchParams.categoryId > 0) {
        requestParams.categoryId = this.data.searchParams.categoryId;
      }
      if (this.data.searchParams.priceRankType !== undefined && this.data.searchParams.priceRankType !== null) {
        requestParams.priceRankType = this.data.searchParams.priceRankType;
      }
      if (this.data.searchParams.bottomPrice !== undefined && this.data.searchParams.bottomPrice !== null) {
        requestParams.bottomPrice = this.data.searchParams.bottomPrice;
      }
      if (this.data.searchParams.topPrice !== undefined && this.data.searchParams.topPrice !== null) {
        requestParams.topPrice = this.data.searchParams.topPrice;
      }
      const res = await searchGoodsWithParams(requestParams);
      if (res.code === 200) {
        const { list, page, pages } = res.data;
        this.setData({
          goodsList: [...this.data.goodsList, ...(list || [])],
          page: page,
          hasMore: page < pages
        });
      }
    } catch (error) {
      console.error('加载更多失败:', error);
    } finally {
      this.setData({
        isLoadingMore: false
      });
    }
  },

  // 跳转到闲置物品详情
  navigateToDetail(e: any) {
    const goodsId = e.currentTarget.dataset.goodsId;
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${goodsId}`
    });
  },

  // 触底加载更多
  onReachBottom() {
    if (this.data.hasSearched) {
      this.loadMore();
    }
  },

  // 下拉刷新
  onPullDownRefresh() {
    if (this.data.hasSearched) {
      this.resetAndSearch();
    }
    wx.stopPullDownRefresh();
  }
}); 