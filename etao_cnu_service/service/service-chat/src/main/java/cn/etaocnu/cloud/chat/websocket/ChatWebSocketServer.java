package cn.etaocnu.cloud.chat.websocket;

import cn.etaocnu.cloud.chat.dto.ChatMessageRequest;
import cn.etaocnu.cloud.chat.service.ChatService;
import cn.etaocnu.cloud.model.vo.ChatMessagesVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/chat/{userId}")
public class ChatWebSocketServer {
    
    // 消息类型
    private static final String MESSAGE_TYPE_TEXT = "text";
    private static final String MESSAGE_TYPE_IMAGE = "image";
    
    // 用静态变量存储在线用户的会话
    private static final Map<Integer, Session> ONLINE_USERS = new ConcurrentHashMap<>();
    
    // 由于WebSocket是多例的，不能直接注入Bean，需要使用静态变量存储服务
    private static ChatService chatService;
    
    // 用于设置静态服务实例
    public static void setChatMessageService(ChatService service) {
        ChatWebSocketServer.chatService = service;
    }
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //连接建立时调用
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        ONLINE_USERS.put(userId, session);
        log.info("用户{}已连接WebSocket，当前在线用户数：{}", userId, ONLINE_USERS.size());
        
        // 发送未读消息
        if (chatService != null) {
            chatService.sendUnreadMessages(userId);
        }
    }
    
    //收到客户端消息时调用
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") Integer userId) {
        log.info("收到用户{}的消息：{}", userId, message);
        
        try {
            // 解析前端发送的简化消息格式
            ChatMessageRequest request = objectMapper.readValue(message, ChatMessageRequest.class);
            
            // 验证消息内容
            if (!validateMessage(request)) {
                log.warn("消息内容验证失败: {}", message);
                return;
            }
            
            // 转换为完整的ChatMessages实体
            ChatMessagesVo chatMessage = convertToChatMessage(request, userId);

            // 处理并保存消息
            if (chatService != null) {
                chatService.processMessage(chatMessage);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }
    
    /**
     * DTO转换为VO
     */
    private ChatMessagesVo convertToChatMessage(ChatMessageRequest request, Integer senderId) {
        ChatMessagesVo message = new ChatMessagesVo();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setGoodsId(request.getGoodsId());
        message.setContent(request.getContent());
        
        // 设置消息类型，默认为文本
        message.setType(StringUtils.hasText(request.getType()) ? request.getType() : MESSAGE_TYPE_TEXT);
        
        return message;
    }

    // 验证消息内容
    private boolean validateMessage(ChatMessageRequest request) {
        // 检查基本字段
        if (request.getReceiverId() == null || request.getGoodsId() == null || !StringUtils.hasText(request.getContent())) {
            return false;
        }
        
        // 验证消息类型
        String type = request.getType();
        if (!StringUtils.hasText(type)) {
            return true; // 默认为文本消息，无需额外验证
        }
        
        // 根据消息类型验证内容
        if (MESSAGE_TYPE_TEXT.equals(type)) {
            return true;
        } else if (MESSAGE_TYPE_IMAGE.equals(type)) {
            return request.getContent().startsWith("http") || request.getContent().startsWith("/");
        }
        
        return false;
    }

    // 连接关闭时调用
    @OnClose
    public void onClose(@PathParam("userId") Integer userId) {
        ONLINE_USERS.remove(userId);
        log.info("用户{}断开连接，当前在线用户数：{}", userId, ONLINE_USERS.size());
    }

    // 发生错误时调用
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") Integer userId) {
        log.error("用户{}的WebSocket连接发生错误：{}", userId, error.getMessage());
        ONLINE_USERS.remove(userId);
    }

    // 发送消息给指定用户
    public static void sendMessage(Integer userId, ChatMessagesVo message) {
        Session session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(jsonMessage);
                log.info("已向用户{}发送消息", userId);
            } catch (IOException e) {
                log.error("向用户{}发送消息失败：{}", userId, e.getMessage());
            }
        } else {
            log.info("用户{}不在线，消息已保存到数据库", userId);
        }
    }
} 