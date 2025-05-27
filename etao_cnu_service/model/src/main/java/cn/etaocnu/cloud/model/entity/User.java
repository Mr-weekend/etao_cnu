package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：user
 * 表注释：用户信息表
*/
@Table(name = "users")
public class User {
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "JDBC")
    private Integer userId;

    /**
     * 微信openId
     */
    @Column(name = "wx_open_id")
    private String wxOpenId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 个人简介
     */
    @Column(name = "user_profile")
    private String userProfile;

    /**
     * 账号状态：0-违规 1-正常 2-未认证
     */
    private Byte status;

    /**
     * 违规原因
     */
    @Column(name = "violation_reason")
    private String violationReason;

    /**
     * 注册时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * 获取用户ID
     *
     * @return userId - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取微信openId
     *
     * @return wxOpenId - 微信openId
     */
    public String getWxOpenId() {
        return wxOpenId;
    }

    /**
     * 设置微信openId
     *
     * @param wxOpenId 微信openId
     */
    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    /**
     * 获取用户名
     *
     * @return userName - 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取性别
     *
     * @return gender - 性别
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别
     *
     * @param gender 性别
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取头像URL
     *
     * @return avatarUrl - 头像URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置头像URL
     *
     * @param avatarUrl 头像URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 获取个人简介
     *
     * @return userProfile - 个人简介
     */
    public String getUserProfile() {
        return userProfile;
    }

    /**
     * 设置个人简介
     *
     * @param userProfile 个人简介
     */
    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * 获取账号状态：0-违规 1-正常 2-未认证
     *
     * @return status - 账号状态：0-违规 1-正常 2-未认证
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置账号状态：0-违规 1-正常 2-未认证
     *
     * @param status 账号状态：0-违规 1-正常 2-未认证
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取违规原因
     *
     * @return violationReason - 违规原因
     */
    public String getViolationReason() {
        return violationReason;
    }

    /**
     * 设置违规原因
     *
     * @param violationReason 违规原因
     */
    public void setViolationReason(String violationReason) {
        this.violationReason = violationReason;
    }

    /**
     * 获取注册时间
     *
     * @return createdAt - 注册时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置注册时间
     *
     * @param createdAt 注册时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     *
     * @return updatedAt - 更新时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}