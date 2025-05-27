package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.model.entity.User;
import cn.etaocnu.cloud.model.vo.UserAuthVo;

import java.util.List;

public interface SysUserInfoService {
    //获取用户列表（带分页）
    List<User> getUserList(int page, int size, String keyword);
    //获取用户总数
    int countUserList(String keyword);
    //封禁用户
    Boolean banUser(int userId, String violationReason);
    //解禁用户
    Boolean unbanUser(int userId);
    //获取认证申请列表（带分页）
    List<UserAuthVo> getUserAuthList(int page, int size);
    //获取认证申请总数
    int countUserAuthList();
    //通过认证申请
    Boolean passAuth(int userId);
    //驳回认证申请
    Boolean unPassAuth(int userId, String reject_reason);
}
