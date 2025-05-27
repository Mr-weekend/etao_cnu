package cn.etaocnu.cloud.file.client;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.file.client.config.FeignMultipartConfig1;
import cn.etaocnu.cloud.model.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "service-file", configuration = FeignMultipartConfig1.class)
public interface FileFeignClient {

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module);

    @PostMapping(value = "/file/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<List<FileResponse>> uploadFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "module", required = false) String module);

    @DeleteMapping("/file/delete")
    Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl);

    @PostMapping(value = "/file/upload/with-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<List<FileResponse>> uploadFilesWithData(
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart("goodsInfo") String goodsInfoJson);
}