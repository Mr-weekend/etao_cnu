package cn.etaocnu.cloud.system.controller;


import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.entity.Category;
import cn.etaocnu.cloud.system.service.SysCategoryInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "闲置物品分类管理接口")
@RestController
@RequestMapping("/sysCategory")
public class SysCategoryInfoController {
    @Resource
    private SysCategoryInfoService sysCategoryInfoService;

    @Operation(summary = "添加分类信息")
    @PostMapping("/add")
    Result<Boolean> addCategory(@RequestParam("categoryName") String categoryName,
                                @RequestParam("icon") MultipartFile icon){
        log.info("添加分类信息，categoryName: {}，icon: {}", categoryName, icon.getOriginalFilename());
        Boolean result = sysCategoryInfoService.addCategory(categoryName, icon);
        return result ? Result.success("添加分类信息成功！", true): Result.fail("添加分类信息失败！");
    }

    @Operation(summary = "修改分类名称")
    @PutMapping("/name/{categoryId}")
    Result<Boolean> updateCategoryName(@PathVariable Integer categoryId,
                                       @RequestParam("categoryName") String categoryName){
        log.info("修改分类信息，categoryId: {}, categoryName: {}", categoryId, categoryName);
        Boolean result = sysCategoryInfoService.updateCategoryName(categoryId, categoryName);
        return result ? Result.success("更新分类名称成功！", true): Result.fail("更新分类名称失败！");
    }
    @Operation(summary = "修改分类图标")
    @PutMapping("/icon/{categoryId}")
    Result<Boolean> updateCategoryIcon(@PathVariable Integer categoryId,
                                       @RequestParam("icon") MultipartFile icon){
        log.info("修改分类信息，categoryId: {}, icon: {}", categoryId, icon);
        Boolean result = sysCategoryInfoService.updateCategoryIcon(categoryId, icon);
        return result ? Result.success("更新分类图标成功！", true): Result.fail("更新分类图标失败！");
    }

    @Operation(summary = "删除分类信息")
    @DeleteMapping("/{categoryId}")
    Result<Boolean> deleteCategory(@PathVariable Integer categoryId){
        log.info("删除分类信息，categoryId: {}", categoryId);
        Boolean result = sysCategoryInfoService.deleteCategory(categoryId);
        return result ? Result.success("删除分类信息成功！", true): Result.fail("删除分类信息失败！");
    }

    @Operation(summary = "获取分类信息列表")
    @GetMapping("/list")
    Result<Map<String, Object>> getCategoryList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size){
        log.info("获取分类信息列表");
        List<Category> categoryList = sysCategoryInfoService.getCategoryList(page, size);
        int total = sysCategoryInfoService.countCategory();
        Map<String, Object> result = new HashMap<>();
        result.put("list", categoryList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("获取分类信息列表成功！", result);
    }
}
