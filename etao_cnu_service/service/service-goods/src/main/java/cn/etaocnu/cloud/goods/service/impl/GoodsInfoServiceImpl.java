package cn.etaocnu.cloud.goods.service.impl;

import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.goods.mapper.CategoryMapper;
import cn.etaocnu.cloud.goods.mapper.GoodsImageMapper;
import cn.etaocnu.cloud.goods.mapper.GoodsMapper;
import cn.etaocnu.cloud.goods.service.GoodsInfoService;
import cn.etaocnu.cloud.model.entity.Category;
import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.GoodsImage;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.model.vo.UserVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsInfoServiceImpl implements GoodsInfoService {
    
    @Resource
    private GoodsMapper goodsMapper;
    
    @Resource
    private GoodsImageMapper goodsImageMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private FileService fileService;

    @Override
    public Boolean publishGoods(int userId, String goodsInfoJson, MultipartFile[] images){
        try {
            GoodsCUVo goodsCUVo = new GoodsCUVo();
            if (goodsInfoJson != null && !goodsInfoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                goodsCUVo = objectMapper.readValue(goodsInfoJson, GoodsCUVo.class);
                log.info("解析后的闲置物品信息对象: {}", goodsCUVo);
            }
            // 保存闲置物品基本信息
            Goods goods = new Goods();
            BeanUtils.copyProperties(goodsCUVo, goods);
            goods.setUserId(userId);
            goods.setStatus("published");
            goods.setPublishTime(new Date());

            if (goodsMapper.insertSelective(goods) <= 0) {
                return false;
            }
            if (images != null && images.length > 0) {
                //上传图片到本地
                List<FileResponse> fileResponseList = fileService.uploadFiles(images,"goods");
                if (!fileResponseList.isEmpty()){
                    for (FileResponse fileResponse : fileResponseList) {
                        GoodsImage goodsImage = new GoodsImage();
                        goodsImage.setGoodsId(goods.getGoodsId());
                        goodsImage.setImageUrl(fileResponse.getFileUrl());
                        goodsImageMapper.insertSelective(goodsImage);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("发布闲置物品失败！", e);
            return false;
        }
    }

    @Override
    public Boolean updateGoods(int goodsId, int userId, String goodsInfoJson, MultipartFile[] images, String imageUpdateType, List<String> deleteImageUrls) {
        try {
            GoodsCUVo goodsCUVo = new GoodsCUVo();
            if (goodsInfoJson != null && !goodsInfoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                goodsCUVo = objectMapper.readValue(goodsInfoJson, GoodsCUVo.class);
                log.info("解析后的闲置物品信息对象: {}", goodsCUVo);
            }
            // 检查闲置物品是否存在且属于该用户
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("goodsId", goodsId)
                    .andEqualTo("userId", userId);
            
            Goods oldGoods = goodsMapper.selectOneByExample(example);
            if (oldGoods == null) {
                log.error("要更新的闲置物品不存在或不属于该用户, goodsId={}, userId={}", goodsId, userId);
                return false;
            }
            
            // 更新基本闲置物品信息
            Goods goods = new Goods();
            BeanUtils.copyProperties(goodsCUVo, goods);
            goods.setGoodsId(goodsId);
            goods.setUserId(userId);
            goods.setUpdatedAt(new Date());

            if (goodsMapper.updateByExampleSelective(goods, example) <= 0) {
                return false;
            }

            // 根据imageUpdateType处理图片
            if (imageUpdateType != null) {
                switch (imageUpdateType) {
                    case "keep":
                        // 保持原图，不做任何处理
                        break;
                        
                    case "replace":
                        // 完全替换图片
                        if (images != null && images.length > 0) {
                            // 删除旧图片
                            Example imageExample = new Example(GoodsImage.class);
                            imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                            List<GoodsImage> oldImages = goodsImageMapper.selectByExample(imageExample);
                            for (GoodsImage oldImage : oldImages) {
                                fileService.deleteFile(oldImage.getImageUrl());
                            }
                            goodsImageMapper.deleteByExample(imageExample);
                            
                            // 保存新图片
                            List<FileResponse> fileResponseList = fileService.uploadFiles(images, "goods");
                            for (FileResponse fileResponse : fileResponseList) {
                                GoodsImage goodsImage = new GoodsImage();
                                goodsImage.setGoodsId(goods.getGoodsId());
                                goodsImage.setImageUrl(fileResponse.getFileUrl());
                                goodsImage.setCreatedAt(new Date());
                                goodsImageMapper.insertSelective(goodsImage);
                            }
                        }
                        break;
                        
                    case "append":
                        // 追加新图片
                        if (images != null && images.length > 0) {
                            List<FileResponse> fileResponseList = fileService.uploadFiles(images, "goods");
                            for (FileResponse fileResponse : fileResponseList) {
                                GoodsImage goodsImage = new GoodsImage();
                                goodsImage.setGoodsId(goods.getGoodsId());
                                goodsImage.setImageUrl(fileResponse.getFileUrl());
                                goodsImage.setCreatedAt(new Date());
                                goodsImageMapper.insertSelective(goodsImage);
                            }
                        }
                        break;

                    case "mixed":
                        // 处理要删除的图片
                        if (deleteImageUrls != null && !deleteImageUrls.isEmpty()) {
                            for (String imageUrl : deleteImageUrls) {
                                // 删除数据库记录
                                Example imageExample = new Example(GoodsImage.class);
                                imageExample.createCriteria()
                                        .andEqualTo("goodsId", goods.getGoodsId())
                                        .andEqualTo("imageUrl", imageUrl);
                                goodsImageMapper.deleteByExample(imageExample);
                                
                                // 删除文件
                                fileService.deleteFile(imageUrl);
                            }
                        }
                        
                        // 添加新图片
                        if (images != null && images.length > 0) {
                            List<FileResponse> fileResponseList = fileService.uploadFiles(images, "goods");
                            for (FileResponse fileResponse : fileResponseList) {
                                GoodsImage goodsImage = new GoodsImage();
                                goodsImage.setGoodsId(goods.getGoodsId());
                                goodsImage.setImageUrl(fileResponse.getFileUrl());
                                goodsImage.setCreatedAt(new Date());
                                goodsImageMapper.insertSelective(goodsImage);
                            }
                        }
                        break;
                        
                    default:
                        log.warn("未知的图片更新类型: {}", imageUpdateType);
                        break;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("更新闲置物品失败", e);
            return false;
        }
    }

    @Override
    public Boolean removeGoods(int goodsId, int userId) {
        try {
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("goodsId", goodsId)
                    .andEqualTo("userId", userId);
            
            Goods goods = new Goods();
            goods.setStatus("removed");
            return goodsMapper.updateByExampleSelective(goods, example) > 0;
        } catch (Exception e) {
            log.error("下架闲置失败", e);
            return false;
        }
    }

    @Override
    public Boolean putGoods(int goodsId, int userId) {
        try{
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("goodsId", goodsId)
                    .andEqualTo("userId", userId);
            Goods goods = new Goods();
            goods.setStatus("published");
            return goodsMapper.updateByExampleSelective(goods, example) > 0;
        }catch (Exception e){
            log.error("上架闲置失败", e);
            return false;
        }
    }

    @Override
    public Boolean deleteGoods(int goodsId, int userId) {
        try {

            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("goodsId", goodsId)
                    .andEqualTo("userId", userId);
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            if (goods == null) {
                return false;
            }else if (goods.getUserId() != userId) {
                return false;
            }
            //删除本地图片
            Example imageExample = new Example(GoodsImage.class);
            imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
            List<GoodsImage> oldImages = goodsImageMapper.selectByExample(imageExample);
            if (oldImages != null && !oldImages.isEmpty()) {
                for (GoodsImage oldImage : oldImages) {
                    fileService.deleteFile(oldImage.getImageUrl());
                }
            }

            return goodsMapper.deleteByExample(example) > 0;
        } catch (Exception e) {
            log.error("删除闲置失败", e);
            return false;
        }
    }

    @Override
    public Boolean soldGoods(int goodsId) {
        try{
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("goodsId", goodsId);
            Goods goods = new Goods();
            goods.setStatus("sold");
            goods.setSoldTime(new Date());
            return goodsMapper.updateByExampleSelective(goods, example) > 0;
        }catch (Exception e){
            log.error("卖出闲置失败", e);
            return false;
        }
    }

    @Override
    public List<GoodsVo> getMyGoodsList(int userId, int page, int size) {
        try {
            UserVo userVo = goodsMapper.getGoodsPublisher(userId);
            if (userVo == null) {
                return null;
            }
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "published");
            example.orderBy("publishTime").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            List<Goods> goodsList = goodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并查询图片
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);

                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                goodsVo.setPublisher(userVo);
                goodsVoList.add(goodsVo);
            }

            return goodsVoList;
        } catch (Exception e) {
            log.error("获取用户闲置列表失败", e);
            return null;
        }
    }

    @Override
    public int countMyGoods(int userId) {
        try {
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "published");
            return goodsMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算用户闲置物品数量失败", e);
            return 0;
        }
    }

    @Override
    public List<GoodsVo> getGoodsListByUserId(int userId, int page, int size) {
        try {
            UserVo userVo = goodsMapper.getGoodsPublisher(userId);
            if (userVo == null) {
                return null;
            }
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "published");
            example.orderBy("publishTime").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            List<Goods> goodsList = goodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并查询图片
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);

                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                goodsVo.setPublisher(userVo);
                goodsVoList.add(goodsVo);
            }

            return goodsVoList;
        } catch (Exception e) {
            log.error("获取用户发布的闲置列表失败", e);
            return null;
        }
    }

    @Override
    public List<GoodsVo> getRemoveGoodsListByUserId(int userId, int page, int size) {
        try{
            UserVo userVo = goodsMapper.getGoodsPublisher(userId);
            if (userVo == null) {
                return null;
            }
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "removed");
            example.orderBy("publishTime").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            List<Goods> goodsList = goodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并查询图片
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);

                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                goodsVo.setPublisher(userVo);
                goodsVoList.add(goodsVo);
            }

            return goodsVoList;
        }catch (Exception e){
            log.error("获取用户下架的闲置列表失败", e);
            return null;
        }
    }

    @Override
    public int countUserGoods(int userId) {
        try {
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "published");
            return goodsMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算用户闲置物品数量失败", e);
            return 0;
        }
    }

    @Override
    public int countUserRemoveGoods(int userId) {
        try {
            Example example = new Example(Goods.class);
            example.createCriteria()
                    .andEqualTo("userId", userId)
                    .andEqualTo("status", "removed");
            return goodsMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算用户下架的闲置物品数量失败", e);
            return 0;
        }
    }

    @Override
    public GoodsVo getGoodsById(int goodsId) {
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            return null;
        }
        GoodsVo goodsVo = new GoodsVo();
        BeanUtils.copyProperties(goods, goodsVo);
        // 查询闲置物品图片
        Example imageExample = new Example(GoodsImage.class);
        imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
        List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
        List<String> imageUrls = images.stream()
                .map(GoodsImage::getImageUrl)
                .collect(Collectors.toList());
        goodsVo.setImageUrls(imageUrls);
        log.info("用户id {}:", goods.getUserId());
        UserVo userVo = goodsMapper.getGoodsPublisher(goods.getUserId());
        goodsVo.setPublisher(userVo);
        return goodsVo;
    }

    @Override
    public List<GoodsVo> searchGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType,
                                              Integer bottomPrice, Integer topPrice, int page, int size) {
        try {
            // 构建查询条件
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();

            // 添加闲置物品描述模糊查询条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                criteria.andLike("description", "%" + keyword + "%");
            }

            // 只查询已发布的闲置物品
            criteria.andEqualTo("status", "published");

            // 如果指定了分类，则按分类过滤
            if (categoryId != null) {
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格排序，则按价格排序过滤
            if (priceRankType != null) {
                if (priceRankType == 1){
                    example.orderBy("price");
                }else{
                    example.orderBy("price").desc();
                }
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格，则按价格过滤
            if (bottomPrice != null && topPrice != null) {
                criteria.andBetween("price", bottomPrice, topPrice);
            }

            // 按发布时间降序排序
            example.orderBy("publishTime").desc();

            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            // 执行查询
            List<Goods> goodsList = goodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换为VO对象并添加对应信息
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);

                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);

                // 设置分类名
                goodsVo.setCategoryName(categoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                // 设置发布者信息
                UserVo userVo = goodsMapper.getGoodsPublisher(goods.getUserId());
                goodsVo.setPublisher(userVo);

                goodsVoList.add(goodsVo);
            }
            return goodsVoList;
        } catch (Exception e) {
            log.error("根据闲置物品名称搜索闲置物品失败！", e);
            return new ArrayList<>();
        }
    }

    @Override
    public int countGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType, Integer bottomPrice, Integer topPrice) {
        try {
            // 构建查询条件
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();

            // 添加闲置物品描述模糊查询条件
            if (keyword != null && !keyword.trim().isEmpty()) {
                criteria.andLike("description", "%" + keyword + "%");
            }
            // 如果指定了分类，则按分类过滤
            if (categoryId != null) {
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格排序，则按价格排序过滤
            if (priceRankType != null) {
                if (priceRankType == 1){
                    example.orderBy("price");
                }else{
                    example.orderBy("price").desc();
                }
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格，则按价格过滤
            if (bottomPrice != null && topPrice != null) {
                criteria.andBetween("price", bottomPrice, topPrice);
            }
            // 只统计已发布的闲置物品
            criteria.andEqualTo("status", "published");

            // 执行查询并返回计数
            return goodsMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算符合条件的闲置物品数量失败", e);
            return 0;
        }
    }

    @Override
    public List<GoodsVo> getAllGoodsList(Integer categoryId, Integer bottomPrice, Integer topPrice, int page, int size) {
        try {
            // 构建查询条件
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();

            // 只查询已发布的闲置物品
            criteria.andEqualTo("status", "published");
            // 如果指定了分类，则按分类过滤
            if (categoryId != null) {
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格，则按价格过滤
            if (bottomPrice != null && topPrice != null) {
                criteria.andBetween("price", bottomPrice, topPrice);
            }
            // 按发布时间降序排序
            example.orderBy("publishTime").desc();
            // 计算分页参数
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);

            // 执行查询
            List<Goods> goodsList = goodsMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<GoodsVo> goodsVoList = new ArrayList<>();

            // 转换数据并添加关联信息
            for (Goods goods : goodsList) {
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(goods, goodsVo);

                // 查询闲置物品图片
                Example imageExample = new Example(GoodsImage.class);
                imageExample.createCriteria().andEqualTo("goodsId", goods.getGoodsId());
                List<GoodsImage> images = goodsImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(GoodsImage::getImageUrl)
                        .collect(Collectors.toList());
                goodsVo.setImageUrls(imageUrls);
                // 设置分类名
                goodsVo.setCategoryName(categoryMapper.selectByPrimaryKey(goods.getCategoryId()).getCategoryName());
                // 查询发布者信息
                UserVo userVo = goodsMapper.getGoodsPublisher(goods.getUserId());
                goodsVo.setPublisher(userVo);

                goodsVoList.add(goodsVo);
            }

            return goodsVoList;
        } catch (Exception e) {
            log.error("获取所有闲置物品列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public int countAllGoods(Integer categoryId, Integer bottomPrice, Integer topPrice) {
        try {
            // 构建查询条件
            Example example = new Example(Goods.class);
            Example.Criteria criteria = example.createCriteria();
            // 只统计已发布的闲置物品
            criteria.andEqualTo("status", "published");
            // 如果指定了分类，则按分类过滤
            if (categoryId != null) {
                criteria.andEqualTo("categoryId", categoryId);
            }
            // 如果指定了价格，则按价格过滤
            if (bottomPrice != null && topPrice != null) {
                criteria.andBetween("price", bottomPrice, topPrice);
            }
            // 执行查询并返回计数
            return goodsMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("计算闲置物品数量失败！", e);
            return -1;
        }
    }


    @Override
    public List<Category> getAllCategoryList() {
        return categoryMapper.selectAll();
    }

}
