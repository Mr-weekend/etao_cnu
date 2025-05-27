package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.entity.Category;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.system.mapper.SysCategoryMapper;
import cn.etaocnu.cloud.system.service.SysCategoryInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Slf4j
public class SysCategoryInfoServiceImpl implements SysCategoryInfoService {

    @Resource
    private FileService fileService;

    @Resource
    private SysCategoryMapper sysCategoryMapper;

    @Override
    public Boolean addCategory(String categoryName, MultipartFile icon) {
        try{
            if (!StringUtils.hasText(categoryName)) {
                return false;
            }
            FileResponse fileResponse = fileService.uploadFile(icon,"category");
            Category category = new Category();
            if (fileResponse != null) {
                category.setCategoryName(categoryName);
                category.setIcon(fileResponse.getFileUrl());
            }else{
                return false;
            }
            return sysCategoryMapper.insertSelective(category) > 0;
        }catch (Exception e){
            log.error("添加分类信息失败！", e);
            return false;
        }
    }

    @Override
    public Boolean deleteCategory(Integer categoryId) {
        try{
            Category category = sysCategoryMapper.selectByPrimaryKey(categoryId);
            fileService.deleteFile(category.getIcon());
            return sysCategoryMapper.deleteByPrimaryKey(categoryId) > 0;
        }catch (Exception e){
            log.error("删除分类信息失败！");
        }
        return null;
    }

    @Override
    public Boolean updateCategoryName(Integer categoryId, String categoryName) {
        try{
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("categoryId", categoryId);
            Category category = sysCategoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                return false;
            }
            Category newCategory = new Category();
            newCategory.setCategoryName(categoryName);
            return sysCategoryMapper.updateByExampleSelective(newCategory, example) > 0;
        }catch (Exception e){
            log.error("更新分类名称失败！");
            return false;
        }
    }

    @Override
    public Boolean updateCategoryIcon(Integer categoryId, MultipartFile icon) {
        try{
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("categoryId", categoryId);
            Category category = sysCategoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                return false;
            }
            Category newCategory = new Category();
            //删除原来图标
            fileService.deleteFile(category.getIcon());
            FileResponse fileResponse = fileService.uploadFile(icon,"category");
            if (fileResponse != null) {
                newCategory.setIcon(fileResponse.getFileUrl());
            }else{
                return false;
            }
            return sysCategoryMapper.updateByExampleSelective(newCategory, example) > 0;
        }catch (Exception e){
            log.error("更新分类图标失败！");
            return false;
        }
    }

    @Override
    public List<Category> getCategoryList(int page, int size) {
        try{
            Example example = new Example(Category.class);
            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            return sysCategoryMapper.selectByExampleAndRowBounds(example, rowBounds);
        }catch (Exception e){
            log.error("获取分类列表失败！");
            return null;
        }
    }

    @Override
    public int countCategory() {
        try{
            Example example = new Example(Category.class);
            return sysCategoryMapper.selectCountByExample(example);
        }catch (Exception e){
            log.error("计算分类数量失败！");
            return 0;
        }
    }
}
