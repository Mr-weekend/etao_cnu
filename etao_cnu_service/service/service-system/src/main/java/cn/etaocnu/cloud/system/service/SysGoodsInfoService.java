package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.model.vo.GoodsVo;

import java.util.List;

public interface SysGoodsInfoService {
    // 获取所有闲置物品列表（包括闲置物品当前状态）
    List<GoodsVo> getAllGoodsList(String status, int page, int size);
    // 关键字模糊查询闲置物品（可以设置闲置物品状态筛选）
    List<GoodsVo> SearchGoodsByKeyword(String keyword, String status, Integer categoryId, int page, int size);
    // 删除闲置
    Boolean deleteGoodsById(int goodsId);
    // 统计闲置物品总数
    int countGoods(String status);
    // 统计模糊查询结果总记录数
    int countGoodsByKeyword(String keyword, String status, Integer categoryId);
}
