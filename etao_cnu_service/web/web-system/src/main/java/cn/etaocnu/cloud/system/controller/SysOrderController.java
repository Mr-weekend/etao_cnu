package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "订单接口")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/sysOrder")
public class SysOrderController {

    @Resource
    private SysOrderService sysOrderService;

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    Result<Map<String, Object>> getAllOrderList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size){
        return sysOrderService.getAllOrderList(page, size);
    }

    @Operation(summary = "获取订单总数")
    @GetMapping("/count")
    public Result<Integer> countOrder(){
        return sysOrderService.countOrder();
    }

    @Operation(summary = "通过闲置物品关键字查询订单信息")
    @GetMapping("/search/goods")
    public Result<Map<String, Object>> searchOrderByGoodsKey(@RequestParam("goodsKeyword") String goodsKeyword,
                                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                                             @RequestParam(value = "size", defaultValue = "10") int size){
        return sysOrderService.searchOrderByGoodsKey(goodsKeyword, page, size);
    }

    @Operation(summary = "通过用户ID查询订单信息")
    @GetMapping("/search/user")
    public Result<Map<String, Object>> searchOrderByUserId(@RequestParam("userId") int userId,
                                                           @RequestParam(value = "page", defaultValue = "1") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size){
        return sysOrderService.searchOrderByUserId(userId, page, size);
    }
}
