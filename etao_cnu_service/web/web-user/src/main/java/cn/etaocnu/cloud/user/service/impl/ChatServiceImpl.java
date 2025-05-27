package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.chat.client.ChatFeignClient;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.user.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatFeignClient chatFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Map<String, Object>> startChat(String token, Integer sellerId, Integer goodsId) {
        Integer buyerId = getUserIdFromToken(token);
        if(buyerId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.startChat(buyerId, sellerId, goodsId);
    }

    @Override
    public Result<Map<String, Object>> getChatHistory(String token, Integer senderId, Integer goodsId, Integer page, Integer size) {
        Integer receiverId = getUserIdFromToken(token);
        if(receiverId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.getChatHistory(receiverId, senderId, goodsId, page, size);
    }

    @Override
    public Result<Map<String, Object>> getChatSessions(String token, Integer page, Integer size) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.getChatSessions(userId, page, size);
    }
    @Override
    public Result<Boolean> markAsRead(String token, Integer senderId, Integer goodsId) {
        Integer receiverId = getUserIdFromToken(token);
        if(receiverId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.markMessageAsRead(receiverId, senderId, goodsId);
    }

    @Override
    public Result<Integer> getUnreadCount(String token) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.getUnreadCount(userId);

    }

    @Override
    public Result<Integer> getUnreadCountByUserAndGoods(String token, Integer senderId, Integer goodsId) {
        Integer receiverId = getUserIdFromToken(token);
        if(receiverId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.getUnreadCountByUserAndGoods(receiverId, senderId, goodsId);
    }

    @Override
    public Result<Integer> getChatHistoryCount(String token, Integer senderId, Integer goodsId) {
        Integer receiverId = getUserIdFromToken(token);
        if(receiverId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.getChatHistoryCount(receiverId, senderId, goodsId);
    }

    @Override
    public Result<String> uploadImage(String token, MultipartFile image) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return chatFeignClient.uploadImage(image);
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }


}
