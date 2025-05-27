package cn.etaocnu.cloud.order.client;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "service-order")
public interface OrderFeignClient {

    @PostMapping("/order/create")
    Result<Boolean> createOrder(@RequestBody NewOrderVo newOrderVo);

    @GetMapping("/order/{orderId}")
    Result<OrderVo> getOrderInfo(@PathVariable int orderId);

    @GetMapping("/order/list/user/{userId}")
    Result<Map<String, Object>> getOrderList(
            @PathVariable("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户买到的订单列表")
    @GetMapping("/order/list/asBuyer/{userId}")
    Result<Map<String, Object>> getOrderListAsBuyer(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户卖出的订单列表")
    @GetMapping("/order/list/asSeller/{userId}")
    Result<Map<String, Object>> getOrderListAsSeller(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "关键字搜索用户买到的订单列表")
    @GetMapping("/order/search/asBuyer/{userId}")
    Result<Map<String, Object>> searchOrderAsBuyer(
            @RequestParam String keyword,
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "关键字搜索用户卖出的订单列表")
    @GetMapping("/order/search/asSeller/{userId}")
    Result<Map<String, Object>> searchOrderAsSeller(
            @RequestParam String keyword,
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户买到的待评价订单列表")
    @GetMapping("/order/unComment/asBuyer/{userId}")
    Result<Map<String, Object>> getUncommentOrderAsBuyer(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户卖出的待评价订单列表")
    @GetMapping("/order/unComment/asSeller/{userId}")
    Result<Map<String, Object>> getUncommentOrderAsSeller(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取用户买到的待评价订单数量")
    @GetMapping("/order/count/unComment/asBuyer/{userId}")
    Result<Integer> countUncommentOrderAsBuyer(@PathVariable int userId);

    @Operation(summary = "获取用户卖出的待评价订单数量")
    @GetMapping("/order/count/unComment/asSeller/{userId}")
    Result<Integer> countUncommentOrderAsSeller(@PathVariable int userId);

    @Operation(summary = "用户确认收货")
    @PostMapping("/order/completed/{orderId}/user/{userId}")
    Result<Boolean> completedOrder(@PathVariable int orderId,
                                          @PathVariable int userId);

    @Operation(summary = "用户取消订单")
    @PostMapping("/order/cancel/{orderId}/user/{userId}")
    Result<Boolean> cancelOrder(@PathVariable int orderId,
                                       @PathVariable int userId);

    @Operation(summary = "用户删除订单")
    @DeleteMapping("/order/{orderId}/user/{userId}")
    Result<Boolean> deleteOrder(@PathVariable int orderId,
                                       @PathVariable int userId);

    @Operation(summary = "获取用户待评价订单数量")
    @GetMapping("/order/count/unComment/{userId}")
    Result<Integer> countUncommentOrder(@PathVariable int userId);

    @Operation(summary = "获取用户买到的订单数量")
    @GetMapping("/order/count/asBuyer/{userId}")
    Result<Integer> countOrderAsBuyer(@PathVariable int userId);

    @Operation(summary = "获取用户卖出的订单数量")
    @GetMapping("/order/count/asSeller/{userId}")
    Result<Integer> countOrderAsSeller(@PathVariable int userId);

    @GetMapping("/order/count")
    Result<Integer> countOrder(@RequestParam("userId") int userId);
}