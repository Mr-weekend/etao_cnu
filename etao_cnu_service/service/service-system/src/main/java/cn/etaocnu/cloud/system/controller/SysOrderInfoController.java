package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.OrderVo;
import cn.etaocnu.cloud.system.service.SysOrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "订单接口")
@RestController
@RequestMapping("/sysOrder")
public class SysOrderInfoController {
    @Resource
    private SysOrderInfoService sysOrderInfoService;

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAllOrderList(@RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size){
        List<OrderVo> orderList = sysOrderInfoService.getAllOrderList(page, size);
        int total = sysOrderInfoService.countOrder();
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("获取订单列表成功！", result);
    }

    @Operation(summary = "获取订单总数")
    @GetMapping("/count")
    public Result<Integer> countOrder(){
        int count = sysOrderInfoService.countOrder();
        if(count != -1){
            return Result.success("获取订单数量成功！", count);
        }else{
            return Result.fail("获取订单数量失败！", count);
        }
    }

    @Operation(summary = "通过闲置物品关键字查询订单信息")
    @GetMapping("/search/goods")
    public Result<Map<String, Object>> searchOrderByGoodsKey(@RequestParam("goodsKeyword") String goodsKeyword,
                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size){
        List<OrderVo> orderList = sysOrderInfoService.searchOrderByGoodsKey(goodsKeyword, page, size);
        int total = sysOrderInfoService.countOrderByGoodsKey(goodsKeyword);
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("通过闲置物品关键字查询订单列表成功！", result);
    }

    @Operation(summary = "通过用户ID查询订单信息")
    @GetMapping("/search/user")
    public Result<Map<String, Object>> searchOrderByUserId(@RequestParam("userId") int userId,
                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size){
        List<OrderVo> orderList = sysOrderInfoService.searchOrderByUserId(userId, page, size);
        int total = sysOrderInfoService.countOrderByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", orderList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("通过用户ID查询订单列表成功！", result);
    }

}
