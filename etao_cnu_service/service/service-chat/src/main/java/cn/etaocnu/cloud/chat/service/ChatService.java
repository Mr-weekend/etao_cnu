package cn.etaocnu.cloud.chat.service;

import cn.etaocnu.cloud.model.vo.ChatMessagesVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ChatService {

     //处理新接收的消息
    void processMessage(ChatMessagesVo message);

     //发送用户的未读消息
    void sendUnreadMessages(Integer userId);

     //获取两个用户之间关于某个闲置物品的聊天记录
    List<ChatMessagesVo> getChatHistory(Integer receiverId, Integer senderId, Integer goodsId, Integer page, Integer size);

     //获取用户的聊天会话列表
    List<Map<String, Object>> getChatSessionList(Integer userId);

     //将消息标记为已读
    boolean markAsRead(Integer receiverId, Integer senderId, Integer goodsId);

     //获取用户未读消息数量
    int getUnreadCount(Integer userId);

    //获取指定用户与另一用户关于特定闲置物品的未读消息数量
    int getUnreadCountByUserAndGoods(Integer receiverId, Integer senderId, Integer goodsId);

    //获取两个用户间关于闲置物品的聊天记录总数
    int getChatHistoryCount(Integer receiverId, Integer senderId, Integer goodsId);

    //上传图片 返回图片地址
    String uploadImage(MultipartFile image);
} 