package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "浏览记录")
public class BrowseHistoryVo {

    @Schema(description = "闲置物品ID")
    private Integer goodsId;

    @Schema(description = "闲置物品描述")
    private String description;

    @Schema(description = "闲置物品价格")
    private BigDecimal price;

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "闲置物品状态")
    private String status;

    @Schema(description = "闲置物品首图")
    private String imageUrl;

    @Schema(description = "用户Id")
    private Integer userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "浏览时间")
    private Date browseTime;
}
