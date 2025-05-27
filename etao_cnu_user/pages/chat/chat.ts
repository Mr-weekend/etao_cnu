import { getGoodsDetail, getUserInfo, GoodsItem, UserInfo, getChatHistory, 
  ChatMessage, markMessagesAsRead, uploadChatImage, createOrder} from '../../services/api';

interface IChatMessage extends ChatMessage {
  formattedTime?: string;
}

Page({
  data: {
    userInfo: {} as UserInfo,
    selfInfo: {} as UserInfo,
    goods: {} as GoodsItem,
    messages: [] as IChatMessage[],
    inputContent: '',
    scrollTop: 0,
    userId: 0,
    goodsId: 0,
    page: 1,
    size: 20,
    hasMore: true,
    loading: true,
    sending: false, // 是否正在发送消息
    socketConnected: false, // WebSocket连接状态
    watcher: null // WebSocket监听器
  },

  onLoad(options: { userId: string; goodsId: string }) {
    const userId = parseInt(options.userId);
    const goodsId = parseInt(options.goodsId);
    this.setData({
      userId,
      goodsId
    });
    this.initData();
    this.setupSocket();
  },

  onReady() {
    // 设置导航栏标题
    if (this.data.userInfo.userName) {
      wx.setNavigationBarTitle({
        title: this.data.userInfo.userName
      });
    }
  },

  onUnload() {
    // 页面卸载时移除WebSocket消息监听器
    const app = getApp();
    if (app.globalData.socket && this.data.watcher) {
      app.globalData.socket.offMessage(this.data.watcher);
    }
  },

  onReachBottom() {
    this.loadMoreMessages()
    console.log('加载更多');
    
  },
  // 初始化数据
  async initData() {
    try {
      this.setData({ loading: true });
      await Promise.all([
        this.getUserInfo(this.data.userId),
        this.getGoodsInfo(this.data.goodsId),
        this.getMessages()
      ]);
    } catch (error) {
      console.error('初始化数据失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 设置WebSocket消息监听
  setupSocket() {
    const app = getApp();
    if (!app.globalData.socket) {
      // 如果socket未连接，通知App进行连接
      app.connectWebSocket();
      // 定时检查socket是否连接成功
      this.checkSocketConnection();
      return;
    }

    // 设置消息监听
    const watcher = (res: any) => {
      try {
        const msg = JSON.parse(res.data);
        // 只处理当前聊天相关的消息
        if (msg.goodsId === this.data.goodsId && 
          (msg.senderId === this.data.userId || msg.receiverId === this.data.userId)) {
          // 处理新消息，把它添加到消息列表中
          this.addNewMessage(msg);
          // 标记消息为已读
          this.markAsRead();
        }
      } catch (error) {
        console.error('处理WebSocket消息失败:', error);
      }
    };

    // 保存监听器引用，以便在页面卸载时移除
    this.setData({ watcher });
    app.globalData.socket.onMessage(watcher);

    // 设置连接状态
    this.setData({
      socketConnected: app.globalData.socket.readyState === 1
    });
  },

  // 检查WebSocket连接状态
  checkSocketConnection() {
    const app = getApp();
    if (!app.globalData.socket) {
      setTimeout(() => this.checkSocketConnection(), 1000);
      return;
    }

    // 设置消息监听
    this.setupSocket();
  },

  // 获取对方用户信息
  async getUserInfo(userId: number) {
    try {
      const res = await getUserInfo(userId);
      if (res.code === 200) {
        this.setData({ userInfo: res.data });
        // 设置导航栏标题
        wx.setNavigationBarTitle({
          title: res.data.userName || '聊天'
        });
      }

      // 获取自己的信息
      const selfInfo = wx.getStorageSync('userInfo');
      if (selfInfo) {
        this.setData({ selfInfo });
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
  },

  // 获取闲置物品信息
  async getGoodsInfo(goodsId: number) {
    try {
      const res = await getGoodsDetail(goodsId);
      if (res.code === 200) {
        this.setData({ goods: res.data });
      }
    } catch (error) {
      console.error('获取闲置物品信息失败:', error);
    }
  },

  // 获取聊天记录
  async getMessages() {
    try {
      const { userId, goodsId, page, size } = this.data;
      const selfInfo = wx.getStorageSync('userInfo');
      if (!selfInfo || !selfInfo.userId) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }

      const res = await getChatHistory({
        page,
        size,
        senderId: userId,
        goodsId: goodsId
      });

      if (res.code === 200) {
        const messages = res.data.list.map(msg => {
          // 格式化时间显示
          const formattedTime = this.formatTime(msg.createdAt);
          return { ...msg, formattedTime };
        });

        this.setData({
          messages: messages.reverse(), // 要颠倒顺序，最新的消息在底部
          hasMore: res.data.pages > page
        }, () => {
          this.scrollToBottom();
        });

        // 标记消息为已读
        this.markAsRead();
      }
    } catch (error) {
      console.error('获取聊天记录失败:', error);
    }
  },

  // 发送文本消息
  async sendTextMessage() {
    const { inputContent, userId, goodsId } = this.data;
    if (!inputContent.trim() || this.data.sending) return;

    this.setData({ sending: true });

    try {
      const selfInfo = wx.getStorageSync('userInfo');
      if (!selfInfo || !selfInfo.userId) {
        wx.showToast({
          title: '请先登录',
          icon: 'none'
        });
        return;
      }

      // 创建新消息对象
      const newMessage = {
        receiverId: userId,
        goodsId: goodsId,
        content: inputContent.trim(),
        type: 'text'
      };

      // 添加消息到UI
      const uiMessage = {
        ...newMessage,
        senderId: selfInfo.userId,
        createdAt: new Date().toISOString(),
        formattedTime: this.formatTime(new Date().toISOString())
      };

      this.addNewMessage(uiMessage);

      // 清空输入框
      this.setData({ inputContent: '' });

      // 发送到WebSocket
      this.sendToWebSocket(newMessage);
    } catch (error) {
      console.error('发送消息失败:', error);
      wx.showToast({
        title: '发送失败',
        icon: 'error'
      });
    } finally {
      this.setData({ sending: false });
    }
  },

  // 发送图片消息
  async sendImageMessage() {
    if (this.data.sending) return;

    try {
      this.setData({ sending: true });

      // 选择图片
      const res = await new Promise((resolve, reject) => {
        wx.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera'],
          success: resolve,
          fail: reject
        });
      });

      const tempFilePath = res.tempFilePaths[0];

      // 上传图片
      const uploadRes = await uploadChatImage(tempFilePath);
      if (uploadRes.code !== 200) {
        throw new Error('上传图片失败');
      }

      const selfInfo = wx.getStorageSync('userInfo');
      const {userId, goodsId} = this.data

      const newMessage = {
        receiverId: userId,
        goodsId: goodsId,
        content: uploadRes.data,
        type: 'image'
      };
      const uiMessage = {
        ...newMessage,
        senderId: selfInfo.userId,
        createdAt: new Date().toISOString(),
        formattedTime: this.formatTime(new Date().toISOString())
      }

      // 添加消息到UI
      this.addNewMessage(uiMessage);

      // 发送到WebSocket
      this.sendToWebSocket(newMessage);
    } catch (error) {
      console.error('发送图片失败:', error);
      wx.showToast({
        title: '发送图片失败',
        icon: 'error'
      });
    } finally {
      this.setData({ sending: false });
    }
  },

  // 通过WebSocket发送消息
  sendToWebSocket(message: any) {
    const app = getApp();
    if (!app.globalData.socket || app.globalData.socket.readyState !== 1) {
      wx.showToast({
        title: '连接已断开，请重试',
        icon: 'none'
      });
      return;
    }

    app.globalData.socket.send({
      data: JSON.stringify(message),
      fail: (err) => {
        console.error('发送消息失败:', err);
        wx.showToast({
          title: '发送失败',
          icon: 'error'
        });
      }
    });
  },

  // 添加新消息到列表
  addNewMessage(message: any) {
    // 如果是新的一天，添加日期分割线
    const formattedTime = message.formattedTime || this.formatTime(message.createdAt);
    const newMessage = { ...message, formattedTime };

    this.setData({
      messages: [...this.data.messages, newMessage]
    }, () => {
      this.scrollToBottom();
    });
  },

  // 标记消息为已读
  async markAsRead() {
    try {
      await markMessagesAsRead({
        senderId: this.data.userId,
        goodsId: this.data.goodsId
      });
      // 更新全局未读消息数
      const app = getApp();
      app.getUserTotalUnreadCount();
    } catch (error) {
      console.error('标记消息已读失败:', error);
    }
  },

  // 格式化时间显示
  formatTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();
    
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    
    if (isToday) {
      return `${hours}:${minutes}`;
    } else {
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${month}-${day} ${hours}:${minutes}`;
    }
  },

  // 滚动到底部
  scrollToBottom() {
    wx.createSelectorQuery()
      .select('.messages')
      .boundingClientRect(rect => {
        if (rect) {
          this.setData({
            scrollTop: rect.height + 1000 // 加上足够大的值确保滚动到底部
          });
        }
      })
      .exec();
  },

  // 加载更多历史消息
  async loadMoreMessages() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      page: this.data.page + 1,
      loading: true
    });

    try {
      const { userId, goodsId, page, size } = this.data;
      const res = await getChatHistory({
        page,
        size,
        senderId: userId,
        goodsId: goodsId
      });

      if (res.code === 200) {
        // 保存当前滚动位置
        let currentScrollTop = 0;
        const query = wx.createSelectorQuery();
        query.select('.messages').boundingClientRect((rect) => {
          if (rect) {
            currentScrollTop = rect.height;
          }
        }).exec();

        const newMessages = res.data.list.map(msg => {
          const formattedTime = this.formatTime(msg.createdAt);
          return { ...msg, formattedTime };
        });

        // 合并消息列表，新消息在前面
        this.setData({
          messages: [...newMessages.reverse(), ...this.data.messages],
          hasMore: res.data.pages > page
        }, () => {
          // 恢复到原来的位置
          setTimeout(() => {
            const query = wx.createSelectorQuery();
            query.select('.messages').boundingClientRect((rect) => {
              if (rect) {
                const newScrollTop = rect.height - currentScrollTop;
                this.setData({
                  scrollTop: newScrollTop > 0 ? newScrollTop : 0
                });
              }
            }).exec();
          }, 100);
        });
      }
    } catch (error) {
      console.error('加载更多消息失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 输入框输入事件
  onInput(e: any) {
    this.setData({
      inputContent: e.detail.value
    });
  },

  // 预览图片
  previewImage(e: any) {
    const { url } = e.currentTarget.dataset;
    const imageMessages = this.data.messages
      .filter(msg => msg.type === 'image')
      .map(msg => msg.content);
    
    wx.previewImage({
      current: url,
      urls: imageMessages
    });
  },

  // 返回上一页
  navigateBack() {
    wx.navigateBack();
  },

  // 跳转到用户主页
  navigateToUserProfile() {
    wx.navigateTo({
      url: `/pages/mine/user-home/user-home?userInfo=${encodeURIComponent(JSON.stringify(this.data.userInfo))}`
    });
  },

  // 跳转到闲置物品详情
  navigateToGoods() {
    wx.navigateTo({
      url: `/pages/goods-detail/goods-detail?id=${this.data.goodsId}`
    });
  },

  // 立即购买
  buyGoods(e: any) {
    wx.showModal({
      title: '提示',
      content: '确认购买该闲置物品吗？',
      success: async (res) => {
        if (res.confirm) {
          const goodsId = this.data.goodsId;
          const buyerId = this.data.selfInfo.userId;
          const res = await createOrder({goodsId,buyerId});
          console.log(res);
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
          if (res.code === 200) {
            this.getGoodsInfo(this.data.goodsId);
            wx.showToast({
              title: '购买成功',
              icon: 'success'
            });
          }
        }
      }
    });
  }
}); 