package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "收藏信息VO")
public class CollectionVo {
    @Schema(description = "闲置物品信息")
    private GoodsVo goodsInfo;

    @Schema(description = "收藏时间")
    private Date CreatedAt;
}
