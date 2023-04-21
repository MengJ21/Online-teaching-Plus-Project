package com.meng.media.service;

import com.meng.model.PageParams;
import com.meng.model.PageResult;
import com.meng.model.RestResponse;
import com.meng.model.dto.QueryMediaParamsDto;
import com.meng.model.dto.UploadFileParamsDto;
import com.meng.model.dto.UploadFileResultDto;
import com.meng.model.po.MediaFiles;
import com.meng.model.po.MediaProcess;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    /**
     * @param companyId
     * @param fileMd5
     * @param uploadFileResultDto
     * @param bucket
     * @param objectName
     * @return com.meng.model.po.MediaFiles
     * @description 将文件写入到数据库文件表
     * @author 梦举
     * @date 2023/4/9 10:43
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileResultDto, String bucket, String objectName);
    /**
     * @description 根据文件的绝对路径来将文件上传到minio。
     * @param filePath 文件的绝对路径
     * @param bucket 桶名称
     * @param objectName 文件存储，这名称里是合并后的文件的存储名称
     * @return void
     * @author 梦举
     * @date 2023/4/9 17:33
     */

    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName);
    /**
     * @param fileMd5
     * @return com.meng.model.RestResponse<java.lang.Boolean>
     * @description 检查要上传的文件是否存在。
     * @author 梦举
     * @date 2023/4/9 13:04
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @param fileMd5 文件md5
     * @param chunkIndex 分块索引
     * @return com.meng.model.RestResponse<java.lang.Boolean>
     * @description 检查分块是否存在
     * @author 梦举
     * @date 2023/4/9 13:05
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
    /**
    * @description 上传分块
    * @param fileMd5 文件md5
     * @param chunk 分块序号
     * @param bytes 文件字节
    * @return com.meng.model.RestResponse
    * @author 梦举
    * @date 2023/4/9 13:34
    */
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes);
    /**
    * @description 合并分块
    * @param companyId 机构id
     * @param fileMd5 文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
    * @return com.meng.model.RestResponse
    * @author 梦举
    * @date 2023/4/9 16:21
    */
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);
    /**
    * @description 根据文件id查询文件信息。
    * @param id 文件id
    * @return com.meng.model.po.MediaFiles
    * @author 梦举
    * @date 2023/4/12 11:16
    */
    public MediaFiles getFileById(String  id);

    /**
     * @description 下载具体的文件块，为了汇总所有的文件块。
     * @param file 文件块存储的对象
     * @param bucket 文件块所在桶
     * @param objectName 文件块名称
     * @return java.io.File
     * @author 梦举
     * @date 2023/4/9 17:36
     */
    public File downloadFileFromMinIO(File file, String bucket, String objectName);

}
