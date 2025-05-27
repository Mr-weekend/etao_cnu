package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.user.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
@Tag(name = "聊天接口", description = "提供聊天相关功能")
public class ChatController {

    @Resource
    private ChatService chatService;

    @Operation(summary = "发起基于闲置物品的聊天")
    @PostMapping("/start")
    public Result<Map<String, Object>> startChat(
            @RequestHeader("token") String token,
            @RequestParam("sellerId") Integer sellerId,
            @RequestParam("goodsId") Integer goodsId) {
        return chatService.startChat(token, sellerId, goodsId);
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/history")
    public Result<Map<String, Object>> getChatHistory(
            @RequestHeader("token") String token,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return chatService.getChatHistory(token, senderId, goodsId, page, size);
    }

    @Operation(summary = "获取用户聊天会话列表")
    @GetMapping("/sessions")
    public Result<Map<String, Object>> getChatSessions(
            @RequestHeader("token") String token,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return chatService.getChatSessions(token,page,size);
    }

    @Operation(summary = "标记消息为已读")
    @PostMapping("/mark-read")
    public Result<Boolean> markMessageAsRead(
            @RequestHeader("token") String token,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        return chatService.markAsRead(token, senderId, goodsId);
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(@RequestHeader("token") String token) {
        return chatService.getUnreadCount(token);
    }

    @Operation(summary = "获取用户间关于闲置物品的未读消息数量")
    @GetMapping("/unread/count/goods")
    public Result<Integer> getUnreadCountByUserAndGoods(
            @RequestHeader("token") String token,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        return chatService.getUnreadCountByUserAndGoods(token, senderId, goodsId);
    }

    @Operation(summary = "获取两个用户间关于闲置物品的聊天记录总数")
    @GetMapping("/history/count")
    public Result<Integer> getChatHistoryCount(
            @RequestHeader("token") String token,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        return chatService.getChatHistoryCount(token, senderId, goodsId);
    }

    @Operation(summary = "上传聊天图片")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestHeader("token") String token,
                                      @RequestParam("image") MultipartFile image) {
        return chatService.uploadImage(token, image);
    }
    
    @Operation(summary = "获取WebSocket连接地址")
    @GetMapping("/ws-url/{userId}")
    public Result<String> getWebSocketUrl(@PathVariable("userId") Integer userId) {
        // 返回WebSocket连接URL，前端可以通过这个URL连接到WebSocket服务
        String webSocketUrl = "ws://" + "localhost:8989" + "/chat/" + userId;
        return Result.success(webSocketUrl);
    }
}
