package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.history.HistoryFeignClient;
import cn.etaocnu.cloud.user.service.HistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HistoryServiceImpl implements HistoryService {

    @Resource
    private HistoryFeignClient historyFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Boolean> addHistory(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.addHistory(userId, goodsId);
    }

    @Override
    public Result<Boolean> deleteHistory(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.deleteHistory(userId, goodsId);
    }

    @Override
    public Result<Boolean> batchDeleteHistory(String token, List<Integer> goodsIds) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.batchDeleteHistory(userId, goodsIds);
    }

    @Override
    public Result<Boolean> clearHistory(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.clearHistory(userId);
    }

    @Override
    public Result<Map<String, Object>> getHistoryList(String token, int page, int size) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.getHistoryList(userId, page, size);
    }


    @Override
    public Result<Integer> countHistory(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return historyFeignClient.countHistory(userId);
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }
}
