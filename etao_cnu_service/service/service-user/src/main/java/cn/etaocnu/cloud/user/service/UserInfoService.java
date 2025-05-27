package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.model.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {
    //微信小程序登录接口
    int login(String code);
    //获取用户登录信息
    UserVo getUserLoginInfo(int userId);
    //获取指定用户信息
    UserVo getUserInfo(int userId);
    //登出
    boolean logout(String token);
     //更新用户头像
    Boolean updateAvatar(int userId, MultipartFile avatar);
     //更新用户名
    Boolean updateUsername(int userId, String username);
    // 更新性别
    Boolean updateGender(int userId, int gender);
     //更新用户简介
    Boolean updateProfile(int userId, String profile);
    //上传学生证照片
    Boolean uploadCampusCard(int userId, MultipartFile campusCard);
    // 查询当前用户是否有审核记录
    Boolean hasAuditRecord(int userId);
    //查询审核状态
    int getAuditStatus(int userId);
    // 查询审核不通过原因
    String getRejectReason(int userId);
}
