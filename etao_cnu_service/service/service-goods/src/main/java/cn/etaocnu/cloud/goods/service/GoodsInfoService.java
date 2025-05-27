package cn.etaocnu.cloud.goods.service;

import cn.etaocnu.cloud.model.entity.Category;
import cn.etaocnu.cloud.model.vo.GoodsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoodsInfoService {
    // 发布闲置
    Boolean publishGoods(int userId, String goodsInfoJson, MultipartFile[] images);
    // 将闲置存入草稿
//    Boolean saveGoodsDraft(int userId, String goodsInfoJson, MultipartFile[] images);
    // 更新闲置物品信息
    Boolean updateGoods(int goodsId, int userId, String goodsInfoJson, MultipartFile[] images, String imageUpdateType, List<String> deleteImageUrls);
    // 下架闲置
    Boolean removeGoods(int goodsId, int userId);
    // 重新上架闲置
    Boolean putGoods(int goodsId, int userId);
    // 删除闲置
    Boolean deleteGoods(int goodsId, int userId);
    // 卖出闲置
    Boolean soldGoods(int goodsId);
    // 获取用户自己发布的闲置物品列表
    List<GoodsVo> getMyGoodsList(int userId, int page, int size);
    // 获取用户发布的闲置物品总数
    int countMyGoods(int userId);
    // 获取指定用户发布的闲置物品列表
    List<GoodsVo> getGoodsListByUserId(int userId, int page, int size);
    // 获取指定用户下架的闲置物品列表
    List<GoodsVo> getRemoveGoodsListByUserId(int userId, int page, int size);
    // 获取指定用户发布的闲置物品总数
    int countUserGoods(int userId);
    // 获取指定用户下架的闲置物品总数
    int countUserRemoveGoods(int userId);
    //获取闲置物品详细信息
    GoodsVo getGoodsById(int goodsId);
//    //获取用户草稿箱内容
//    GoodsCRUDVo getGoodsDraftInfo(int userId);
    // 关键字查询闲置物品列表(priceRankType:0 降序 1 升序)
    List<GoodsVo> searchGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType,
                                       Integer bottomPrice, Integer topPrice, int page, int size);
    // 获取关键字查询结果总记录数
    int countGoodsByKeyword(String keyword, Integer categoryId, Integer priceRankType, Integer bottomPrice, Integer topPrice);
    // 获取所有已发布的闲置物品列表
    List<GoodsVo> getAllGoodsList(Integer categoryId, Integer bottomPrice, Integer topPrice, int page, int size);
    // 获取闲置物品列表总记录数
    int countAllGoods(Integer categoryId, Integer bottomPrice, Integer topPrice);
    // 获取闲置物品分类列表
    List<Category> getAllCategoryList();
}
