package cn.etaocnu.cloud.collection.service;

import cn.etaocnu.cloud.model.vo.CollectionVo;

import java.util.List;

public interface CollectionInfoService {
    // 添加收藏
    Boolean addCollection(int userId, int goodsId);
    // 取消收藏
    Boolean cancelCollection(int userId, int goodsId);
    // 批量取消收藏
    Boolean batchCancelCollection(int userId, List<Integer> goodsIds);
    // 清空收藏
    Boolean clearCollection(int userId);
    // 获取收藏列表
    List<CollectionVo> getCollectionList(int userId, int page, int size);
    // 获取用户总收藏记录数
    int countUserCollection(int userId);
    // 判断用户是否收藏了某个闲置物品
    int isCollectionExist(int goodsId, int userId);
}
