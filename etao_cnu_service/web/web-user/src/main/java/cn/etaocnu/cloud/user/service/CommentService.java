package cn.etaocnu.cloud.user.service;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.vo.CommentVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CommentService {
    //发布评价
    Result<Boolean> publishComment(String token, String commentInfo, MultipartFile[] files);
    //获取某个闲置物品的评价信息
    Result<CommentVo> getCommentByOrderId(String token, int orderId);
    //获取用户的评价信息列表
    Result<Map<String, Object>> getCommentByUserId(int userId, int page, int size);
    //查看作为买家某个订单是否已评价
    Result<Boolean> isCommentAsBuyer(String token, int orderId);
    //查看作为卖家某个订单是否已评价
    Result<Boolean> isCommentAsSeller(String token, int orderId);
    //获取用户收到的评价总数
    Result<Integer> countComment(int userId);
}
