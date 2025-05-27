import request from './request'

// 管理员相关接口
export const adminApi = {
  login: (data) => request.post('/admin/login', data),
  logout: () => request.post('/admin/logout'),
  updateAdminName: (data) => request.put('/admin/adminName', data),
  updatePassword: (data) => request.put('/admin/password', data),
  getInfo: () => request.get('/admin/info')
}

// 用户管理相关接口
export const userApi = {
  getList: (params) => request.get('/sysUser/list', { params }),
  banUser: (userId, data) => request.post(`/sysUser/ban/${userId}`, data),
  unbanUser: (userId) => request.post(`/sysUser/unban/${userId}`),
  getAuthList: () => request.get('/sysUser/auth/list'),
  passAuth: (userId) => request.post(`/sysUser/auth/pass/${userId}`),
  rejectAuth: (userId, data) => request.post(`/sysUser/auth/reject/${userId}`, data)
}

// 闲置物品管理相关接口
export const goodsApi = {
  getList: (params) => request.get('/sysGoods/list', { params }),
  search: (params) => request.get('/sysGoods/search', { params }),
  delete: (goodsId) => request.delete(`/sysGoods/delete/${goodsId}`)
}

// 分类管理相关接口
export const categoryApi = {
  getList: (params) => request.get('/sysCategory/list', { params }),
  add: (data) => request.post('/sysCategory/add', data),
  updateName: (categoryId, data) => request.put(`/sysCategory/name/${categoryId}`, data),
  updateIcon: (categoryId, data) => request.put(`/sysCategory/icon/${categoryId}`, data),
  delete: (categoryId) => request.delete(`/sysCategory/${categoryId}`)
}

// 订单管理相关接口
export const orderApi = {
  getList: (params) => request.get('/sysOrder/list', { params }),
  searchByGoods: (params) => request.get('/sysOrder/search/goods', { params }),
  searchByUser: (params) => request.get('/sysOrder/search/user', { params }),
  getCount: () => request.get('/sysOrder/count')
} 