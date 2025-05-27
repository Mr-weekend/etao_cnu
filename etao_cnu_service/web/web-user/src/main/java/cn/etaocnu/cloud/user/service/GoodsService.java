package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    //发布闲置
    Result<Boolean> publishGoods(String token, String goodsInfoJson, MultipartFile[] images);
    //更新闲置物品信息
    Result<Boolean> updateGoods(String token, int goodsId, String goodsInfoJson, MultipartFile[] images, 
                              String imageUpdateType, List<String> deleteImageUrls);
    //下架闲置
    Result<Boolean> removeGoods(String token, int goodsId);
    // 重新上架闲置
    Result<Boolean> putGoods(String token, int goodsId);
    //删除闲置
    Result<Boolean> deleteGoods(String token, int goodsId);
    //卖出闲置
    Result<Boolean> soldGoods(int goodsId);
    //获取用户自己发布的闲置物品列表
    Result<Map<String, Object>> getMyGoodsList(String token, int page, int size);
    //获取指定用户发布的闲置物品列表
    Result<Map<String, Object>> getGoodsListByUserId(int userId, int page, int size);
    // 获取指定用户下架的闲置物品列表
    Result<Map<String, Object>> getRemoveGoodsListByUserId(String token, int page, int size);
    // 获取指定用户发布的闲置物品总数
    Result<Integer> countUserGoods(int userId);
    // 获取指定用户下架的闲置物品总数
    Result<Integer> countUserRemoveGoods(String token);
    //获取闲置物品详细信息
    Result<GoodsVo> getGoodsById(int goodsId);
    // 关键字查询闲置物品列表(priceRankType:0 降序 1 升序)
    Result<Map<String, Object>> searchGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType,
                                                     Integer bottomPrice, Integer topPrice, int page, int size);
    // 获取所有已发布的闲置物品列表
    Result<Map<String, Object>> getAllGoodsList(Integer categoryId, Integer bottomPrice, Integer topPrice, int page, int size);
    // 获取闲置物品分类列表
    Result<Map<String, Object>> getAllCategoryList();
}
