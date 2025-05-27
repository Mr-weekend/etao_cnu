package cn.etaocnu.cloud;

import cn.etaocnu.cloud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-collection")
public interface CollectionFeignClient {
    @Operation(summary = "添加收藏")
    @PostMapping("/collection/add/{goodsId}")
    Result<Boolean> addCollection(@PathVariable int goodsId,
                                  @RequestParam("userId") int userId);

    @Operation(summary = "取消收藏")
    @DeleteMapping("/collection/cancel/{goodsId}")
    Result<Boolean> cancelCollection(@PathVariable int goodsId,
                                     @RequestParam("userId") int userId);

    @Operation(summary = "批量取消收藏")
    @DeleteMapping("/collection/batch/delete")
    Result<Boolean> batchCancelCollection(
            @RequestParam("userId") int userId,
            @RequestParam("goodsIds") List<Integer> goodsIds);

    @Operation(summary = "清空收藏")
    @DeleteMapping("/collection/clear")
    Result<Boolean> clearCollection(@RequestParam("userId") int userId);

    @Operation(summary = "获取收藏列表")
    @GetMapping("/collection/list/{userId}")
    Result<Map<String, Object>> getCollectionList(@PathVariable("userId") int userId,
                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户收藏数量")
    @GetMapping("/collection/count/user/{userId}")
    Result<Integer> countUserCollection(@PathVariable int userId);

    @Operation(summary = "获取用户是否收藏闲置物品")
    @GetMapping("/collection/isCollected/goods/{goodsId}/user/{userId}")
    public Result<Map<String, Object>> isCollectionExist(@PathVariable int goodsId,
                                                         @PathVariable int userId);
}

