package cn.etaocnu.cloud.chat.controller;

import cn.etaocnu.cloud.chat.service.ChatService;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.ChatMessagesVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RefreshScope
@RequestMapping("/chat")
@Tag(name = "聊天服务接口", description = "提供聊天记录查询、会话列表等功能")
public class ChatController {

    @Resource
    private ChatService chatService;

    @Operation(summary = "发起基于闲置物品的聊天")
    @PostMapping("/start")
    public Result<Map<String, Object>> startChat(
            @RequestParam("buyerId") Integer buyerId,  // 买家ID（当前用户）
            @RequestParam("sellerId") Integer sellerId,  // 卖家ID
            @RequestParam("goodsId") Integer goodsId) {
        // 返回聊天会话信息
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", buyerId + "_" + sellerId + "_" + goodsId);
        result.put("buyerId", buyerId);
        result.put("sellerId", sellerId);
        result.put("goodsId", goodsId);
        
        return Result.success(result);
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/history")
    public Result<Map<String, Object>> getChatHistory(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {

        List<ChatMessagesVo> history = chatService.getChatHistory(receiverId, senderId, goodsId, page, size);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        int total = chatService.getChatHistoryCount(receiverId, senderId, goodsId);
        result.put("list", history);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success(result);
    }

    @Operation(summary = "获取用户的聊天会话列表")
    @GetMapping("/sessions")
    public Result<Map<String, Object>> getChatSessions(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<Map<String, Object>> sessions = chatService.getChatSessionList(userId);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        int total = sessions.size();
        result.put("list", sessions);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return Result.success(result);
    }

    @Operation(summary = "将消息标记为已读")
    @PostMapping("/mark-read")
    public Result<Boolean> markMessageAsRead(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        
        boolean success = chatService.markAsRead(receiverId, senderId, goodsId);
        return Result.success(success);
    }

    @Operation(summary = "获取用户未读消息数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(@RequestParam("userId") Integer userId) {
        int count = chatService.getUnreadCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "获取用户间关于闲置物品的未读消息数量")
    @GetMapping("/unread/count/goods")
    public Result<Integer> getUnreadCountByUserAndGoods(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        int count = chatService.getUnreadCountByUserAndGoods(receiverId, senderId, goodsId);
        return Result.success(count);
    }

    @Operation(summary = "获取两个用户间关于闲置物品的聊天记录总数")
    @GetMapping("/history/count")
    public Result<Integer> getChatHistoryCount(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId) {
        int count = chatService.getChatHistoryCount(receiverId, senderId, goodsId);
        return Result.success(count);
    }

    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<String> updateAvatar(@RequestParam("image") MultipartFile image) {
        log.info("上传图片: image={}", image.getOriginalFilename());
        String result = chatService.uploadImage(image);
        return result != null ? Result.success("图片保存成功",result) : Result.fail("图片保存失败");
    }
} 