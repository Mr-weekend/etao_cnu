package cn.etaocnu.cloud.collection.mapper;

import cn.etaocnu.cloud.model.entity.Collection;
import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CollectionMapper extends Mapper<Collection> {
    @Update("UPDATE goods SET collect_count = collect_count + 1 WHERE goods_id = #{goodsId}")
    int addCollectionCount(@Param("goodsId") int goodsId);

    @Update("UPDATE goods SET collect_count = GREATEST(collect_count - 1, 0) WHERE goods_id = #{goodsId}")
    int reduceCollectionCount(@Param("goodsId") int goodsId);

    @Update("<script>" +
            "UPDATE goods SET collect_count = GREATEST(collect_count - 1, 0) " +
            "WHERE goods_id IN " +
            "<foreach item='id' collection='goodsIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchReduceCollectionCount(@Param("goodsIds") List<Integer> goodsIds);

    @Select("SELECT user_id, goods_id, description, price, category_id, status, view_count, collect_count, publish_time FROM goods WHERE goods_id = #{goodsId}")
    Goods getGoodsById(@Param("goodsId") int goodsId);

    @Select("SELECT image_url FROM goods_image WHERE goods_id = #{goodsId}")
    List<String> getGoodsImageList(@Param("goodsId") int goodsId);

    @Select("SELECT user_id, user_name, gender, avatar_url, user_profile, status FROM users WHERE user_id = #{userId}")
    UserVo getUserInfo(@Param("userId") int userId);

    @Select("SELECT category_name FROM category WHERE category_id = #{categoryId}")
    String getCategoryName(@Param("categoryId") int categoryId);

    @Select("SELECT collection_id FROM collection WHERE goods_id = #{goodsId} AND user_id = #{userId}")
    Collection isCollectionExist(@Param("goodsId") int goodsId, @Param("userId") int userId);
}