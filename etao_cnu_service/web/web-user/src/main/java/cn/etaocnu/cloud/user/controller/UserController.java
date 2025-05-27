package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.UserVo;
import cn.etaocnu.cloud.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "用户API接口管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Value("${config.info:default}")
    private String info;

    @GetMapping("/config/info")
    public String getInfo() {
        return info;
    }

    @Operation(summary = "小程序授权登录")
    @GetMapping("/login/{code}")
    public Result<String> Login(@PathVariable String code) {
        return userService.login(code);
    }

    @Operation(summary = "获取用户登录信息")
    @GetMapping("/getUserLoginInfo")
    public Result<UserVo> getUserLoginInfo(@RequestHeader(value = "token") String token) {
        return userService.getUserLoginInfo(token);
    }

    @Operation(summary = "获取指定用户信息")
    @GetMapping("/getUserInfo/{userId}")
    public Result<UserVo> getUserLoginInfo(@PathVariable int userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader(value = "token") String token) {
        return userService.logout(token);
    }

    @Operation(summary = "更新用户头像")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> updateAvatar(@RequestHeader(value = "token") String token,
                                        @RequestPart("avatar") MultipartFile avatar) {
        return userService.updateAvatar(token, avatar);
    }

    @Operation(summary = "更新用户名")
    @PutMapping("/username")
    public Result<Boolean> updateUsername(@RequestHeader(value = "token") String token,
                                          @RequestParam("username") String username) {
        return userService.updateUsername(token, username);
    }

    @Operation(summary = "更新性别")
    @PutMapping("/gender")
    public Result<Boolean> updateGender(@RequestHeader(value = "token") String token,
                                          @RequestParam("gender") int gender) {
        return userService.updateGender(token, gender);
    }

    @Operation(summary = "更新用户简介")
    @PutMapping("/profile")
    public Result<Boolean> updateProfile(@RequestHeader(value = "token") String token,
                                         @RequestParam("profile") String profile) {
        return userService.updateProfile(token, profile);
    }

    @Operation(summary = "上传校园卡照片")
    @PostMapping(value = "/upload/campusCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> uploadCampusCard(@RequestHeader(value = "token") String token,
                                     @RequestPart("campusCard") MultipartFile campusCard){
        return userService.uploadCampusCard(token, campusCard);
    }

    @Operation(summary = "查询当前用户是否有审核记录")
    @GetMapping("/hasAuditRecord")
    public Result<Boolean> hasAuditRecord(@RequestHeader(value = "token") String token) {
        return userService.hasAuditRecord(token);
    }

    @Operation(summary = "查询当前用户审核状态")
    @GetMapping("/auditStatus")
    public Result<Integer> getAuditStatus(@RequestHeader(value = "token") String token) {
        return userService.getAuditStatus(token);
    }

    @Operation(summary = "查询当前用户审核不通过原因")
    @GetMapping("/rejectReason")
    public Result<String> getRejectReason(@RequestHeader(value = "token") String token) {
        return userService.getRejectReason(token);
    }
}
