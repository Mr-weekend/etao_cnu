package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：browse_history
 * 表注释：浏览历史表
*/
@Table(name = "browse_history")
public class BrowseHistory {
    /**
     * 记录ID
     */
    @Id
    @Column(name = "history_id")
    @GeneratedValue(generator = "JDBC")
    private Integer historyId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 闲置物品ID
     */
    @Column(name = "goods_id")
    private Integer goodsId;

    /**
     * 浏览时间
     */
    @Column(name = "browse_time")
    private Date browseTime;

    /**
     * 获取记录ID
     *
     * @return historyId - 记录ID
     */
    public Integer getHistoryId() {
        return historyId;
    }

    /**
     * 设置记录ID
     *
     * @param historyId 记录ID
     */
    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    /**
     * 获取用户ID
     *
     * @return userId - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取闲置物品ID
     *
     * @return goodsId - 闲置物品ID
     */
    public Integer getGoodsId() {
        return goodsId;
    }

    /**
     * 设置闲置物品ID
     *
     * @param goodsId 闲置物品ID
     */
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取浏览时间
     *
     * @return browseTime - 浏览时间
     */
    public Date getBrowseTime() {
        return browseTime;
    }

    /**
     * 设置浏览时间
     *
     * @param browseTime 浏览时间
     */
    public void setBrowseTime(Date browseTime) {
        this.browseTime = browseTime;
    }
}