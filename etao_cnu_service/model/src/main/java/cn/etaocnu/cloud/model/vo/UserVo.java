package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserVo {
    @Schema(description = "用户Id")
    private Integer userId;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "性别")
    private Byte gender;

    @Schema(description = "头像")
    private String avatarUrl;

    @Schema(description = "个人简介")
    private String userProfile;

    @Schema(description = "账号状态")
    private Byte status;

    @Schema(description = "违规原因")
    private String violationReason;
}
