import { getBrowseHistoryList, batchDeleteBrowseHistory, clearBrowseHistory, deleteBrowseHistory, BrowseHistoryItem } from '../../../services/api';

interface GroupedBrowseHistories {
  date: string;
  items: BrowseHistoryItem[];
}

Page({
  data: {
    groupedBrowseHistories: [] as GroupedBrowseHistories[],
    isLoading: false,
    isRefreshing: false,
    hasMore: true,
    page: 1,
    size: 10,
    selectedItems: [] as number[],
    isEditMode: false,
    totalSelected: 0,
    totalItems: 0,
    longPressTimer: null as any,
    longPressGoodsId: null as number | null
  },

  onLoad() {
    this.loadBrowseHistoryList();
  },

  groupBrowseHistoriesByDate(browseHistories: BrowseHistoryItem[]) {
    const groups: { [key: string]: BrowseHistoryItem[] } = {};
    browseHistories.forEach(item => {
      const date = item.browseTime.split(' ')[0];
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(item);
    });

    return Object.entries(groups)
      .map(([date, items]) => ({ date, items }))
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  },

  async loadBrowseHistoryList() {
    try {
      this.setData({ isLoading: true });
      const res = await getBrowseHistoryList(this.data.page, this.data.size);
      if (res.code === 200) {
        let newList;
        if (this.data.page === 1) {
          newList = res.data.list;
        } else {
          const existingItems = this.data.groupedBrowseHistories.flatMap(g => g.items);
          newList = [...existingItems, ...res.data.list];
        }

        const groupedBrowseHistories = this.groupBrowseHistoriesByDate(newList);
        const totalItems = groupedBrowseHistories.reduce((acc, group) => acc + group.items.length, 0);

        this.setData({
          groupedBrowseHistories,
          hasMore: this.data.page < res.data.pages,
          totalItems
        });
      }
    } catch (error) {
      console.error('加载浏览记录列表失败:', error);
      wx.showToast({ title: '加载失败', icon: 'error' });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    this.setData({ page: this.data.page + 1 });
    await this.loadBrowseHistoryList();
  },

  toggleEditMode() {
    this.setData({
      isEditMode: !this.data.isEditMode,
      selectedItems: [],
      totalSelected: 0
    });
  },

  toggleSelect(e: any) {
    const goodsId = Number(e.currentTarget.dataset.goodsId);
    console.log(goodsId);
    
    const selectedItems = [...this.data.selectedItems];
    const index = selectedItems.indexOf(goodsId);
    if (index > -1) {
      selectedItems.splice(index, 1);
    } else {
      selectedItems.push(goodsId);
    }

    this.setData({ 
      selectedItems,
      totalSelected: selectedItems.length
    });
  },

  toggleSelectAll() {
    const allGoodsIds = this.data.groupedBrowseHistories
      .flatMap(group => group.items.map(item => item.goodsId));
    const shouldSelectAll = this.data.selectedItems.length !== allGoodsIds.length;
    
    this.setData({
      selectedItems: shouldSelectAll ? allGoodsIds : [],
      totalSelected: shouldSelectAll ? allGoodsIds.length : 0
    });
  },

  async batchCancel() {
    if (this.data.selectedItems.length === 0) {
      wx.showToast({ title: '请选择闲置物品', icon: 'none' });
      return;
    }

    wx.showModal({
      title: '提示',
      content: '确定删除这些闲置物品的浏览记录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await batchDeleteBrowseHistory(this.data.selectedItems);
            if (response.code === 200) {
              wx.showToast({ title: '删除成功', icon: 'success' });
              this.setData({ 
                page: 1,
                selectedItems: [],
                isEditMode: false,
                totalSelected: 0
              });
              this.loadBrowseHistoryList();
            }
          } catch (error) {
            console.error('批量删除浏览记录失败:', error);
            wx.showToast({ title: '操作失败', icon: 'error' });
          }
        }
      }
    });
  },

  async clearAll() {
    wx.showModal({
      title: '提示',
      content: '确定清空所有浏览记录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await clearBrowseHistory();
            if (response.code === 200) {
              wx.showToast({ title: '清空成功', icon: 'success' });
              this.setData({
                groupedCollections: [],
                selectedItems: [],
                isEditMode: false,
                totalSelected: 0,
                totalItems: 0
              });
            }
          } catch (error) {
            console.error('清空收藏失败:', error);
            wx.showToast({ title: '操作失败', icon: 'error' });
          }
        }
      }
    });
  },

  // 长按删除浏览记录
  deleteBrowseHistory(e: any) {
    const goodsId = Number(e.currentTarget.dataset.id);
    this.setData({ longPressGoodsId: goodsId });
    
    wx.showModal({
      title: '提示',
      content: '确定删除浏览记录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await deleteBrowseHistory(goodsId);
            if (response.code === 200) {
              wx.showToast({ title: '删除成功', icon: 'success' });
              this.setData({ page: 1 });
              this.loadBrowseHistoryList();
            }
          } catch (error) {
            console.error('删除单个浏览记录失败:', error);
            wx.showToast({ title: '操作失败', icon: 'error' });
          }
        }
      }
    });
  },

  navigateToGoodsDetail(e: any){
    const goodsId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${goodsId}`
    })
  }
});