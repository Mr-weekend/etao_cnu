package cn.etaocnu.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "聊天信息VO")
public class ChatMessagesVo {

    @Schema(description = "发送者ID")
    private Integer senderId;

    @Schema(description = "接收者ID")
    private Integer receiverId;

    @Schema(description = "闲置物品ID")
    private Integer goodsId;

    @Schema(description = "消息类型")
    private String type;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "发送时间")
    private Date createdAt;
}
