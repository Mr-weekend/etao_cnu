package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.User;
import cn.etaocnu.cloud.model.vo.UserAuthVo;
import cn.etaocnu.cloud.system.service.SysUserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/sysUser")
public class SysUserInfoController {

    @Resource
    private SysUserInfoService sysUserInfoService;

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        List<User> userList = sysUserInfoService.getUserList(page, size, keyword);
        int total = sysUserInfoService.countUserList(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("list", userList);
        result.put("total", total);
        result.put("size", size);
        result.put("current", page);
        result.put("pages", (total + size - 1) / size); // 总页数计算
        return Result.success("获取用户列表成功！", result);
    }

    @Operation(summary = "封禁用户")
    @PostMapping("/ban/{userId}")
    public Result<Boolean> banUser(
            @PathVariable int userId,
            @RequestParam("violationReason") String violationReason) {
        Boolean result = sysUserInfoService.banUser(userId, violationReason);
        return result ? Result.success("用户封禁成功", true) : Result.fail("用户封禁失败");
    }

    @Operation(summary = "解禁用户")
    @PostMapping("/unban/{userId}")
    public Result<Boolean> unbanUser(@PathVariable int userId) {
        Boolean result = sysUserInfoService.unbanUser(userId);
        return result ? Result.success("用户解禁成功",true) : Result.fail("用户解禁失败");
    }

    @Operation(summary = "获取认证申请列表")
    @GetMapping("/auth/list")
    public Result<Map<String, Object>> getUserAuthList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserAuthVo> authList = sysUserInfoService.getUserAuthList(page, size);
        int total = sysUserInfoService.countUserAuthList();
        Map<String, Object> result = new HashMap<>();
        result.put("list", authList);
        result.put("total", total);
        result.put("size", size);
        result.put("current", page);
        result.put("pages", (total + size - 1) / size); // 总页数计算
        return Result.success("获取认证申请列表成功！", result);
    }

    @Operation(summary = "通过认证申请")
    @PostMapping("/auth/pass/{userId}")
    public Result<Boolean> passAuth(@PathVariable int userId) {
        Boolean result = sysUserInfoService.passAuth(userId);
        return result ? Result.success("认证申请已通过！",true) : Result.fail("处理认证申请失败！");
    }

    @Operation(summary = "驳回认证申请")
    @PostMapping("/auth/reject/{userId}")
    public Result<Boolean> rejectAuth(
            @PathVariable int userId,
            @RequestParam("rejectReason") String rejectReason) {
        Boolean result = sysUserInfoService.unPassAuth(userId, rejectReason);
        return result ? Result.success("认证申请已驳回！",true) : Result.fail("处理认证申请失败！");
    }
} 