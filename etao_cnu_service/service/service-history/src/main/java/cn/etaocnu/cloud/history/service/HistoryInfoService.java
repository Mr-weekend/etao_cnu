package cn.etaocnu.cloud.history.service;

import cn.etaocnu.cloud.model.vo.BrowseHistoryVo;

import java.util.List;

public interface HistoryInfoService {
    // 新增一条浏览记录
    Boolean addHistory(int userId, int goodsId);
    // 删除一条浏览记录
    Boolean deleteHistory(int userId, int goodsId);
    // 批量删除浏览记录
    Boolean batchDeleteHistory(int userId, List<Integer> goodsIds);
    // 清空浏览记录
    Boolean clearHistory(int userId);
    // 获取用户浏览记录列表
    List<BrowseHistoryVo> getBrowseHistory(int userId, int page, int size);
    // 获取用户浏览记录数
    int countHistory(int userId);
}
