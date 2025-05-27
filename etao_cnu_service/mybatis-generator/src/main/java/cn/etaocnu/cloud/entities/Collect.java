package cn.etaocnu.cloud.entities;

import java.util.Date;
import javax.persistence.*;

/**
 * 表名：collect
 * 表注释：收藏记录表
*/
@Table(name = "collect")
public class Collect {
    /**
     * 收藏ID
     */
    @Id
    @Column(name = "collect_id")
    @GeneratedValue(generator = "JDBC")
    private Integer collectId;

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
     * 收藏时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 获取收藏ID
     *
     * @return collectId - 收藏ID
     */
    public Integer getCollectId() {
        return collectId;
    }

    /**
     * 设置收藏ID
     *
     * @param collectId 收藏ID
     */
    public void setCollectId(Integer collectId) {
        this.collectId = collectId;
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
     * 获取收藏时间
     *
     * @return createdAt - 收藏时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置收藏时间
     *
     * @param createdAt 收藏时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}