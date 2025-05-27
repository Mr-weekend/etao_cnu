import { getCollectionList, batchCancelCollection, clearCollection, cancelCollection } from '../../../services/api';

interface CollectionItem {
  goodsInfo: {
    goodsId: number;
    description: string;
    price: number;
    imageUrls: string[];
    publisher: {
      userId: number;
      userName: string;
      avatarUrl: string;
    };
  };
  createdAt: string;
}

interface GroupedCollections {
  date: string;
  items: CollectionItem[];
}

Page({
  data: {
    groupedCollections: [] as GroupedCollections[],
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
    this.loadCollectionList();
  },

  groupCollectionsByDate(collections: CollectionItem[]) {
    const groups: { [key: string]: CollectionItem[] } = {};
    
    collections.forEach(item => {
      const date = item.createdAt.split(' ')[0];
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(item);
    });

    return Object.entries(groups)
      .map(([date, items]) => ({ date, items }))
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  },

  async loadCollectionList() {
    try {
      this.setData({ isLoading: true });
      const res = await getCollectionList(this.data.page, this.data.size);
      
      if (res.code === 200) {
        // 转换闲置物品ID为数字类型
        const processList = (list: CollectionItem[]) => list.map(item => ({
          ...item,
          goodsInfo: {
            ...item.goodsInfo,
            goodsId: Number(item.goodsInfo.goodsId)
          }
        }));

        let newList;
        if (this.data.page === 1) {
          newList = processList(res.data.list);
        } else {
          const existingItems = this.data.groupedCollections.flatMap(g => g.items);
          newList = [...existingItems, ...processList(res.data.list)];
        }

        const groupedCollections = this.groupCollectionsByDate(newList);
        const totalItems = groupedCollections.reduce((acc, group) => acc + group.items.length, 0);

        this.setData({
          groupedCollections,
          hasMore: this.data.page < res.data.pages,
          totalItems
        });
      }
    } catch (error) {
      console.error('加载收藏列表失败:', error);
      wx.showToast({ title: '加载失败', icon: 'error' });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // async onPullDownRefresh() {
  //   this.setData({
  //     isRefreshing: true,
  //     page: 1,
  //     hasMore: true,
  //     groupedCollections: []
  //   });
  //   try {
  //     const res = await getCollectionList(this.data.page, this.data.size);
  //     if (res.code === 200) {
  //       const isLastPage = res.data.page >= res.data.pages;
  //       const noMoreData = res.data.list.length === 0;
        
  //       this.setData({
  //         // groupedCollections: res.data.list,
  //         hasMore: !isLastPage && !noMoreData,
  //       });

  //       wx.showToast({
  //         title: '刷新成功',
  //         icon: 'success'
  //       });
  //     }
  //   } catch (error) {
  //     console.error('刷新失败:', error);
  //     wx.showToast({
  //       title: '刷新失败',
  //       icon: 'error'
  //     });
  //   } finally {
  //     this.setData({ isRefreshing: false });
  //     wx.stopPullDownRefresh();
  //   }
  // },

  async onReachBottom() {
    if (!this.data.hasMore || this.data.isLoading) return;
    this.setData({ page: this.data.page + 1 });
    await this.loadCollectionList();
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
    const allGoodsIds = this.data.groupedCollections
      .flatMap(group => group.items.map(item => item.goodsInfo.goodsId));
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
      content: '确定取消收藏这些闲置物品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await batchCancelCollection(this.data.selectedItems);
            if (response.code === 200) {
              wx.showToast({ title: '取消成功', icon: 'success' });
              this.setData({ 
                page: 1,
                selectedItems: [],
                isEditMode: false,
                totalSelected: 0
              });
              this.loadCollectionList();
            }
          } catch (error) {
            console.error('批量取消收藏失败:', error);
            wx.showToast({ title: '操作失败', icon: 'error' });
          }
        }
      }
    });
  },

  async clearAll() {
    wx.showModal({
      title: '提示',
      content: '确定清空所有收藏吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await clearCollection();
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

  // 长按取消收藏
  cancelCollection(e: any) {
    const goodsId = Number(e.currentTarget.dataset.id);
    this.setData({ longPressGoodsId: goodsId });
    
    wx.showModal({
      title: '提示',
      content: '确定取消收藏该闲置物品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const response = await cancelCollection(goodsId);
            if (response.code === 200) {
              wx.showToast({ title: '取消收藏成功', icon: 'success' });
              this.setData({ page: 1 });
              this.loadCollectionList();
            }
          } catch (error) {
            console.error('取消单个闲置物品收藏失败:', error);
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