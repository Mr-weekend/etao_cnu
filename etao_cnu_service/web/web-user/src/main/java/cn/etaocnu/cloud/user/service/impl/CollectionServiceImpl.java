package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.CollectionFeignClient;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.user.service.CollectionService;
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
public class CollectionServiceImpl implements CollectionService {

    @Resource
    private CollectionFeignClient collectionFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Boolean> addCollection(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.addCollection(goodsId, userId);
    }

    @Override
    public Result<Boolean> cancelCollection(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.cancelCollection(goodsId, userId);
    }

    @Override
    public Result<Boolean> batchCancelCollection(String token, List<Integer> goodsIds) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.batchCancelCollection(userId, goodsIds);
    }

    @Override
    public Result<Boolean> clearCollection(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.clearCollection(userId);
    }

    @Override
    public Result<Map<String, Object>> getCollectionList(String token, int page, int size) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.getCollectionList(userId, page, size);
    }

    @Override
    public Result<Integer> countUserCollection(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.countUserCollection(userId);
    }

    @Override
    public Result<Map<String, Object>> isCollectionExist(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return collectionFeignClient.isCollectionExist(goodsId, userId);
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }
}
