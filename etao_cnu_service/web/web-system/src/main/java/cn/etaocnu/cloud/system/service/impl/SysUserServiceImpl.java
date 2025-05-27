package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.client.SysUserFeignClient;
import cn.etaocnu.cloud.system.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserFeignClient sysUserFeignClient;

    @Override
    public Result<Map<String, Object>> getUserList(int page, int size, String keyword) {
        return sysUserFeignClient.getUserList(page, size, keyword);
    }

    @Override
    public Result<Boolean> banUser(int userId, String violationReason) {
        return sysUserFeignClient.banUser(userId, violationReason);
    }

    @Override
    public Result<Boolean> unbanUser(int userId) {
        return sysUserFeignClient.unbanUser(userId);
    }

    @Override
    public Result<Map<String, Object>> getUserAuthList(int page, int size) {
        return sysUserFeignClient.getUserAuthList(page, size);
    }

    @Override
    public Result<Boolean> passAuth(int userId) {
        return sysUserFeignClient.passAuth(userId);
    }

    @Override
    public Result<Boolean> rejectAuth(int userId, String rejectReason) {
        return sysUserFeignClient.rejectAuth(userId, rejectReason);
    }
} 