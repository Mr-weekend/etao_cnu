package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.user.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "闲置物品服务接口")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    
    @Resource
    private GoodsService goodsService;

    @Operation(summary = "发布闲置")
    @PostMapping(value = "/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> publishGoods(@RequestHeader("token") String token,
                                        @RequestPart("goodsInfo") String goodsInfo,
                                        @RequestParam(value = "images", required = false) MultipartFile[] images) {
        log.info("发布闲置，token: {}, 文件数量={}, 闲置物品信息={}",
                token, images != null ? images.length : 0, goodsInfo);
        if (images != null) {
            for (MultipartFile file : images) {
                log.info("file: {}", file.getOriginalFilename());
            }
        }

        return goodsService.publishGoods(token, goodsInfo, images);
    }


    @Operation(summary = "更新闲置物品信息")
    @PostMapping(value = "/{goodsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> updateGoods(@PathVariable int goodsId,
                                       @RequestHeader("token") String token,
                                       @RequestPart("goodsInfo") String goodsInfo,
                                       @RequestParam(value = "imageUpdateType", defaultValue = "keep") String imageUpdateType,
                                       @RequestParam(value = "deleteImageUrls", required = false) List<String> deleteImageUrls,
                                       @RequestPart(value = "images", required = false) MultipartFile[] images) {
        log.info("更新闲置物品信息，token: {}, imageUpdateType: {}, 要删除的图片数量: {}, 新增图片数量: {}",
                token, imageUpdateType,
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

        return goodsService.updateGoods(token, goodsId, goodsInfo, images, imageUpdateType, deleteImageUrls);
    }

    @Operation(summary = "下架闲置")
    @PostMapping("/remove/{goodsId}")
    public Result<Boolean> removeGoods(@PathVariable int goodsId, @RequestHeader("token") String token) {
        return goodsService.removeGoods(token, goodsId);
    }

    @Operation(summary = "下架闲置")
    @PostMapping("/put/{goodsId}")
    public Result<Boolean> putGoods(@PathVariable int goodsId, @RequestHeader("token") String token) {
        return goodsService.putGoods(token, goodsId);
    }

    @Operation(summary = "删除闲置")
    @DeleteMapping("/{goodsId}")
    public Result<Boolean> deleteGoods(@PathVariable int goodsId, @RequestHeader("token") String token) {
        return goodsService.deleteGoods(token, goodsId);
    }

    @Operation(summary = "卖出闲置")
    @PostMapping("/sold/{goodsId}")
    public Result<Boolean> soldGoods(@PathVariable int goodsId) {
        return goodsService.soldGoods(goodsId);
    }

    @Operation(summary = "获取用户自己发布的闲置物品列表")
    @GetMapping("/my/list")
    public Result<Map<String, Object>> getMyGoodsList(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户自己发布的闲置物品列表，page: {}, size: {}", page, size);
        return goodsService.getMyGoodsList(token, page, size);
    }

    @Operation(summary = "获取指定用户的闲置列表")
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserGoodsList(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取指定用户的闲置列表，userId: {}, page: {}, size: {}", userId, page, size);
        return goodsService.getGoodsListByUserId(userId, page, size);
    }

    @Operation(summary = "获取用户下架的闲置物品列表")
    @GetMapping("/removeList")
    public Result<Map<String, Object>> getRemoveGoodsListByUserId(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        log.info("获取指定用户的闲置列表，token: {}, page: {}, size: {}", token, page, size);
        return goodsService.getRemoveGoodsListByUserId(token, page, size);
    }

    @Operation(summary = "获取闲置物品详细信息")
    @GetMapping("/{goodsId}")
    public Result<GoodsVo> getGoodsById(@PathVariable int goodsId) {
        return goodsService.getGoodsById(goodsId);
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
        return goodsService.searchGoodsByKeyword(keyword, categoryId, priceRankType, bottomPrice, topPrice, page, size);
    }

    @Operation(summary = "获取所有闲置物品列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "bottomPrice", required = false) Integer bottomPrice,
            @RequestParam(value = "topPrice", required = false) Integer topPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return goodsService.getAllGoodsList(categoryId, bottomPrice, topPrice, page, size);
    }

    @Operation(summary = "获取指定用户发布的闲置物品数量")
    @GetMapping("/count/user/{userId}")
    Result<Integer> countUserGoods(@PathVariable int userId){
        return goodsService.countUserGoods(userId);
    }

    @Operation(summary = "获取指定用户下架的闲置物品数量")
    @GetMapping("/count/remove")
    public Result<Integer> countUserRemoveGoods(@RequestHeader("token") String token) {
        return goodsService.countUserRemoveGoods(token);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/category/list")
    Result<Map<String, Object>> getAllCategoryList(){
        return goodsService.getAllCategoryList();
    }

}

