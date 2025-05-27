package cn.etaocnu.cloud.goods.client;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.goods.client.config.FeignMultipartConfig;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-goods", configuration = FeignMultipartConfig.class)
public interface GoodsFeignClient {

    @Operation(summary = "发布闲置物品")
    @PostMapping(value = "/goods/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> publishGoods(@RequestParam("userId") int userId,
                                 @RequestPart("goodsInfo") String goodsInfoJson,
                                 @RequestPart(value = "images", required = false) MultipartFile[] images);

    @PostMapping("/goods/draft")
    Result<Boolean> saveGoodsDraft(@RequestParam("userId") int userId, @RequestBody GoodsCUVo goodsCUVo);

    @Operation(summary = "更新闲置物品信息")
    @PostMapping(value = "/goods/{goodsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<Boolean> updateGoods(@PathVariable int goodsId,
                                @RequestParam("userId") int userId,
                                @RequestPart("goodsInfo") String goodsInfoJson,
                                @RequestParam(value = "imageUpdateType", defaultValue = "keep") String imageUpdateType,
                                @RequestParam(value = "deleteImageUrls", required = false) List<String> deleteImageUrls,
                                @RequestPart(value = "images", required = false) MultipartFile[] images);

    @Operation(summary = "下架闲置")
    @PostMapping("/goods/remove/{goodsId}")
    Result<Boolean> removeGoods(@PathVariable("goodsId") int goodsId, @RequestParam("userId") int userId);

    @Operation(summary = "重新上架闲置")
    @PostMapping("/goods/put/{goodsId}")
    Result<Boolean> putGoods(@PathVariable int goodsId, @RequestParam("userId") int userId);

    @Operation(summary = "删除闲置")
    @DeleteMapping("/goods/{goodsId}")
    Result<Boolean> deleteGoods(@PathVariable("goodsId") int goodsId, @RequestParam("userId") int userId);

    @Operation(summary = "卖出闲置")
    @PostMapping("/goods/sold/{goodsId}")
    Result<Boolean> soldGoods(@PathVariable int goodsId);

    @Operation(summary = "获取用户自己的闲置物品列表")
    @GetMapping("/goods/my/list")
    Result<Map<String, Object>> getMyGoodsList(
            @RequestParam("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取指定用户发布的闲置物品列表")
    @GetMapping("/goods/user/{userId}")
    Result<Map<String, Object>> getGoodsListByUserId(
            @PathVariable("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户下架的闲置物品列表")
    @GetMapping("/goods/removeList/user/{userId}")
    Result<Map<String, Object>> getRemoveGoodsListByUserId(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "通过闲置物品id获取闲置物品详细信息")
    @GetMapping("/goods/{goodsId}")
    Result<GoodsVo> getGoodsById(@PathVariable("goodsId") int goodsId);

    @Operation(summary = "根据闲置物品关键字搜索闲置物品")
    @GetMapping("/goods/search")
    Result<Map<String, Object>> searchGoodsByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "priceRankType", required = false) Integer priceRankType,
            @RequestParam(value = "bottomPrice", required = false) Integer bottomPrice,
            @RequestParam(value = "topPrice", required = false) Integer topPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取所有已发布的闲置物品列表")
    @GetMapping("/goods/list")
    Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "bottomPrice", required = false) Integer bottomPrice,
            @RequestParam(value = "topPrice", required = false) Integer topPrice,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取指定用户发布的闲置物品数量")
    @GetMapping("/goods/count/user/{userId}")
    Result<Integer> countUserGoods(@PathVariable int userId);

    @Operation(summary = "获取指定用户下架的闲置物品数量")
    @GetMapping("/goods/count/remove/user/{userId}")
    public Result<Integer> countUserRemoveGoods(@PathVariable int userId);

    @Operation(summary = "获取分类列表")
    @GetMapping("/goods/category/list")
    Result<Map<String, Object>> getAllCategoryList();

}
