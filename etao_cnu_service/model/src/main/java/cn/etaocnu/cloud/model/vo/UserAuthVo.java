package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "认证信息VO")
public class UserAuthVo {

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "校园卡照片")
    private String authImage;

}
