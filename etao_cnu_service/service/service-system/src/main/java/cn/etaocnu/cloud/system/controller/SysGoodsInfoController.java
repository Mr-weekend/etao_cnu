package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.system.service.SysGoodsInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "闲置物品管理接口")
@RestController
@RequestMapping("/sysGoods")
public class SysGoodsInfoController {

    @Resource
    private SysGoodsInfoService sysGoodsInfoService;

    @Operation(summary = "获取所有闲置物品列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("获取所有闲置物品列表，status: {}, page: {}, size: {}", status, page, size);
        List<GoodsVo> goodsList = sysGoodsInfoService.getAllGoodsList(status, page, size);
        int total = sysGoodsInfoService.countGoods(status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size); // 计算总页数
        return Result.success("获取所有闲置物品列表成功！", result);
    }

    @Operation(summary = "根据闲置物品关键字搜索闲置物品")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchGoods(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("根据关键字搜索闲置物品，keyword: {}, status: {}, categoryId: {}, page: {}, size: {}", keyword, status, categoryId, page, size);
        List<GoodsVo> goodsList = sysGoodsInfoService.SearchGoodsByKeyword(keyword, status, categoryId, page, size);
        int total = sysGoodsInfoService.countGoodsByKeyword(keyword, status, categoryId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", goodsList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("根据关键字搜索闲置物品成功！", result);
    }

    @Operation(summary = "删除闲置")
    @DeleteMapping("/delete/{goodsId}")
    public Result<Boolean> deleteGoods(@PathVariable int goodsId) {
        log.info("删除闲置，goodsId: {}", goodsId);
        Boolean result = sysGoodsInfoService.deleteGoodsById(goodsId);
        return result ? Result.success("删除闲置物品成功！", true): Result.fail("删除闲置物品失败！", false);
    }

}
