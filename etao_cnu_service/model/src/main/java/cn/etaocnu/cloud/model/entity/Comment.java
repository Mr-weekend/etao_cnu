package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：comment
 * 表注释：评价表
*/
@Table(name = "comments")
public class Comment {
    /**
     * 评价ID
     */
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(generator = "JDBC")
    private Integer commentId;

    /**
     * 订单ID
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 评价人ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 评价类型：1-好评 2-中评 3-差评
     */
    private Byte type;

    /**
     * 是否匿名：0-否 1-是
     */
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    /**
     * 评价时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 获取评价ID
     *
     * @return commentId - 评价ID
     */
    public Integer getCommentId() {
        return commentId;
    }

    /**
     * 设置评价ID
     *
     * @param commentId 评价ID
     */
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    /**
     * 获取订单ID
     *
     * @return orderId - 订单ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取评价人ID
     *
     * @return userId - 评价人ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置评价人ID
     *
     * @param userId 评价人ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取评价类型：1-好评 2-中评 3-差评
     *
     * @return type - 评价类型：1-好评 2-中评 3-差评
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置评价类型：1-好评 2-中评 3-差评
     *
     * @param type 评价类型：1-好评 2-中评 3-差评
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取是否匿名：0-否 1-是
     *
     * @return isAnonymous - 是否匿名：0-否 1-是
     */
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    /**
     * 设置是否匿名：0-否 1-是
     *
     * @param isAnonymous 是否匿名：0-否 1-是
     */
    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    /**
     * 获取评价时间
     *
     * @return createdAt - 评价时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置评价时间
     *
     * @param createdAt 评价时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取评价内容
     *
     * @return content - 评价内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置评价内容
     *
     * @param content 评价内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}