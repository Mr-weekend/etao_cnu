package cn.etaocnu.cloud.goods.mapper;

import cn.etaocnu.cloud.model.entity.Goods;
import cn.etaocnu.cloud.model.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsMapper extends Mapper<Goods> {
    @Select("SELECT user_id, user_name, gender, avatar_url, user_profile, status FROM users WHERE user_id = #{userId}")
    UserVo getGoodsPublisher(@Param("userId") int userId);
}