package cn.etaocnu.cloud.user.mapper;

import cn.etaocnu.cloud.model.entity.User;
import cn.etaocnu.cloud.model.entity.UserAuth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface UserMapper extends Mapper<User> {
    @Select("SELECT * FROM user_auth WHERE user_id = #{userId}")
    UserAuth getUserAuth(@Param("userId") int userId);
    @Update("UPDATE user_auth SET auth_url = #{authUrl}, auth_status = 0 WHERE user_id = #{userId}")
    int updateUserAuth(@Param("userId") int userId, @Param("authUrl") String authUrl);
    @Insert("INSERT INTO user_auth(user_id, auth_url, auth_status, created_at) VALUES(#{userId}, #{authUrl}, 0, #{Date})")
    int insertUserAuth(@Param("userId") int userId, @Param("authUrl") String authUrl, @Param("Date") Date date);
}