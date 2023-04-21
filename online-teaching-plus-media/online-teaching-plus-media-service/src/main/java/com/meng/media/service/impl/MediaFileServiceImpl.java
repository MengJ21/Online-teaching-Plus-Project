package com.meng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.meng.exception.OnlineTeachingPlusException;
import com.meng.media.config.MinioConfig;
import com.meng.media.mapper.MediaProcessHistoryMapper;
import com.meng.media.mapper.MediaProcessMapper;
import com.meng.model.PageParams;
import com.meng.model.PageResult;
import com.meng.media.mapper.MediaFilesMapper;
import com.meng.model.RestResponse;
import com.meng.model.dto.QueryMediaParamsDto;
import com.meng.model.dto.UploadFileParamsDto;
import com.meng.model.dto.UploadFileResultDto;
import com.meng.model.po.MediaFiles;
import com.meng.media.service.MediaFileService;
import com.meng.model.po.MediaProcess;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {
    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    // 通过currentProxy实现仅有操作数据库时才实现的事务。
    @Autowired
    MediaFileService currentProxy;

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;


    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()), MediaFiles::getFileType, queryMediaParamsDto.getFileType());
        queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getAuditStatus()), MediaFiles::getAuditStatus, queryMediaParamsDto.getAuditStatus());
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    /**
     * @param companyId
     * @param uploadFileParamsDto
     * @param bytes
     * @param folder
     * @param objectName
     * @return com.meng.model.dto.UploadFileResultDto
     * @description 上传文件方法。
     * @author 梦举
     * @date 2023/4/9 10:17
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
        // 生成文件id，文件的md5值
        String fileId = DigestUtils.md5Hex(bytes);
        // 文件名称。
        String filename = uploadFileParamsDto.getFilename();
        // 构造objectName
        if (StringUtils.isEmpty(objectName)) {
            // 将文件id和提取的文件名称（不包括格式），新建为objectName。
            objectName = fileId + filename.substring(filename.lastIndexOf("."));
        }
        if (StringUtils.isEmpty(folder)) {
            // 通过日期构造文件存储路径
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder = folder + "/";
        }
        // minio存储的对象名称
        objectName = folder + objectName;
        MediaFiles mediaFiles = null;
        try {
            // 上传至文件系统
            addMediaFilesToMinIO(bytes, bucket_files, objectName);
            // 写入文件表
            mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileId, uploadFileParamsDto, bucket_files, objectName);
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            e.printStackTrace();
            OnlineTeachingPlusException.cast("上传过程出错！");
        }
        return null;
    }

    /**
     * @param bytes
     * @param bucketName
     * @param objectName
     * @return void
     * @description 上传文件到minio系统
     * @author 梦举
     * @date 2023/4/9 10:09
     */
    public void addMediaFilesToMinIO(byte[] bytes, String bucketName, String objectName) {
        // 转为流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 扩展名
        String extension = null;
        if (objectName.contains(".")) {
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        String contentType = getMimeTypeByExtension(extension);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(byteArrayInputStream, byteArrayInputStream.available(), -1).contentType(contentType).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            OnlineTeachingPlusException.cast("上传文件到minio出错");
        }
    }

    /**
     * @param extension
     * @return java.lang.String
     * @description 获取文件的格式。
     * @author 梦举
     * @date 2023/4/9 10:04
     */

    public String getMimeTypeByExtension(String extension) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

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
    @Override
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileResultDto, String bucket, String objectName) {
        // 根据文件名称取出媒体类型
        // 扩展名
        String extension = null;
        if (objectName.indexOf(".") >= 0) {
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        // 获取扩展名对应的媒体类型
        String contentType = getMimeTypeByExtension(extension);
        // 从数据库中查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileResultDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            // 图片及mp4文件设置url。
            if (contentType.contains("image") || contentType.contains("mp4")) {
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                OnlineTeachingPlusException.cast("保存文件信息失败");
            }
            // 如果是avi视频添加到视频待处理表
            if (contentType.equals("video/x-msvideo")) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                mediaProcess.setStatus("1");
                mediaProcessMapper.insert(mediaProcess);
            }
        }
        return mediaFiles;
    }

    /**
     * @param fileMd5
     * @return com.meng.model.RestResponse<java.lang.Boolean>
     * @description 判断文件是否存在。
     * @author 梦举
     * @date 2023/4/9 13:09
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            // 桶
            String bucket = mediaFiles.getBucket();
            // 存储目录
            String filePath = mediaFiles.getFilePath();
            // 文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filePath).build());
                if (stream != null) {
                    // 文件存在。
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }
        }
        return RestResponse.success(false);
    }

    /**
     * @param fileMd5
     * @param chunkIndex
     * @return com.meng.model.RestResponse<java.lang.Boolean>
     * @description 判断分块是否存在。
     * @author 梦举
     * @date 2023/4/9 13:09
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 获取分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 获取分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket_videoFiles).object(chunkFilePath).build());
            if (fileInputStream != null) {
                // 分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        // 分块文件不存在。
        return RestResponse.success(false);
    }

    /**
     * @param fileMd5
     * @param chunk
     * @param bytes
     * @return com.meng.model.RestResponse
     * @description 上传分块文件。
     * @author 梦举
     * @date 2023/4/9 16:13
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        // 得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        try {
            addMediaFilesToMinIO(bytes, bucket_videoFiles, chunkFilePath);
            return RestResponse.success(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("上传分块文件：{}，失败：{}", chunkFilePath, ex.getMessage());
        }
        return RestResponse.validfail(false, "上传分块文件失败");
    }

    /**
    * @description 合并所有的文件块，并将合并后的文件进行存储，主要就是获取所有块，并进行合并。
    * @param companyId  机构id
     * @param fileMd5 文件md5
     * @param chunkTotal 总块数
     * @param uploadFileParamsDto 上传文件的参数
    * @return com.meng.model.RestResponse
    * @author 梦举
    * @date 2023/4/9 17:31
    */
    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        String fileName = uploadFileParamsDto.getFilename();
        // 下载所有分块文件
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
        // 扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        // 创建临时吻技安作为合并文件。
        File mergeFile = null;
        try {
            mergeFile = File.createTempFile(fileMd5, extName);
        } catch (IOException e) {
            OnlineTeachingPlusException.cast("合并文件过程中创建临时文件出现异常");
        }
        try {
            // 开始合并。
            byte[] b = new byte[1024];
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw")) {
                for (File chunkFile : chunkFiles) {
                    try (FileInputStream chunkFileStream = new FileInputStream(chunkFile)) {
                        int len = -1;
                        while ((len = chunkFileStream.read(b)) != -1) {
                            // 向合并后的文件写
                            raf_write.write(b, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                OnlineTeachingPlusException.cast("合并文件过程中出错");
            }
            log.debug("合并文件完成{}", mergeFile.getAbsoluteFile());
            try (InputStream mergeFileInputStream = new FileInputStream(mergeFile)) {
                String newFileMd5 = DigestUtils.md5Hex(mergeFileInputStream);
                if (!fileMd5.equalsIgnoreCase(newFileMd5)) {
                    // 校验失败
                    OnlineTeachingPlusException.cast("合并文件校验失败");
                }
                log.debug("合并文件校验通过{}", mergeFile.getAbsoluteFile());
            } catch (Exception e) {
                e.printStackTrace();
                // 校验失败
                OnlineTeachingPlusException.cast("合并文件校验异常");
            }
            // 将临时文件上传到minio
            String mergeFilePath = getFilePathByMd5(fileMd5, extName);
            try {
                // 上传合并后的文件到minIO
                addMediaFilesToMinIO(String.valueOf(mergeFile.getAbsoluteFile()), bucket_videoFiles, mergeFilePath);
                log.debug("合并文件上传MinIO完成{}", mergeFile.getAbsoluteFile());
            } catch (Exception e) {
                e.printStackTrace();
                OnlineTeachingPlusException.cast("合并文件时上传文件出错");
            }
            // 入数据库
            MediaFiles mediaFiles = null;
            try {
                mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
            } catch (Exception e) {
                e.printStackTrace();
                OnlineTeachingPlusException.cast("上传文件到数据库时报错。");
            }
            if (mediaFiles == null) {
                OnlineTeachingPlusException.cast("媒资文件入库错误");
            }
            return RestResponse.success();
        } finally {
            //删除临时文件
            for (File file : chunkFiles) {
                try {
                    file.delete();
                } catch (Exception e) {

                }
            }
            try {
                mergeFile.delete();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        return mediaFilesMapper.selectById(id);
    }

    /**
   * @description 根据文件的绝对路径来将文件上传到minio。
   * @param filePath 文件的绝对路径
    * @param bucket 桶名称
    * @param objectName 文件存储，这名称里是合并后的文件的存储名称
   * @return void
   * @author 梦举
   * @date 2023/4/9 17:33
   */
    @Override
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
        // 扩展名
        String extension = null;
        if (objectName.contains(".")) {
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        String contentType = getMimeTypeByExtension(extension);
        try {
            minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectName).filename(filePath).contentType(contentType).build());
        } catch (Exception e) {
            e.printStackTrace();
            OnlineTeachingPlusException.cast("上传文件到文件系统出错");
        }
    }

    /**
     * @param date
     * @param year
     * @param month
     * @param day
     * @return java.lang.String
     * @description 将日期转换为“2023/4/9的格式”
     * @author 梦举
     * @date 2023/4/9 9:48
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前日期字符串
        String dateString = sdf.format(new Date());
        // 取出年，月，日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    /**
     * @param fileMd5
     * @return java.lang.String
     * @description 根据fileid获得分块文件的目录。
     * @author 梦举
     * @date 2023/4/9 13:19
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
    /**
    * @description 获取所有的文件块。
    * @param fileMd5 文件的md5。
     * @param chunkTotal 文件块总数。
    * @return java.io.File[]
    * @author 梦举
    * @date 2023/4/9 17:35
    */
    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        for (int i = 0; i < chunkTotal; i++) {
            String chunkFilePath = chunkFileFolderPath + i;
            // 下载文件
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (IOException e) {
                e.printStackTrace();
                OnlineTeachingPlusException.cast("下载分块时，创建临时文件出错");
            }
            downloadFileFromMinIO(chunkFile, bucket_videoFiles, chunkFilePath);
            files[i] = chunkFile;
        }
        return files;
    }
    /**
    * @description 下载具体的文件块，为了汇总所有的文件块。
    * @param file 文件块存储的对象
     * @param bucket 文件块所在桶
     * @param objectName 文件块名称
    * @return java.io.File
    * @author 梦举
    * @date 2023/4/9 17:36
    */
    @Override
    public File downloadFileFromMinIO(File file, String bucket, String objectName) {
        InputStream fileInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            fileInputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
            try {
                fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(fileInputStream, fileOutputStream);
            } catch (IOException e) {
                OnlineTeachingPlusException.cast("下载文件" + objectName + "出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            OnlineTeachingPlusException.cast("文件不存在" + objectName);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
    /**
    * @description 获取文件的名称，根据md5生成的所有块合成之后的名称。
    * @param fileMd5
     * @param fileExt 扩展名。
    * @return java.lang.String
    * @author 梦举
    * @date 2023/4/9 17:37
    */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}
