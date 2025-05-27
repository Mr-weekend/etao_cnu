package cn.etaocnu.cloud.system.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.system.client.SysGoodsFeignClient;
import cn.etaocnu.cloud.system.service.SysGoodsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SysGoodsServiceImpl implements SysGoodsService{

    @Resource
    private SysGoodsFeignClient sysGoodsFeignClient;

    @Override
    public Result<Map<String, Object>> getAllGoodsList(String status, int page, int size) {
        Result<Map<String, Object>> result = sysGoodsFeignClient.getAllGoodsList(status, page, size);
        if (result.getCode() == 200){
            return Result.success(result.getMessage(), result.getData());
        }
        return Result.fail(result.getMessage());
    }

    @Override
    public Result<Map<String, Object>> SearchGoodsByKeyword(String keyword, String status, Integer categoryId, int page, int size) {
        Result<Map<String, Object>> result = sysGoodsFeignClient.searchGoods(keyword, status, categoryId, page, size);
        if (result.getCode() == 200){
            return Result.success(result.getMessage(), result.getData());
        }
        return Result.fail(result.getMessage());
    }

    @Override
    public Result<Boolean> deleteGoodsById(int goodsId) {
        Result<Boolean> result = sysGoodsFeignClient.deleteGoods(goodsId);
        if (result.getCode() == 200){
            return Result.success(result.getMessage(), result.getData());
        }
        return Result.fail(result.getMessage());
    }
}
