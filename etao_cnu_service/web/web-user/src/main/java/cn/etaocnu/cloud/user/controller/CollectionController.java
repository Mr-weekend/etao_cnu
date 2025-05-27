package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.user.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "收藏服务接口")
@RestController
@RefreshScope
@RequestMapping("/collection")
public class CollectionController {
    @Resource
    private CollectionService collectionService;

    @Operation(summary = "添加收藏")
    @PostMapping("/add/{goodsId}")
    Result<Boolean> addCollection(@PathVariable int goodsId,
                                  @RequestHeader("token") String token){
        return collectionService.addCollection(token, goodsId);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/cancel/{goodsId}")
    Result<Boolean> cancelCollection(@PathVariable int goodsId,
                                     @RequestHeader("token") String token){
        return collectionService.cancelCollection(token, goodsId);
    }

    @Operation(summary = "批量取消收藏")
    @DeleteMapping("/batch/delete")
    Result<Boolean> batchCancelCollection(
            @RequestHeader("token") String token,
            @RequestParam("goodsIds") List<Integer> goodsIds){
        return collectionService.batchCancelCollection(token, goodsIds);
    }

    @Operation(summary = "清空收藏")
    @DeleteMapping("/clear")
    Result<Boolean> clearCollection(@RequestHeader("token") String token){
        return collectionService.clearCollection(token);
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping("/list")
    Result<Map<String, Object>> getCollectionList(@RequestHeader("token") String token,
                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size){
        return collectionService.getCollectionList(token, page, size);
    }

    @Operation(summary = "获取用户收藏数量")
    @GetMapping("/count/user")
    Result<Integer> countCollection(@RequestHeader("token") String token){
        return collectionService.countUserCollection(token);
    }

    @Operation(summary = "获取用户是否收藏闲置物品")
    @GetMapping("/isCollected/goods/{goodsId}/user")
    public Result<Map<String, Object>> isCollectionExist(@PathVariable int goodsId,
                                                         @RequestHeader("token") String token){
        return collectionService.isCollectionExist(token, goodsId);
    }
}
