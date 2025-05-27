package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Admin;
import cn.etaocnu.cloud.system.service.AdminInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "管理员接口")
@RestController
@RequestMapping("/admin")
public class AdminInfoController {

    @Resource
    private AdminInfoService adminInfoService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Integer> login(@RequestParam String adminName, @RequestParam String password) {
        log.info("管理员登录: {}", adminName);
        int result = adminInfoService.login(adminName, password);
        return switch (result) {
            case 0 -> Result.fail("请输入管理员账号和密码！", result);
            case -1 -> Result.fail("管理员不存在！", result);
            case -2 -> Result.fail("密码错误！", result);
            default -> Result.success("登录成功！", result);
        };
    }

    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader("AdminToken") String token) {
        log.info("管理员登出");
        boolean result = adminInfoService.logout(token);
        return result ? Result.success("登出成功！",true) : Result.fail("登出失败！");
    }

    @Operation(summary = "添加管理员")
    @PostMapping("/add")
    public Result<Boolean> addAdmin(@RequestBody Admin admin) {
        log.info("添加管理员: {}", admin.getAdminName());
        boolean result = adminInfoService.addAdmin(admin);
        return result ? Result.success("添加成功",true) : Result.fail("添加失败，用户名已存在");
    }

    @Operation(summary = "修改用户名")
    @PutMapping("/adminName")
    public Result<Boolean> updateAdminName(
            @RequestHeader("AdminToken") String token,
            @RequestParam String newAdminName) {
        Admin admin = adminInfoService.getAdminInfoByToken(token);
        if (admin == null) {
            return Result.fail("未登录或登录已过期");
        }
        
        log.info("修改用户名: {} -> {}", admin.getAdminName(), newAdminName);
        boolean result = adminInfoService.updateAdminName(admin.getAdminId(), newAdminName);
        return result ? Result.success("修改成功",true) : Result.fail("修改失败，用户名已存在");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Boolean> updatePassword(
            @RequestHeader("AdminToken") String token,
            @RequestParam String oldPassword, 
            @RequestParam String newPassword) {
        Admin admin = adminInfoService.getAdminInfoByToken(token);
        if (admin == null) {
            return Result.fail("未登录或登录已过期");
        }
        
        log.info("修改密码: {}", admin.getAdminName());
        boolean result = adminInfoService.updatePassword(admin.getAdminId(), oldPassword, newPassword);
        return result ? Result.success("修改成功",true) : Result.fail("修改失败，旧密码错误");
    }

    @Operation(summary = "获取管理员信息")
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(@RequestHeader("AdminToken") String token) {
        Admin admin = adminInfoService.getAdminInfoByToken(token);
        if (admin == null) {
            return Result.fail("未登录或登录已过期");
        }
        
        // 不返回密码
        admin.setPassword(null);
        return Result.success(admin);
    }
} 