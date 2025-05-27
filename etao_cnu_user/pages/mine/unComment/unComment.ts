import { getBuyerUncommentOrders, getSellerUncommentOrders, getBuyerUncommentOrderCount, getSellerUncommentOrderCount, OrderDetail } from '../../../services/api';

interface IData {
  activeTab: 'bought' | 'sold';
  boughtOrders: OrderDetail[];
  soldOrders: OrderDetail[];
  boughtCount: number;
  soldCount: number;
  loading: boolean;
  page: number;
  size: number;
  hasMore: boolean;
}

Page({
  data: {
    activeTab: 'bought',
    boughtOrders: [],
    soldOrders: [],
    boughtCount: 0,
    soldCount: 0,
    loading: true,
    page: 1,
    size: 10,
    hasMore: true
  } as IData,

  onLoad() {
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData();
  },

  onReachBottom() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      page: this.data.page + 1
    });
    
    this.loadOrders(false);
  },

  async loadData(isRefresh: boolean = true) {
    if (isRefresh) {
      this.setData({
        page: 1,
        hasMore: true,
        loading: true
      });
    }

    try {
      const [boughtCountRes, soldCountRes] = await Promise.all([
        getBuyerUncommentOrderCount(),
        getSellerUncommentOrderCount()
      ]);
      console.log(boughtCountRes);
      console.log(soldCountRes);
      
      if(boughtCountRes.code===200&&soldCountRes.code==200){
        this.setData({
          boughtCount: boughtCountRes.data,
          soldCount: soldCountRes.data
        });
      }
      await this.loadOrders(isRefresh);
    } catch (error) {
      console.error('加载数据失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  async loadOrders(isRefresh: boolean = true) {
    const { activeTab, page, size } = this.data;
    
    try {
      const api = activeTab === 'bought' ? getBuyerUncommentOrders : getSellerUncommentOrders;
      const res = await api(page, size);
      
      if (res.code === 200) {
        const orders = res.data.list;
        
        const ordersKey = `${activeTab}Orders`;
        
        this.setData({
          [ordersKey]: isRefresh ? orders : [...this.data[ordersKey], ...orders],
          hasMore: res.data.pages > page
        });
      }
    } catch (error) {
      console.error('加载订单列表失败:', error);
    }
  },

  // 切换选项卡
  switchTab(e: any) {
    const tab = e.currentTarget.dataset.tab;
    if (tab !== this.data.activeTab) {
      this.setData({ activeTab: tab }, () => {
        this.loadData();
      });
    }
  },

  // 跳转到评价页面
  goToComment(e: any) {
    const orderId  = e.currentTarget.dataset.orderId;
    wx.navigateTo({
      url: `/pages/publish-comment/publish-comment?orderId=${orderId}`
    });
  }
})