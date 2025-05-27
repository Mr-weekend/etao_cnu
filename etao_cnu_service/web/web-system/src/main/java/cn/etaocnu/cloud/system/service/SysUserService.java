package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.common.result.Result;

import java.util.Map;

public interface SysUserService {
    //获取用户列表
    Result<Map<String, Object>> getUserList(int page, int size, String keyword);
    //封禁用户
    Result<Boolean> banUser(int userId, String violationReason);
    //解禁用户
    Result<Boolean> unbanUser(int userId);
    //获取认证申请列表
    Result<Map<String, Object>> getUserAuthList(int page, int size);
    //通过认证申请
    Result<Boolean> passAuth(int userId);
    //驳回认证申请
    Result<Boolean> rejectAuth(int userId, String rejectReason);
} 