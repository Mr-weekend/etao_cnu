package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "新评价VO")
public class NewCommentVo {

    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "评价类型")
    private Byte type;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "是否匿名")
    private Boolean isAnonymous;

}
