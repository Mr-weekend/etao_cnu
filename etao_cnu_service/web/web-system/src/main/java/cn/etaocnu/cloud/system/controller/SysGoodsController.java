package cn.etaocnu.cloud.system.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.service.SysGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "闲置物品管理接口")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/sysGoods")
public class SysGoodsController {

    @Resource
    private SysGoodsService sysGoodsService;

    @Operation(summary = "获取所有闲置物品列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        return sysGoodsService.getAllGoodsList(status, page, size);
    }

    @Operation(summary = "根据闲置物品关键字搜索闲置物品")
    @GetMapping("/search")
    public Result<Map<String, Object>> searchGoods(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return sysGoodsService.SearchGoodsByKeyword(keyword, status, categoryId, page, size);
    }

    @Operation(summary = "删除闲置")
    @DeleteMapping("/delete/{goodsId}")
    public Result<Boolean> deleteGoods(@PathVariable int goodsId){
        return sysGoodsService.deleteGoodsById(goodsId);
    }
}
