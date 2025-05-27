package cn.etaocnu.cloud.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.entity.User;
import cn.etaocnu.cloud.model.entity.UserAuth;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.user.mapper.UserAuthMapper;
import cn.etaocnu.cloud.user.mapper.UserMapper;
import cn.etaocnu.cloud.user.service.UserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Slf4j
@Service
class UserInfoServiceImpl implements UserInfoService {
    //微信小程序登录接口
    @Autowired
    private WxMaService wxMaService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private FileService fileService;
    @Override
    public int login(String code) {
        String openid = null;
        try {
            WxMaJscode2SessionResult sessionInfo =
                    wxMaService.getUserService().getSessionInfo(code);
            openid = sessionInfo.getOpenid();
            log.info("小程序接口查到的openid:{}", openid);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        //2 根据openid查询数据库表，判断是否第一次登录
        //如果openid不存在返回null，如果存在返回一条记录
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("wxOpenId", openid);
        User user = userMapper.selectOneByExample(example);
        //3 如果第一次登录，添加信息到用户表
        if(user == null) {
            log.info("第一次登录");
            user = new User();
            user.setUserName(String.valueOf(System.currentTimeMillis()));
            user.setAvatarUrl("https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0");
            user.setWxOpenId(openid);
            userMapper.insertSelective(user);
        }else{
            log.info("不是第一次登录");
        }
        //5 返回用户id
        return user.getUserId();
    }

    @Override
    public UserVo getUserLoginInfo(int userId) {
        //1 根据用户id查询用户信息
        User user = userMapper.selectByPrimaryKey(userId);
        //2 封装到UserVo
        UserVo userLoginVo = new UserVo();
        BeanUtils.copyProperties(user,userLoginVo);
        //3 UserVo返回
        return userLoginVo;
    }

    @Override
    public UserVo getUserInfo(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
        return userVo;
    }

//    @Override
//    public boolean logout(int userId) {
//        try {
//
//            // 根据userId查询所有相关的token
//            String keyPattern = "user:login:*";
//            Set<String> keys = redisTemplate.keys(keyPattern);
//            if (keys != null && !keys.isEmpty()) {
//                for (String key : keys) {
//                    String value = redisTemplate.opsForValue().get(key);
//                    if (String.valueOf(userId).equals(value)) {
//                        // 找到匹配的userId的token，删除它
//                        redisTemplate.delete(key);
//                        log.info("用户[{}]登出成功，已删除token", userId);
//                        return true;
//                    }
//                }
//            }
//            log.warn("未找到用户[{}]的登录token", userId);
//            return false;
//        } catch (Exception e) {
//            log.error("用户登出异常：", e);
//            return false;
//        }
//    }

    @Override
    public boolean logout(String token) {
        try{
            if (!StringUtils.hasText(token)) {
                return false;
            }
            String key = "user:login:" + token;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(hasKey)) {
                Boolean delete = redisTemplate.delete(key);
                return Boolean.TRUE.equals(delete);
            }
        }catch (Exception e){
            log.error("退出登录异常！");
            return false;
        }
        return false;
    }

    @Override
    public Boolean updateAvatar(int userId, MultipartFile avatar) {
        try {
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);
            User user = new User();
            String originalAvatarUrl = userMapper.selectByPrimaryKey(userId).getAvatarUrl();
            //以https开头，默认头像，不用删除原来的头像
            if(originalAvatarUrl.startsWith("https")){
                FileResponse fileResponse = fileService.uploadFile(avatar,"user");
                if (fileResponse != null) {
                    //设置新图片地址
                    user.setAvatarUrl(fileResponse.getFileUrl());
                }else{
                    return false;
                }
            }else {//删除原来头像，更改新头像
                fileService.deleteFile(originalAvatarUrl);
                FileResponse fileResponse = fileService.uploadFile(avatar,"user");
                if (fileResponse != null) {
                    user.setAvatarUrl(fileResponse.getFileUrl());
                }else {
                    return false;
                }
            }
            user.setUpdatedAt(new Date());
            return userMapper.updateByExampleSelective(user, example) > 0;
        } catch (Exception e) {
            log.error("更新用户头像失败", e);
            return false;
        }
    }

    @Override
    public Boolean updateUsername(int userId, String username) {
        try {
            if (!StringUtils.hasText(username)) {
                return false;
            }

            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);

            User user = new User();
            user.setUserName(username);
            user.setUpdatedAt(new Date());

            return userMapper.updateByExampleSelective(user, example) > 0;
        } catch (Exception e) {
            log.error("更新用户名失败", e);
            return false;
        }
    }

    @Override
    public Boolean updateGender(int userId, int gender) {
        try {
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);

            User user = new User();
            user.setGender((byte) gender);
            user.setUpdatedAt(new Date());

            return userMapper.updateByExampleSelective(user, example) > 0;
        } catch (Exception e) {
            log.error("更新性别失败", e);
            return false;
        }
    }

    @Override
    public Boolean updateProfile(int userId, String profile) {
        try {
            // 简介可以为空，表示清空简介
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("userId", userId);

            User user = new User();
            user.setUserProfile(profile);
            user.setUpdatedAt(new Date());

            return userMapper.updateByExampleSelective(user, example) > 0;
        } catch (Exception e) {
            log.error("更新用户简介失败", e);
            return false;
        }
    }

    @Override
    public Boolean uploadCampusCard(int userId, MultipartFile campusCard) {
        try {
            Example example = new Example(UserAuth.class);
            example.createCriteria().andEqualTo("userId", userId);
            UserAuth userAuth = userAuthMapper.selectOneByExample(example);
            //查询是否有认证记录，有则更新
//            UserAuth userAuth = userMapper.getUserAuth(userId);
            if (userAuth != null) {
                //删除原来的图片
                fileService.deleteFile(userAuth.getAuthImage());
                //上传新图片
                FileResponse fileResponse = fileService.uploadFile(campusCard, "userAuth");
                //更新数据库
                if (fileResponse != null) {
                    userAuth.setAuthImage(fileResponse.getFileUrl());
                    userAuth.setAuthStatus((byte) 0);
                    return userAuthMapper.updateByExampleSelective(userAuth,example) > 0;
//                    return userMapper.updateUserAuth(userId,fileResponse.getFileUrl()) > 0;
                }
            }else{//没有就添加新纪录
                //上传图片
                FileResponse fileResponse = fileService.uploadFile(campusCard, "userAuth");
                if (fileResponse != null) {
                    UserAuth newUserAuth = new UserAuth();
                    newUserAuth.setUserId(userId);
                    newUserAuth.setAuthImage(fileResponse.getFileUrl());
                    newUserAuth.setAuthStatus((byte) 0);
                    newUserAuth.setCreatedAt(new Date());
                    return userAuthMapper.insert(newUserAuth) > 0;
                    //插入新纪录
//                    return userMapper.insertUserAuth(userId, fileResponse.getFileUrl(), new Date()) > 0;
                }
            }
        } catch (Exception e) {
            log.error("上传校园卡照片失败！", e);
            return false;
        }
        return null;
    }

    @Override
    public Boolean hasAuditRecord(int userId) {
        Example example = new Example(UserAuth.class);
        example.createCriteria().andEqualTo("userId", userId);
        UserAuth userAuth = userAuthMapper.selectOneByExample(example);
        return userAuth != null;
    }

    @Override
    public int getAuditStatus(int userId) {
        Example example = new Example(UserAuth.class);
        example.createCriteria().andEqualTo("userId", userId);
        UserAuth userAuth = userAuthMapper.selectOneByExample(example);
        if (userAuth != null) {
            return userAuth.getAuthStatus();
        }
        return -1;
    }

    @Override
    public String getRejectReason(int userId) {
        Example example = new Example(UserAuth.class);
        example.createCriteria().andEqualTo("userId", userId);
        UserAuth userAuth = userAuthMapper.selectOneByExample(example);
        if (userAuth != null) {
            return userAuth.getRejectReason();
        }
        return null;
    }
    //
//    @Override
//    public UserStatsVo getUserStats(int userId) {
//        Example example = new Example(User.class);
//        example.createCriteria().andEqualTo("userId", userId);
//        int favoriteCount = favoritesMapper.selectCountByExample(example);
//        UserStatsVo userStatsVo = new UserStatsVo();
//        userStatsVo.setFavoriteCount(favoriteCount);
//        return userStatsVo;
//    }
}

