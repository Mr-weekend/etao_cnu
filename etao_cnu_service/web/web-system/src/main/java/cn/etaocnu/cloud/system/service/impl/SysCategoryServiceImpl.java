package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.client.SysCategoryFeignClient;
import cn.etaocnu.cloud.system.service.SysCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class SysCategoryServiceImpl implements SysCategoryService {

    @Resource
    private SysCategoryFeignClient sysCategoryFeignClient;

    @Override
    public Result<Boolean> addCategory(String categoryName, MultipartFile icon) {
        try {
            Result<Boolean> result = sysCategoryFeignClient.addCategory(categoryName, icon);
            if (result.getCode() == 200){
                return Result.success(result.getMessage(), result.getData());
            }
            return Result.fail(result.getMessage());
        } catch (Exception e) {
            log.error("添加分类失败！", e);
            return Result.fail("添加分类失败！");
        }
    }

    @Override
    public Result<Boolean> updateCategoryName(Integer categoryId, String categoryName) {
        try {
            Result<Boolean> result = sysCategoryFeignClient.updateCategoryName(categoryId, categoryName);
            if (result.getCode() == 200){
                return Result.success(result.getMessage(), result.getData());
            }
            return Result.fail(result.getMessage());
        } catch (Exception e) {
            log.error("更新分类名称失败！", e);
            return Result.fail("更新分类名称失败！");
        }
    }

    @Override
    public Result<Boolean> updateCategoryIcon(Integer categoryId, MultipartFile icon) {
        try {
            Result<Boolean> result = sysCategoryFeignClient.updateCategoryIcon(categoryId, icon);
            if (result.getCode() == 200){
                return Result.success(result.getMessage(), result.getData());
            }
            return Result.fail(result.getMessage());
        } catch (Exception e) {
            log.error("更新分类图标失败！", e);
            return Result.fail("更新分类图标失败！");
        }
    }

    @Override
    public Result<Boolean> deleteCategory(Integer categoryId) {
        try {
            return sysCategoryFeignClient.deleteCategory(categoryId);
        } catch (Exception e) {
            log.error("删除分类失败！", e);
            return Result.fail("删除分类失败！");
        }
    }

    @Override
    public Result<Map<String, Object>> getCategoryList(int page, int size) {
        try {
            Result<Map<String, Object>> result = sysCategoryFeignClient.getCategoryList(page, size);
            if (result.getCode() == 200){
                return Result.success(result.getMessage(), result.getData());
            }
            return Result.fail(result.getMessage());
        } catch (Exception e) {
            log.error("获取分类列表失败！", e);
            return Result.fail("获取分类列表失败！");
        }
    }
}
