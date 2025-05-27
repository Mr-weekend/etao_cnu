package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CommentVo;
import cn.etaocnu.cloud.user.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Tag(name = "评论服务接口")
@RestController
@RefreshScope
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "发布评价")
    @PostMapping(value = "/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> publishComment(
            @RequestHeader("token") String token,
            @RequestPart("commentInfo") String commentInfo,
            @RequestParam(value = "files", required = false) MultipartFile[] files){
        return commentService.publishComment(token, commentInfo, files);
    }

    @Operation(summary = "通过订单ID获取评价详情")
    @GetMapping("/order/{orderId}")
    public Result<CommentVo> getCommentByOrderId(@RequestHeader("token") String token,
                                                 @PathVariable int orderId){
        return commentService.getCommentByOrderId(token, orderId);
    }

    @Operation(summary = "通过用户ID获取评价详情")
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getCommentByUserId(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        return commentService.getCommentByUserId(userId, page, size);
    }

    @Operation(summary = "获取作为买家是否已评价")
    @GetMapping("/isComment/buyer")
    public Result<Boolean> getIsCommentAsBuyer(@RequestHeader("token") String token,
                                               @RequestParam("orderId") int orderId) {
        return commentService.isCommentAsBuyer(token, orderId);
    }

    @Operation(summary = "获取作为卖家是否已评价")
    @GetMapping("/isComment/seller")
    public Result<Boolean> getIsCommentAsSeller(@RequestHeader("token") String token,
                                                @RequestParam("orderId") int orderId) {
        return commentService.isCommentAsSeller(token, orderId);
    }

    @Operation(summary = "获取用户收到的评价数量")
    @GetMapping("/count/{userId}")
    public Result<Integer> getCommentCount(@PathVariable int userId){
        return commentService.countComment(userId);
    }
}
