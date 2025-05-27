package cn.etaocnu.cloud.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileResponse {

    @Schema(description = "原始文件名")
    private String originalFilename;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;
}
