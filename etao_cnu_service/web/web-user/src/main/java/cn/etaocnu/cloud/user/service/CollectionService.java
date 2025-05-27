package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;

import java.util.List;
import java.util.Map;

public interface CollectionService {
    // 添加收藏
    Result<Boolean> addCollection(String token, int goodsId);
    // 取消收藏
    Result<Boolean> cancelCollection(String token, int goodsId);
    // 批量取消收藏
    Result<Boolean> batchCancelCollection(String token, List<Integer> goodsIds);
    // 清空收藏
    Result<Boolean> clearCollection(String token);
    // 获取收藏列表
    Result<Map<String, Object>> getCollectionList(String token, int page, int size);
    // 获取用户总收藏记录数
    Result<Integer> countUserCollection(String token);
    // 获取用户是否收藏闲置
    Result<Map<String, Object>> isCollectionExist(String token, int goodsId);

}
