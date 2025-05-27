package cn.etaocnu.cloud.system.client;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(value = "service-system")
public interface SysCategoryFeignClient {

    @PostMapping(value = "sysCategory/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> addCategory(@RequestParam("categoryName") String categoryName,
                                @RequestPart("icon") MultipartFile icon);

    @PutMapping("sysCategory/name/{categoryId}")
    Result<Boolean> updateCategoryName(@PathVariable Integer categoryId,
                                       @RequestParam("categoryName") String categoryName);

    @PutMapping(value = "sysCategory/icon/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> updateCategoryIcon(@PathVariable Integer categoryId,
                                       @RequestPart("icon") MultipartFile icon);

    @DeleteMapping("sysCategory/{categoryId}")
    Result<Boolean> deleteCategory(@PathVariable("categoryId") Integer categoryId);

    @GetMapping("sysCategory/list")
    Result<Map<String, Object>> getCategoryList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size);
}
