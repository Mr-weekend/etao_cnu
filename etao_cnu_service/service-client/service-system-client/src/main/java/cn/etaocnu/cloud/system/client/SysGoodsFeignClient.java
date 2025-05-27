package cn.etaocnu.cloud.system.client;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-system")
public interface SysGoodsFeignClient {

    @GetMapping("sysGoods/list")
    Result<Map<String, Object>> getAllGoodsList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("sysGoods/search")
    Result<Map<String, Object>> searchGoods(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @DeleteMapping("sysGoods/delete/{goodsId}")
    Result<Boolean> deleteGoods(@PathVariable int goodsId);
}
