import { hasAuditRecord, uploadCampusCard, auditStatus, rejectReason } from '../../../services/api';
Page({

  data: {
    hasRecord: false,
    auditStatus: 0, // 0 审核中, 1: 通过, 2: 拒绝
    rejectReason: '',
    tempImagePath: ''
  },

  onLoad() {
    this.checkAuthStatus();
  },

  // 检查认证状态
  async checkAuthStatus() {
    try {
      // 检查是否有审核记录
      const hasRecordRes = await hasAuditRecord();
      if (hasRecordRes.code === 200) {
        console.log(hasRecordRes.data);
        
        this.setData({ hasRecord: hasRecordRes.data });
        
        if (hasRecordRes.data) {
          // 获取审核状态
          const statusRes = await auditStatus();
          if (statusRes.code === 200) {
            this.setData({ auditStatus: statusRes.data });
            
            // 如果审核不通过，获取原因
            if (statusRes.data === 2) {
              const reasonRes = await rejectReason();
              if (reasonRes.code === 200) {
                this.setData({ rejectReason: reasonRes.data });
              }
            }
          }
        }
      }
    } catch (error) {
      console.error('获取认证状态失败:', error);
      wx.showToast({
        title: '获取状态失败',
        icon: 'error'
      });
    }
  },

  // 选择图片
  chooseImage() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({
          tempImagePath: res.tempFilePaths[0]
        });
      }
    });
  },
  // 提交认证
  async submitAuth() {    
    if (!this.data.tempImagePath) {
      wx.showToast({
        title: '请选择图片',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '提交中...' });
    try {
      const res = await uploadCampusCard(this.data.tempImagePath);
      if (res.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        });
        // 重新检查状态
        this.checkAuthStatus();
      }
    } catch (error) {
      console.error('上传失败:', error);
      wx.showToast({
        title: '上传失败',
        icon: 'error'
      });
    } finally {
      wx.hideLoading();
    }
  },

})