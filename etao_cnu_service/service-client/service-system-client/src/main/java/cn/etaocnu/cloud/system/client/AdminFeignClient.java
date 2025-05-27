package cn.etaocnu.cloud.system.client;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Admin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "service-system")
public interface AdminFeignClient {
    @PostMapping("/admin/login")
    Result<Integer> login(@RequestParam("adminName") String adminName,
                          @RequestParam("password") String password);

    @PostMapping("/admin/logout")
    Result<Boolean> logout(@RequestHeader("AdminToken") String token);

    @PostMapping("/admin/add")
    Result<Boolean> addAdmin(@RequestBody Admin admin);

    @PutMapping("/admin/adminName")
    Result<Boolean> updateAdminName(@RequestHeader("AdminToken") String token,
                                    @RequestParam("newAdminName") String newAdminName);

    @PutMapping("/admin/password")
    Result<Boolean> updatePassword(@RequestHeader("AdminToken") String token,
                                    @RequestParam("oldPassword") String oldPassword, 
                                    @RequestParam("newPassword") String newPassword);

    @GetMapping("/admin/info")
    Result<Admin> getAdminInfo(@RequestHeader("AdminToken") String token);
}
