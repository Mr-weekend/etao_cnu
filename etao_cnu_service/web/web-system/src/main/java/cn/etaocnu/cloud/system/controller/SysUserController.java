package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户管理接口")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return sysUserService.getUserList(page, size, keyword);
    }

    @Operation(summary = "封禁用户")
    @PostMapping("/ban/{userId}")
    public Result<Boolean> banUser(
            @PathVariable int userId,
            @RequestParam("violationReason") String violationReason) {
        return sysUserService.banUser(userId, violationReason);
    }

    @Operation(summary = "解禁用户")
    @PostMapping("/unban/{userId}")
    public Result<Boolean> unbanUser(@PathVariable int userId) {
        return sysUserService.unbanUser(userId);
    }

    @Operation(summary = "获取认证申请列表")
    @GetMapping("/auth/list")
    public Result<Map<String, Object>> getUserAuthList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return sysUserService.getUserAuthList(page, size);
    }

    @Operation(summary = "通过认证申请")
    @PostMapping("/auth/pass/{userId}")
    public Result<Boolean> passAuth(@PathVariable int userId) {
        return sysUserService.passAuth(userId);
    }

    @Operation(summary = "驳回认证申请")
    @PostMapping("/auth/reject/{userId}")
    public Result<Boolean> rejectAuth(
            @PathVariable int userId,
            @RequestParam("rejectReason") String rejectReason) {
        return sysUserService.rejectAuth(userId, rejectReason);
    }
} 