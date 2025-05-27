package cn.etaocnu.cloud.model.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 表名：goods_image
 * 表注释：闲置物品图片表
*/
@Table(name = "goods_image")
public class GoodsImage {
    /**
     * 图片唯一标识
     */
    @Id
    @Column(name = "image_id")
    @GeneratedValue(generator = "JDBC")
    private Integer imageId;

    /**
     * 关联的闲置物品ID
     */
    @Column(name = "goods_id")
    private Integer goodsId;

    /**
     * 图片URL地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 上传时间
     */
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * 获取图片唯一标识
     *
     * @return imageId - 图片唯一标识
     */
    public Integer getImageId() {
        return imageId;
    }

    /**
     * 设置图片唯一标识
     *
     * @param imageId 图片唯一标识
     */
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    /**
     * 获取关联的闲置物品ID
     *
     * @return goodsId - 关联的闲置物品ID
     */
    public Integer getGoodsId() {
        return goodsId;
    }

    /**
     * 设置关联的闲置物品ID
     *
     * @param goodsId 关联的闲置物品ID
     */
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取图片URL地址（支持本地存储或CDN）
     *
     * @return imageUrl - 图片URL地址（支持本地存储或CDN）
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置图片URL地址（支持本地存储或CDN）
     *
     * @param imageUrl 图片URL地址（支持本地存储或CDN）
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 获取上传时间
     *
     * @return createdAt - 上传时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置上传时间
     *
     * @param createdAt 上传时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}