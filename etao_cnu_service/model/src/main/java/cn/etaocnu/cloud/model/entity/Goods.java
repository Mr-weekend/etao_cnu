package cn.etaocnu.cloud.model.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 表名：goods
 * 表注释：闲置物品信息表
*/
@Data
@Table(name = "goods")
public class Goods {
    /**
     * 闲置物品ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goodsId;

    /**
     * 发布者ID
     */
    private Integer userId;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 闲置物品价格
     */
    private BigDecimal price;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 闲置物品状态
     */
    private String status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 售出时间
     */
    private Date soldTime;

    /**
     * 更新时间
     */
    private Date updatedAt;



}