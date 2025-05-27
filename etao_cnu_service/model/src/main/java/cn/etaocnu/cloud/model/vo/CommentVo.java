package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "评价信息VO")
public class CommentVo {
    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "闲置物品信息")
    private CommentGoodsVo goodsInfo;

    @Schema(description = "订单成交时间")
    private Date completedTime;

    @Schema(description = "买家信息")
    private UserVo buyer;

    @Schema(description = "买家评价类型")
    private Byte buyerCommentType;

    @Schema(description = "买家评价内容")
    private String buyerCommentContent;

    @Schema(description = "买家评价关联图片")
    private List<String> buyerCommentImageUrls;

    @Schema(description = "买家评价时间")
    private Date buyerCommentTime;

    @Schema(description = "卖家信息")
    private UserVo seller;

    @Schema(description = "卖家评价类型")
    private Byte sellerCommentType;

    @Schema(description = "买家评价内容")
    private String sellerCommentContent;

    @Schema(description = "买家评价关联图片")
    private List<String> sellerCommentImageUrls;

    @Schema(description = "买家评价时间")
    private Date sellerCommentTime;
}
