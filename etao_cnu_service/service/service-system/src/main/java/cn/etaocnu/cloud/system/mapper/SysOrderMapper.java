package cn.etaocnu.cloud.system.mapper;


import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.entity.Order;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysOrderMapper extends Mapper<Order> {
    @Select("SELECT user_id, user_name, gender, avatar_url, user_profile, status FROM users WHERE user_id = #{userId}")
    UserVo getUserInfo(@Param("userId") int userId);

    @Select("SELECT user_id, goods_id, description, price, category_id, status, view_count, collect_count, publish_time FROM goods " +
            "WHERE goods_id = #{goodsId}")
    Goods getGoodsById(@Param("goodsId") int goodsId);

    @Select("SELECT image_url FROM goods_image WHERE goods_id = #{goodsId}")
    List<String> getGoodsImageList(@Param("goodsId") int goodsId);
    @Select("SELECT o.* FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE (g.user_id = #{userId} AND g.status = 'sold') OR (o.buyer_id = #{userId}) " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit} ")
    List<Order> getOrderListByUserId(@Param("userId") int userId,@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT o.* FROM orders o JOIN goods g ON o.goods_id = g.goods_id WHERE " +
            "g.description LIKE CONCAT('%', #{goodsKeyword}, '%') AND ((g.user_id = 1 AND g.status = 'sold') OR (o.buyer_id = 1)) " +
            "ORDER BY o.created_at DESC LIMIT #{offset}, #{limit}")
    List<Order> getOrderByGoodsKey(@Param("goodsKeyword") String goodsKeyword,@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM orders o JOIN goods g ON o.goods_id = g.goods_id WHERE " +
            "g.description LIKE CONCAT('%', #{goodsKeyword}, '%') AND ((g.user_id = 1 AND g.status = 'sold') OR (o.buyer_id = 1)) ")
    int countOrderByGoodsKey(@Param("goodsKeyword") String goodsKeyword);

    @Select("SELECT COUNT(*) FROM orders o JOIN goods g ON o.goods_id = g.goods_id " +
            "WHERE (g.user_id = #{userId} AND g.status = 'sold') OR (o.buyer_id = #{userId})")
    int countOrderListByUserId(@Param("userId") int userId);
}