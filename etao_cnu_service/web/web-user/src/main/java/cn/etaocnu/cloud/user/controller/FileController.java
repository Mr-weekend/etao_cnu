package cn.etaocnu.cloud.user.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import cn.etaocnu.cloud.user.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Slf4j
@Tag(name = "文件服务接口")
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module){
        return fileService.uploadFile(file, module);
    }

    @PostMapping(value = "/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<List<FileResponse>> uploadFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "module", required = false) String module){
        return fileService.uploadFiles(files, module);
    }

    @DeleteMapping("/delete")
    Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl){
        return fileService.deleteFile(fileUrl);
    }

    @PostMapping(value = "/upload/with-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<List<FileResponse>> uploadFilesWithData(
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart("goodsInfo") GoodsCUVo goodsInfo) {
        log.info("Web层接收到上传请求: 文件数量={}, 闲置信息={}", 
                files != null ? files.length : 0, goodsInfo);
        return fileService.uploadFilesWithData(files, goodsInfo);
    }
}
