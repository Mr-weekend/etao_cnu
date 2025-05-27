package cn.etaocnu.cloud.history.service.impl;

import cn.etaocnu.cloud.history.mapper.BrowseHistoryMapper;
import cn.etaocnu.cloud.history.service.HistoryInfoService;
import cn.etaocnu.cloud.model.entity.BrowseHistory;
import cn.etaocnu.cloud.model.vo.BrowseHistoryVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HistoryInfoServiceImpl implements HistoryInfoService {

    @Resource
    private BrowseHistoryMapper browseHistoryMapper;

    @Override
    @Transactional
    public Boolean addHistory(int userId, int goodsId) {
        try {
            // 先查询是否已存在记录
            Example example = new Example(BrowseHistory.class);
            example.createCriteria()
                  .andEqualTo("userId", userId)
                  .andEqualTo("goodsId", goodsId);
            
            BrowseHistory existingHistory = browseHistoryMapper.selectOneByExample(example);
            
            if (existingHistory != null) {
                // 如果记录已存在，则更新浏览时间
                existingHistory.setBrowseTime(new Date());
                browseHistoryMapper.updateByPrimaryKeySelective(existingHistory);
                //增加闲置物品的浏览次数
                browseHistoryMapper.updateViewCount(goodsId);
                log.info("浏览记录已更新: historyId={}", existingHistory.getHistoryId());
            } else {
                // 如果记录不存在，则创建新记录
                BrowseHistory newHistory = new BrowseHistory();
                newHistory.setUserId(userId);
                newHistory.setGoodsId(goodsId);
                newHistory.setBrowseTime(new Date());
                browseHistoryMapper.insertSelective(newHistory);
                //增加闲置物品的浏览次数
                browseHistoryMapper.updateViewCount(goodsId);
                log.info("新浏览记录已创建: historyId={}", newHistory.getHistoryId());
            }
            return true;
        } catch (Exception e) {
            log.error("添加浏览记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean deleteHistory(int userId, int goodsId) {
        try {
            Example example = new Example(BrowseHistory.class);
            example.createCriteria()
                  .andEqualTo("userId", userId)
                  .andEqualTo("goodsId", goodsId);
            
            int result = browseHistoryMapper.deleteByExample(example);
            
            log.info("删除浏览记录结果: {}", result > 0 ? "成功" : "未找到记录");
            return result > 0;
        } catch (Exception e) {
            log.error("删除浏览记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean batchDeleteHistory(int userId, List<Integer> goodsIds) {
        try {
            Example example = new Example(BrowseHistory.class);
            example.createCriteria()
                  .andEqualTo("userId", userId)
                  .andIn("goodsId", goodsIds);
            int result = browseHistoryMapper.deleteByExample(example);
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除浏览记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean clearHistory(int userId) {
        try {
            Example example = new Example(BrowseHistory.class);
            example.createCriteria()
                  .andEqualTo("userId", userId);
            return browseHistoryMapper.deleteByExample(example) > 0;
        } catch (Exception e) {
            log.error("清空浏览记录失败", e);
            return false;
        }
    }

    @Override
    public List<BrowseHistoryVo> getBrowseHistory(int userId, int page, int size) {
        log.info("获取用户浏览记录: userId={}, page={}, size={}", userId, page, size);
        try {
            // 分页查询
            List<BrowseHistoryVo> browseHistoryVoList = browseHistoryMapper.getBrowseHistoryDetails(userId,(page - 1) * size, size);
            if (browseHistoryVoList != null && !browseHistoryVoList.isEmpty()) {
                for (BrowseHistoryVo browseHistoryVo : browseHistoryVoList) {
                    //设置首图
                    browseHistoryVo.setImageUrl(browseHistoryMapper.getImageUrl(browseHistoryVo.getGoodsId()));
                    log.info("浏览时间:{}",browseHistoryVo);
                }
            }
            return browseHistoryVoList;
        } catch (Exception e) {
            log.error("获取浏览记录失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public int countHistory(int userId) {
        try {
            Example example = new Example(BrowseHistory.class);
            example.createCriteria()
                  .andEqualTo("userId", userId);
            return browseHistoryMapper.selectCountByExample(example);
        } catch (Exception e) {
            log.error("统计用户浏览记录数量失败", e);
            return 0;
        }
    }

}
