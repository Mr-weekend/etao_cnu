package cn.etaocnu.cloud.order.mapper;


import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.Order;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface OrderMapper extends Mapper<Order> {
    @Update("UPDATE goods SET status = 'sold', sold_time = #{date} WHERE goods_id = #{goodsId}")
    int updateStatus(@Param("goodsId") int goodsId, @Param("date") Date date);

    @Select("SELECT user_id, user_name, gender, avatar_url, user_profile, status FROM users WHERE user_id = #{userId}")
    UserVo getUserInfo(@Param("userId") int userId);

    @Select("SELECT user_id, goods_id, description, price, category_id, status, view_count, collect_count, publish_time FROM goods " +
            "WHERE goods_id = #{goodsId}")
    Goods getGoodsById(@Param("goodsId") int goodsId);

    @Select("SELECT image_url FROM goods_image WHERE goods_id = #{goodsId}")
    List<String> getGoodsImageList(@Param("goodsId") int goodsId);

    @Select("SELECT o.* FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE g.user_id = #{userId} AND g.status = 'sold' AND o.seller_deleted = 0 " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getOrderAsSeller(@Param("userId")int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE g.user_id = #{userId} AND g.status = 'sold' AND o.seller_deleted = 0 ")
    int countOrderAsSeller(@Param("userId")int userId);

    @Select("SELECT o.* FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE o.buyer_id = #{userId} AND g.status = 'sold' AND o.buyer_deleted = 0 " +
            "AND description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> searchOrderAsBuyer(@Param("keyword") String keyword, @Param("userId")int userId,
                                   @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT o.* FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE g.user_id = #{userId} AND g.status = 'sold' AND o.seller_deleted = 0 " +
            "AND description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> searchOrderAsSeller(@Param("keyword") String keyword, @Param("userId")int userId,
                                    @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE o.buyer_id = #{userId} AND g.status = 'sold' AND o.buyer_deleted = 0 " +
            "AND description LIKE CONCAT('%', #{keyword}, '%') ")
    int countSearchOrderAsBuyer(@Param("keyword") String keyword, @Param("userId")int userId);

    @Select("SELECT COUNT(*) FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE g.user_id = #{userId} AND g.status = 'sold' AND o.seller_deleted = 0 " +
            "AND description LIKE CONCAT('%', #{keyword}, '%') ")
    int countSearchOrderAsSeller(@Param("keyword") String keyword, @Param("userId")int userId);

    @Select("SELECT o.* FROM orders o INNER JOIN goods g ON o.goods_id = g.goods_id " +
            "LEFT JOIN comments c ON o.order_id = c.order_id AND c.user_id = o.buyer_id " +
            "WHERE o.buyer_id = #{userId} AND o.status = 'completed' AND c.comment_id IS NULL " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getUnCommentOrderAsBuyer(@Param("userId")int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT o.* FROM orders o INNER JOIN goods g ON o.goods_id = g.goods_id " +
            "LEFT JOIN comments c ON o.order_id = c.order_id AND c.user_id = g.user_id " +
            "WHERE g.user_id = #{userId} AND o.status = 'completed' AND c.order_id IS NULL " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getUnCommentOrderAsSeller(@Param("userId")int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM orders o LEFT JOIN comments c ON o.order_id = c.order_id " +
            "AND c.user_id = o.buyer_id WHERE o.buyer_id = #{userId} AND o.status = 'completed' AND c.comment_id IS NULL")
    int countUnCommentOrderAsBuyer(@Param("userId")int userId);

    @Select("SELECT COUNT(*) FROM orders o INNER JOIN goods g ON o.goods_id = g.goods_id " +
            "LEFT JOIN comments c ON o.order_id = c.order_id AND c.user_id = g.user_id " +
            "WHERE g.user_id = #{userId} AND o.status = 'completed' AND c.order_id IS NULL")
    int countUnCommentOrderAsSeller(@Param("userId")int userId);

    @Select("SELECT COUNT(*) AS total_uncommented FROM orders o LEFT JOIN goods g " +
            "ON o.goods_id = g.goods_id LEFT JOIN comments c_seller " +
            "ON o.order_id = c_seller.order_id AND c_seller.user_id = g.user_id " +
            "LEFT JOIN comments c_buyer ON o.order_id = c_buyer.order_id " +
            "AND c_buyer.user_id = o.buyer_id WHERE o.status = 'completed' " +
            "AND ((g.user_id = #{userId} AND c_seller.comment_id IS NULL) OR (o.buyer_id = #{userId} AND c_buyer.comment_id IS NULL))")
    int countUncommentOrder(@Param("userId")int userId);
}