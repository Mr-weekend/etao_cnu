package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "新订单VO")
public class NewOrderVo {

    @Schema(description = "闲置物品ID")
    private Integer goodsId;

    @Schema(description = "买家ID")
    private Integer buyerId;

}
