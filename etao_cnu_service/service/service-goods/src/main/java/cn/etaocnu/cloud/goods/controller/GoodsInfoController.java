package cn.etaocnu.cloud.goods.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.goods.service.GoodsInfoService;
import cn.etaocnu.cloud.model.entity.Category;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "闲置物品服务接口")
@RestController
@RefreshScope
@RequestMapping("/goods")
public class GoodsInfoController {
    
    @Resource
    private GoodsInfoService goodsInfoService;

    @Operation(summary = "发布闲置物品")
    @PostMapping(value = "/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean>  publishGoods(@RequestParam("userId") int userId,
                                         @RequestPart("goodsInfo") String goodsInfoJson,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images){
        log.info("发布闲置: 图片数量={}, 闲置信息JSON={}",
                images != null ? images.length : 0, goodsInfoJson);
        if (images != null) {
            for (MultipartFile file : images) {
                log.info("接收到文件: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            }
        }
        Boolean result = goodsInfoService.publishGoods(userId, goodsInfoJson, images);
        return result ? Result.success("发布闲置成功！",true) : Result.fail("发布闲置失败！",false);
    }

    @Operation(summary = "修改闲置物品信息")
    @PostMapping(value = "/{goodsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> updateGoods(@PathVariable int goodsId,
                                       @RequestParam("userId") int userId,
                                       @RequestPart("goodsInfo") String goodsInfoJson,
                                       @RequestParam(value = "imageUpdateType", defaultValue = "keep") String imageUpdateType,
                                       @RequestParam(value = "deleteImageUrls", required = false) List<String> deleteImageUrls,
                                       @RequestPart(value = "images", required = false) MultipartFile[] images) {
        log.info("更新闲置物品信息: goodsId={}, userId={}, imageUpdateType={}, 要删除的图片数量={}, 新增图片数量={}", 
                goodsId, userId, imageUpdateType, 
                deleteImageUrls != null ? deleteImageUrls.size() : 0,
                images != null ? images.length : 0);
        
        if (images != null) {
            for (MultipartFile file : images) {
                log.info("接收到文件: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            }
        }

        if (deleteImageUrls != null && !deleteImageUrls.isEmpty()) {
            log.info("要删除的图片URL: {}", deleteImageUrls);
        }

        Boolean result = goodsInfoService.updateGoods(goodsId, userId, goodsInfoJson, images, imageUpdateType, deleteImageUrls);
        return result ? Result.success("修改闲置物品信息成功！", true): Result.fail("修改闲置物品信息失败！",false);
    }

    @Operation(summary = "下架闲置")
    @PostMapping("/remove/{goodsId}")
    public Result<Boolean> removeGoods(@PathVariable int goodsId, @RequestParam("userId") int userId) {
        log.info("下架闲置，userId: {}, goodsId: {}", userId, goodsId);
        Boolean result = goodsInfoService.removeGoods(goodsId, userId);
        return result ? Result.success("下架闲置物品成功！", true): Result.fail("下架闲置物品失败！", false);
    }

    @Operation(summary = "重新上架闲置")
    @PostMapping("/put/{goodsId}")
    public Result<Boolean> putGoods(@PathVariable int goodsId, @RequestParam("userId") int userId) {
        log.info("重新上架闲置，userId: {}, goodsId: {}", userId, goodsId);
        Boolean result = goodsInfoService.putGoods(goodsId, userId);
        return result ? Result.success("重新上架闲置物品成功！", true): Result.fail("重新上架闲置物品失败！", false);
    }

    @Operation(summary = "删除闲置")
    @DeleteMapping("/{goodsId}")
    public Result<Boolean> deleteGoods(@PathVariable int goodsId, @RequestParam("userId") int userId) {
        log.info("删除闲置，userId: {}, goodsId: {}", userId, goodsId);
        Boolean result = goodsInfoService.deleteGoods(goodsId, userId);
        return result ? Result.success("删除闲置物品成功！", true): Result.fail("删除闲置物品失败！", false);
    }

    @Operation(summary = "卖出闲置")
    @PostMapping("/sold/{goodsId}")
    public Result<Boolean> soldGoods(@PathVariable int goodsId) {
        log.info("卖出闲置，goodsId: {}",goodsId);
        Boolean result = goodsInfoService.soldGoods(goodsId);
        return result ? Result.success("卖出闲置物品成功！", true) : Result.fail("卖出闲置物品失败！", false);
    }

    @Operation(summary = "获取用户自己的闲置物品列表")
    @GetMapping("/my/list")
    public Result<Map<String, Object>> getMyGoodsList(
            @RequestParam("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户自己的闲置物品列表，userId: {}, page: {}, size: {}", userId, page, size);
        
        // 查询数据
        List<GoodsVo> goodsList = goodsInfoService.getMyGoodsList(userId, page, size);
        if (goodsList != null && !goodsList.isEmpty()) {
            // 查询总数
            int total = goodsInfoService.countMyGoods(userId);
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", goodsList);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (total + size - 1) / size); // 计算总页数
            return Result.success("获取用户闲置物品列表成功！", result);
        }
        return Result.fail("获取用户闲置物品列表失败！");
    }

    @Operation(summary = "获取指定用户发布的闲置物品列表")
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getGoodsListByUserId(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取指定用户发布的闲置物品列表，userId: {}, page: {}, size: {}", userId, page, size);
        
        // 查询数据
        List<GoodsVo> goodsList = goodsInfoService.getGoodsListByUserId(userId, page, size);
        // 查询总数
        int total = goodsInfoService.countUserGoods(userId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取指定用户发布的闲置物品列表成功！",result);
    }

    @Operation(summary = "获取用户下架的闲置物品列表")
    @GetMapping("/removeList/user/{userId}")
    public Result<Map<String, Object>> getRemoveGoodsListByUserId(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户下架的闲置物品列表，userId: {}, page: {}, size: {}", userId, page, size);

        // 查询数据
        List<GoodsVo> goodsList = goodsInfoService.getRemoveGoodsListByUserId(userId, page, size);
        // 查询总数
        int total = goodsInfoService.countUserRemoveGoods(userId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取用户下架的闲置物品列表成功！",result);
    }

    @Operation(summary = "通过闲置物品id获取闲置物品详细信息")
    @GetMapping("/{goodsId}")
    public Result<GoodsVo> getGoodsById(@PathVariable int goodsId) {
        log.info("获取闲置物品详细信息，goodsId: {}", goodsId);
        GoodsVo goodsVo = goodsInfoService.getGoodsById(goodsId);
        return goodsVo != null ? Result.success("获取闲置物品详细信息成功！", goodsVo) : Result.fail("获取闲置物品详细信息失败！", null);
    }

    @Operation(summary = "根据闲置物品关键字搜索闲置物品")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchGoodsByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "priceRankType", required = false) Integer priceRankType,
            @RequestParam(value = "bottomPrice", required = false) Integer bottomPrice,
            @RequestParam(value = "topPrice", required = false) Integer topPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("根据关键字搜索闲置物品，keyword: {}, page: {}, size: {}", keyword, page, size);
        
        // 查询数据
        List<GoodsVo> goodsList = goodsInfoService.searchGoodsByKeyword(keyword, categoryId, priceRankType, bottomPrice, topPrice, page,size);
        // 查询总数
        int total = goodsInfoService.countGoodsByKeyword(keyword, categoryId, priceRankType, bottomPrice, topPrice);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("根据关键字搜索闲置物品成功！", result);
    }

    @Operation(summary = "获取所有已发布的闲置物品列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "bottomPrice", required = false) Integer bottomPrice,
            @RequestParam(value = "topPrice", required = false) Integer topPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取所有已发布的闲置物品列表，categoryId: {}, page: {}, size: {}", categoryId, page, size);
        // 查询数据
        List<GoodsVo> goodsList = goodsInfoService.getAllGoodsList(categoryId, bottomPrice, topPrice, page, size);
        // 查询总数
        int total = goodsInfoService.countAllGoods(categoryId, bottomPrice, topPrice);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取所有已发布的闲置物品列表成功！", result);
    }

    @Operation(summary = "获取指定用户发布的闲置物品数量")
    @GetMapping("/count/user/{userId}")
    public Result<Integer> countUserGoods(@PathVariable int userId) {
        return Result.success("获取指定用户发布的闲置物品数量成功！", goodsInfoService.countUserGoods(userId));
    }

    @Operation(summary = "获取指定用户下架的闲置物品数量")
    @GetMapping("/count/remove/user/{userId}")
    public Result<Integer> countUserRemoveGoods(@PathVariable int userId) {
        return Result.success("获取指定用户下架的闲置物品数量成功！", goodsInfoService.countUserRemoveGoods(userId));
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    public Result<Map<String, Object>> getAllCategoryList(){
        log.info("获取分类列表");
        List<Category> categoryList = goodsInfoService.getAllCategoryList();
        int total = categoryList.size();
        Map<String, Object> result = new HashMap<>();
        result.put("list", categoryList);
        result.put("total", total);
        return Result.success("获取分类列表成功！", result);
    }

}
