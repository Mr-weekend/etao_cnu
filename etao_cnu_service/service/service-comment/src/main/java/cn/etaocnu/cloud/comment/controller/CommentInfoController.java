package cn.etaocnu.cloud.comment.controller;

import cn.etaocnu.cloud.comment.service.CommentInfoService;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CommentVo;
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
@Tag(name = "评价服务接口")
@RestController
@RefreshScope
@RequestMapping("/comment")
public class CommentInfoController {
    @Resource
    private CommentInfoService commentInfoService;

    @Operation(summary = "发布评价")
    @PostMapping("/publish")
    public Result<Boolean> publishComment(@RequestParam("userId") int userId,
                                          @RequestPart("commentInfo") String commentInfoJson,
                                          @RequestPart(value = "images", required = false) MultipartFile[] files) {
        log.info("发布评价: 图片数量={}, 评价信息JSON={}",
                files != null ? files.length : 0, commentInfoJson);
        if (files != null) {
            for (MultipartFile file : files) {
                log.info("接收到文件: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            }
        }
        Boolean result = commentInfoService.publishComment(userId, commentInfoJson, files);
        return result ? Result.success("发布评价成功！", true) : Result.fail("发布评价失败！",false);
    }

    @Operation(summary = "通过订单ID获取评价详情")
    @GetMapping("/order/{orderId}")
    public Result<CommentVo> getCommentByOrderId(@PathVariable int orderId) {
        log.info("获取评价详情，订单号: {}", orderId);
        CommentVo commentVo = commentInfoService.getCommentByOrderId(orderId);
        return commentVo == null ? Result.fail("通过订单ID获取评价详情失败！") : Result.success("通过订单ID获取评价详情成功！", commentVo);
    }

    @Operation(summary = "通过用户ID获取评价列表")
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getCommentByUserId(
            @PathVariable int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("获取评价详情，userId: {}, page: {}, size: {}", userId, page, size);
        
        // 获取评价列表
        List<CommentVo> commentList = commentInfoService.getCommentByUserId(userId, page, size);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        if (commentList != null && !commentList.isEmpty()) {
            // 获取总记录数
            int total = commentInfoService.countComment(userId);
            log.info("获取到{}条评价记录", total);
            // 计算总页数
            int pages = (total + size - 1) / size;
            // 填充结果
            result.put("list", commentList);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", pages);
        } else {
            // 空结果
            result.put("list", commentList);
            result.put("total", 0);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", 0);
        }
        return Result.success("获取用户评价列表成功！",result);
    }

    @Operation(summary = "获取作为买家是否已评价")
    @GetMapping("/isComment/buyer/{userId}")
    public Result<Boolean> getIsCommentAsBuyer(@PathVariable int userId,
                                                 @RequestParam("orderId") int orderId) {
        Boolean result = commentInfoService.isCommentAsBuyer(userId, orderId);
        log.info("获取用户作为买家是否已评价，userId: {}, orderId: {}", userId, orderId);
        return Result.success("获取用户作为买家是否已评价的结果成功！",result);
    }

    @Operation(summary = "获取作为卖家是否已评价")
    @GetMapping("/isComment/seller/{userId}")
    public Result<Boolean> getIsCommentAsSeller(@PathVariable int userId,
                                                 @RequestParam("orderId") int orderId) {
        log.info("获取用户作为卖家是否已评价，userId: {}, orderId: {}", userId, orderId);
        Boolean result = commentInfoService.isCommentAsSeller(userId, orderId);
        return Result.success("获取用户作为卖家是否已评价的结果成功！",result);
    }

    @Operation(summary = "获取用户收到的评价数量")
    @GetMapping("/count/{userId}")
    public Result<Integer> getCommentCount(@PathVariable int userId) {
        return Result.success("获取用户收到的评价数量成功！", commentInfoService.countComment(userId));
    }

}
