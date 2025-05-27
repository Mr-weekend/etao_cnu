package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "闲置物品信息VO")
public class GoodsVo {

    @Schema(description = "闲置物品ID")
    private Integer goodsId;
    
    @Schema(description = "闲置物品描述")
    private String description;
    
    @Schema(description = "闲置物品价格")
    private BigDecimal price;
    
    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "分类名")
    private String categoryName;

    @Schema(description = "闲置物品状态")
    private String status;
    
    @Schema(description = "浏览次数")
    private Integer viewCount;
    
    @Schema(description = "收藏次数")
    private Integer collectCount;
    
    @Schema(description = "发布时间")
    private Date publishTime;
    
    @Schema(description = "图片URL列表")
    private List<String> imageUrls;
    
    @Schema(description = "发布者信息")
    private UserVo publisher;
}