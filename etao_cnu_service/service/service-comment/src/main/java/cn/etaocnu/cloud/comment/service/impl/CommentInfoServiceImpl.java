package cn.etaocnu.cloud.comment.service.impl;

import cn.etaocnu.cloud.comment.mapper.CommentImageMapper;
import cn.etaocnu.cloud.comment.mapper.CommentMapper;
import cn.etaocnu.cloud.comment.service.CommentInfoService;
import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.entity.*;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.CommentGoodsVo;
import cn.etaocnu.cloud.model.vo.CommentVo;
import cn.etaocnu.cloud.model.vo.NewCommentVo;
import cn.etaocnu.cloud.model.vo.UserVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentInfoServiceImpl implements CommentInfoService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentImageMapper commentImageMapper;

    @Resource
    private FileService fileService;

    @Override
    public Boolean publishComment(int userId, String commentInfoJson, MultipartFile[] images) {
        try {
            NewCommentVo newCommentVo = new NewCommentVo();
            if (commentInfoJson != null && !commentInfoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                newCommentVo = objectMapper.readValue(commentInfoJson, NewCommentVo.class);
                log.info("解析后的闲置物品信息对象: {}", newCommentVo);
            }
            Example example = new Example(Comment.class);
            example.createCriteria()
                    .andEqualTo("orderId",newCommentVo.getOrderId())
                    .andEqualTo("userId",userId);
            if (commentMapper.selectCountByExample(example) > 0) {
                log.error("评价已存在！");
                return false;
            }

            Comment comment = new Comment();
            BeanUtils.copyProperties(newCommentVo,comment);
            comment.setUserId(userId);
            comment.setCreatedAt(new Date());
            if (commentMapper.insertSelective(comment) <= 0) {
                return false;
            }
            // 保存评价图片
            if (images != null && images.length > 0) {
                //上传图片到本地
                List<FileResponse> fileResponseList = fileService.uploadFiles(images,"comment");
                if (!fileResponseList.isEmpty()){
                    for (FileResponse fileResponse : fileResponseList) {
                        CommentImage commentImage = new CommentImage();
                        commentImage.setCommentId(comment.getCommentId());
                        commentImage.setImageUrl(fileResponse.getFileUrl());
                        commentImageMapper.insertSelective(commentImage);
                    }
                }
            }
            return true;
        }catch (Exception e) {
            log.error("发布评价失败");
            return false;
        }
    }

    @Override
    public CommentVo getCommentByOrderId(int orderId) {
        if (commentMapper.getOrder(orderId) == null) {
            return null;
        }
        Example example = new Example(Comment.class);
        example.createCriteria().andEqualTo("orderId",orderId);
        //查询评价列表
        List<Comment> comments = commentMapper.selectByExample(example);
        if (comments == null || comments.isEmpty()) {
            return null;
        }
        //通过订单id查询闲置物品id
        Order order = commentMapper.getOrder(orderId);
        //通过闲置物品id查询闲置物品信息
        Goods goods = commentMapper.getGoods(order.getGoodsId());
        CommentGoodsVo commentGoodsVo = new CommentGoodsVo();
        List<GoodsImage> goodsImages = commentMapper.getGoodsImage(order.getGoodsId());
        BeanUtils.copyProperties(goods,commentGoodsVo);
        commentGoodsVo.setImageUrl(goodsImages.get(0).getImageUrl());


        CommentVo commentVo = new CommentVo();
        commentVo.setOrderId(orderId);
        //设置闲置物品信息
        commentVo.setGoodsInfo(commentGoodsVo);
        //设置订单成交时间
        commentVo.setCompletedTime(order.getCompletedAt());
        //查询买家信息
        //如何确定是买家还是卖家发布的评价？用comment表中的orderid查询order表(order表中有buyerid)，如果与comment表中的userid相等，则为买家
        for (Comment comment : comments) {
            if(Objects.equals(comment.getUserId(), order.getBuyerId())){
                log.info("这是买家");
                //设置买家信息
                //匿名，设置买家信息为空
                if (comment.getIsAnonymous()){
                    commentVo.setBuyer(null);
                }else{
                    UserVo userVo = commentMapper.getUserInfo(comment.getUserId());
                    commentVo.setBuyer(userVo);
                }
                commentVo.setBuyerCommentType(comment.getType());
                commentVo.setBuyerCommentContent(comment.getContent());
                commentVo.setBuyerCommentTime(comment.getCreatedAt());
                //查询评价关联图片
                Example imageExample = new Example(CommentImage.class);
                imageExample.createCriteria().andEqualTo("commentId",comment.getCommentId());
                List<CommentImage> images = commentImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(CommentImage::getImageUrl)
                        .collect(Collectors.toList());
                commentVo.setBuyerCommentImageUrls(imageUrls);
            }else{//设置卖家信息
                log.info("这是卖家哦");
                if (comment.getIsAnonymous()){
                    commentVo.setSeller(null);
                }else{
                    UserVo userVo = commentMapper.getUserInfo(comment.getUserId());
                    commentVo.setSeller(userVo);
                }
                commentVo.setSellerCommentType(comment.getType());
                commentVo.setSellerCommentContent(comment.getContent());
                commentVo.setSellerCommentTime(comment.getCreatedAt());
                //查询评价关联图片
                Example imageExample = new Example(CommentImage.class);
                imageExample.createCriteria().andEqualTo("commentId", comment.getCommentId());
                List<CommentImage> images = commentImageMapper.selectByExample(imageExample);
                List<String> imageUrls = images.stream()
                        .map(CommentImage::getImageUrl)
                        .collect(Collectors.toList());
                commentVo.setSellerCommentImageUrls(imageUrls);
            }
        }
        return commentVo;
    }

    @Override
    public List<CommentVo> getCommentByUserId(int userId, int page, int size) {
        try {
            // 查询该用户参与的所有评论
            Example userExample = new Example(Comment.class);
            userExample.createCriteria().andEqualTo("userId", userId);
            List<Comment> userComments = commentMapper.selectByExample(userExample);
            
            // 获取所有相关的订单ID
            Set<Integer> orderIds = userComments.stream()
                    .map(Comment::getOrderId)
                    .collect(Collectors.toSet());
            
            // 对每个订单ID检查是否有双方的评价
            List<Integer> completedOrderIds = new ArrayList<>();
            for (Integer orderId : orderIds) {
                Example orderExample = new Example(Comment.class);
                orderExample.createCriteria().andEqualTo("orderId", orderId);
                List<Comment> orderComments = commentMapper.selectByExample(orderExample);
                
                // 只有当订单有两条评价记录时才添加到完成列表
                if (orderComments.size() == 2) {
                    completedOrderIds.add(orderId);
                }
            }
            
            // 按时间倒序排序
            completedOrderIds.sort((a, b) -> {
                Order orderA = commentMapper.getOrder(a);
                Order orderB = commentMapper.getOrder(b);
                if (orderA != null && orderB != null) {
                    return orderB.getCreatedAt().compareTo(orderA.getCreatedAt());
                }
                return 0;
            });
            
            // 分页处理
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, completedOrderIds.size());
            
            // 边界检查
            if (startIndex >= completedOrderIds.size()) {
                return new ArrayList<>();
            }
            
            // 获取当前页的订单ID
            List<Integer> currentPageOrderIds = completedOrderIds.subList(startIndex, endIndex);
            
            // 构建结果列表
            List<CommentVo> commentVoList = new ArrayList<>();
            
            for (Integer orderId : currentPageOrderIds) {
                // 获取该订单的所有评价
                Example orderExample = new Example(Comment.class);
                orderExample.createCriteria().andEqualTo("orderId", orderId);
                List<Comment> orderComments = commentMapper.selectByExample(orderExample);
                
                // 获取订单和闲置物品信息
                Order order = commentMapper.getOrder(orderId);
                if (order != null) {
                    Goods goods = commentMapper.getGoods(order.getGoodsId());
                    CommentGoodsVo commentGoodsVo = new CommentGoodsVo();
                    List<GoodsImage> goodsImages = commentMapper.getGoodsImage(order.getGoodsId());
                    BeanUtils.copyProperties(goods,commentGoodsVo);
                    commentGoodsVo.setImageUrl(goodsImages.get(0).getImageUrl());

                    // 构建CommentVo对象
                    CommentVo commentVo = new CommentVo();
                    commentVo.setOrderId(orderId);
                    commentVo.setGoodsInfo(commentGoodsVo);
                    commentVo.setCompletedTime(order.getCompletedAt());
                    
                    // 处理买家和卖家的评价信息
                    for (Comment comment : orderComments) {
                        if (Objects.equals(comment.getUserId(), order.getBuyerId())) {
                            // 买家信息处理
                            if (comment.getIsAnonymous()) {
                                commentVo.setBuyer(null);
                            } else {
                                UserVo userVo = commentMapper.getUserInfo(comment.getUserId());
                                commentVo.setBuyer(userVo);
                            }
                            commentVo.setBuyerCommentType(comment.getType());
                            commentVo.setBuyerCommentContent(comment.getContent());
                            commentVo.setBuyerCommentTime(comment.getCreatedAt());
                            
                            // 买家评价图片
                            Example imageExample = new Example(CommentImage.class);
                            imageExample.createCriteria().andEqualTo("commentId", comment.getCommentId());
                            List<CommentImage> images = commentImageMapper.selectByExample(imageExample);
                            List<String> imageUrls = images.stream()
                                    .map(CommentImage::getImageUrl)
                                    .collect(Collectors.toList());
                            commentVo.setBuyerCommentImageUrls(imageUrls);
                        } else {
                            // 卖家信息处理
                            if (comment.getIsAnonymous()) {
                                commentVo.setSeller(null);
                            } else {
                                UserVo userVo = commentMapper.getUserInfo(comment.getUserId());
                                commentVo.setSeller(userVo);
                            }
                            commentVo.setSellerCommentType(comment.getType());
                            commentVo.setSellerCommentContent(comment.getContent());
                            commentVo.setSellerCommentTime(comment.getCreatedAt());
                            
                            // 卖家评价图片
                            Example imageExample = new Example(CommentImage.class);
                            imageExample.createCriteria().andEqualTo("commentId", comment.getCommentId());
                            List<CommentImage> images = commentImageMapper.selectByExample(imageExample);
                            List<String> imageUrls = images.stream()
                                    .map(CommentImage::getImageUrl)
                                    .collect(Collectors.toList());
                            commentVo.setSellerCommentImageUrls(imageUrls);
                        }
                    }
                    
                    commentVoList.add(commentVo);
                }
            }
            
            return commentVoList;
            
        } catch (Exception e) {
            log.error("获取用户评价列表失败！", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Boolean isCommentAsBuyer(int userId, int orderId) {
        Comment comment = commentMapper.getIsCommentAsBuyer(orderId, userId);
        return comment != null;
    }

    @Override
    public Boolean isCommentAsSeller(int userId, int orderId) {
        Comment comment = commentMapper.getIsCommentAsSeller(orderId, userId);
        return comment != null;
    }


    @Override
    public int countComment(int userId) {
        log.info("统计用户评价记录数量: userId={}", userId);
        
        try {
            // 查询该用户参与的所有评论
            Example userExample = new Example(Comment.class);
            userExample.createCriteria().andEqualTo("userId", userId);
            List<Comment> userComments = commentMapper.selectByExample(userExample);
            
            // 获取所有与该用户相关的订单ID
            Set<Integer> orderIds = userComments.stream()
                    .map(Comment::getOrderId)
                    .collect(Collectors.toSet());
            
            // 统计双方都已评价的订单数量
            int completedCount = 0;
            for (Integer orderId : orderIds) {
                Example orderExample = new Example(Comment.class);
                orderExample.createCriteria().andEqualTo("orderId", orderId);
                List<Comment> orderComments = commentMapper.selectByExample(orderExample);
                
                // 只有当订单有两条评价记录时才计数
                if (orderComments.size() == 2) {
                    completedCount++;
                }
            }
            
            return completedCount;
        } catch (Exception e) {
            log.error("统计评价记录数量失败！", e);
            return 0;
        }
    }
}
