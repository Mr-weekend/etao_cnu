package cn.etaocnu.cloud.system.client;

import cn.etaocnu.cloud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-system")
public interface SysOrderFeignClient {
    @Operation(summary = "获取订单列表")
    @GetMapping("/sysOrder/list")
    Result<Map<String, Object>> getAllOrderList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取订单总数")
    @GetMapping("/sysOrder/count")
    Result<Integer> countOrder();

    @Operation(summary = "通过闲置物品关键字查询订单信息")
    @GetMapping("/sysOrder/search/goods")
    Result<Map<String, Object>> searchOrderByGoodsKey(
            @RequestParam("goodsKeyword") String goodsKeyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "通过用户ID查询订单信息")
    @GetMapping("/sysOrder/search/user")
    Result<Map<String, Object>> searchOrderByUserId(
            @RequestParam("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);
}
