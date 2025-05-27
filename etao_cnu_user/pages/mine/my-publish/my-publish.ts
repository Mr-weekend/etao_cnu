import { getMyPublishedGoods, GoodsItem, removeGoods, deleteGoods, getUserGoodsCount, putGoods, getUserRemoveGoods, getUserRemoveGoodsCount } from '../../../services/api';

Page({
  data: {
    goodsList: [] as GoodsItem[],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
    activeTab: 'published', // 当前激活的标签页
    publishCount: 0, // 发布的闲置数量
    removeCount: 0, // 下架的闲置数量
  },

  onLoad() {
    this.loadCounts();
    this.loadGoodsList(true);
  },

  onPullDownRefresh() {
    this.loadCounts();
    this.loadGoodsList(true);
  },

  async loadCounts() {
    try {
      const userInfo = wx.getStorageSync('userInfo');
      if (!userInfo) {
        console.error('未获取到用户信息');
        return;
      }
      const userId = userInfo.userId;
      const [publishRes, removeRes] = await Promise.all([
        getUserGoodsCount(userId),
        getUserRemoveGoodsCount()
      ]);
      if (publishRes.code === 200 && removeRes.code === 200) {
        this.setData({
          publishCount: publishRes.data,
          removeCount: removeRes.data
        });
      } else {
        console.error('获取数量失败:', publishRes.message, removeRes.message);
      }
    } catch (error) {
      console.error('获取数量统计失败:', error);
    }
  },

  // 切换标签页
  switchTab(e: any) {
    const tab = e.currentTarget.dataset.tab;
    if (tab !== this.data.activeTab) {
      this.setData({
        activeTab: tab,
        goodsList: [],
        page: 1,
        hasMore: true
      });
      this.loadGoodsList(true);
    }
  },

  // 加载闲置物品列表
  async loadGoodsList(refresh = false) {
    if (this.data.loading) return;
    const page = refresh ? 1 : this.data.page;
    this.setData({ loading: true });
    try {
      const res = await (this.data.activeTab === 'published' 
        ? getMyPublishedGoods(page, this.data.size)
        : getUserRemoveGoods(page, this.data.size));
      
      if (res.code === 200) {
        const { list, total } = res.data;
        const hasMore = list.length > 0 && (page * this.data.size) < total;
        
        // 格式化发布时间
        const formattedList = list.map(item => ({
          ...item,
          publishTime: this.formatTime(item.publishTime)
        }));
        
        this.setData({
          goodsList: refresh ? formattedList : [...this.data.goodsList, ...formattedList],
          page: refresh ? 2 : this.data.page + 1,
          hasMore
        });
      } else {
        wx.showToast({
          title: res.message || '获取列表失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('获取列表失败:', error);
      wx.showToast({
        title: '获取列表失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
      if (wx.stopPullDownRefresh) {
        wx.stopPullDownRefresh();
      }
    }
  },

  // 加载更多
  loadMoreGoods() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadGoodsList(false);
    }
  },

  // 格式化时间
  formatTime(timeStr: string): string {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    
    // 不到1分钟
    if (diff < 60 * 1000) {
      return '刚刚';
    }
    
    // 不到1小时
    if (diff < 60 * 60 * 1000) {
      return `${Math.floor(diff / (60 * 1000))}分钟前`;
    }
    
    // 不到24小时
    if (diff < 24 * 60 * 60 * 1000) {
      return `${Math.floor(diff / (60 * 60 * 1000))}小时前`;
    }
    
    // 不到30天
    if (diff < 30 * 24 * 60 * 60 * 1000) {
      return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`;
    }
    
    // 格式化为年月日
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
  },

  // 导航到详情页
  navigateToDetail(e: any) {
    const goodsId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${goodsId}`
    });
  },

  // 编辑闲置物品
  editGoods(e: any) {
    const goods = e.currentTarget.dataset.goods;
    wx.navigateTo({
      url: `/pages/publish-goods/publish-goods?type=edit&goodsInfo=${encodeURIComponent(JSON.stringify(goods))}`
    });
  },

  // 下架闲置物品
  async removeGoods(e: any) {
    const goodsId = e.currentTarget.dataset.id;
    const self = this;
    
    wx.showModal({
      title: '提示',
      content: '确定要下架该闲置物品吗？',
      async success(res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '下架中...' });
            const result = await removeGoods(goodsId);
            wx.hideLoading();
            
            if (result.code === 200) {
              wx.showToast({
                title: '下架成功',
                icon: 'success'
              });
              // 刷新列表
              self.loadGoodsList(true);
            } else {
              wx.showToast({
                title: result.message || '下架失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            console.error('下架闲置物品失败:', error);
            wx.showToast({
              title: '下架失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 重新上架闲置物品
  async onsaleGoods(e: any) {
    const goodsId = e.currentTarget.dataset.id;
    const self = this;
    
    wx.showModal({
      title: '提示',
      content: '确定要重新上架该闲置物品吗？',
      async success(res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '上架中...' });
            const result = await putGoods(goodsId);
            wx.hideLoading();
            
            if (result.code === 200) {
              wx.showToast({
                title: '上架成功',
                icon: 'success'
              });
              // 刷新列表和数量统计
              self.loadCounts();
              self.loadGoodsList(true);
            } else {
              wx.showToast({
                title: result.message || '上架失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            console.error('上架闲置物品失败:', error);
            wx.showToast({
              title: '上架失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 删除闲置物品
  async deleteGoods(e: any) {
    const goodsId = e.currentTarget.dataset.id;
    const self = this;
    
    wx.showModal({
      title: '提示',
      content: '确定要删除该闲置物品吗？删除后无法恢复',
      async success(res) {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '删除中...' });
            const result = await deleteGoods(goodsId);
            wx.hideLoading();
            
            if (result.code === 200) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              });
              // 刷新列表
              self.loadGoodsList(true);
            } else {
              wx.showToast({
                title: result.message || '删除失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            console.error('删除闲置物品失败:', error);
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 导航到发布页
  navigateToPublish() {
    wx.navigateTo({
      url: '/pages/publish-goods/publish-goods'
    });
  },

}); 