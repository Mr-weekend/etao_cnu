package cn.etaocnu.cloud.user.service.impl;

import cn.etaocnu.cloud.common.result.Result;
import cn.etaocnu.cloud.file.client.FileFeignClient;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import cn.etaocnu.cloud.user.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Resource
    private FileFeignClient fileFeignClient;
    @Override
    public Result<FileResponse> uploadFile(MultipartFile file, String module) {
        Result<FileResponse> result = fileFeignClient.uploadFile(file, module);
        return result.getCode() == 200 ? Result.success("上传成功！",result.getData())
                : Result.fail("上传失败");
    }

    @Override
    public Result<List<FileResponse>> uploadFiles(MultipartFile[] files, String module) {
        Result<List<FileResponse>> result = fileFeignClient.uploadFiles(files, module);

        return result.getCode() == 200 ? Result.success("上传成功！",result.getData())
                : Result.fail("上传失败！");
    }

    @Override
    public Result<Boolean> deleteFile(String fileUrl) {
        Result<Boolean> result = fileFeignClient.deleteFile(fileUrl);

        return result.getCode() == 200 ? Result.success("删除成功！",result.getData())
                : Result.fail("删除失败！");
    }

    @Override
    public Result<List<FileResponse>> uploadFilesWithData(MultipartFile[] files, GoodsCUVo goodsInfo) {
        try {
            // 将对象转换为JSON字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String goodsInfoJson = objectMapper.writeValueAsString(goodsInfo);
            
            log.info("web服务层转换的goodsInfoJson：{}", goodsInfoJson);
            Result<List<FileResponse>> result = fileFeignClient.uploadFilesWithData(files, goodsInfoJson);
            
            return result.getCode() == 200 ? 
                   Result.success("文件上传成功！", result.getData()) : 
                   Result.fail("文件上传失败！");
        } catch (Exception e) {
            log.error("转换对象到JSON失败", e);
            return Result.fail("系统错误");
        }
    }
}
