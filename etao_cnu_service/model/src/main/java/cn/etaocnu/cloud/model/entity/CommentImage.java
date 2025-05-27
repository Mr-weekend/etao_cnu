package cn.etaocnu.cloud.model.entity;

import javax.persistence.*;

/**
 * 表名：comment_image
 * 表注释：评价图片表
*/
@Table(name = "comments_image")
public class CommentImage {
    /**
     * 图片ID
     */
    @Id
    @Column(name = "image_id")
    @GeneratedValue(generator = "JDBC")
    private Integer imageId;

    /**
     * 评价ID
     */
    @Column(name = "comment_id")
    private Integer commentId;

    /**
     * 图片URL
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 获取图片ID
     *
     * @return imageId - 图片ID
     */
    public Integer getImageId() {
        return imageId;
    }

    /**
     * 设置图片ID
     *
     * @param imageId 图片ID
     */
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    /**
     * 获取评价ID
     *
     * @return commentId - 评价ID
     */
    public Integer getCommentId() {
        return commentId;
    }

    /**
     * 设置评价ID
     *
     * @param commentId 评价ID
     */
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    /**
     * 获取图片URL
     *
     * @return imageUrl - 图片URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置图片URL
     *
     * @param imageUrl 图片URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}