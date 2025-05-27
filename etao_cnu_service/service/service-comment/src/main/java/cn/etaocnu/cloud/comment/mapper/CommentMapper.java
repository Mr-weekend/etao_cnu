package cn.etaocnu.cloud.comment.mapper;

import cn.etaocnu.cloud.model.entity.Comment;
import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.GoodsImage;
import cn.etaocnu.cloud.model.entity.Order;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CommentMapper extends Mapper<Comment> {
    @Select("SELECT user_id, user_name, gender, avatar_url, user_profile, status FROM users WHERE user_id = #{userId}")
    UserVo getUserInfo(@Param("userId") int userId);

    @Select("SELECT * FROM goods WHERE goods_id = #{goodsId}")
    Goods getGoods(@Param("goodsId") int goodsId);

    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order getOrder(@Param("orderId") int orderId);

    @Select("SELECT c.* FROM comments c JOIN orders o ON o.order_id = c.order_id " +
            "WHERE o.order_id = #{orderId} AND o.buyer_id = #{userId} " +
            "AND c.user_id = #{userId} AND o.status = 'completed'")
    Comment getIsCommentAsBuyer(@Param("orderId") int orderId, @Param("userId") int userId);

    @Select("SELECT c.* FROM comments c JOIN orders o ON o.order_id = c.order_id " +
            "JOIN goods g ON o.goods_id = g.goods_id WHERE o.order_id = #{orderId} " +
            "AND g.user_id = #{userId} AND c.user_id = #{userId} AND o.status = 'completed'")
    Comment getIsCommentAsSeller(@Param("orderId") int orderId, @Param("userId") int userId);

    @Select("SELECT * FROM goods_image WHERE goods_id = #{goodsId}")
    List<GoodsImage> getGoodsImage(@Param("goodsId") int goodsId);
}