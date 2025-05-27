package cn.etaocnu.cloud.file.service;

import cn.etaocnu.cloud.model.response.FileResponse;
import cn.etaocnu.cloud.model.vo.GoodsCUVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    /**
     * 上传单个文件
     * @param file 文件对象
     * @param module 模块名称(如goods、user等)
     * @return 文件上传响应
     */
    FileResponse uploadFile(MultipartFile file, String module);

    /**
     * 上传多个文件
     * @param files 文件对象数组
     * @param module 模块名称
     * @return 文件上传响应列表
     */
    List<FileResponse> uploadFiles(MultipartFile[] files, String module);

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 批量上传文件并附带闲置物品数据
     * @param files 文件数组（可选）
     * @param goodsCUVo 闲置物品信息（必须）
     * @return 上传的文件响应列表
     */
    List<FileResponse> uploadFilesWithData(MultipartFile[] files, GoodsCUVo goodsCUVo);
}
