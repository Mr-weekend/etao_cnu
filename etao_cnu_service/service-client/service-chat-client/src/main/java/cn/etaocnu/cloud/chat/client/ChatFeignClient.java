package cn.etaocnu.cloud.chat.client;

import cn.etaocnu.cloud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(value = "service-chat")
public interface ChatFeignClient {

    @PostMapping("/chat/start")
    Result<Map<String, Object>> startChat(
            @RequestParam("buyerId") Integer buyerId,
            @RequestParam("sellerId") Integer sellerId,
            @RequestParam("goodsId") Integer goodsId);

    @GetMapping("/chat/history")
    Result<Map<String, Object>> getChatHistory(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size);

    @Operation(summary = "获取用户的聊天会话列表")
    @GetMapping("/chat/sessions")
    Result<Map<String, Object>> getChatSessions(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size);

    @PostMapping("/chat/mark-read")
    Result<Boolean> markMessageAsRead(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId);

    @GetMapping("/chat/unread/count")
    Result<Integer> getUnreadCount(
            @RequestParam("userId") Integer userId);

    @GetMapping("/chat/unread/count/goods")
    Result<Integer> getUnreadCountByUserAndGoods(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId);


    @GetMapping("/chat/history/count")
    Result<Integer> getChatHistoryCount(
            @RequestParam("receiverId") Integer receiverId,
            @RequestParam("senderId") Integer senderId,
            @RequestParam("goodsId") Integer goodsId);

    @PostMapping(value = "/chat/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> uploadImage(@RequestPart("image") MultipartFile image);
}
