package cn.etaocnu.cloud.history;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-history")
public interface HistoryFeignClient {

    @PostMapping("/history/add/{goodsId}")
    Result<Boolean> addHistory(
            @RequestParam("userId") int userId,
            @PathVariable("goodsId") int goodsId);

    @DeleteMapping("/history/delete/{goodsId}")
    Result<Boolean> deleteHistory(
            @RequestParam("userId") int userId,
            @PathVariable("goodsId") int goodsId);

    @DeleteMapping("/history/batch/delete")
    Result<Boolean> batchDeleteHistory(
            @RequestParam("userId") int userId,
            @RequestParam("goodsIds") List<Integer> goodsIds);

    @DeleteMapping("/history/clear")
    Result<Boolean> clearHistory(@RequestParam("userId") int userId);

    @GetMapping("/history/list")
    Result<Map<String, Object>> getHistoryList(
            @RequestParam("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/history/count/{userId}")
    Result<Integer> countHistory(@PathVariable("userId") int userId);

}
