package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Admin;

public interface AdminService {
    
    // 管理员登录
    Result<String> login(String adminName, String password);
    
    // 管理员登出
    Result<Boolean> logout(String token);
    
    // 添加管理员
    Result<Boolean> addAdmin(Admin admin);
    
    // 修改用户名
    Result<Boolean> updateAdminName(String token, String newAdminName);
    
    // 修改密码
    Result<Boolean> updatePassword(String token, String oldPassword, String newPassword);
    
    // 获取管理员信息
    Result<Admin> getAdminInfo(String token);
}
