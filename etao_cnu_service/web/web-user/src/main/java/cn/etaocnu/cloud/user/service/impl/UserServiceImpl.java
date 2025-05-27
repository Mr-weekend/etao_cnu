package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.user.client.UserFeignClient;
import cn.etaocnu.cloud.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<String> login(String code) {
        //1 拿着code进行远程调用，返回用户id
        Result<Integer> loginResult = userFeignClient.login(code);
        //2 判断如果返回失败了，返回错误提示
        Integer codeResult = loginResult.getCode();
        if(codeResult != 200) {
            throw new RuntimeException("登录异常！");
        }
        //3 远程调用用户id
        Integer userId = loginResult.getData();
        //4 判断返回用户id是否为空，如果为空，返回错误提示
        if(userId == null) {
            throw new RuntimeException("未能获取到用户Id！");
        }
        //5 生成token字符串
        String token = UUID.randomUUID().toString().replace("-", "");
        //6 把用户id放到Redis，设置过期时间 15天
        redisTemplate.opsForValue().set("user:login:" + token,
                userId.toString(),60 * 60 * 24 * 15,
                TimeUnit.SECONDS);
        // 7 返回token
        return Result.success(loginResult.getMessage(), token);
    }

    @Override
    public Result<UserVo> getUserLoginInfo(String token) {
        System.out.println("前端传入的token：" + token);
        //根据token查询redis
        String userId = redisTemplate.opsForValue().get("user:login:"+ token);
        if(StringUtils.isEmpty(userId)) {
            return Result.fail("未能从redis中查询到用户Id！",null);
        }

        return userFeignClient.getUserLoginInfo(Integer.parseInt(userId));
    }

    @Override
    public Result<UserVo> getUserInfo(int userId) {
        return userFeignClient.getUserInfo(userId);
    }

    @Override
    public Result<Boolean> logout(String token) {
        if (!StringUtils.hasText(token)) {
            return Result.fail("token不能为空");
        }
        log.info("用户登出");
        return userFeignClient.logout(token);
    }

    @Override
    public Result<Boolean> updateAvatar(String token, MultipartFile avatar) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        
        if (avatar == null) {
            return Result.fail("头像不能为空");
        }
        
        return userFeignClient.updateAvatar(userId, avatar);
    }

    @Override
    public Result<Boolean> updateUsername(String token, String username) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        
        if (!StringUtils.hasText(username)) {
            return Result.fail("用户名不能为空");
        }
        
        return userFeignClient.updateUsername(userId, username);

    }

    @Override
    public Result<Boolean> updateGender(String token, int gender) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        return userFeignClient.updateGender(userId, gender);
    }

    @Override
    public Result<Boolean> updateProfile(String token, String profile) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }

        return userFeignClient.updateProfile(userId, profile);
    }

    @Override
    public Result<Boolean> uploadCampusCard(String token, MultipartFile campusCard) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        return userFeignClient.uploadCampusCard(userId, campusCard);
    }

    @Override
    public Result<Boolean> hasAuditRecord(String token) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        return userFeignClient.hasAuditRecord(userId);
    }

    @Override
    public Result<Integer> getAuditStatus(String token) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        return userFeignClient.getAuditStatus(userId);
    }

    @Override
    public Result<String> getRejectReason(String token) {
        Integer userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("用户不存在！");
        }
        return userFeignClient.getRejectReason(userId);
    }

    // 获取用户ID方法
    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        return StringUtils.hasText(userId) ? Integer.parseInt(userId) : null;
    }
}
