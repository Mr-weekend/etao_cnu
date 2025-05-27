package cn.etaocnu.cloud.history.mapper;

import cn.etaocnu.cloud.model.entity.BrowseHistory;
import cn.etaocnu.cloud.model.vo.BrowseHistoryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrowseHistoryMapper extends Mapper<BrowseHistory> {
    @Select("SELECT bh.browse_time, g.goods_id, g.description, g.price, g.category_id, g.status, " +
            "u.user_id, u.user_name, u.avatar_url " +
            "FROM browse_history bh " +
            "JOIN goods g ON bh.goods_id = g.goods_id " +
            "JOIN users u ON g.user_id = u.user_id " +
            "WHERE bh.user_id = #{userId} " +
            "ORDER BY bh.browse_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<BrowseHistoryVo> getBrowseHistoryDetails(
            @Param("userId") int userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select("SELECT gi.image_url FROM goods_image gi JOIN browse_history bh ON bh.goods_id = gi.goods_id WHERE bh.goods_id = #{goodsId} LIMIT 1")
    String getImageUrl(@Param("goodsId") int goodsId);

    @Update("UPDATE goods SET view_count = view_count + 1 WHERE goods_id = #{goodsId}")
    int updateViewCount(@Param("goodsId") int goodsId);

}