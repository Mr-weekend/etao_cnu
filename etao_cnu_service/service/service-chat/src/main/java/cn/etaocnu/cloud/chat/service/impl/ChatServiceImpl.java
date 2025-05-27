package cn.etaocnu.cloud.chat.service.impl;

import cn.etaocnu.cloud.chat.mapper.ChatMessagesMapper;
import cn.etaocnu.cloud.chat.service.ChatService;
import cn.etaocnu.cloud.chat.websocket.ChatWebSocketServer;
import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.entity.ChatMessages;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.ChatMessagesVo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatMessagesMapper chatMessagesMapper;
    @Resource
    private FileService fileService;
    @PostConstruct
    public void init() {
        // 初始化WebSocket服务
        ChatWebSocketServer.setChatMessageService(this);
    }
    
    @Override
    @Transactional
    public void processMessage(ChatMessagesVo message) {
        try {
            // 补充消息信息
//            message.setIsRead(false); // 设置为未读
            message.setCreatedAt(new Date()); // 设置发送时间
            ChatMessages chatMessages = new ChatMessages();
            BeanUtils.copyProperties(message,chatMessages);
            chatMessages.setIsRead(false);
            // 保存消息到数据库
            chatMessagesMapper.insertSelective(chatMessages);
            log.info("消息已保存到数据库，ID: {}", chatMessages.getMessageId());
            
            // 发送消息给接收者
            ChatWebSocketServer.sendMessage(message.getReceiverId(), message);
            
        } catch (Exception e) {
            log.error("处理消息失败", e);
            throw new RuntimeException("处理消息失败", e);
        }
    }
    
    @Override
    public void sendUnreadMessages(Integer userId) {
        try {
            // 查询用户的所有未读消息
            Example example = new Example(ChatMessages.class);
            example.createCriteria()
                  .andEqualTo("receiverId", userId)
                  .andEqualTo("isRead", false);
            
            // 按时间升序，先发送较早的消息
            example.orderBy("createdAt").asc();
            
            List<ChatMessages> unreadMessages = chatMessagesMapper.selectByExample(example);
            List<ChatMessagesVo> chatMessagesVoList = new ArrayList<>();
            for (ChatMessages chatMessages : unreadMessages) {
                ChatMessagesVo chatMessagesVo = new ChatMessagesVo();
                BeanUtils.copyProperties(chatMessages,chatMessagesVo);
                chatMessagesVoList.add(chatMessagesVo);
            }

            log.info("用户{}有{}条未读消息", userId, unreadMessages.size());
            
            // 发送每条未读消息
            for (ChatMessagesVo messageVo : chatMessagesVoList) {
                ChatWebSocketServer.sendMessage(userId, messageVo);
            }
            
        } catch (Exception e) {
            log.error("发送未读消息失败", e);
        }
    }
    
    @Override
    public List<ChatMessagesVo> getChatHistory(Integer receiverId, Integer senderId, Integer goodsId, Integer page, Integer size) {
        try {
            Example example = new Example(ChatMessages.class);
            Example.Criteria criteria = example.createCriteria();
            
            // 构建查询条件：两个用户之间关于特定闲置物品的聊天记录
            criteria.andEqualTo("goodsId", goodsId);

            // 使用条件表达式：(senderId=userId AND receiverId=otherUserId OR senderId=otherUserId AND receiverId=userId)
            String condition = "(sender_id = " + senderId + " AND receiver_id = " + receiverId + " OR " +
                              "sender_id = " + receiverId + " AND receiver_id = " + senderId + ")";
            criteria.andCondition(condition);
            
            // 按时间降序排列，最新消息在前
            example.orderBy("createdAt").desc();
            
            // 分页查询
            int offset = (page - 1) * size;
            RowBounds rowBounds = new RowBounds(offset, size);
            
            List<ChatMessages> messages = chatMessagesMapper.selectByExampleAndRowBounds(example, rowBounds);
            List<ChatMessagesVo> messagesVoList = new ArrayList<>();
            for (ChatMessages message : messages) {
                ChatMessagesVo chatMessagesVo = new ChatMessagesVo();
                BeanUtils.copyProperties(message, chatMessagesVo);
                messagesVoList.add(chatMessagesVo);
            }

            // 标记消息为已读
            markAsRead(receiverId, senderId, goodsId);
            
            return messagesVoList;
            
        } catch (Exception e) {
            log.error("获取聊天历史记录失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getChatSessionList(Integer userId) {
        try {
            // 用于存储唯一的会话，键为"otherUserId:goodsId"格式
            Map<String, Map<String, Object>> uniqueSessions = new HashMap<>();
            
            // 查询用户作为发送者的最新消息
            Example senderExample = new Example(ChatMessages.class);
            senderExample.createCriteria().andEqualTo("senderId", userId);
            List<ChatMessages> senderMessages = chatMessagesMapper.selectByExample(senderExample);
            
            // 查询用户作为接收者的最新消息
            Example receiverExample = new Example(ChatMessages.class);
            receiverExample.createCriteria().andEqualTo("receiverId", userId);
            List<ChatMessages> receiverMessages = chatMessagesMapper.selectByExample(receiverExample);
            
            // 处理发送的消息
            for (ChatMessages message : senderMessages) {
                String sessionKey = message.getReceiverId() + ":" + message.getGoodsId();
                updateSessionMap(uniqueSessions, sessionKey, message, userId, false);
            }
            
            // 处理接收的消息
            for (ChatMessages message : receiverMessages) {
                String sessionKey = message.getSenderId() + ":" + message.getGoodsId();
                updateSessionMap(uniqueSessions, sessionKey, message, userId, true);
            }
            
            // 将会话Map转换为List并按最新消息时间排序
            return uniqueSessions.values().stream()
                    .sorted((a, b) -> {
                        Date dateA = (Date) a.get("lastMessageTime");
                        Date dateB = (Date) b.get("lastMessageTime");
                        return dateB.compareTo(dateA);
                    })
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取聊天会话列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public boolean markAsRead(Integer receiverId, Integer senderId, Integer goodsId) {
        try {
            // 构建查询条件
            Example example = new Example(ChatMessages.class);
            example.createCriteria()
                  .andEqualTo("receiverId", receiverId)
                  .andEqualTo("senderId", senderId)
                  .andEqualTo("goodsId", goodsId)
                  .andEqualTo("isRead", false);
            
            // 创建更新对象
            ChatMessages record = new ChatMessages();
            record.setIsRead(true);
            
            // 批量更新
            int updatedCount = chatMessagesMapper.updateByExampleSelective(record, example);
            log.info("已将{}条消息标记为已读", updatedCount);
            
            return updatedCount > 0;
            
        } catch (Exception e) {
            log.error("标记消息为已读失败", e);
            return false;
        }
    }
    
    @Override
    public int getUnreadCount(Integer userId) {
        try {
            Example example = new Example(ChatMessages.class);
            example.createCriteria()
                  .andEqualTo("receiverId", userId)
                  .andEqualTo("isRead", false);
            
            return chatMessagesMapper.selectCountByExample(example);
            
        } catch (Exception e) {
            log.error("获取未读消息数量失败", e);
            return 0;
        }
    }

    @Override
    public String uploadImage(MultipartFile image) {
        FileResponse fileResponse = fileService.uploadFile(image,"chat");
        if (fileResponse != null) {
            log.info("成功保存图片到服务器！");
            return fileResponse.getFileUrl();
        }else{
            log.error("保存图片到服务器失败！");
            return null;
        }
    }

    @Override
    public int getUnreadCountByUserAndGoods(Integer receiverId, Integer senderId, Integer goodsId) {
        try {
            Example example = new Example(ChatMessages.class);
            example.createCriteria()
                  .andEqualTo("receiverId", receiverId)
                  .andEqualTo("senderId", senderId)
                  .andEqualTo("goodsId", goodsId)
                  .andEqualTo("isRead", false);
            
            return chatMessagesMapper.selectCountByExample(example);
            
        } catch (Exception e) {
            log.error("获取指定用户和闲置物品的未读消息数量失败", e);
            return 0;
        }
    }

    @Override
    public int getChatHistoryCount(Integer receiverId, Integer senderId, Integer goodsId) {
        try {
            Example example = new Example(ChatMessages.class);
            Example.Criteria criteria = example.createCriteria();
            
            // 构建查询条件：两个用户之间关于特定闲置物品的聊天记录
            criteria.andEqualTo("goodsId", goodsId);

            // 使用条件表达式：(senderId=userId AND receiverId=otherUserId OR senderId=otherUserId AND receiverId=userId)
            String condition = "(sender_id = " + senderId + " AND receiver_id = " + receiverId + " OR " +
                    "sender_id = " + receiverId + " AND receiver_id = " + senderId + ")";
            criteria.andCondition(condition);
            
            return chatMessagesMapper.selectCountByExample(example);
            
        } catch (Exception e) {
            log.error("获取聊天记录总数失败", e);
            return 0;
        }
    }

    //更新会话映射
    private void updateSessionMap(Map<String, Map<String, Object>> sessions, String key, 
                                ChatMessages message, Integer userId, boolean isReceived) {
        
        // 检查会话是否已存在
        Map<String, Object> session = sessions.computeIfAbsent(key, k -> new HashMap<>());
        
        // 获取现有的最后消息时间
        Date existingTime = (Date) session.get("lastMessageTime");
        
        // 如果没有现有时间或当前消息更新，则更新会话
        if (existingTime == null || message.getCreatedAt().after(existingTime)) {
            // 确定对方用户ID
            Integer otherUserId = isReceived ? message.getSenderId() : message.getReceiverId();
            
            session.put("otherUserId", otherUserId);
            session.put("goodsId", message.getGoodsId());
            session.put("lastMessage", message.getContent());
            session.put("lastMessageType", message.getType());
            session.put("lastMessageTime", message.getCreatedAt());
            session.put("unreadCount", getSessionUnreadCount(userId, otherUserId, message.getGoodsId()));

        }
    }

     //获取特定会话的未读消息数量
    private int getSessionUnreadCount(Integer userId, Integer otherUserId, Integer goodsId) {
        Example example = new Example(ChatMessages.class);
        example.createCriteria()
              .andEqualTo("receiverId", userId)
              .andEqualTo("senderId", otherUserId)
              .andEqualTo("goodsId", goodsId)
              .andEqualTo("isRead", false);
        
        return chatMessagesMapper.selectCountByExample(example);
    }
} 