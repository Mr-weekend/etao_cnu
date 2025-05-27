interface TabItem {
  pagePath: string;
  text: string;
  iconPath: string;
  selectedIconPath: string;
}

Component({
  data: {
    selected: 0,
    color: "#999999",
    selectedColor: "#ff5401",
    unreadCount: 0,
    list: [
      {
        pagePath: "/pages/index/index",
        text: "首页",
        iconPath: "/images/tabIcon/home.png",
        selectedIconPath: "/images/tabIcon/home-active.png"
      },
      {
        pagePath: "/pages/message/message",
        text: "消息",
        iconPath: "/images/tabIcon/message.png",
        selectedIconPath: "/images/tabIcon/message-active.png"
      },
      {
        pagePath: "/pages/mine/mine",
        text: "我的",
        iconPath: "/images/tabIcon/account.png",
        selectedIconPath: "/images/tabIcon/account-active.png"
      }
    ] as TabItem[]
  },
  methods: {
    switchTab(e: WechatMiniprogram.TouchEvent) {
      const data = e.currentTarget.dataset as {
        path: string;
        index: number;
      };
      wx.switchTab({
        url: data.path,
        fail(err) {
          console.error('switchTab failed:', err);
        }
      });
      this.setData({
        selected: data.index
      });
    },

    // 更新未读消息数的方法
    updateUnreadCount() {
      const app = getApp();
      if (app.globalData) {
        this.setData({
          unreadCount: app.globalData.unreadMessageCount
        });
      }
    }
  },
  // 组件加载时同步全局状态
  lifetimes: {
    attached() {
      // 组件创建时，同步全局状态
      this.updateUnreadCount();
    },

    ready() {
      // 确保在组件完全准备好后开始监听页面显示事件
      const app = getApp();
      if (app.globalData) {
        this.setData({
          unreadCount: app.globalData.unreadMessageCount
        });
      }
    }
  },
  pageLifetimes: {
    // 页面显示时同步未读消息数
    show() {
      this.updateUnreadCount();
    }
  }
}); 