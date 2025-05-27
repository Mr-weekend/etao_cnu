package cn.etaocnu.cloud.system.client;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "service-system")
public interface SysUserFeignClient {

    @GetMapping("/sysUser/list")
    Result<Map<String, Object>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword);

    @PostMapping("/sysUser/ban/{userId}")
    Result<Boolean> banUser(
            @PathVariable("userId") int userId,
            @RequestParam("violationReason") String violationReason);

    @PostMapping("/sysUser/unban/{userId}")
    Result<Boolean> unbanUser(@PathVariable("userId") int userId);

    @GetMapping("/sysUser/auth/list")
    Result<Map<String, Object>> getUserAuthList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @PostMapping("/sysUser/auth/pass/{userId}")
    Result<Boolean> passAuth(@PathVariable("userId") int userId);

    @PostMapping("/sysUser/auth/reject/{userId}")
    Result<Boolean> rejectAuth(
            @PathVariable("userId") int userId,
            @RequestParam("rejectReason") String rejectReason);
} 