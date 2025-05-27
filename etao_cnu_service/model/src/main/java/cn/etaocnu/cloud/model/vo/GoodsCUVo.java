package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "闲置物品信息增删改查Vo")
public class GoodsCUVo {
    
    @Schema(description = "闲置物品描述")
    private String description;

    @Schema(description = "闲置物品价格")
    private BigDecimal price;

    @Schema(description = "分类ID")
    private Integer categoryId;
    
}
