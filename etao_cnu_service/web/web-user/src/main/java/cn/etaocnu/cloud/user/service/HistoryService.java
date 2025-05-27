package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;

import java.util.List;
import java.util.Map;

public interface HistoryService {
    // 新增一条浏览记录
    Result<Boolean> addHistory(String token, int goodsId);
    // 删除一条浏览记录
    Result<Boolean> deleteHistory(String token, int goodsId);
    // 批量删除浏览记录
    Result<Boolean> batchDeleteHistory(String token, List<Integer> goodsIds);
    // 清空浏览记录
    Result<Boolean> clearHistory(String token);
    // 获取用户浏览记录列表
    Result<Map<String, Object>> getHistoryList(String token, int page, int size);
    // 获取总浏览记录数
    Result<Integer> countHistory(String token);
}
