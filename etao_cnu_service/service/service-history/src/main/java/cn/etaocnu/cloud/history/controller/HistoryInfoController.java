package cn.etaocnu.cloud.history.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.history.service.HistoryInfoService;
import cn.etaocnu.cloud.model.vo.BrowseHistoryVo;
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
@RestController
@RefreshScope
@RequestMapping("/history")
@Tag(name = "浏览历史接口", description = "提供浏览历史的增删改查功能")
public class HistoryInfoController {
    
    @Resource
    private HistoryInfoService historyInfoService;
    
    @Operation(summary = "添加浏览记录")
    @PostMapping("/add/{goodsId}")
    public Result<Boolean> addHistory(
            @RequestParam("userId") int userId,
            @PathVariable("goodsId") int goodsId) {
        log.info("添加浏览记录: userId={}, goodsId={}", userId, goodsId);
        Boolean result = historyInfoService.addHistory(userId, goodsId);
        return result ? Result.success("添加浏览记录成功！",true) : Result.fail("添加浏览记录失败！");
    }
    
    @Operation(summary = "删除单条浏览记录")
    @DeleteMapping("/delete/{goodsId}")
    public Result<Boolean> deleteHistory(
            @RequestParam("userId") int userId,
            @PathVariable("goodsId") int goodsId) {
        
        Boolean result = historyInfoService.deleteHistory(userId, goodsId);
        return result ? Result.success("删除浏览记录成功！",true) : Result.fail("删除浏览记录失败！");
    }
    
    @Operation(summary = "批量删除浏览记录")
    @DeleteMapping("/batch/delete")
    public Result<Boolean> batchDeleteHistory(
            @RequestParam("userId") int userId,
            @RequestParam("goodsIds") List<Integer> goodsIds) {
        log.info("批量删除浏览记录: userId={}, goodsIds={}", userId, goodsIds);
        Boolean result = historyInfoService.batchDeleteHistory(userId, goodsIds);
        return result ? Result.success("批量删除浏览记录成功！",true) : Result.fail("批量删除浏览记录失败！");
    }
    
    @Operation(summary = "清空浏览记录")
    @DeleteMapping("/clear")
    public Result<Boolean> clearHistory(@RequestParam("userId") int userId) {
        log.info("清空用户浏览记录: userId={}", userId);
        Boolean result = historyInfoService.clearHistory(userId);
        return result ? Result.success("清空浏览记录成功！",true) : Result.fail("清空浏览记录失败！");
    }
    
    @Operation(summary = "获取浏览记录列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getHistoryList(
            @RequestParam("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取用户浏览记录: userId={}, page={}, size={}", userId, page, size);
        // 获取浏览记录列表
        List<BrowseHistoryVo> historyList = historyInfoService.getBrowseHistory(userId, page, size);
        // 获取总记录数
        int total = historyInfoService.countHistory(userId);
        log.info("获取到{}条浏览记录", total);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", historyList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success("获取浏览记录列表成功！",result);
    }
    
    @Operation(summary = "获取用户浏览记录数量")
    @GetMapping("/count/{userId}")
    public Result<Integer> countHistory(@PathVariable("userId") int userId) {
        log.info("统计用户浏览记录数量: userId={}", userId);
        int count = historyInfoService.countHistory(userId);
        return Result.success("获取用户浏览记录数量成功！",count);
    }

}
