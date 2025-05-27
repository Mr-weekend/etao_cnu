package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.goods.client.GoodsFeignClient;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import cn.etaocnu.cloud.user.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {
    
    @Autowired
    private GoodsFeignClient goodsFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Boolean> publishGoods(String token, String goodsInfo, MultipartFile[] images) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        if (goodsInfo != null) {
            log.info("service层收到goodsCRUDVo：{}",goodsInfo);
        }
        if (images != null) {
            log.info("service层收到images：{}",images);
        }
        return goodsFeignClient.publishGoods(userId, goodsInfo, images);
    }


    @Override
    public Result<Boolean> updateGoods(String token, int goodsId, String goodsInfo, MultipartFile[] images, 
                                     String imageUpdateType, List<String> deleteImageUrls) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        if (goodsInfo != null) {
            log.info("service层收到goodsInfo：{}", goodsInfo);
        }
        if (images != null) {
            log.info("service层收到images：{}", images.length);
        }
        if (deleteImageUrls != null && !deleteImageUrls.isEmpty()) {
            log.info("service层收到要删除的图片：{}", deleteImageUrls);
        }
        log.info("图片更新类型：{}", imageUpdateType);
        
        return goodsFeignClient.updateGoods(goodsId, userId, goodsInfo, imageUpdateType, deleteImageUrls, images);
    }

    @Override
    public Result<Boolean> removeGoods(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.removeGoods(goodsId, userId);
    }

    @Override
    public Result<Boolean> putGoods(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.putGoods(goodsId, userId);
    }

    @Override
    public Result<Boolean> deleteGoods(String token, int goodsId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.deleteGoods(goodsId, userId);
    }

    @Override
    public Result<Map<String, Object>> getMyGoodsList(String token, int page, int size) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.getMyGoodsList(userId, page, size);
    }

    @Override
    public Result<Boolean> soldGoods( int goodsId) {
        return goodsFeignClient.soldGoods(goodsId);
    }

    @Override
    public Result<Map<String, Object>> getGoodsListByUserId(int userId, int page, int size) {
        return goodsFeignClient.getGoodsListByUserId(userId, page, size);
    }

    @Override
    public Result<Map<String, Object>> getRemoveGoodsListByUserId(String token, int page, int size) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.getRemoveGoodsListByUserId(userId, page, size);
    }

    @Override
    public Result<Integer> countUserGoods(int userId) {
        return goodsFeignClient.countUserGoods(userId);
    }

    @Override
    public Result<Integer> countUserRemoveGoods(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return goodsFeignClient.countUserRemoveGoods(userId);
    }

    @Override
    public Result<GoodsVo> getGoodsById(int goodsId) {
        return goodsFeignClient.getGoodsById(goodsId);
    }

    @Override
    public Result<Map<String, Object>> searchGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType,
                                                            Integer bottomPrice, Integer topPrice, int page, int size) {
        return goodsFeignClient.searchGoodsByKeyword(keyword, categoryId, priceRankType, bottomPrice, topPrice, page, size);
    }

    @Override
    public Result<Map<String, Object>> getAllGoodsList(Integer categoryId, Integer bottomPrice, Integer topPrice, int page, int size) {
        return goodsFeignClient.getAllGoodsList(categoryId, bottomPrice, topPrice, page, size);
    }

    @Override
    public Result<Map<String, Object>> getAllCategoryList() {
        return goodsFeignClient.getAllCategoryList();
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }

}
