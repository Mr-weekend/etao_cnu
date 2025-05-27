package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "评价信息里的闲置物品信息VO")
public class CommentGoodsVo {
    @Schema(description = "闲置物品id")
    private Integer goodsId;

    @Schema(description = "发布者id")
    private Integer userId;

    @Schema(description = "描述信息")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "分类id")
    private Integer categoryId;

    @Schema(description = "图片URL")
    private String imageUrl;
}
