package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.service.SysCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "分类管理接口")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/sysCategory")
public class SysCategoryController {

    @Resource
    private SysCategoryService sysCategoryService;

    @Operation(summary = "添加分类")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> addCategory(@RequestParam("categoryName") String categoryName,
                                       @RequestPart("icon") MultipartFile icon) {
        return sysCategoryService.addCategory(categoryName, icon);
    }

    @Operation(summary = "更新分类名称")
    @PutMapping("/name/{categoryId}")
    public Result<Boolean> updateCategoryName(@PathVariable Integer categoryId,
                                              @RequestParam("categoryName") String categoryName) {
        return sysCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @Operation(summary = "更新分类图标")
    @PutMapping(value = "/icon/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> updateCategoryIcon(@PathVariable Integer categoryId,
                                              @RequestPart("icon") MultipartFile icon) {
        return sysCategoryService.updateCategoryIcon(categoryId, icon);
    }
    @Operation(summary = "删除分类")
    @DeleteMapping("/{categoryId}")
    public Result<Boolean> deleteCategory(@PathVariable Integer categoryId) {
        return sysCategoryService.deleteCategory(categoryId);
    }
    @Operation(summary = "获取分类列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getCategoryList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return sysCategoryService.getCategoryList(page, size);
    }
}
