package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Admin;
import cn.etaocnu.cloud.system.client.AdminFeignClient;
import cn.etaocnu.cloud.system.service.AdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminFeignClient adminFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<String> login(String adminName, String password) {
        Result<Integer> loginResult = adminFeignClient.login(adminName, password);
        if (loginResult.getCode() != 200) {
            if (loginResult.getData() == 0 || loginResult.getData() == -1 || loginResult.getData() == -2) {
                return Result.fail(loginResult.getMessage());
            }
        }
        Integer adminId = loginResult.getData();
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("admin:login:" + token,
                adminId.toString(),60 * 60 * 24 * 15,
                TimeUnit.SECONDS);
        return Result.success(loginResult.getMessage(), token);
    }

    @Override
    public Result<Boolean> logout(String token) {
        if (!StringUtils.hasText(token)) {
            return Result.fail("token不能为空");
        }
        Result<Boolean> logoutResult = adminFeignClient.logout(token);
        log.info("管理员登出");
        if (logoutResult.getCode() != 200) {
            return Result.fail(logoutResult.getMessage());
        }
        return Result.success(logoutResult.getMessage(),logoutResult.getData());
    }

    @Override
    public Result<Boolean> addAdmin(Admin admin) {
        log.info("添加管理员，用户名：{}", admin.getAdminName());
        return adminFeignClient.addAdmin(admin);
    }

    @Override
    public Result<Boolean> updateAdminName(String token, String newAdminName) {
        log.info("修改管理员用户名，新用户名：{}", newAdminName);
        return adminFeignClient.updateAdminName(token, newAdminName);
    }

    @Override
    public Result<Boolean> updatePassword(String token, String oldPassword, String newPassword) {
        log.info("修改管理员密码");
        return adminFeignClient.updatePassword(token, oldPassword, newPassword);
    }

    @Override
    public Result<Admin> getAdminInfo(String token) {
        log.info("获取管理员信息");
        return adminFeignClient.getAdminInfo(token);
    }
}
