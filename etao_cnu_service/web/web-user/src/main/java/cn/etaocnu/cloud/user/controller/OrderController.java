package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.user.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "订单管理接口")
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Resource
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Boolean> createOrder(@RequestHeader("token") String token,
                                       @RequestBody NewOrderVo newOrderVo) {
        return orderService.createOrder(token, newOrderVo);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVo> getOrderInfoById(@RequestHeader("token") String token,
                                            @PathVariable int orderId) {
        return orderService.getOrderInfoById(token, orderId);
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list/user")
    public Result<Map<String, Object>> getOrderListByUserId(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return orderService.getOrderListByUserId(token, page, size);
    }

    @Operation(summary = "获取用户买到的订单列表")
    @GetMapping("/list/asBuyer")
    Result<Map<String, Object>> getOrderListAsBuyer(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.getOrderListAsBuyer(token, page, size);
    }

    @Operation(summary = "获取用户卖出的订单列表")
    @GetMapping("/list/asSeller")
    Result<Map<String, Object>> getOrderListAsSeller(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.getOrderListAsSeller(token, page, size);
    }

    @Operation(summary = "关键字搜索用户买到的订单列表")
    @GetMapping("/search/asBuyer")
    Result<Map<String, Object>> searchOrderAsBuyer(
            @RequestParam String keyword,
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.searchOrderAsBuyer(keyword, token, page, size);
    }

    @Operation(summary = "关键字搜索用户卖出的订单列表")
    @GetMapping("/search/asSeller")
    Result<Map<String, Object>> searchOrderAsSeller(
            @RequestParam String keyword,
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.searchOrderAsSeller(keyword, token, page, size);
    }

    @Operation(summary = "获取用户买到的待评价订单列表")
    @GetMapping("/unComment/asBuyer")
    Result<Map<String, Object>> getUncommentOrderAsBuyer(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.getUncommentOrderAsBuyer(token, page, size);
    }

    @Operation(summary = "获取用户卖出的待评价订单列表")
    @GetMapping("/unComment/asSeller")
    Result<Map<String, Object>> getUncommentOrderAsSeller(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return orderService.getUncommentOrderAsSeller(token, page, size);
    }

    @Operation(summary = "用户确认收货")
    @PostMapping("/completed/{orderId}")
    public Result<Boolean> completedOrder(@RequestHeader("token") String token,
                                          @PathVariable int orderId) {
        return orderService.completedOrder(token, orderId);
    }

    @Operation(summary = "用户取消订单")
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancelOrder(@RequestHeader("token") String token,
                                       @PathVariable int orderId) {
        return orderService.cancelOrder(token, orderId);
    }

    @Operation(summary = "用户删除订单")
    @DeleteMapping("/{orderId}")
    public Result<Boolean> deleteOrder(@RequestHeader("token") String token,
                                       @PathVariable int orderId) {
        return orderService.deleteOrder(token, orderId);
    }

    @Operation(summary = "获取用户买到的待评价订单数量")
    @GetMapping("/count/unComment/asBuyer")
    public Result<Integer> countUncommentOrderAsBuyer(@RequestHeader("token") String token) {
        return orderService.countUncommentOrderAsBuyer(token);
    }

    @Operation(summary = "获取用户卖出的待评价订单数量")
    @GetMapping("/count/unComment/asSeller")
    public Result<Integer> countUncommentOrderAsSeller(@RequestHeader("token") String token) {
        return orderService.countUncommentOrderAsSeller(token);
    }

    @Operation(summary = "获取用户买到的订单数量")
    @GetMapping("/count/asBuyer")
    Result<Integer> countOrderAsBuyer(@RequestHeader("token") String token){
        return orderService.countOrderAsBuyer(token);
    }

    @Operation(summary = "获取用户卖出的订单数量")
    @GetMapping("/count/asSeller")
    Result<Integer> countOrderAsSeller(@RequestHeader("token") String token){
        return orderService.countOrderAsSeller(token);
    }

    @Operation(summary = "获取用户待评价订单数量")
    @GetMapping("/count/unComment")
    public Result<Integer> countUncommentOrder(@RequestHeader("token") String token) {
        return orderService.countUncommentOrder(token);
    }

    @Operation(summary = "获取订单数量")
    @GetMapping("/count")
    public Result<Integer> countOrder(@RequestHeader("token") String token) {
        return orderService.countOrder(token);
    }
} 