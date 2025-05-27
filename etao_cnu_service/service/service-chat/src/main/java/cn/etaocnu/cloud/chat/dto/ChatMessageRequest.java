package cn.etaocnu.cloud.chat.dto;

import lombok.Data;

/**
 * 聊天消息请求DTO
 */
@Data
public class ChatMessageRequest {
    // 接收者ID
    private Integer receiverId;
    
    // 闲置物品ID
    private Integer goodsId;
    
    // 消息内容
    private String content;
    
    // 消息类型：text或image
    private String type;
} 