package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：chat_messages
 * 表注释：聊天记录表
*/
@Table(name = "chat_messages")
public class ChatMessages {
    /**
     * 消息ID
     */
    @Id
    @Column(name = "message_id")
    @GeneratedValue(generator = "JDBC")
    private Integer messageId;

    /**
     * 发送者ID
     */
    @Column(name = "sender_id")
    private Integer senderId;

    /**
     * 接收者ID
     */
    @Column(name = "receiver_id")
    private Integer receiverId;

    /**
     * 关联闲置物品ID
     */
    @Column(name = "goods_id")
    private Integer goodsId;

    /**
     * 消息类型：text：文本，img：图片
     */
    private String type;

    /**
     * 是否已读：0-未读 1-已读
     */
    @Column(name = "is_read")
    private Boolean isRead;

    /**
     * 发送时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 获取消息ID
     *
     * @return messageId - 消息ID
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * 设置消息ID
     *
     * @param messageId 消息ID
     */
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取发送者ID
     *
     * @return senderId - 发送者ID
     */
    public Integer getSenderId() {
        return senderId;
    }

    /**
     * 设置发送者ID
     *
     * @param senderId 发送者ID
     */
    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    /**
     * 获取接收者ID
     *
     * @return receiverId - 接收者ID
     */
    public Integer getReceiverId() {
        return receiverId;
    }

    /**
     * 设置接收者ID
     *
     * @param receiverId 接收者ID
     */
    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * 获取关联闲置物品ID
     *
     * @return goodsId - 关联闲置物品ID
     */
    public Integer getGoodsId() {
        return goodsId;
    }

    /**
     * 设置关联闲置物品ID
     *
     * @param goodsId 关联闲置物品ID
     */
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取消息类型：text：文本，img：图片
     *
     * @return type - 消息类型：text：文本，img：图片
     */
    public String getType() {
        return type;
    }

    /**
     * 设置消息类型：text：文本，img：图片
     *
     * @param type 消息类型：text：文本，img：图片
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取是否已读：0-未读 1-已读
     *
     * @return isRead - 是否已读：0-未读 1-已读
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * 设置是否已读：0-未读 1-已读
     *
     * @param isRead 是否已读：0-未读 1-已读
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * 获取发送时间
     *
     * @return createdAt - 发送时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置发送时间
     *
     * @param createdAt 发送时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取消息内容
     *
     * @return content - 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}