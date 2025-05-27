package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RefreshScope
@RequestMapping("/user")
public class UserInfoController {
    @Value("${config.info:default}")
    private String info;

    @GetMapping("/config/info")
    public String getInfo() {
        return info;
    }

    @Resource
    private UserInfoService userInfoService;

    //微信小程序登录接口
    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result <Integer> login(@PathVariable String code) {
        return Result.success("登录成功！",userInfoService.login(code));
    }
    @Operation(summary = "获取用户登录信息")
    @GetMapping("/getUserLoginInfo/{userId}")
    public Result<UserVo> getUserLoginInfo(@PathVariable int userId) {
        return Result.success("获取用户登录信息成功！",userInfoService.getUserLoginInfo(userId));
    }
    @Operation(summary = "获取指定用户信息")
    @GetMapping("/getUserInfo/{userId}")
    public Result<UserVo> getUserLInfo(@PathVariable int userId) {
        return Result.success("获取用户信息成功！",userInfoService.getUserInfo(userId));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader("token") String token) {
        boolean result = userInfoService.logout(token);
        return result ? Result.success("登出成功！", true) : Result.fail("登出失败！");
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/avatar")
    public Result<Boolean> updateAvatar(@RequestParam("userId") int userId,
                                        @RequestParam("avatar") MultipartFile avatar) {
        log.info("更新用户头像: userId={}, avatar={}", userId, avatar.getOriginalFilename());
        Boolean result = userInfoService.updateAvatar(userId, avatar);
        return result ? Result.success("更新头像成功！",true) : Result.fail("更新头像失败！");
    }

    @Operation(summary = "更新用户名")
    @PutMapping("/username")
    public Result<Boolean> updateUsername(@RequestParam("userId") int userId,
                                        @RequestParam("username") String username) {
        log.info("更新用户名: userId={}, username={}", userId, username);
        Boolean result = userInfoService.updateUsername(userId, username);
        return result ? Result.success("更新用户名成功！",true) : Result.fail("更新用户名失败！");
    }

    @Operation(summary = "更新性别")
    @PutMapping("/gender")
    public Result<Boolean> updateGender(@RequestParam("userId") int userId,
                                          @RequestParam("gender") int gender) {
        log.info("更新性别: userId={}, username={}", userId, gender);
        Boolean result = userInfoService.updateGender(userId, gender);
        return result ? Result.success("更新性别成功！",true) : Result.fail("更新性别失败！");
    }

    @Operation(summary = "更新用户简介")
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestParam("userId") int userId,
                                       @RequestParam("profile") String profile) {
        log.info("更新用户简介: userId={}, profile={}", userId, profile);
        Boolean result = userInfoService.updateProfile(userId, profile);
        return result ? Result.success("更新简介成功！",true) : Result.fail("更新简介失败！");
    }

    @Operation(summary = "上传校园卡照片")
    @PostMapping("/upload/campusCard")
    public Result<Boolean> uploadCampusCard(@RequestParam("userId") int userId,
                                        @RequestParam("campusCard") MultipartFile campusCard) {
        log.info("上传校园卡照片: userId={}, campusCard={}", userId, campusCard.getOriginalFilename());
        Boolean result = userInfoService.uploadCampusCard(userId, campusCard);
        return result ? Result.success("上传校园卡照片成功",true) : Result.fail("上传校园卡照片失败");
    }

    @Operation(summary = "查询当前用户是否有审核记录")
    @GetMapping("/hasAuditRecord/{userId}")
    public Result<Boolean> hasAuditRecord(@PathVariable("userId") int userId) {
        Boolean result =  userInfoService.hasAuditRecord(userId);
        return Result.success("查询是否有审核记录成功", result);
    }

    @Operation(summary = "查询当前用户审核状态")
    @GetMapping("/auditStatus/{userId}")
    public Result<Integer> getAuditStatus(@PathVariable("userId") int userId) {
        int result =  userInfoService.getAuditStatus(userId);
        return result != -1 ? Result.success("查询审核状态成功", result) : Result.fail("查询审核状态失败");
    }

    @Operation(summary = "查询当前用户审核不通过原因")
    @GetMapping("/rejectReason/{userId}")
    public Result<String> getRejectReason(@PathVariable("userId") int userId) {
        String result =  userInfoService.getRejectReason(userId);
        return result != null ? Result.success("查询审核不通过原因成功", result) : Result.fail("查询审核不通过原因失败");
    }

}
