package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ChatService {

    Result<Map<String, Object>> startChat(String token, Integer sellerId, Integer goodsId);

    Result<Map<String, Object>> getChatHistory(String token, Integer senderId, Integer goodsId, Integer page, Integer size);

    Result<Map<String, Object>> getChatSessions(String token, Integer page, Integer size);

    Result<Boolean> markAsRead(String token, Integer senderId, Integer goodsId);

    Result<Integer> getUnreadCount(String token);

    Result<Integer> getUnreadCountByUserAndGoods(String token, Integer senderId, Integer goodsId);

    Result<Integer> getChatHistoryCount(String token, Integer senderId, Integer goodsId);

    Result<String> uploadImage(String token, MultipartFile image);
}
