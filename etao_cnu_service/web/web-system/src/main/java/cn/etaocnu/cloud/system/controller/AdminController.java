package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Admin;
import cn.etaocnu.cloud.system.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController

@RequestMapping("/admin")
@Tag(name = "管理员接口")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<String> login(@RequestParam String adminName, 
                                @RequestParam String password) {
        log.info("管理员登录，用户名：{}", adminName);
        return adminService.login(adminName, password);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader("AdminToken") String token) {
        return adminService.logout(token);
    }

    @Operation(summary = "添加管理员")
    @PostMapping("/add")
    public Result<Boolean> addAdmin(@RequestBody Admin admin) {
        return adminService.addAdmin(admin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "修改用户名")
    @PutMapping("/adminName")
    public Result<Boolean> updateAdminName(@RequestHeader("AdminToken") String token,
                                              @RequestParam String newAdminName) {
        return adminService.updateAdminName(token, newAdminName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestHeader("AdminToken") String token,
                                          @RequestParam String oldPassword, 
                                          @RequestParam String newPassword) {
        return adminService.updatePassword(token, oldPassword, newPassword);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取管理员信息")
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(@RequestHeader("AdminToken") String token) {
        return adminService.getAdminInfo(token);
    }
}
