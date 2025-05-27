package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.client.SysOrderFeignClient;
import cn.etaocnu.cloud.system.service.SysOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@Slf4j
public class SysOrderServiceImpl implements SysOrderService {
    @Resource
    private SysOrderFeignClient sysOrderFeignClient;

    @Override
    public Result<Map<String, Object>> getAllOrderList(int page, int size) {
        return sysOrderFeignClient.getAllOrderList(page, size);
    }

    @Override
    public Result<Map<String, Object>> searchOrderByGoodsKey(String goodsKeyword, int page, int size) {
        return sysOrderFeignClient.searchOrderByGoodsKey(goodsKeyword, page, size);
    }

    @Override
    public Result<Map<String, Object>> searchOrderByUserId(int userId, int page, int size) {
        return sysOrderFeignClient.searchOrderByUserId(userId, page, size);
    }

    @Override
    public Result<Integer> countOrder() {
        return sysOrderFeignClient.countOrder();
    }
}
