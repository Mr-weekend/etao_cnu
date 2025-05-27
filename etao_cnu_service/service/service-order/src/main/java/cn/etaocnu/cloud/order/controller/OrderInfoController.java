package cn.etaocnu.cloud.order.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.NewOrderVo;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.order.service.OrderInfoService;
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
@Tag(name = "订单服务接口")
@RestController
@RefreshScope
@RequestMapping("/order")
public class OrderInfoController {
    
    @Resource
    private OrderInfoService orderInfoService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Boolean> addOrder(@RequestBody NewOrderVo newOrderVo) {
        log.info("创建订单，order: {}", newOrderVo);
        Boolean result = orderInfoService.createOrder(newOrderVo);
        return result ? Result.success("创建订单成功！", true) : Result.fail("创建订单失败！");
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVo> getOrderInfo(@PathVariable int orderId) {
        log.info("获取订单详情，orderId: {}", orderId);
        OrderVo orderVo = orderInfoService.getOrderInfoById(orderId);
        return orderVo != null ? Result.success("获取订单详情成功！", orderVo) : Result.fail("获取订单详情失败！");
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/list/user/{userId}")
    public Result<Map<String, Object>> getOrderList(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.getOrderListByUserId(userId);
        if (orderList != null && !orderList.isEmpty()) {
            int total = orderInfoService.countOrder(userId);
            log.info("获取到{}条订单", total);
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", orderList);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (total + size - 1) / size); // 计算总页数
            return Result.success("获取订单列表成功！",result);
        }
        return Result.fail("获取订单列表失败！");
    }

    @Operation(summary = "获取用户买到的订单列表")
    @GetMapping("/list/asBuyer/{userId}")
    public Result<Map<String, Object>> getOrderListAsBuyer(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户买到的订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.getOrderListAsBuyer(userId, page, size);
        int total = orderInfoService.countOrderAsBuyer(userId);

        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取用户买到的订单列表成功！",result);
    }

    @Operation(summary = "获取用户卖出的订单列表")
    @GetMapping("/list/asSeller/{userId}")
    public Result<Map<String, Object>> getOrderListAsSeller(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户卖出的订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.getOrderListAsSeller(userId, page, size);
        int total = orderInfoService.countOrderAsSeller(userId);
        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取用户卖出的订单列表成功！",result);
    }

    @Operation(summary = "关键字搜索用户买到的订单列表")
    @GetMapping("/search/asBuyer/{userId}")
    public Result<Map<String, Object>> searchOrderAsBuyer(
            @RequestParam String keyword,
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("关键字搜索用户买到的订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.searchOrderAsBuyer(keyword, userId, page, size);
        int total = orderInfoService.countSearchOrderAsBuyer(keyword, userId);
        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("关键字搜索用户买到的订单列表成功！",result);
    }

    @Operation(summary = "关键字搜索用户卖出的订单列表")
    @GetMapping("/search/asSeller/{userId}")
    public Result<Map<String, Object>> searchOrderAsSeller(
            @RequestParam String keyword,
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("关键字搜索用户卖出的订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.searchOrderAsSeller(keyword, userId, page, size);
        int total = orderInfoService.countSearchOrderAsSeller(keyword, userId);
        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("关键字搜索用户卖出的订单列表成功！",result);
    }

    @Operation(summary = "获取用户买到的待评价订单列表")
    @GetMapping("/unComment/asBuyer/{userId}")
    public Result<Map<String, Object>> getUncommentOrderAsBuyer(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户买到的待评价订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.getUncommentOrderAsBuyer(userId, page, size);
        int total = orderInfoService.countUncommentOrderAsBuyer(userId);
        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取用户买到的待评价订单列表成功！",result);
    }

    @Operation(summary = "获取用户卖出的待评价订单列表")
    @GetMapping("/unComment/asSeller/{userId}")
    public Result<Map<String, Object>> getUncommentOrderAsSeller(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户卖出的待评价订单列表，userId: {}, page={}, size={}", userId, page, size);
        List<OrderVo> orderList = orderInfoService.getUncommentOrderAsSeller(userId, page, size);
        int total = orderInfoService.countUncommentOrderAsSeller(userId);
        log.info("获取到{}条订单", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取用户卖出的待评价订单列表成功！",result);
    }

    @Operation(summary = "用户确认收货")
    @PostMapping("/completed/{orderId}/user/{userId}")
    public Result<Boolean> completedOrder(@PathVariable int orderId,
                                          @PathVariable int userId) {
        Boolean result = orderInfoService.completedOrder(userId, orderId);
        return result ? Result.success("确认收货成功！", true) : Result.fail("确认收货失败！", true);
    }

    @Operation(summary = "用户取消订单")
    @PostMapping("/cancel/{orderId}/user/{userId}")
    public Result<Boolean> cancelOrder(@PathVariable int orderId,
                                          @PathVariable int userId) {
        Boolean result = orderInfoService.cancelOrder(userId, orderId);
        return result ? Result.success("取消订单成功！", true) : Result.fail("取消订单失败！", true);
    }

    @Operation(summary = "用户删除订单")
    @DeleteMapping("/{orderId}/user/{userId}")
    public Result<Boolean> deleteOrder(@PathVariable int orderId,
                                       @PathVariable int userId) {
        Boolean result = orderInfoService.deleteOrder(userId, orderId);
        return result ? Result.success("删除订单成功！", true) : Result.fail("删除订单失败！", true);
    }

    @Operation(summary = "获取用户买到的待评价订单数量")
    @GetMapping("/count/unComment/asBuyer/{userId}")
    public Result<Integer> countUncommentOrderAsBuyer(@PathVariable int userId) {
        log.info("统计用户买到的待评价订单数量: userId={}", userId);
        int count = orderInfoService.countUncommentOrderAsBuyer(userId);
        return Result.success("获取用户买到的待评价订单数量成功！",count);
    }

    @Operation(summary = "获取用户卖出的待评价订单数量")
    @GetMapping("/count/unComment/asSeller/{userId}")
    public Result<Integer> countUncommentOrderAsSeller(@PathVariable int userId) {
        log.info("统计用户卖出的待评价订单数量: userId={}", userId);
        int count = orderInfoService.countUncommentOrderAsSeller(userId);
        return Result.success("获取用户卖出的待评价订单数量成功！",count);
    }

    @Operation(summary = "获取用户买到的订单数量")
    @GetMapping("/count/asBuyer/{userId}")
    public Result<Integer> countOrderAsBuyer(@PathVariable int userId) {
        log.info("统计用户买到的订单数量: userId={}", userId);
        int count = orderInfoService.countOrderAsBuyer(userId);
        return Result.success("获取用户买到的订单数量成功！",count);
    }

    @Operation(summary = "获取用户卖出的订单数量")
    @GetMapping("/count/asSeller/{userId}")
    public Result<Integer> countOrderAsSeller(@PathVariable int userId) {
        log.info("统计用户卖出的订单数量: userId={}", userId);
        int count = orderInfoService.countOrderAsSeller(userId);
        return Result.success("获取用户卖出的订单数量成功！",count);
    }

    @Operation(summary = "获取用户待评价订单数量")
    @GetMapping("/count/unComment/{userId}")
    public Result<Integer> countUncommentOrder(@PathVariable int userId) {
        log.info("统计用户待评价订单数量: userId={}", userId);
        int count = orderInfoService.countUncommentOrder(userId);
        return Result.success("获取用户待评价订单数量成功！",count);
    }

    @Operation(summary = "获取订单数量")
    @GetMapping("/count")
    public Result<Integer> countOrder(@RequestParam("userId") int userId) {
        log.info("统计用户订单数量: userId={}", userId);
        int count = orderInfoService.countOrder(userId);
        return Result.success("获取用户订单数量成功！",count);
    }
}
