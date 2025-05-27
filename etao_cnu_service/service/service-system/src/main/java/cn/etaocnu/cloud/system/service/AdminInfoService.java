package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.model.entity.Admin;

public interface AdminInfoService {
    
    // 管理员登录
    int login(String adminName, String password);
    
    // 管理员登出
    boolean logout(String token);

    // 添加管理员
    boolean addAdmin(Admin admin);
    
    // 修改用户名
    boolean updateAdminName(Integer adminId, String newAdminName);
    
    // 修改密码
    boolean updatePassword(Integer adminId, String oldPassword, String newPassword);
    
    // 根据token获取管理员信息
    Admin getAdminInfoByToken(String token);
} 