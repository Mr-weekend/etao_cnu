package cn.etaocnu.cloud.file.controller;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RefreshScope
@RequestMapping("/file")
@Tag(name = "文件服务接口")
public class FileController {

    @Resource
    private FileService fileService;

    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public Result<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module) {
        log.info("上传文件: 文件名={}, 大小={}, 模块={}",
                file.getOriginalFilename(), file.getSize(), module);

        FileResponse response = fileService.uploadFile(file, module);
        if (response == null) {
            return Result.fail("文件上传失败");
        }

        return Result.success(response);
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<List<FileResponse>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "module", required = false) String module) {
        log.info("批量上传文件: 文件数量={}, 模块={}", files.length, module);

        List<FileResponse> responses = fileService.uploadFiles(files, module);
        if (responses.isEmpty()) {
            return Result.fail("文件上传失败");
        }

        return Result.success(responses);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        log.info("删除文件: fileUrl={}", fileUrl);

        boolean success = fileService.deleteFile(fileUrl);
        return success ? Result.success(true) : Result.fail("文件删除失败");
    }

    @Operation(summary = "批量上传文件并附带闲置物品数据")
    @PostMapping(value = "/upload/with-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<List<FileResponse>> uploadFilesWithData(
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart("goodsInfo") String goodsInfoJson) {
        
        log.info("批量上传文件并附带闲置物品数据: 文件数量={}, 闲置物品信息JSON={}", 
                files != null ? files.length : 0, goodsInfoJson);

        // 将JSON字符串转回对象
        GoodsCUVo goodsInfo = null;
        try {
            if (goodsInfoJson != null && !goodsInfoJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                goodsInfo = objectMapper.readValue(goodsInfoJson, GoodsCUVo.class);
                log.info("解析后的闲置物品信息对象: {}", goodsInfo);
            }
        } catch (Exception e) {
            log.error("解析JSON失败", e);
            return Result.fail("闲置物品信息格式错误");
        }
        
        // 打印文件信息
        if (files != null) {
            for (MultipartFile file : files) {
                log.info("接收到文件: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            }
        }
        
        List<FileResponse> responses = fileService.uploadFilesWithData(files, goodsInfo);
        if (files != null && !responses.isEmpty()) {
            return Result.success(responses);
        } else if (files == null) {
            // 如果没有文件上传，也返回成功，但返回空列表
            return Result.success(new ArrayList<>());
        } else {
            return Result.fail("文件上传失败");
        }
    }

}
