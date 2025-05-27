import { UserLoginInfo, updateUserName, updateGender,updateUserProfile, updateUserAvatar } from '../../../services/api';
Page({
  data: {
    userInfo: {} as UserLoginInfo,
    maxBioLength: 100,
    isUploading: false,
    updateUserAvatarTag: false,
    updateUserNameTag: false,
    updateUserProfileTag: false,
    updateGenderTag: false,
    genderIndex: -1 , //选中的性别索引
    genderList:[{'gender':'女'},{'gender':'男'}]
  },

  onLoad() {
    // 获取当前用户信息
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({
        userInfo,
        genderIndex: userInfo.gender
      })
      this.setData({ userInfo });
    }
  },

  // 输入昵称
  onNicknameInput(e: any) {
    this.setData({
      'userInfo.userName': e.detail.value
    });
  },

  // 输入简介
  onBioInput(e: any) {
    this.setData({
      'userInfo.userProfile': e.detail.value
    });
  },

  // 选择头像
  async chooseAvatar() {
    try {
      const { tempFilePaths } = await wx.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera']
      });

      if (tempFilePaths.length > 0) {
        this.setData({ isUploading: true });
        setTimeout(() => {
          this.setData({
            'userInfo.avatarUrl': tempFilePaths[0],
            isUploading: false
          });
        }, 1000);
      }
    } catch (error) {
      console.error('选择头像失败:', error);
      this.setData({ isUploading: false });
    }
  },
  // 选择性别
  onGenderChange(e: any) {
    this.setData({
      genderIndex: parseInt(e.detail.value)
    });
  },
  // 保存用户信息
  async saveUserInfo() {
    const { userName, userProfile, avatarUrl} = this.data.userInfo;
    const { genderIndex } = this.data;
    if (!userName.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      });
      return;
    }
    try {
      if(userName !== wx.getStorageSync('userInfo').userName){
        updateUserName(userName);
        this.setData({
          updateUserNameTag: true
        })
      }
      if(genderIndex !== wx.getStorageSync('userInfo').gender){
        updateGender(genderIndex)
        this.setData({
          updateGenderTag: true
        })
      }
      if(userProfile !== wx.getStorageSync('userInfo').userProfile){
        updateUserProfile(userProfile)
        this.setData({
          updateUserNameTag: true
        })
      }
      if(avatarUrl !== wx.getStorageSync('userInfo').avatarUrl){
        updateUserAvatar(avatarUrl)
        this.setData({
          updateUserAvatarTag: true
        })
      }
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });
      // 返回上一页并刷新
      setTimeout(() => {
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        if (prevPage) {
          if(this.data.updateUserNameTag === true || 
            this.data.updateUserAvatarTag === true || 
            this.data.updateUserProfileTag === true ||
            this.data.updateGenderTag === true){
              prevPage.getUserInfo();
          }
        }
        wx.navigateBack();
      }, 1500);
    } catch (error) {
      console.error('保存用户信息失败:', error);
      wx.showToast({
        title: '保存失败',
        icon: 'error'
      });
    }
  }
}); 