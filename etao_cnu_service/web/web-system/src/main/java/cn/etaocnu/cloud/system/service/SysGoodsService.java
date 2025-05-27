package cn.etaocnu.cloud.system.service;

import cn.etaocnu.cloud.common.result.Result;

import java.util.Map;

public interface SysGoodsService {
    // 获取所有闲置物品列表（包括闲置物品当前状态）
    Result<Map<String, Object>> getAllGoodsList(String status, int page, int size);
    // 关键字模糊查询闲置物品（可以设置闲置物品状态筛选）
    Result<Map<String, Object>> SearchGoodsByKeyword(String keyword, String status, Integer categoryId, int page, int size);
    // 删除闲置
    Result<Boolean> deleteGoodsById(int goodsId);
}
