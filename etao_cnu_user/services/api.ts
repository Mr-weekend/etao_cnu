const BASE_URL = 'http://localhost:8001/user-api/'; // 后端服务的基础URL
const FormData = require('../utils/formData.js')

// 定义接口类型
export interface Publisher {
  userId: number;
  userName: string;
  gender: string;
  avatarUrl: string;
  userProfile: string | null;
  status: number;
}

export interface GoodsItem {
  goodsId: number;
  description: string;
  price: number;
  categoryId: number;
  categoryName: string | null;
  status: string;
  viewCount: number;
  collectCount: number;
  publishTime: string;
  imageUrls: string[];
  publisher: Publisher;
}

export interface PageResult<T> {
  total: number;
  pages: number;
  size: number;
  page: number;
  list: T[];
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 用户登录信息接口返回数据类型
export interface UserLoginInfo {
  userId: number;
  wxOpenId: string;
  userName: string;
  gender: number;
  avatarUrl: string;
  userProfile: string;
  status: number;
}

// 获取指定用户信息接口返回数据类型
export interface UserInfo {
  userId: number;
  userName: string;
  gender: number;
  avatarUrl: string;
  userProfile: string | null;
  status: number,
  violationReason: string;
}

// 用户登录
export function userLogin(): Promise<ApiResponse<boolean>>{
  return new Promise((resolve, reject) => {
    wx.login({
      success: (res) => {
        if (res.code) {
          wx.request({
            url: `${BASE_URL}user/login/${res.code}`,
            success: (res) => {
              resolve(res.data as Promise<ApiResponse<boolean>>);
            },
            fail: (error) => {
              console.error('登录失败:', error);
              reject(error);
            },
            complete: () => {
              wx.hideLoading();
            }
          });
        }
      },
    });
  });
}

// 用户登出
export function userLogout(): Promise<ApiResponse<boolean>>{
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/logout`,
      method: 'POST',
      header:{
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as Promise<ApiResponse<boolean>>);
      },
      fail: (error) => {
        console.error('登出失败:', error);
        reject(error);
      }
    })
  });
}

// 获取用户登录信息
export function getUserLoginInfo(): Promise<ApiResponse<UserInfo>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/getUserLoginInfo`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as Promise<ApiResponse<UserInfo>>);
      },
      fail: (error) => {
        console.error('获取用户登录信息失败:', error);
        reject(error);
      }
    });
  });
}

// 获取指定用户信息
export function getUserInfo(userId: number): Promise<ApiResponse<UserInfo>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/getUserInfo/${userId}`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<UserInfo>);
      },
      fail: (error) => {
        console.error('获取指定用户信息失败:', error);
        reject(error);
      }
    })
  });
}

// 更新用户名
export function updateUserName(username: string): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary' + Math.random().toString(16).slice(2);
    let formData = '';
    
    // 添加 username
    formData += `--${boundary}\r\n`;
    formData += `Content-Disposition: form-data; name="username"\r\n\r\n`;
    formData += `${username}\r\n`;

    // 结束标记 (只在最后添加一次)
    formData += `--${boundary}--\r\n`;
    wx.request({
      url: `${BASE_URL}user/username`,
      method: 'PUT',
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': `multipart/form-data; boundary=${boundary}`
      },
      data: formData,
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('更新昵称失败:', error);
        reject(error);
      }
    });
  });
}

// 更新性别
export function updateGender(gender: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary' + Math.random().toString(16).slice(2);
    let formData = '';
    
    // 添加 username
    formData += `--${boundary}\r\n`;
    formData += `Content-Disposition: form-data; name="gender"\r\n\r\n`;
    formData += `${gender}\r\n`;

    // 结束标记 (只在最后添加一次)
    formData += `--${boundary}--\r\n`;
    wx.request({
      url: `${BASE_URL}user/gender`,
      method: 'PUT',
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': `multipart/form-data; boundary=${boundary}`
      },
      data: formData,
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('更新性别失败:', error);
        reject(error);
      }
    });
  });
}

// 更新用户简介
export function updateUserProfile(profile: string): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary' + Math.random().toString(16).slice(2);
    let formData = '';
    
    // 添加 username
    formData += `--${boundary}\r\n`;
    formData += `Content-Disposition: form-data; name="profile"\r\n\r\n`;
    formData += `${profile}\r\n`;

    // 结束标记 (只在最后添加一次)
    formData += `--${boundary}--\r\n`;
    wx.request({
      url: `${BASE_URL}user/profile`,
      method: 'PUT',
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': `multipart/form-data; boundary=${boundary}`
      },
      data: formData,
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('更新简介失败:', error);
        reject(error);
      }
    });
  });
}

// 更新用户头像
export function updateUserAvatar(avatar: string): Promise<ApiResponse<boolean>>{
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: `${BASE_URL}user/avatar`,
      filePath: avatar,
      name: 'avatar',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        const response:  ApiResponse<boolean> = JSON.parse(res.data);
        resolve(response);
      },
      fail: (error) => {
        console.error('更新头像失败', error);
        reject(error);
      }
    });
  });
}

// 上传校园卡照片
export function uploadCampusCard(campusCard: string): Promise<ApiResponse<boolean>>{
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: `${BASE_URL}user/upload/campusCard`,
      filePath: campusCard,
      name: 'campusCard',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        const response:  ApiResponse<boolean> = JSON.parse(res.data);
        resolve(response);
      },
      fail: (error) => {
        console.error('上传校园卡照片失败', error);
        reject(error);
      }
    });
  });
}

// 获取当前用户是否有审核记录
export function hasAuditRecord(): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/hasAuditRecord`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('获取用户是否有审核记录失败:', error);
        reject(error);
      }
    })
  });
}

// 获取当前用户认证审核状态
export function auditStatus(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/auditStatus`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取用户认证审核状态失败:', error);
        reject(error);
      }
    })
  });
}

// 获取审核不通过原因
export function rejectReason(): Promise<ApiResponse<string>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}user/rejectReason`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<string>);
      },
      fail: (error) => {
        console.error('获取审核不通过原因失败:', error);
        reject(error);
      }
    })
  });
}
// 分类接口类型定义
export interface Category {
  categoryId: number;
  categoryName: string;
  icon: string | null;
  createdAt: string;
}

export interface CategoryListResponse {
  total: number;
  list: Category[];
}

// 获取分类列表
export function getCategoryList(): Promise<ApiResponse<CategoryListResponse>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/category/list`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<CategoryListResponse>);
      },
      fail: (error) => {
        console.error('获取分类列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取闲置物品列表
export function getGoodsList(page: number = 1, size: number = 10, categoryId?: number): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    const params: any = {
      page,
      size
    };
    
    // 如果有分类ID，添加到请求参数中
    if (categoryId) {
      params.categoryId = categoryId;
    }

    wx.request({
      url: `${BASE_URL}goods/list`,
      method: 'GET',
      data: params,
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('获取闲置物品列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取轮播图数据
export function getBannerList(): Promise<any[]> {
  return Promise.resolve([
    { id: 1, imageUrl: 'https://img.alicdn.com/imgextra/i4/xxx/banner2.jpg' },
    { id: 2, imageUrl: 'https://img.alicdn.com/imgextra/i4/xxx/banner3.jpg' }
  ]);
}

export interface PublishGoodsData {
  description: string;
  price: number;
  categoryId: number;
  imageUrls: string[];
}

// 发布闲置物品
export function publishGoods(goodsInfo: PublishGoodsData, images: string[]): Promise<ApiResponse<any>> {
  return new Promise((resolve, reject) => {
    let data = JSON.stringify({
      description: goodsInfo.description,
      price: goodsInfo.price,
      categoryId: goodsInfo.categoryId
    })
    let formData = new FormData()
    formData.append('goodsInfo', data);
    // 多文件上传
    for(var i in images){
      formData.appendFile('images', images[i]);
    }
    let newData = formData.getData();
    wx.request({
      url: `${BASE_URL}/goods/publish`,
      method: 'POST',
      data: newData.buffer,
      header:{
        'content-type': newData.contentType,
        'token': wx.getStorageSync('token')
      },
      success(res){
        try {
          const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
          resolve(data as ApiResponse<any>);
        } catch (error) {
          reject(new Error('解析响应数据失败'));
        }
      },
      fail: (error) => {
        console.error('发布闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 获取闲置物品详情
export function getGoodsDetail(id: number): Promise<ApiResponse<GoodsItem>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/${id}`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<GoodsItem>);
      },
      fail: (error) => {
        console.error('获取闲置物品详情失败:', error);
        reject(error);
      }
    });
  });
}

// 搜索参数接口
export interface SearchGoodsParams {
  keyword: string;
  categoryId?: number;
  priceRankType?: 0 | 1;
  bottomPrice?: number;
  topPrice?: number;
  page: number;
  size: number;
}

// 删除闲置物品
export function deleteGoods(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/${goodsId}`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('删除闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 图片更新类型
export type ImageUpdateType = 'keep' | 'replace' | 'append' | 'mixed';
// 更新闲置
export interface UpdateGoodsData {
  description: string;
  price: number;
  categoryId: number;
}

// 更新闲置物品信息
export function updateGoods(
  goodsId: number, 
  goodsInfo: UpdateGoodsData, 
  imageUpdateType: ImageUpdateType,
  newImages?: string[],
  deleteImageUrls?: string[]
): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    // 构建基础的闲置物品信息
    const goodsInfoData = JSON.stringify({
      description: goodsInfo.description,
      price: goodsInfo.price,
      categoryId: goodsInfo.categoryId
    });
    
    // 根据不同的 imageUpdateType 处理请求
    switch (imageUpdateType) {
      case 'mixed': {
        // 判断是否只有删除图片
        const hasNewImages = Array.isArray(newImages) && newImages.length > 0;
        let formData = new FormData()
        formData.append('goodsInfo', goodsInfoData)
        formData.append('imageUpdateType', imageUpdateType)
        formData.append('deleteImageUrls', Array.isArray(deleteImageUrls) ? deleteImageUrls.join(',') : undefined)
        if (hasNewImages) {
          // 多文件上传
          if(newImages){
            let images = newImages
            for(var i in newImages){
              formData.appendFile('images', images[i]);
            }
            let newData = formData.getData();
            wx.request({
              url: `${BASE_URL}goods/${goodsId}`,
              method: 'POST',
              data: newData.buffer,
              header:{
                'content-type': newData.contentType,
                'token': wx.getStorageSync('token')
              },
              success(res){
                try {
                  const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
                  resolve(data as ApiResponse<any>);
                } catch (error) {
                  reject(new Error('解析响应数据失败'));
                }
              },
              fail: (error) => {
                console.error('更新闲置物品失败:', error);
                reject(error);
              }
            });
          }
        } else {
          console.log('只删除图片');
          console.log('要删除的图片列表：', deleteImageUrls);
          let newData = formData.getData();
            wx.request({
              url: `${BASE_URL}goods/${goodsId}`,
              method: 'POST',
              data: newData.buffer,
              header:{
                'content-type': newData.contentType,
                'token': wx.getStorageSync('token')
              },
              success(res){
                try {
                  const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
                  resolve(data as ApiResponse<any>);
                } catch (error) {
                  reject(new Error('解析响应数据失败'));
                }
              },
              fail: (error) => {
                console.error('更新闲置物品失败:', error);
                reject(error);
              }
            });
        }
        break;
      }
      case 'append':
      case 'replace': {
        console.log('图片更新类型');
        console.log(imageUpdateType);
        
        let formData = new FormData()
        formData.append('goodsInfo', goodsInfoData)
        formData.append('imageUpdateType', imageUpdateType)
        if(newImages){
          let images = newImages
          for(var i in newImages){
            formData.appendFile('images', images[i]);
          }
          let newData = formData.getData();
          wx.request({
            url: `${BASE_URL}goods/${goodsId}`,
            method: 'POST',
            data: newData.buffer,
            header:{
              'content-type': newData.contentType,
              'token': wx.getStorageSync('token')
            },
            success(res){
              try {
                const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
                resolve(data as ApiResponse<any>);
              } catch (error) {
                reject(new Error('解析响应数据失败'));
              }
            },
            fail: (error) => {
              console.error('更新闲置物品失败:', error);
              reject(error);
            }
          });
        }
        break;
      }
      case 'keep':
      default: {
        let formData = new FormData()
        formData.append('goodsInfo', goodsInfoData)
        formData.append('imageUpdateType', imageUpdateType)
        let newData = formData.getData();
        console.log(newData);
        
        wx.request({
          url: `${BASE_URL}goods/${goodsId}`,
          method: 'POST',
          data: newData.buffer,
          header:{
            'content-type': newData.contentType,
            'token': wx.getStorageSync('token')
          },
          success(res){
            try {
              const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
              resolve(data as ApiResponse<any>);
            } catch (error) {
              reject(new Error('解析响应数据失败'));
            }
          },
          fail: (error) => {
            console.error('更新闲置物品失败:', error);
            reject(error);
          }
        });
        break;
      }
    }
  });
}

// 下架闲置物品
export function removeGoods(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/remove/${goodsId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('下架闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 上架闲置物品
export function putGoods(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/put/${goodsId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('上架闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 获取指定用户发布的闲置物品列表
export function getUserGoods(userId: number, page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/user/${userId}`,
      method: 'GET',
      data: { page, size },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('获取用户发布的闲置物品列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取用户下架的闲置物品列表
export function getUserRemoveGoods(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/removeList`,
      method: 'GET',
      header:{
        'token': wx.getStorageSync('token')
      },
      data: { page, size },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('获取用户下架的闲置物品列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取当前登录用户发布的闲置物品列表
export function getMyPublishedGoods(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/my/list`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('获取我的发布列表失败:', error);
        reject(error);
      }
    });
  });
}

// 搜索闲置物品
export function searchGoods(params: SearchGoodsParams): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/search`,
      method: 'GET',
      data: params,
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('搜索闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 获取指定用户发布的闲置物品数量
export function getUserGoodsCount(userId: number): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/count/user/${userId}`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取用户闲置物品数量失败:', error);
        reject(error);
      }
    });
  });
}

// 获取指定用户下架的闲置物品数量
export function getUserRemoveGoodsCount():
Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/count/remove`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取用户闲置物品数量失败:', error);
        reject(error);
      }
    });
  });
}

// 搜索闲置物品 (只传递有值的参数)
export function searchGoodsWithParams(params: any): Promise<ApiResponse<PageResult<GoodsItem>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}goods/search`,
      method: 'GET',
      data: params,
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<GoodsItem>>);
      },
      fail: (error) => {
        console.error('搜索闲置物品失败:', error);
        reject(error);
      }
    });
  });
}

// 收藏相关接口类型定义
export interface CollectionItem {
  goodsInfo: GoodsItem;
  createdAt: string;
}

export interface CollectionListResponse {
  total: number;
  pages: number;
  size: number;
  page: number;
  list: CollectionItem[];
}

// 添加收藏
export function addCollection(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/add/${goodsId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('添加收藏失败:', error);
        reject(error);
      }
    });
  });
}

// 取消收藏
export function cancelCollection(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/cancel/${goodsId}`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('取消收藏失败:', error);
        reject(error);
      }
    });
  });
}

// 获取用户收藏列表
export function getCollectionList(page: number = 1, size: number = 10): Promise<ApiResponse<CollectionListResponse>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/list`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<CollectionListResponse>);
      },
      fail: (error) => {
        console.error('获取收藏列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取用户收藏数量
export function getCollectionCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/count/user`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取收藏数量失败:', error);
        reject(error);
      }
    });
  });
}

// 批量取消收藏
export function batchCancelCollection(goodsIds: number[]): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary' + Math.random().toString(16).slice(2);
    let formData = '';
    
    // 添加 goodsIds
    formData += `--${boundary}\r\n`;
    formData += `Content-Disposition: form-data; name="goodsIds"\r\n\r\n`;
    formData += `${goodsIds.join(',')}\r\n`;
    
    // 结束标记 (只在最后添加一次)
    formData += `--${boundary}--\r\n`;
    wx.request({
      url: `${BASE_URL}collection/batch/delete`,
      method: 'DELETE',
      data: formData,
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': `multipart/form-data; boundary=${boundary}`
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('批量取消收藏失败:', error);
        reject(error);
      }
    });
  });
}

// 清空收藏
export function clearCollection(): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/clear`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('清空收藏失败:', error);
        reject(error);
      }
    });
  });
}

export interface isCollectResponse{
  isCollected: boolean
}

// 获取用户是否收藏闲置物品
export function isCollected(goodsId: number): Promise<ApiResponse<isCollectResponse>>{
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}collection/isCollected/goods/${goodsId}/user`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<isCollectResponse>);
      },
      fail: (error) => {
        console.error('获取用户是否收藏闲置失败:', error);
        reject(error);
      }
    });
  });
} 

// 浏览记录相关接口类型定义
export interface BrowseHistoryItem {
  goodsId: number;
  description: string;
  price: number;
  categoryId: number;
  status: string;
  imageUrl: string;
  userId: number;
  userName: string;
  avatarUrl: string;
  browseTime: string;
}

export interface BrowseHistoryListResponse {
  total: number;
  pages: number;
  size: number;
  page: number;
  list: BrowseHistoryItem[];
}

// 获取浏览记录列表
export function getBrowseHistoryList(page: number = 1, size: number = 10): Promise<ApiResponse<BrowseHistoryListResponse>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}history/list`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<BrowseHistoryListResponse>);
      },
      fail: (error) => {
        console.error('获取浏览记录列表失败:', error);
        reject(error);
      }
    });
  });
}

// 获取浏览记录数量
export function getBrowseHistoryCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}history/count`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取浏览记录数量失败:', error);
        reject(error);
      }
    });
  });
}

// 批量删除浏览记录
export function batchDeleteBrowseHistory(goodsIds: number[]): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    const boundary = '----WebKitFormBoundary' + Math.random().toString(16).slice(2);
    let formData = '';
    
    // 添加 goodsIds
    formData += `--${boundary}\r\n`;
    formData += `Content-Disposition: form-data; name="goodsIds"\r\n\r\n`;
    formData += `${goodsIds.join(',')}\r\n`;

    // 结束标记
    formData += `--${boundary}--\r\n`;

    wx.request({
      url: `${BASE_URL}history/batch/delete`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': `multipart/form-data; boundary=${boundary}`
      },
      data: formData,
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('批量删除浏览记录失败:', error);
        reject(error);
      }
    });
  });
}

// 新增浏览记录
export function addBrowseHistory(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}history/add/${goodsId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('添加浏览记录失败:', error);
        reject(error);
      }
    });
  });
}

// 删除单条浏览记录
export function deleteBrowseHistory(goodsId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}history/delete/${goodsId}`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('删除浏览记录失败:', error);
        reject(error);
      }
    });
  });
}

// 清空浏览记录
export function clearBrowseHistory(): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}history/clear`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('清空浏览记录失败:', error);
        reject(error);
      }
    });
  });
}

// 聊天消息类型定义
export interface ChatMessage {
  senderId: number;
  receiverId: number;
  goodsId: number;
  type: 'text' | 'image';
  content: string;
  createdAt: string;
}

export interface ChatHistoryResponse {
  total: number;
  pages: number;
  size: number;
  page: number;
  list: ChatMessage[];
}

// 聊天列表类型
export interface ChatList {
  goodsId: number;
  lastMessage: string;
  unreadCount: number;
  lastMessageTime: string;
  otherUserId: number;
  lastMessageType: string;
}

export interface ChatListResponse{
  total: number;
  pages: number;
  size: number;
  page: number;
  list: ChatList[];
}

// 获取WebSocket连接地址
export function getChatWsUrl(userId: number): Promise<ApiResponse<string>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/ws-url/${userId}`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<string>);
      },
      fail: (error) => {
        console.error('获取WebSocket地址失败:', error);
        reject(error);
      }
    });
  });
}

// 获取聊天记录
export function getChatHistory(params: {
  page: number;
  size: number;
  senderId: number;
  goodsId: number;
}): Promise<ApiResponse<PageResult<ChatList>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/history`,
      method: 'GET',
      data: params,
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<ChatHistoryResponse>);
      },
      fail: (error) => {
        console.error('获取聊天记录失败:', error);
        reject(error);
      }
    });
  });
}

// 获取用户聊天会话列表
export function getMessageList(page: number = 1, size: number = 10): Promise<ApiResponse<ChatListResponse>>{
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/sessions`,
      method: 'GET',
      data: {page, size},
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<ChatListResponse>);
      },
      fail: (error) => {
        console.error('获取聊天列表失败:', error);
        reject(error);
      }
    });
  });
}

// 上传聊天图片
export function uploadChatImage(imagePath: string): Promise<ApiResponse<string>> {
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: `${BASE_URL}chat/upload/image`,
      filePath: imagePath,
      name: 'image',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        const data = JSON.parse(res.data);
        resolve(data as ApiResponse<string>);
      },
      fail: (error) => {
        console.error('上传聊天图片失败:', error);
        reject(error);
      }
    });
  });
}

// 获取总未读消息数
export function getTotalUnreadCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/unread/count`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取未读消息数失败:', error);
        reject(error);
      }
    });
  });
}

// 获取特定闲置物品的未读消息数
export function getGoodsUnreadCount(params: {
  senderId: number;
  goodsId: number;
}): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/unread/count/goods`,
      method: 'GET',
      data: params,
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取闲置物品未读消息数失败:', error);
        reject(error);
      }
    });
  });
}

// 标记消息为已读
export function markMessagesAsRead(params: {
  senderId: number;
  goodsId: number;
}): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/mark-read?senderId=${params.senderId}&goodsId=${params.goodsId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('标记消息已读失败:', error);
        reject(error);
      }
    });
  });
}

// 获取聊天记录总数
export function getChatHistoryCount(params: {
  senderId: number;
  goodsId: number;
}): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}chat/history/count`,
      method: 'GET',
      data: params,
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取聊天记录总数失败:', error);
        reject(error);
      }
    });
  });
}

// 订单接口类型定义
export interface CreateOrderParams {
  goodsId: number;
  buyerId: number;
}

export interface OrderDetail {
  orderId: number;
  buyer: UserInfo;
  goodsInfo: GoodsItem;
  createdAt: string;
  status: string,
  completedAt: string;
}

export interface CommentDetail {
  orderId: number,
  goodsInfo: {
    goodsId: number;
    userId: number;
    description: string;
    price: number;
    categoryId: number;
    imageUrl: string;
  };
  completedTime: string;
  buyer: UserInfo | null;
  buyerCommentType: number;
  buyerCommentContent: string;
  buyerCommentImageUrls: string[];
  buyerCommentTime: string;
  seller: UserInfo | null;
  sellerCommentType: number;
  sellerCommentContent: string;
  sellerCommentImageUrls: string[];
  sellerCommentTime: string;
}

export interface PublishCommentData {
  orderId: number;
  type: number;
  content: string;
  isAnonymous: number;
}

// 订单相关接口
// 1. 创建订单
export function createOrder(params: CreateOrderParams): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/create`,
      method: 'POST',
      data: params,
      header: {
        'token': wx.getStorageSync('token'),
        'Content-Type': 'application/json'
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('创建订单失败:', error);
        reject(error);
      }
    });
  });
}

// 2. 取消订单
export function cancelOrder(orderId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/cancel/${orderId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('取消订单失败:', error);
        reject(error);
      }
    });
  });
}

// 3. 删除订单
export function deleteOrder(orderId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/${orderId}`,
      method: 'DELETE',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('删除订单失败:', error);
        reject(error);
      }
    });
  });
}

// 4. 确认收货
export function confirmOrder(orderId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/completed/${orderId}`,
      method: 'POST',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('确认收货失败:', error);
        reject(error);
      }
    });
  });
}

// 5. 获取订单详情
export function getOrderDetail(orderId: number): Promise<ApiResponse<OrderDetail>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/${orderId}`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<OrderDetail>);
      },
      fail: (error) => {
        console.error('获取订单详情失败:', error);
        reject(error);
      }
    });
  });
}

// 6. 获取用户买到的订单列表
export function getBuyerOrders(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/list/asBuyer`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('获取买到的订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 7. 获取用户卖出的订单列表
export function getSellerOrders(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/list/asSeller`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('获取卖出的订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 8. 获取用户买到的订单数量
export function getBuyerOrderCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/count/asBuyer`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取买到的订单数量失败:', error);
        reject(error);
      }
    });
  });
}

// 9. 获取用户卖出的订单数量
export function getSellerOrderCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/count/asSeller`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取卖出的订单数量失败:', error);
        reject(error);
      }
    });
  });
}

// 10. 搜索用户卖出的订单列表
export function searchSellerOrders(keyword: string, page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/search/asSeller`,
      method: 'GET',
      data: { keyword, page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('搜索卖出的订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 11. 搜索用户买到的订单列表
export function searchBuyerOrders(keyword: string, page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/search/asBuyer`,
      method: 'GET',
      data: { keyword, page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('搜索买到的订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 12. 获取用户买到的待评价订单列表
export function getBuyerUncommentOrders(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/unComment/asBuyer`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        console.log('接口值');
        console.log(res.data);
        
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('获取买到的待评价订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 13. 获取用户卖出的待评价订单列表
export function getSellerUncommentOrders(page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<OrderDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/unComment/asSeller`,
      method: 'GET',
      data: { page, size },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<OrderDetail>>);
      },
      fail: (error) => {
        console.error('获取卖出的待评价订单列表失败:', error);
        reject(error);
      }
    });
  });
}

// 14. 获取用户买到的待评价订单数量
export function getBuyerUncommentOrderCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/count/unComment/asBuyer`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取买到的待评价订单数量失败:', error);
        reject(error);
      }
    });
  });
}

// 15. 获取用户卖出的待评价订单数量
export function getSellerUncommentOrderCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/count/unComment/asSeller`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取卖出的待评价订单数量失败:', error);
        reject(error);
      }
    });
  });
}

// 16. 获取用户待评价订单数量
export function getAllUncommentOrderCount(): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}order/count/unComment`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取待评价订单数量失败:', error);
        reject(error);
      }
    });
  });
}

// 评价相关接口
// 1. 发布评价
export function publishComment(commentInfo: PublishCommentData, images?: string[]): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    let formData = new FormData()
    formData.append('commentInfo',JSON.stringify(commentInfo))
    let newData = formData.getData()
    if (images && images.length !== 0) {
      for(var i in images){
        formData.appendFile('images', images[i])
      }
    }
    wx.request({
      url: `${BASE_URL}comment/publish`,
      method: 'POST',
      data: newData.buffer,
      header:{
        'content-type': newData.contentType,
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('发布评价失败:', error);
        reject(error);
      }
    })
  });
}

// 2. 通过订单ID获取评价详情
export function getCommentByOrderId(orderId: number): Promise<ApiResponse<CommentDetail>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}comment/order/${orderId}`,
      method: 'GET',
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        console.log('评价接口');
        console.log(res.data);
        
        resolve(res.data as ApiResponse<CommentDetail>);
      },
      fail: (error) => {
        console.error('获取评价详情失败:', error);
        reject(error);
      }
    });
  });
}

// 3. 通过用户ID获取评价列表
export function getUserComments(userId: number, page: number = 1, size: number = 10): Promise<ApiResponse<PageResult<CommentDetail>>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}comment/user/${userId}`,
      method: 'GET',
      data: { page, size },
      success: (res) => {
        resolve(res.data as ApiResponse<PageResult<CommentDetail>>);
      },
      fail: (error) => {
        console.error('获取用户评价列表失败:', error);
        reject(error);
      }
    });
  });
}

// 4. 获取用户收到的评价数量
export function getUserCommentCount(userId: number): Promise<ApiResponse<number>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}comment/count/${userId}`,
      method: 'GET',
      success: (res) => {
        resolve(res.data as ApiResponse<number>);
      },
      fail: (error) => {
        console.error('获取用户收到的评价数量失败:', error);
        reject(error);
      }
    });
  });
}

// 5. 获取用户作为买家是否已评价
export function getIsCommentAsBuyer(orderId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}comment/isComment/buyer`,
      method: 'GET',
      data: { orderId },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('获取用户作为买家是否已评价失败:', error);
        reject(error);
      }
    });
  });
}

// 6. 获取用户作为卖家是否已评价
export function getIsCommentAsSeller(orderId: number): Promise<ApiResponse<boolean>> {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL}comment/isComment/seller`,
      method: 'GET',
      data: { orderId },
      header: {
        'token': wx.getStorageSync('token')
      },
      success: (res) => {
        resolve(res.data as ApiResponse<boolean>);
      },
      fail: (error) => {
        console.error('获取用户作为卖家是否已评价失败:', error);
        reject(error);
      }
    });
  });
}
