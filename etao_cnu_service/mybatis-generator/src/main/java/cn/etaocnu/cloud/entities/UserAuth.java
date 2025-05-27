package cn.etaocnu.cloud.entities;

import java.util.Date;
import javax.persistence.*;

/**
 * 表名：user_auth
 * 表注释：认证信息表
*/
@Table(name = "user_auth")
public class UserAuth {
    /**
     * 认证记录唯一标识
     */
    @Id
    @Column(name = "auth_id")
    @GeneratedValue(generator = "JDBC")
    private Integer authId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 学生证照片
     */
    @Column(name = "auth_image")
    private String authImage;

    /**
     * 认证状态：0-待审核 1-通过 2-拒绝
     */
    @Column(name = "auth_status")
    private Boolean authStatus;

    /**
     * 提交时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 拒绝原因
     */
    @Column(name = "reject_reason")
    private String rejectReason;

    /**
     * 审核时间
     */
    @Column(name = "reviewed_at")
    private Date reviewedAt;

    /**
     * 获取认证记录唯一标识
     *
     * @return authId - 认证记录唯一标识
     */
    public Integer getAuthId() {
        return authId;
    }

    /**
     * 设置认证记录唯一标识
     *
     * @param authId 认证记录唯一标识
     */
    public void setAuthId(Integer authId) {
        this.authId = authId;
    }

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
     * 获取学生证照片
     *
     * @return authImage - 学生证照片
     */
    public String getAuthImage() {
        return authImage;
    }

    /**
     * 设置学生证照片
     *
     * @param authImage 学生证照片
     */
    public void setAuthImage(String authImage) {
        this.authImage = authImage;
    }

    /**
     * 获取认证状态：0-待审核 1-通过 2-拒绝
     *
     * @return authStatus - 认证状态：0-待审核 1-通过 2-拒绝
     */
    public Boolean getAuthStatus() {
        return authStatus;
    }

    /**
     * 设置认证状态：0-待审核 1-通过 2-拒绝
     *
     * @param authStatus 认证状态：0-待审核 1-通过 2-拒绝
     */
    public void setAuthStatus(Boolean authStatus) {
        this.authStatus = authStatus;
    }

    /**
     * 获取提交时间
     *
     * @return createdAt - 提交时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置提交时间
     *
     * @param createdAt 提交时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取拒绝原因
     *
     * @return rejectReason - 拒绝原因
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * 设置拒绝原因
     *
     * @param rejectReason 拒绝原因
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * 获取审核时间
     *
     * @return reviewedAt - 审核时间
     */
    public Date getReviewedAt() {
        return reviewedAt;
    }

    /**
     * 设置审核时间
     *
     * @param reviewedAt 审核时间
     */
    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}