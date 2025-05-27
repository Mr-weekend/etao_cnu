package cn.etaocnu.cloud.comment.client;

import cn.etaocnu.cloud.comment.client.config.FeignMultipartConfig;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CommentVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(value = "service-comment", configuration = FeignMultipartConfig.class)
public interface CommentFeignClient {

    @Operation(summary = "发布评价")
    @PostMapping(value = "/comment/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> publishComment(@RequestParam("userId") int userId,
                                          @RequestPart("commentInfo") String commentInfoJson,
                                          @RequestPart(value = "files", required = false) MultipartFile[] files);

    @Operation(summary = "通过订单ID获取评价详情")
    @GetMapping("comment/order/{orderId}")
    Result<CommentVo> getCommentByOrderId(@PathVariable int orderId);

    @Operation(summary = "通过用户ID获取评价详情")
    @GetMapping("/comment/user/{userId}")
    Result<Map<String, Object>> getCommentByUserId(
            @PathVariable("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @Operation(summary = "获取作为买家是否已评价")
    @GetMapping("/comment/isComment/buyer/{userId}")
    Result<Boolean> getIsCommentAsBuyer(@PathVariable int userId,
                                        @RequestParam("orderId") int orderId);

    @Operation(summary = "获取作为卖家是否已评价")
    @GetMapping("/comment/isComment/seller/{userId}")
    Result<Boolean> getIsCommentAsSeller(@PathVariable int userId,
                                         @RequestParam("orderId") int orderId);

    @Operation(summary = "获取用户收到的评价数量")
    @GetMapping("/comment/count/{userId}")
    Result<Integer> getCommentCount(@PathVariable int userId);
}
