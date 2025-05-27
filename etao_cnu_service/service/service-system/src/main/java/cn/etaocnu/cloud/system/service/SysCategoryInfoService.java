package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.model.entity.Category;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SysCategoryInfoService {
    // 添加分类
    Boolean addCategory(String categoryName, MultipartFile icon);
    // 删除分类
    Boolean deleteCategory(Integer categoryId);
    // 修改分类名
    Boolean updateCategoryName(Integer categoryId, String categoryName);
    // 修改分类图片
    Boolean updateCategoryIcon(Integer categoryId, MultipartFile icon);
    // 获取所有分类列表
    List<Category> getCategoryList(int page, int size);
    // 统计分类数量
    int countCategory();
}
