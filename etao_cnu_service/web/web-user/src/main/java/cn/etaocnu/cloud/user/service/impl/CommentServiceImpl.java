package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.comment.client.CommentFeignClient;
import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CommentVo;
import cn.etaocnu.cloud.user.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentFeignClient commentFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<Boolean> publishComment(String token, String commentInfo, MultipartFile[] files) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return commentFeignClient.publishComment(userId, commentInfo, files);
    }

    @Override
    public Result<CommentVo> getCommentByOrderId(String token, int orderId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return commentFeignClient.getCommentByOrderId(orderId);
    }

    @Override
    public Result<Boolean> isCommentAsBuyer(String token, int orderId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return commentFeignClient.getIsCommentAsBuyer(userId, orderId);
    }

    @Override
    public Result<Boolean> isCommentAsSeller(String token, int orderId) {
        Integer userId = getUserIdFromToken(token);
        if(userId == null) {
            return Result.fail("token过期，请重新登录！");
        }
        return commentFeignClient.getIsCommentAsSeller(userId, orderId);
    }

    @Override
    public Result<Map<String, Object>> getCommentByUserId(int userId, int page, int size) {
        return commentFeignClient.getCommentByUserId(userId, page, size);
    }

    @Override
    public Result<Integer> countComment(int userId) {
        return commentFeignClient.getCommentCount(userId);
    }

    private Integer getUserIdFromToken(String token) {
        String userId = redisTemplate.opsForValue().get("user:login:" + token);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return Integer.parseInt(userId);
    }
}
