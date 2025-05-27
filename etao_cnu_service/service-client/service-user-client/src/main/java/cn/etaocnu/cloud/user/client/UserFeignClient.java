package cn.etaocnu.cloud.user.client;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "service-user")
public interface UserFeignClient {
    @GetMapping("/user/login/{code}")
    Result<Integer> login(@PathVariable("code") String code);

    @GetMapping("/user/getUserLoginInfo/{userId}")
    Result<UserVo> getUserLoginInfo(@PathVariable("userId") int userId);

    @GetMapping("/user/getUserInfo/{userId}")
    Result<UserVo> getUserInfo(@PathVariable("userId") int userId);

    @PostMapping("/user/logout")
    Result<Boolean> logout(@RequestHeader("token") String token);


    @PostMapping(value = "/user/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> updateAvatar(@RequestParam("userId") int userId,
                                 @RequestPart("avatar") MultipartFile avatar);


    @PutMapping("/user/username")
    Result<Boolean> updateUsername(@RequestParam("userId") int userId,
                                   @RequestParam("username") String username);

    @PutMapping("/user/gender")
    Result<Boolean> updateGender(@RequestParam("userId") int userId,
                                   @RequestParam("gender") int gender);

    @PutMapping("/user/profile")
    Result<Boolean> updateProfile(@RequestParam("userId") int userId,
                                  @RequestParam("profile") String profile);

    @PostMapping(value = "/user/upload/campusCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> uploadCampusCard(@RequestParam("userId") int userId,
                                     @RequestPart("campusCard") MultipartFile campusCard);

    @GetMapping("/user/hasAuditRecord/{userId}")
    Result<Boolean> hasAuditRecord(@PathVariable("userId") int userId);

    @GetMapping("/user/auditStatus/{userId}")
    Result<Integer> getAuditStatus(@PathVariable("userId") int userId);

    @GetMapping("/user/rejectReason/{userId}")
    Result<String> getRejectReason(@PathVariable("userId") int userId);
}
