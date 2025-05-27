import { getGoodsDetail, deleteGoods, removeGoods, addCollection, cancelCollection, addBrowseHistory, isCollected, createOrder } from '../../services/api';

Page({
  data: {
    goods: {} as any,
    isCollectedResult: false,
    collectCount: 0,
    isOwnGoods: false, // 是否是自己发布的闲置物品
    showManageModal: false, // 管理操作菜单显示状态
    currentImageIndex: 0
  },

  onLoad(options: any) {
    const goodsId = options.id;
    this.getGoodsDetail(parseInt(goodsId));
  },

  // 获取闲置物品详情
  async getGoodsDetail(id: number) {
    try {
      const response = await getGoodsDetail(id);
      
      if (response.code === 200) {
        // 检查是否是自己发布的闲置物品
        const userInfo = wx.getStorageSync('userInfo');
        const isOwnGoods = userInfo && userInfo.userId === response.data.publisher.userId;
        // 检查当前登录用户是否已收藏该闲置物品
        let isCollectedResult = false;
        const token = wx.getStorageSync('token');
        if (token && !isOwnGoods) {
          try {
            const isCollectedResponse = await isCollected(id)
            if(isCollectedResponse.code === 200 ){  
              isCollectedResult = isCollectedResponse.data.isCollected
            }
          } catch (error) {
            console.error('检查收藏状态失败:', error);
          }
        }
        if(token){
          await addBrowseHistory(id)
        }
        this.setData({ 
          goods: response.data,
          isOwnGoods,
          isCollectedResult,
          collectCount: response.data.collectCount
        });
      } else {
        wx.showToast({
          title: response.message || '获取闲置物品详情失败',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error('获取闲置物品详情失败:', error);
      wx.showToast({
        title: '获取闲置物品详情失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 预览图片
  previewImage(e: any) {
    const { current } = e.currentTarget.dataset;
    wx.previewImage({
      current,
      urls: this.data.goods.imageUrls
    });
  },

  // 切换收藏状态
  async changeCollect() {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    if (this.data.isOwnGoods) {
      wx.showToast({
        title: '不能收藏自己的闲置物品',
        icon: 'none'
      });
      return;
    }

    try {
      wx.showLoading({ title: '处理中...' });
      const goodsId = this.data.goods.goodsId;
      
      // 调用收藏或取消收藏接口
      const response = await (this.data.isCollectedResult ? 
        cancelCollection(goodsId) : 
        addCollection(goodsId));
      
      if (response.code === 200) {
        console.log('成功');
        
        // 操作成功后更新状态
        const isCollectedResponse = await  isCollected(goodsId) 
        const isCollectedResult = isCollectedResponse.code === 200 ?
        isCollectedResponse.data.isCollected : this.data.isCollectedResult
        const res = await getGoodsDetail(this.data.goods.goodsId)
        if(res.code == 200){
          this.setData({
            collectCount: res.data.collectCount
          })
        }
        this.setData({
          isCollectedResult,
        });
        
        wx.showToast({
          title: isCollectedResult ? '收藏成功' : '取消收藏成功',
          icon: 'success'
        });
      }
    } catch (error) {
      console.error('收藏操作失败:', error);
      wx.showToast({
        title: '操作失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 跳转到聊天页面
  startChat(e: any) {
    const {userId, goodsId} = e.currentTarget.dataset;
    const status = wx.getStorageSync('userInfo').status
    if (status === 0) {
      const violationReason = wx.getStorageSync('userInfo').violationReason
      wx.showToast({
        title: `账号已被封禁，违规原因：${violationReason}`,
        icon: 'none'
      })
      return;
    }else if(status === 2){
      wx.showToast({
        title:'请先进行认证',
        icon:'none'
      })
      return;
    }
    wx.navigateTo({
      url: `/pages/chat/chat?userId=${userId}&goodsId=${goodsId}`
    });
  },

  // 跳转到卖家主页
  navigateToSeller() {
    const publisherInfo = this.data.goods.publisher;
    wx.navigateTo({
      url: `/pages/mine/user-home/user-home?userInfo=${encodeURIComponent(JSON.stringify(publisherInfo))}`
    });
  },

  // 显示管理操作菜单
  showManageActions() {
    this.setData({
      showManageModal: true
    });
  },

  // 隐藏管理操作菜单
  hideManageModal() {
    this.setData({
      showManageModal: false
    });
  },

  // 阻止冒泡
  stopPropagation() {
    // 空函数，用于阻止事件冒泡
  },

  // 编辑闲置物品
  editGoods() {
    const { goods } = this.data;
    // 构造编辑需要的闲置物品数据
    const editData = {
      goodsId: goods.goodsId,
      description: goods.description,
      price: goods.price,
      categoryId: goods.categoryId,
      imageUrls: goods.imageUrls
    };
    
    wx.navigateTo({
      url: `/pages/publish-goods/publish-goods?type=edit&goodsInfo=${encodeURIComponent(JSON.stringify(editData))}`
    });
    this.hideManageModal();
  },

  // 切换闲置物品上下架状态
  async changeGoodsStatus() {
    const { goods } = this.data;
    try {
      wx.showLoading({ title: '处理中...' });
      
      const response = await removeGoods(goods.goodsId);
      if (response.code === 200) {
        wx.showToast({
          title: '下架成功',
          icon: 'success'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
        // 更新闲置物品状态
        this.setData({
          'goods.status': 'removed'
        });
      } else {
        wx.showToast({
          title: response.message || '操作失败',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error('更新闲置物品状态失败:', error);
      wx.showToast({
        title: '操作失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
      this.hideManageModal();
    }
  },

  // 删除闲置物品
  deleteGoods() {
    const { goods } = this.data;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个闲置物品吗？',
      success: (res) => {
        if (res.confirm) {
          this.doDeleteGoods(goods.goodsId);
        }
      }
    });
  },

  // 执行删除闲置物品操作
  async doDeleteGoods(goodsId: number) {
    try {
      wx.showLoading({ title: '删除中...' });
      const response = await deleteGoods(goodsId);
      
      if (response.code === 200) {
        wx.showToast({
          title: '删除成功',
          icon: 'success'
        });
        // 延迟返回上一页
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({
          title: response.message || '删除失败',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error('删除闲置物品失败:', error);
      wx.showToast({
        title: '删除失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
      this.hideManageModal();
    }
  },

  // 购买闲置
  async buyGoods(){
    console.log(this.data.goods);
    const buyerId = wx.getStorageSync('userInfo').userId;

    const goodsId = this.data.goods.goodsId;
    const buyGoods = {
      goodsId: goodsId,
      buyerId: buyerId
    }
    wx.showModal({
      title: '购买闲置',
      content: '确定要购买该闲置吗？',
      async success(res) {
        if (res.confirm) {
          try {
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
            const result = await createOrder(buyGoods);
            wx.hideLoading();
            if (result.code === 200) {
              wx.showToast({
                title: '购买成功',
                icon: 'success'
              })
              // 延迟返回，给用户看到成功提示的时间
              setTimeout(() => {
                // 获取页面栈
                const pages = getCurrentPages();
                // 获取上一个页面
                const prevPage = pages[pages.length - 2];
                if (prevPage && prevPage.onPullDownRefresh()) {
                  // 让上一页刷新闲置物品详情
                  prevPage.onPullDownRefresh();
                  }
                wx.navigateBack();
              }, 1500);
            } else {
              wx.showToast({
                title: result.message || '购买失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            console.error('购买闲置失败:', error);
            wx.showToast({
              title: '购买失败',
              icon: 'none'
            });
          }
        }
      }
    });
  }
}); 