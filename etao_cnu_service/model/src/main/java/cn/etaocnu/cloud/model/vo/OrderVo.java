package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "订单信息VO")
public class OrderVo {

    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "买家信息")
    private UserVo buyer;

    @Schema(description = "闲置物品信息")
    private GoodsVo goodsInfo;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "完成时间")
    private Date completedAt;

}
