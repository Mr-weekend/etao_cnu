package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SysCategoryService {
    // 添加分类
    Result<Boolean> addCategory(String categoryName, MultipartFile icon);
    // 删除分类
    Result<Boolean> deleteCategory(Integer categoryId);
    // 修改分类名
    Result<Boolean> updateCategoryName(Integer categoryId, String categoryName);
    // 修改分类图标
    Result<Boolean> updateCategoryIcon(Integer categoryId, MultipartFile icon);
    // 获取所有分类列表
    Result<Map<String, Object>> getCategoryList(int page, int size);
}
