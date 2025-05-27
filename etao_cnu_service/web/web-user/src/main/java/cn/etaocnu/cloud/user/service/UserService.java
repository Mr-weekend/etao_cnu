package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

public interface UserService
{
    // 微信登录
    Result<String> login(String code);
    // 获取用户登录信息
    Result<UserVo> getUserLoginInfo(String token);

    Result<UserVo> getUserInfo(int userId);

    // 登出
    Result<Boolean> logout(String token);

    // 更新用户头像
    Result<Boolean> updateAvatar(String token, MultipartFile avatar);

    // 更新用户名
    Result<Boolean> updateUsername(String token, String username);

    // 更新性别
    Result<Boolean> updateGender(String token, int gender);

    // 更新用户简介
    Result<Boolean> updateProfile(String token, String profile);

    // 上传校园卡照片
    Result<Boolean> uploadCampusCard(String token, MultipartFile campusCard);

    // 查询当前用户是否有审核记录
    Result<Boolean> hasAuditRecord(String token);

    //查询审核状态
    Result<Integer> getAuditStatus(String token);

    // 查询审核不通过原因
    Result<String> getRejectReason(String token);
}
