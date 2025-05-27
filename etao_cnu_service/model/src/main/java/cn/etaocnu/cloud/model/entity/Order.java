package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：orders
 * 表注释：订单信息表
*/
@Table(name = "orders")
public class Order {
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    @GeneratedValue(generator = "JDBC")
    private Integer orderId;

    /**
     * 闲置物品ID
     */
    @Column(name = "goods_id")
    private Integer goodsId;

    /**
     * 买家ID
     */
    @Column(name = "buyer_id")
    private Integer buyerId;

    /**
     * 买家逻辑删除标记
     */
    @Column(name = "buyer_deleted")
    private Byte buyerDeleted;

    /**
     * 卖家逻辑删除标记
     */
    @Column(name = "seller_deleted")
    private Byte sellerDeleted;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 订单状态：等待见面交易 已完成 已取消 
     */
    private String status;

    @Column(name = "completed_at")
    private Date completedAt;

    /**
     * 获取订单号
     *
     * @return orderId - 订单号
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单号
     *
     * @param orderId 订单号
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
     * 获取买家ID
     *
     * @return buyerId - 买家ID
     */
    public Integer getBuyerId() {
        return buyerId;
    }

    /**
     * 设置买家ID
     *
     * @param buyerId 买家ID
     */
    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * 获取买家逻辑删除标记
     *
     * @return buyerDeleted - 买家逻辑删除标记
     */
    public Byte getBuyerDeleted() {
        return buyerDeleted;
    }

    /**
     * 设置买家逻辑删除标记
     *
     * @param buyerDeleted 买家逻辑删除标记
     */
    public void setBuyerDeleted(Byte buyerDeleted) {
        this.buyerDeleted = buyerDeleted;
    }

    /**
     * 获取卖家逻辑删除标记
     *
     * @return sellerDeleted - 卖家逻辑删除标记
     */
    public Byte getSellerDeleted() {
        return sellerDeleted;
    }

    /**
     * 设置卖家逻辑删除标记
     *
     * @param sellerDeleted 卖家逻辑删除标记
     */
    public void setSellerDeleted(Byte sellerDeleted) {
        this.sellerDeleted = sellerDeleted;
    }

    /**
     * 获取创建时间
     *
     * @return createdAt - 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取订单状态：等待见面交易 已完成 已取消 
     *
     * @return status - 订单状态：等待见面交易 已完成 已取消 
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置订单状态：等待见面交易 已完成 已取消 
     *
     * @param status 订单状态：等待见面交易 已完成 已取消 
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return completedAt
     */
    public Date getCompletedAt() {
        return completedAt;
    }

    /**
     * @param completedAt
     */
    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}