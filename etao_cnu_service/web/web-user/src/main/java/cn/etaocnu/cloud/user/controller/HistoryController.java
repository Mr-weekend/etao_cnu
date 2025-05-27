package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.user.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("history")
@Tag(name = "浏览历史接口", description = "提供浏览历史的查询与管理功能")
public class HistoryController {

    @Resource
    private HistoryService historyService;
    
    @Operation(summary = "添加浏览记录")
    @PostMapping("/add/{goodsId}")
    public Result<Boolean> addHistory(
            @RequestHeader("token") String token,
            @PathVariable("goodsId") int goodsId) {
        return historyService.addHistory(token, goodsId);

    }
    
    @Operation(summary = "删除单条浏览记录")
    @DeleteMapping("/delete/{goodsId}")
    public Result<Boolean> deleteHistory(
            @RequestHeader("token") String token,
            @PathVariable("goodsId") int goodsId) {
        return historyService.deleteHistory(token, goodsId);

    }
    
    @Operation(summary = "批量删除浏览记录")
    @DeleteMapping("/batch/delete")
    public Result<Boolean> batchDeleteHistory(
            @RequestHeader("token") String token,
            @RequestParam("goodsIds") List<Integer> goodsIds) {
        return historyService.batchDeleteHistory(token, goodsIds);

    }
    
    @Operation(summary = "清空浏览记录")
    @DeleteMapping("/clear")
    public Result<Boolean> clearHistory(@RequestHeader("token") String token) {
        return historyService.clearHistory(token);

    }
    
    @Operation(summary = "获取浏览记录列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getHistoryList(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return historyService.getHistoryList(token, page, size);

    }
    
    @Operation(summary = "获取用户浏览记录数量")
    @GetMapping("/count")
    public Result<Integer> countHistory(@RequestHeader("token") String token) {
        return historyService.countHistory(token);
    }
}
