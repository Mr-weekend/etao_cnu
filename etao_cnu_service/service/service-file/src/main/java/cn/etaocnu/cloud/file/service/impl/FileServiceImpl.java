package cn.etaocnu.cloud.file.service.impl;

import cn.etaocnu.cloud.file.service.FileService;
import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.domain}")
    private String domain;

    @Override
    public FileResponse uploadFile(MultipartFile file, String module) {
        if (file.isEmpty()) {
            log.error("上传的文件为空");
            return null;
        }

        try {
            // 生成保存路径: 基础路径/模块名/年月日/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String modulePath = StringUtils.hasText(module) ? module + "/" : "";
            String relativePath = modulePath + datePath + "/";
            Path directory = Paths.get(uploadPath, relativePath);

            // 确保目录存在
            Files.createDirectories(directory);

            // 生成新的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 完整的文件保存路径
            Path filePath = directory.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 构建文件访问URL
            String fileUrl = domain + "/file/" + relativePath + filename;

            return new FileResponse(
                    originalFilename,
                    fileUrl,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FileResponse> uploadFiles(MultipartFile[] files, String module) {
        List<FileResponse> results = new ArrayList<>();

        for (MultipartFile file : files) {
            FileResponse response = uploadFile(file, module);
            if (response != null) {
                results.add(response);
            }
        }

        return results;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.startsWith(domain + "/file/")) {
            log.error("无效的文件URL: {}", fileUrl);
            return false;
        }

        try {
            // 从URL提取相对路径
            String relativePath = fileUrl.substring((domain + "/file/").length());
            Path filePath = Paths.get(uploadPath, relativePath);

            File file = filePath.toFile();
            if (!file.exists()) {
                log.warn("要删除的文件不存在: {}", filePath);
                return false;
            }

            return file.delete();
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }


    @Override
    public List<FileResponse> uploadFilesWithData(MultipartFile[] files, GoodsCUVo goodsCUVo) {
        List<FileResponse> results = new ArrayList<>();

        log.info("基础模块中的goodsVO：{}", goodsCUVo);
        
        // 如果没有文件，直接返回空列表
        if (files == null || files.length == 0) {
            return results;
        }

        for (MultipartFile file : files) {
            FileResponse response = uploadFile(file, null);
            if (response != null) {
                results.add(response);
            }
        }
        
        return results;
    }
}