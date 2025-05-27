package cn.etaocnu.cloud.comment.service;

import cn.etaocnu.cloud.model.vo.CommentVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentInfoService {
    //发布评价
    Boolean publishComment(int userId, String commentInfoJson, MultipartFile[] images);
    //获取某个闲置物品的评价信息
    CommentVo getCommentByOrderId(int orderId);
    //获取用户的评价信息列表(分页)
    List<CommentVo> getCommentByUserId(int userId, int page, int size);
    //查看作为买家某个订单是否已评价
    Boolean isCommentAsBuyer(int userId,int orderId);
    //查看作为卖家某个订单是否已评价
    Boolean isCommentAsSeller(int userId, int orderId);
    //获取用户收到的评价总数
    int countComment(int userId);
}
