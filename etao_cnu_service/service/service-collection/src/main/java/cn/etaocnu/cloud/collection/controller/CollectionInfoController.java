package cn.etaocnu.cloud.collection.controller;

import cn.etaocnu.cloud.collection.service.CollectionInfoService;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CollectionVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "收藏服务接口")
@RestController
@RefreshScope
@RequestMapping("/collection")
public class CollectionInfoController {
    
    @Resource
    private CollectionInfoService collectionInfoService;

    @Operation(summary = "添加收藏")
    @PostMapping("/add/{goodsId}")
    public Result<Boolean> addCollection(@PathVariable int goodsId,
                                         @RequestParam("userId") int userId) {
        log.info("添加收藏，userId: {}, goodsId: {}", userId, goodsId);
        Boolean result = collectionInfoService.addCollection(userId, goodsId);
        return result ? Result.success("添加收藏成功！", true): Result.fail("添加收藏失败！", false);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/cancel/{goodsId}")
    public Result<Boolean> cancelCollection(@PathVariable int goodsId,
                                            @RequestParam("userId") int userId) {
        log.info("取消收藏，userId: {}, goodsId: {}", userId, goodsId);
        Boolean result = collectionInfoService.cancelCollection(userId, goodsId);
        return result ? Result.success("取消收藏成功！", result) : Result.fail("取消收藏失败！", false);
    }

    @Operation(summary = "批量取消收藏")
    @DeleteMapping("/batch/delete")
    public Result<Boolean> batchCancelCollection(
            @RequestParam("userId") int userId,
            @RequestParam("goodsIds") List<Integer> goodsIds) {
        log.info("批量取消收藏: userId={}, goodsIds={}", userId, goodsIds);
        Boolean result = collectionInfoService.batchCancelCollection(userId, goodsIds);
        return result ? Result.success("批量取消收藏成功！",true) : Result.fail("批量取消收藏失败！", false);
    }

    @Operation(summary = "清空收藏")
    @DeleteMapping("/clear")
    public Result<Boolean> clearCollection(@RequestParam("userId") int userId) {
        log.info("清空收藏: userId={}", userId);
        Boolean result = collectionInfoService.clearCollection(userId);
        return result ? Result.success("清空收藏成功！", true) : Result.fail("清空收藏失败！", false);
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping("/list/{userId}")
    public Result<Map<String, Object>> getCollectionList(@PathVariable("userId") int userId,
                                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取收藏列表，userId: {}", userId);
        List<CollectionVo> collectionList = collectionInfoService.getCollectionList(userId, page, size);
        // 查询总数
        int total = collectionInfoService.countUserCollection(userId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", collectionList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数

        return Result.success("获取收藏列表成功！", result);
    }

    @Operation(summary = "获取用户收藏数量")
    @GetMapping("/count/user/{userId}")
    public Result<Integer> countUserCollection(@PathVariable int userId) {
        log.info("获取用户收藏数量: userId={}", userId);
        int count = collectionInfoService.countUserCollection(userId);
        return count!= -1 ? Result.success("获取用户收藏数量成功！", count) : Result.fail("获取用户收藏数量失败！", -1);
    }

    @Operation(summary = "获取用户是否收藏闲置物品")
    @GetMapping("/isCollected/goods/{goodsId}/user/{userId}")
    public Result<Map<String, Object>> isCollectionExist(@PathVariable int goodsId,
                                                @PathVariable int userId) {
        log.info("获取用户是否收藏闲置物品: goodsId={}, userId = {}", goodsId, userId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        int isCollectResult = collectionInfoService.isCollectionExist(goodsId, userId);
        if (isCollectResult != -1) {
            if (isCollectResult == 1) {
                result.put("isCollected", true);
            }else{
                result.put("isCollected", false);
            }
            return Result.success("获取用户是否收藏闲置成功！", result);
        }else{
            result.put("isCollected", "error");
            return Result.fail("获取用户是否收藏闲置失败！",result);
        }
    }

}
